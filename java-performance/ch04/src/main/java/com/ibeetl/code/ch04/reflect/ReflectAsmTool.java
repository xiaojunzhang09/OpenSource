package com.ibeetl.code.ch04.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;

public class ReflectAsmTool implements ReflectTool {
	MethodAccess methodAccess = null;
	int index;

	public ReflectAsmTool(Class target, String attr) {
		methodAccess = MethodAccess.get(target);
		String methodName = buildGetterName(attr);
		index = methodAccess.getIndex(methodName);


	}

	@Override
	public Object getValue(Object target, String attr) {
		return methodAccess.invoke(target, index, attr);
	}


}
