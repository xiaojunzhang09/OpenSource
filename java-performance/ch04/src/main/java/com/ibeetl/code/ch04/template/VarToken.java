package com.ibeetl.code.ch04.template;

import java.io.IOException;

/**
 * 变量
 * @author 公众号 闲谈java开发
 */
public class VarToken implements Token {
	// 变量在变量表中的索引
	int varIndex = 0;

	public VarToken(int varIndex) {
		this.varIndex = varIndex;
	}

	@Override
	public final void render(Context ctx) throws IOException {
		Object obj = ctx.getArgs()[varIndex];
		ctx.getWriter().append(String.valueOf(obj));
	}
}
