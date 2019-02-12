package com.huanshare.tools.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class RedisService {

    private Logger logger = LoggerFactory.getLogger(RedisService.class);

    //默认1天
    private final static Long DEFAULT_EXPIRE_SECONDS = 86400L;

    private RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean set(final String key, final Object value) {
        return set(key, value, DEFAULT_EXPIRE_SECONDS);
    }

    public Boolean set(final String key, final Object value, final Long seconds) {
        if (redisTemplate == null) {
            return false;
        }

        try {
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            stringObjectValueOperations.set(key, value, seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    public Object get(final String key) {
        if (redisTemplate == null) {
            return null;
        }

        try {
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            return stringObjectValueOperations.get(key);
        } catch (Exception e) {
            logger.error("redis service get error " + e.getMessage(), e);
            return null;
        }
    }

    public Boolean delete(final String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("redis service delete error " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public Boolean delete(final Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            logger.error("redis service deletes error " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public Long increment(final String key, final Long delta) {
        if (redisTemplate == null) {
            return 0L;
        }

        try {
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            return stringObjectValueOperations.increment(key, delta);
        } catch (Exception e) {
            logger.error("redis service increment error " + e.getMessage(), e);
            return 0L;
        }
    }

    public Boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        if (redisTemplate == null) {
            return false;
        }

        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            logger.error("redis service expire error " + e.getMessage(), e);
            return false;
        }
    }

    public Long getExpire(final String key) {
        if (redisTemplate == null) {
            return 0L;
        }

        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            logger.error("redis service getExpire error " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 从存储在键中的列表中删除等于值的元素的第一个计数事件。
     * 计数参数以下列方式影响操作：
     * count> 0：删除等于从头到尾移动的值的元素。
     * count <0：删除等于从尾到头移动的值的元素。
     * count = 0：删除等于value的所有元素。
     */
    public Long remove(String key, long count, Object value) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            return stringObjectValueOperations.remove(key, count, value);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据下表获取列表中的值，下标是从0开始的
     */
    public Object index(String key, long index) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            return stringObjectValueOperations.index(key, index);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回存储在键中的列表的指定元素。偏移开始和停止是基于零的索引，其中0是列表的第一个元素（列表的头部），1是下一个元素
     */
    public List<Object> range(String key, long start, long end) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            return stringObjectValueOperations.range(key, start, end);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从右边插入元素
     */
    public Boolean rightPushAll(String key, List<Object> value) {
        if (redisTemplate == null) {
            return false;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            stringObjectValueOperations.rightPushAll(key, value);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 从左边获取元素，并移除该值
     */
    public Object leftPop(String key) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            return stringObjectValueOperations.leftPop(key);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从左边获取元素，并移除该值，设置超时
     */
    public Object leftPop(String key, long timeout, TimeUnit unit) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            return stringObjectValueOperations.leftPop(key, timeout, unit);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取list中元素数量
     */
    public Long getListSize(String key) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            ListOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForList();
            return stringObjectValueOperations.size(key);
        } catch (Exception e) {
            logger.error("redis service set error " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 判断redis是否存在该key
     */
    public Boolean hasKey(final String key) {
        Boolean hasKey = false;
        try {
            hasKey = redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("redis service delete error " + e.getMessage(), e);
            return hasKey;
        }
        return hasKey;
    }

    /**
     * 获取 RedisSerializer
     */
    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

}
