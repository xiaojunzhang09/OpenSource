package com.ibeetl.code.ch04.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class RedisClientPooledObjectFactory extends BasePooledObjectFactory<RedisClient> {
	String ip;
	int port;
	public RedisClientPooledObjectFactory(String ip,int port){
		this.ip = ip;
		this.port = port;
	}

	@Override
	public RedisClient create() throws Exception {
		RedisClient redisClient =  new RedisClient(ip,port);
		redisClient.connect();
		return redisClient;
	}

	@Override
	public PooledObject<RedisClient> wrap(RedisClient redisClient) {
		return new DefaultPooledObject<RedisClient>(redisClient);
	}

	@Override
	public void destroyObject(PooledObject<RedisClient> p) throws Exception{
		RedisClient redisClient = p.getObject();
		redisClient.close();
	}
}
