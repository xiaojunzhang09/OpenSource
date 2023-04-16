package com.ibeetl.code.ch09.reflect;

import com.ibeetl.code.core.om.AttributeAccess;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射获取属性
 * @author 公众号 闲谈java开发
 */
public class ReflectAttributeAccess extends AttributeAccess {
  Map<String, Method> cache = new HashMap<>();
  public ReflectAttributeAccess(Class c) throws Exception{
    BeanInfo beanInfo = Introspector.getBeanInfo(c);
    PropertyDescriptor[] propDescriptors = beanInfo.getPropertyDescriptors();

    for (PropertyDescriptor propertyDescriptor: propDescriptors) {
      cache.put(propertyDescriptor.getName(),propertyDescriptor.getReadMethod());
    }
  }


  @Override
  public Object value(Object o, Object name) {
    try{
      Method m = cache.get(name);
      return m.invoke(o);
    }catch(Exception ex){
      throw new IllegalArgumentException(name.toString(),ex);
    }
  }
}
