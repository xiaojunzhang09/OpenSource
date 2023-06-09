package com.ibeetl.code.ch05.caffeine;

/**
 * 模拟一个电商商品信息
 * @author 公众号 闲谈java开发
 */
public class SkuInfo {
	private final String key;
	private String name="";

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SkuInfo(String key) {
		this.key = key;
		this.name = "商品 "+key;
	}

	public static SkuInfo get(String key){
		SkuInfo info =  new SkuInfo(key);
		return info;
	}



	@Override
	public String toString() {
		return "SkuInfo{" + "key='" + key + '\'' + ", name='" + name + '\'' + '}';
	}
}