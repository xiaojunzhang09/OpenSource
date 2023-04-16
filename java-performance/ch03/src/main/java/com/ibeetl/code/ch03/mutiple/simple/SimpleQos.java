package com.ibeetl.code.ch03.mutiple.simple;


import com.ibeetl.code.ch03.mutiple.Qos;

/**
 * 队列的qos，计算isAction必须保证线程安全。
 *
 * 通过递增Qos的值@{code start},当到达{@code HIGH}时候,允许执行
 */
public class SimpleQos implements Qos {
	int start = 0;
	int base = 0;
	//触发点，当@{code start} 到达此数的时候出发action
	final static int HIGH = 0;


	/**
	 * 每次都可以执行
	 * @return
	 */
	public static SimpleQos fine() {
		return new SimpleQos(0);
	}

	/**
	 * 俩次机会执行一次
	 * @return
	 */
	public static SimpleQos warn() {
		return new SimpleQos(-2);
	}

	/**
	 * 10次机会执行一次
	 * @return
	 */
	public static SimpleQos error() {
		return new SimpleQos(-10);
	}


	public SimpleQos(int start) {
		this.base = start;
		this.start = start;

	}


	public boolean isNormal() {
		return base == HIGH;
	}

	@Override
	public int getValue() {
		return base;
	}

	@Override
	public Qos valueOf(int qos) {
		return new SimpleQos(qos);
	}

	public boolean isAction() {
		if (isNormal()) {
			return true;
		}
		start++;
		if (start == HIGH) {
			start = base;
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String toString() {
		return "" + base;
	}

	@Override
	public int compareTo(Object o) {
		SimpleQos other = (SimpleQos) o;
		if (this.base > other.base) {
			return 1;
		} else if (this.base == other.base) {
			return 0;
		} else {
			return -1;
		}
	}


}
