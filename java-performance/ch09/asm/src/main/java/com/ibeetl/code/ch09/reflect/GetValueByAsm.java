
package com.ibeetl.code.ch09.reflect;

import com.ibeetl.code.core.om.asm.ASMBeanFactory;

/**
 * 字节码生成代码
 * @author 公众号 闲谈java开发
 */
public class GetValueByAsm {
	private GetValueByAsm() {

	}

	private static final ASMBeanFactory asmBeanFactory;
	static {
		asmBeanFactory = new ASMBeanFactory();
		asmBeanFactory.setUsePropertyDescriptor(true);
	}

	public static Object value(Object bean, String attrName) {
		return asmBeanFactory.value(bean, attrName);
	}

}
