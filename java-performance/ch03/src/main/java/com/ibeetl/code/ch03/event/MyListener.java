package com.ibeetl.code.ch03.event;

import com.google.common.eventbus.Subscribe;

public class MyListener {
	@Subscribe
	public void listenInteger(StartEvent startEvent) {
		System.out.println("startEvent ->" + startEvent.getId());
	}

	@Subscribe
	public void listenInteger(StopEvent stopEvent) {
		System.out.println("stopEvent#listenInteger ->" + stopEvent.getId());
	}
}
