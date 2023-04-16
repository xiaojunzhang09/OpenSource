package com.ibeetl.code.ch09.tracer;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * https://z.itpub.net/article/detail/9C47ABA1CC46AEEC8A31A5BBEE078E9C
 * https://www.javassist.org/tutorial/tutorial2.html
 */
public class MyMethodTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if(className==null){
                return null;
            }
            String currentClassName = className.replaceAll("/", ".");
            if(!currentClassName.startsWith("com.ibeetl.code.ch09")){
                return null;
            }


            CtClass ctClass = ClassPool.getDefault().get(currentClassName);

            CtMethod[] methods = ctClass.getMethods();
            for(CtMethod ctMethod : methods){
                TracerConfig tracerConfig = (TracerConfig)ctMethod.getAnnotation(TracerConfig.class);
                if(tracerConfig ==null){
                    continue;
                }
                String name = tracerConfig.value();
                String field="MT_"+name;
                //如何去掉注解
                String fld = "private static java.lang.String "+field
                        +" = \""+name+"\";";

                ctClass.addField(CtField.make(fld,ctClass));

                enhanceMethod(field,ctClass,ctMethod, tracerConfig);

            }

            ctClass.writeFile();

            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    private void enhanceMethod(String fieldName,CtClass ctClass, CtMethod ctMethod, TracerConfig tracerConfig) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        ctMethod.addCatch("{com.ibeetl.code.ch09.tracer.ClientTracer.endError("+fieldName+");throw $e; }", ClassPool.getDefault().get("java.lang.RuntimeException"));   // 添加异常捕获
        ctMethod.insertBefore("com.ibeetl.code.ch09.tracer.ClientTracer.start("+fieldName+"); ");
        ctMethod.insertAfter("com.ibeetl.code.ch09.tracer.ClientTracer.end("+fieldName+"); ");
        // 输出类的内容
        ctClass.writeFile();
    }
}
