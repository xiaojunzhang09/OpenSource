package com.ibeetl.code.ch04.reflect;

public interface ReflectTool {
	/**
	 * 获取对象target的指定属性的值
	 * @param target
	 * @param attr
	 * @return
	 */
	public Object getValue(Object target, String attr);

	/**
	 * 设置对象target的属性，attr是属性名，value是属性值
	 * @param target
	 * @param attr
	 * @param value
	 */
	//  public void setValue(Object target,String attr,Object value);
	default String buildGetterName(String attr) {
		//根据属性名，得到getter方法
		return "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
	}
}
