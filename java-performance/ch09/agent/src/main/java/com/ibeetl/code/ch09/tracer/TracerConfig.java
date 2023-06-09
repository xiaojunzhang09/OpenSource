package com.ibeetl.code.ch09.tracer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface TracerConfig {
    /**
     * 指标名字
     * @return
     */
    String value() default "";

}
