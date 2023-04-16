package com.ibeetl.code.ch01.sample;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CommonUtil {
    //不要使用这种方式，在分布式&和微服务情况下，Collections.EMPTY_MAP不容易被序列化框架识别
    public final static Map EMPTY_MAP =Collections.EMPTY_MAP;
}
