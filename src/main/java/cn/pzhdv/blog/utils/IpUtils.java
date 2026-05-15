package cn.pzhdv.blog.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * IP地址工具类
 * 用于获取客户端真实IP地址，支持代理、负载均衡、CDN等场景
 *
 * @author PanZonghui
 * @since 2025-01-20
 */
public class IpUtils {

    /**
     * 已知的代理IP头（按优先级排序）
     * 包含国内外常见的代理/CDN 转发头
     */
    private static final List<String> PROXY_HEADERS = Arrays.asList(
            // 通用代理头
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "True-Client-IP",
            // 国内常见代理/CDN 头
            "X-Cluster-Client-IP",      // AWS ALB/CLB
            "X-Appengine-Client-IP",    // Google App Engine
            "CF-Connecting-IP",         // CloudFlare
            "CF-Ray",                    // CloudFlare (包含IP信息)
            "X-Azure-ClientIP",         // Azure
            "X-Datacenter-Id",           // 阿里云
            "X-Request-ID",             // 某些网关
            "X-Cdn-Real-Ip",            // 某些CDN
            "X-Cdn-Real-Ip-Port",       // 某些CDN
            "Cdn-Src-Ip",               // 某些CDN
            "Cdn-Real-Ip",              // 某些CDN
            "X-Nateway-Real-IP",        // 某些网关
            "X-Nginx-Proxy-IP"          // 某些Nginx配置
    );

    /**
     * 获取客户端真实IP地址
     * 考虑代理、负载均衡、CDN等多层网络架构
     *
     * <p>优先级说明：
     * <ol>
     *   <li>X-Forwarded-For: 记录完整的代理链路，可能包含多个IP，第一个为真实IP</li>
     *   <li>X-Real-IP: Nginx/Apache 代理常用，直接传递真实IP</li>
     *   <li>其他代理头: 兼容各种代理服务器和CDN</li>
     *   <li>request.getRemoteAddr(): 兜底方案，直接连接的IP</li>
     * </ol>
     *
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 遍历所有已知代理头，优先获取真实IP
        for (String header : PROXY_HEADERS) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                // X-Forwarded-For 可能包含多个IP（格式: client, proxy1, proxy2）
                // 取第一个即为真实客户端IP
                if ("X-Forwarded-For".equalsIgnoreCase(header) || "HTTP_X_FORWARDED_FOR".equalsIgnoreCase(header)) {
                    return ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 兜底：直接连接的IP（可能是真实IP，也可能是最后一层代理IP）
        String remoteAddr = request.getRemoteAddr();

        // 如果是 IPv6 本地回环地址，转为 IPv4 本地地址
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "::1".equals(remoteAddr)) {
            return "127.0.0.1";
        }

        return isValidIp(remoteAddr) ? remoteAddr : "unknown";
    }

    /**
     * 获取客户端真实IP地址（仅返回单个IP，不返回IP链）
     *
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        return getClientIpAddress(request);
    }

    /**
     * 获取完整的IP链路（用于调试和分析）
     *
     * @param request HTTP请求对象
     * @return IP链路字符串，格式: client, proxy1, proxy2, ...
     */
    public static String getClientIpChain(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 优先从 X-Forwarded-For 获取完整链路
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (isValidIp(xForwardedFor)) {
            return xForwardedFor;
        }

        // 尝试获取 X-Real-IP
        String xRealIp = request.getHeader("X-Real-IP");
        if (isValidIp(xRealIp)) {
            return xRealIp;
        }

        // 返回 remoteAddr
        return request.getRemoteAddr();
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip IP地址字符串
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String trimmedIp = ip.trim();

        // 排除无效标识
        if ("unknown".equalsIgnoreCase(trimmedIp)) {
            return false;
        }

        // 排除本地回环地址
        if ("0:0:0:0:0:0:0:1".equals(trimmedIp) || "::1".equals(trimmedIp)) {
            return false;
        }

        if ("127.0.0.1".equals(trimmedIp)) {
            return false;
        }

        // 排除内网地址（如果需要严格外网IP，可启用此检查）
        // if (isPrivateIp(trimmedIp)) return false;

        return true;
    }

    /**
     * 获取客户端IP地址（简化版本，用于日志记录）
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址，获取失败返回"unknown"
     */
    public static String getClientIpForLog(HttpServletRequest request) {
        try {
            return getClientIpAddress(request);
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 判断是否为内网IP
     *
     * @param ip IP地址
     * @return 是否为内网IP
     */
    public static boolean isPrivateIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // 10.0.0.0 - 10.255.255.255
        if (ip.startsWith("10.")) {
            return true;
        }

        // 172.16.0.0 - 172.31.255.255
        if (ip.startsWith("172.")) {
            try {
                int secondOctet = Integer.parseInt(ip.substring(4).split("\\.")[0]);
                return secondOctet >= 16 && secondOctet <= 31;
            } catch (Exception e) {
                return false;
            }
        }

        // 192.168.0.0 - 192.168.255.255
        if (ip.startsWith("192.168.")) {
            return true;
        }

        // 127.0.0.0 - 127.255.255.255 (本地回环)
        if (ip.startsWith("127.")) {
            return true;
        }

        return false;
    }
}

