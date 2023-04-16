package com.ibeetl.code.ch03.event;

import lombok.Data;

@Data
public class BaseEvent {
	String id;
	public BaseEvent(String id){
		this.id = id;
	}

}
