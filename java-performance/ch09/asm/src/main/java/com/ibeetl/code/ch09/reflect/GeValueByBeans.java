package com.ibeetl.code.ch09.reflect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author 公众号 闲谈java开发
 */
public class GeValueByBeans {
    private GeValueByBeans() {

    }

    private static Map<Class<?>, Map<String, Method>> cache = new ConcurrentHashMap<>();

    public static Object value(Object bean, String property) {
        try {
			Map<String, Method> methodMap = cache.get(bean.getClass());
            if (methodMap == null) {
				BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
				PropertyDescriptor[] propDescriptors = beanInfo.getPropertyDescriptors();
				methodMap = new HashMap<String, Method>();
				for (PropertyDescriptor propertyDescriptor: propDescriptors) {
					methodMap.put(propertyDescriptor.getName(),propertyDescriptor.getReadMethod());
				}
				cache.put(bean.getClass(), methodMap);
            }
            Method getter = methodMap.get(property);
            Object value = getter.invoke(bean);
          	return value;
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }

    }
}
