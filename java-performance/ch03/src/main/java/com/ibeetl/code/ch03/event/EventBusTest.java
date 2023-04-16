package com.ibeetl.code.ch03.event;

import com.google.common.eventbus.EventBus;

/**
 * @author 公众号 闲谈java开发
 */
public class EventBusTest {
	public static void main(String[] args) {
		//实际应用，需要构造线程池
		EventBus eventBus = new EventBus();

		eventBus.register(new MyListener());

		String id ="3434X33";
		eventBus.post(new StartEvent(id));
		eventBus.post(new StopEvent(id));
	}
}
