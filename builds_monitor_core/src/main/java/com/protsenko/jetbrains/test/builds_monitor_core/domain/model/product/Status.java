package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product;

public enum Status {
	NEW(0),
	QUEUED(1),
	DOWNLOADING(2),
	UPDATED(3);

	private int id;

	Status(int id) {
		this.id = id;
	}
}
