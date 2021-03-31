package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ReleasesResponse {

	Map<String, ArrayList<Release>> releases = new LinkedHashMap<String, ArrayList<Release>>();

	@JsonAnySetter
	void set(String key, ArrayList<Release> value) {
		releases.put(key, value);
	}
}
