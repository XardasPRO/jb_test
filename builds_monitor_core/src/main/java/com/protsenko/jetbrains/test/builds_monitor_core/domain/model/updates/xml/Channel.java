package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Channel {
	String id;
	String name;
	String status;
	String url;
	String feedback;
	String majorVersion;
	String licensing;
	List<Build> builds;
}
