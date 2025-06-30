package cn.pzhdv.blog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author PanZonghui
 * @since 2025-04-24 13:10:16
 * @Version: 1.0
 * @Description
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean(name = "redisTemplate")
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 我们为了自己开发方便，一般直接使用 <String, Object>
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // //json序列化配置
        Jackson2JsonRedisSerializer<Object> j2rs = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        j2rs.setObjectMapper(om);

        // String 的序列化
        StringRedisSerializer srs = new StringRedisSerializer();

        //key采用String序列化方式
        redisTemplate.setKeySerializer(srs);
        //hash的key采用String的序列化方式
        redisTemplate.setHashKeySerializer(srs);
        //value序列化方式采用jackson
        redisTemplate.setValueSerializer(j2rs);
        //hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(j2rs);
        //必须执行这个函数,初始化RedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
