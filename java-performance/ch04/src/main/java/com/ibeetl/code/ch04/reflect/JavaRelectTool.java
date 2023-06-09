package com.ibeetl.code.ch04.reflect;

import java.lang.reflect.Method;

/**
 * 常规方法，反射，但随时虚拟机的优化，反射性能越来越好
 * @author 公众号 闲谈java开发
 */
public class JavaRelectTool implements ReflectTool {

	Object[] EMPTY_PARA = new Object[]{};
	Class[] EMPTY_CLASS = new Class[0];

	@Override
	public Object getValue(Object target, String attr) {
		String methodName = buildGetterName(attr);
		try {
			Class targetClass = target.getClass();
			Method method = targetClass.getMethod(methodName, EMPTY_CLASS);
			Object value = method.invoke(target, EMPTY_PARA);
			return value;
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}

	}


	public static void main(String[] args) {
		JavaRelectTool tool = new JavaRelectTool();
		User user = new User();
		user.setName("abc");
		String value = (String) tool.getValue(user, "name");
		System.out.println(value);
	}
}
