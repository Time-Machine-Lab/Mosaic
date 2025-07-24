package io.github.tml.mosaic.config.mosaic;

import io.github.tml.mosaic.core.persistence.adapter.file.JSONFileAdapter;
import io.github.tml.mosaic.core.persistence.adapter.file.LocalFileAdapter;
import io.github.tml.mosaic.core.persistence.adapter.mysql.MybatisAdapter;
import io.github.tml.mosaic.core.persistence.adapter.mysql.MysqlAdapter;
import io.github.tml.mosaic.core.persistence.adapter.redis.RedisAdapter;
import io.github.tml.mosaic.core.persistence.adapter.redis.SpringRedisAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */

public class MosaicPersistenceConfig {

//    @Bean
//    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
//    public RedisAdapter springRedisAdapter(Object redisTemplate) {
//        return new SpringRedisAdapter((RedisTemplate<String, String>) redisTemplate);
//    }

//    @Bean
//    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
//    public RedisAdapter jedisAdapter(@Value("${jedis.host:localhost}") String host,
//                                     @Value("${jedis.port:6379}") int port) {
//        try {
//            // 动态加载 Jedis 类，避免类加载阶段出错
//            Class.forName("redis.clients.jedis.Jedis");
//            return new JedisAdapter(host, port);
//        } catch (ClassNotFoundException e) {
//            throw new IllegalStateException("Jedis not available on the classpath.", e);
//        }
//    }

//    @Bean
//    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
//    public RedisAdapter redissonAdapter(ApplicationContext context) {
//        try {
//            Class<?> redissonClientClass = Class.forName("org.redisson.api.RedissonClient");
//            Object redissonClient = context.getBean(redissonClientClass); // 延迟从 ApplicationContext 获取 Bean
//            return new RedissonAdapter((RedissonClient) redissonClient);
//        } catch (ClassNotFoundException e) {
//            throw new IllegalStateException("RedissonClient not available on the classpath.", e);
//        }
//    }

    @Bean
    @ConditionalOnClass(name = "org.mybatis.spring.SqlSessionFactoryBean")
    public MysqlAdapter mybatisAdapter(ApplicationContext context) {
        try {
            // 动态检查 SqlSessionTemplate 是否存在
            Class<?> sqlSessionTemplateClass = Class.forName("org.mybatis.spring.SqlSessionTemplate");
            Object sqlSessionTemplate = context.getBean(sqlSessionTemplateClass); // 延迟获取 Bean
            return new MybatisAdapter((org.mybatis.spring.SqlSessionTemplate) sqlSessionTemplate);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SqlSessionTemplate not available on the classpath.", e);
        }
    }

    @Bean
    @ConditionalOnClass(name = "com.alibaba.fastjson.JSON")
    public LocalFileAdapter fastjsonAdapter() {
        return new JSONFileAdapter();
    }
}