package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class Updates {
	List<Product> products;
}
