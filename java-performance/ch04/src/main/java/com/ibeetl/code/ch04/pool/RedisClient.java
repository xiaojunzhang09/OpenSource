package com.ibeetl.code.ch04.pool;

public class RedisClient {
	String ip;
	int port;
	public RedisClient(String ip,int port){
		this.ip = ip;
		this.port = port;
	}
	public void connect(){
		System.out.println("connect redis ");
	}
	public void close(){
		System.out.println("close  ");
	}

}
