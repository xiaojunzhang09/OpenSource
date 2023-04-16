package com.ibeetl.code.ch09.reflect;

import java.lang.reflect.Field;

/**
 * 反射
 * @author 公众号 闲谈java开发
 */
public class GetValueByReflect {
    private GetValueByReflect() {

    }

    public static Object value(Object bean, String property) {
        try {
            Field field = bean.getClass().getDeclaredField(property);
            field.setAccessible(true);
            return field.get(bean);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e) {
            // Do nothing
        }
        return null;

    }
}
