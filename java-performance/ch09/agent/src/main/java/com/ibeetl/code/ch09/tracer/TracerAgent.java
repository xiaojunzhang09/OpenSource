package com.ibeetl.code.ch09.tracer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class TracerAgent {

    //JVM 首先尝试在代理类上调用以下方法

    public static void premain(String agentArgs, Instrumentation inst) {
        ClassFileTransformer transformer = new MyMethodTransformer();
        inst.addTransformer(transformer);
    }


}
