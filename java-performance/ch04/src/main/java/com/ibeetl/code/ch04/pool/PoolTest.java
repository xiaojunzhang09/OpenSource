package com.ibeetl.code.ch04.pool;

public class PoolTest {
	static RedisClientPoolUtil util = new RedisClientPoolUtil();
	static{
		util.init("127.0.0.1",9090,3);
	}
	public static void main(String[] args) throws Exception {
		RedisClient redisClient1 = util.getOne();
		RedisClient redisClient2 = util.getOne();
		RedisClient redisClient3 = util.getOne();
		//会阻塞住，因为总共创建了3个
		RedisClient redisClient4 = util.getOne();
	}
}
