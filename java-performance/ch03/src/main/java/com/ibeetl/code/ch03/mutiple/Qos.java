package com.ibeetl.code.ch03.mutiple;

public interface Qos extends Comparable {
	/**
	 * 是否允许拉取数据，QosPolicy 可以调用此方法询问是否可以获取到数据
	 * @return
	 */
	boolean isAction();

	/**
	 * 是否是正常qos，QosPolicy可以调用此方法，判断是否是正常qos。
	 * @return
	 */
	boolean isNormal();

	/**
	 * qos的一个int表达式，越高，qos越高
	 * @return
	 */
	int getValue();

	Qos valueOf(int qos);
}
