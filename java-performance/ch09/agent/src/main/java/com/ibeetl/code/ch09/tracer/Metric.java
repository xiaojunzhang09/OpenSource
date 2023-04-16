package com.ibeetl.code.ch09.tracer;

import lombok.Data;

/**
 * 指标名字
 * @author 公众号 闲谈java开发
 */
@Data
public class Metric {
    String name;
    long start;
    long end;
    boolean error;
    String msg;
}
