package cn.pzhdv.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redisClearCurrentDatabase() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        System.out.println("已清空当前数据库");
    }

    @Test
    void contextLoads() {
    }

}
