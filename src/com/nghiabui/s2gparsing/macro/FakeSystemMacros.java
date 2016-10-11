package com.nghiabui.s2gparsing.macro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeSystemMacros extends Macros {
	
	private Map<String, String> data = new HashMap<>();
	
	public void add(String name, String value) {
		data.put(name, value);
	}
	
	@Override
	public Optional<String> get(String name, boolean release) {
		final String value = data.get(name);
		return value == null ? Optional.empty() : Optional.of(value);
	}
	
}
