package com.ibeetl.code.ch09.tracer;

/**
 * 执行时候需要添加vm参数 -javaagent:./target/agent-1.0-SNAPSHOT.jar=xxConfigText
 *
 */
public class TestSample {
    private static java.lang.String MT_sayHello2 = "sayHello2";
    public static void main(String[] args) {
        TestSample testSample = new TestSample();
        testSample.sayHello("java agent");
        testSample.sayHello2("java agent");
    }

    @TracerConfig("sayHello")
    public void sayHello(String name){
        System.out.println("hello,hi "+name);
    }

    /**
     * 通过agent生成代码类似此方法
     * @param name
     */
    public void sayHello2(String name){
        com.ibeetl.code.ch09.tracer.ClientTracer.start(MT_sayHello2);
        try{
            System.out.println("hello,hi "+name);
        }catch (Exception ex){
            com.ibeetl.code.ch09.tracer.ClientTracer.endError(MT_sayHello2);
        }

        com.ibeetl.code.ch09.tracer.ClientTracer.end(MT_sayHello2);
    }
}
