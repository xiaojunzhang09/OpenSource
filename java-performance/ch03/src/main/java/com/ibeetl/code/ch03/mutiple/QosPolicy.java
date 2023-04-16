package com.ibeetl.code.ch03.mutiple;


/**
 * qos策略类，用来从mutl queue中取得一个元素
 * @param <T>
 */
public interface QosPolicy<T> {
	/**
	 * 初始化
	 * @param wrappers
	 */
	void init(QosQueueWrapper[] wrappers);

	/**
	 * 修改qos,子类可以调整整体qos策略和算法，比如此qos优先级非常高，则总是优先取出此wrapper的元素
	 * @param wrapper
	 * @param qos
	 */
	void modify(QosQueueWrapper wrapper, Qos qos);

	/**
	 * 取得一个可用元素，MutilBlockQueue 包含超过一个的时候，会调用此来取得数据
	 * @return
	 */
	T takeOne();


	/**
	 * 放弃一个，用于拒绝策略。优先放弃低优先级队列的元素，如果没有，则从id对应的队列里取出一个
	 * @param id， 队列标识
	 * @return
	 */
	T discardOne(String id);

	/**
	 * 通知QosQueueWrapper 增加了一个元素
	 * @param id
	 */
	default void offer(String id) {
		//do nothing
	}


	Statistics getStatistics();

	void setStatistics(Statistics stat);


}
