package com.ibeetl.code.ch03.mutiple;


import com.ibeetl.code.ch03.mutiple.simple.SimpleQos;

import java.util.concurrent.BlockingQueue;

public class QosQueueWrapper {
	Qos qos;
	BlockingQueue blockingQueue;
	String id;

	/**
	 *
	 * @param id 队列唯一标记
	 * @param qos
	 * @param blockingQueue
	 */
	public QosQueueWrapper(String id, Qos qos, BlockingQueue blockingQueue) {
		this.qos = qos;
		this.blockingQueue = blockingQueue;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Qos getQos() {
		return qos;
	}

	public void setQos(Qos qos) {
		this.qos = qos;
	}

	public void modifyQas(SimpleQos qos) {
		this.qos = qos;
	}

	public BlockingQueue getBlockingQueue() {
		return blockingQueue;
	}

	public String toString() {
		return id + " qos " + qos + " queue： size " + blockingQueue.size() + " content " + blockingQueue;
	}

}
