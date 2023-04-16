package com.ibeetl.code.ch04.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class RedisClientPoolUtil {
	GenericObjectPool<RedisClient> genericObjectPool;
	public void init(String ip,int port,int max){
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(max);
		genericObjectPool = new GenericObjectPool(new RedisClientPooledObjectFactory(ip,port),config);
	}

	public RedisClient getOne() throws Exception {
		return genericObjectPool.borrowObject();
	}


	public void returnObject(RedisClient redisClient){
		genericObjectPool.returnObject(redisClient);
	}
}
