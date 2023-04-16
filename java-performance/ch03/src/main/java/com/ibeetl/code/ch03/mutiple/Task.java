package com.ibeetl.code.ch03.mutiple;

public class Task implements Runnable {

	String id;
	Runnable runnable;

	public Task(String id, Runnable runnable) {
		this.id = id;
		this.runnable = runnable;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		runnable.run();
	}

	public String toString() {
		return runnable.toString();
	}
}
