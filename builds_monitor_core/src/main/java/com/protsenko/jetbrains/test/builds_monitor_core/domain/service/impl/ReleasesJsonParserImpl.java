package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json.Release;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json.ReleasesResponse;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ReleasesJsonParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ReleasesJsonParserImpl implements ReleasesJsonParser {

	@Override
	public List<Release> parse(byte[] json) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			ReleasesResponse releases = mapper.readValue(json, ReleasesResponse.class);
			Collection<ArrayList<Release>> values = releases.getReleases().values();
			if (!values.isEmpty()) {
				return values.stream().findFirst().get();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}
