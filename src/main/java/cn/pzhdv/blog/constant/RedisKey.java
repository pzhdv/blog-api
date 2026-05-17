package cn.pzhdv.blog.constant;

/**
 * Redis 缓存 Key 常量
 *
 * @author PanZonghui
 * @version 1.0
 * @since 2025-12-31
 */
public class RedisKey {

    // 文章分类总数
    public static final String ARTICLE_CATEGORY_TOTAL_KEY = "article:category:total";
    // 文章分类列表
    public static final String ARTICLE_CATEGORY_LIST_KEY = "article:category:list";
    // 文章标签列表
    public static final String ARTICLE_TAG_CACHE_KEY = "article:tag:cache";
    // 文章总数
    public static final String ARTICLE_TOTAL_KEY = "article:total";
    // 文章发布日期列表
    public static final String ARTICLE_PUBLISH_DATE_LIST_KEY = "article:publish:date:list";
    // 首页文章列表
    public static final String ARTICLE_HOME_PAGE_LIST_KEY = "article:home:page:list";
    // 分类页文章列表
    public static final String ARTICLE_CATEGORY_PAGE_LIST_KEY = "article:category:page:list";
    // 文章详情
    public static final String ARTICLE_DETAIL_KEY = "article:detail:";
    // 博客作者信息
    public static final String BLOG_AUTHOR_CACHE_KEY = "blog:author:cache";
    // 博客宗旨/介绍信息
    public static final String BLOG_MISSION_CACHE_KEY = "blog:mission:cache";
    // 工作经历列表
    public static final String JOB_EXPERIENCE_CACHE_KEY = "job:experience:cache";
    private RedisKey() {
        // 工具类禁止实例化
    }
}