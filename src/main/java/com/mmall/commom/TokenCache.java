package com.mmall.commom;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by wuyuanyan on 2019/9/25.
 */
public class TokenCache {
    private static Logger logger=LoggerFactory.getLogger(TokenCache.class);

    //因为这个常量跟TokenCache的业务逻辑联系比较紧密,所以在这个类中进行定义
    public static final String  TOKEN_PREFIX="token_";
    //LRU算法
    //如何理解本地缓存?其实可以理解为map,不过这个map有最大容量限制,超过最大容量将使用LRU算法回收缓存,缓存里的键值对也有时间限制,超过设置时间将从缓存中删除该键值对
    private static LoadingCache<String ,String > localCache= CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载
                @Override
                public String load(String key) throws Exception {
                    return "null";//之所以返回null字符串是为了防止nullpointException
                }
            });
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }
    public static String getKey(String key){
        String value=null;
        try{
            value=localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
