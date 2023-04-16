package com.ibeetl.code.ch04;

import java.util.IdentityHashMap;

/**
 * 演示IdentityHashMap使用，避免计算hash
 * @author 公众号 闲谈java开发
 */
public class IdentityHashMapTest {
	public static void main(String[] args) {
		IdentityHashMap<String, Object> map = new IdentityHashMap<>();
		String key = "xx";
		map.put(new String("xx"), "a");
		map.put(new String("xx"), "b");
		map.put(key, "c");
		//输出 长度为3
		System.out.println("长度为" + map.size());
		System.out.println("value=" + map.get("xx"));
	}
}
