package com.ibeetl.code.ch04.template;

import java.io.IOException;

/**
 * 语法解析的抽象
 * @author 公众号 闲谈java开发
 * @see StaticTextToken
 * @See VarToken
 */
public interface Token {
	public void render(Context ctx) throws IOException;
}
