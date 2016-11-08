package com.nghiabui.s2gparsing.macro;

import com.nghiabui.kommon.SetOperation;

import java.util.*;

public abstract class Macros {

	public abstract Optional<String> get(String name, boolean release);

	public List<String> getAsList(String name, boolean release) {
		final Optional<String> op = get(name, release);
		return op.isPresent() ? split(op.get()) : Collections.emptyList();
	}
	
	public Set<String> getAsSet(String name, boolean release) {
		return SetOperation.newSet(getAsList(name, release));
	}

	public Optional<Boolean> getAsBoolean(String name, boolean release) {
		return get(name, release).map(value -> {
			final String low = value.toLowerCase();
			return low.equals("true") || low.equals("on") || low.equals("yes") || low.equals("1");
		});
	}

	protected List<String> split(String text) {
		return TextSplitter.split(text);
	}

}
