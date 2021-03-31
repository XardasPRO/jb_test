package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
public class Build {
	String number;
	String version;
	String fullNumber;
	String releaseDate;
}