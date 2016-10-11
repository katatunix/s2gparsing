package com.nghiabui.s2gparsing.macro;

import com.nghiabui.kommon.AppException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResolvedMacros extends Macros {

	private final Macros origin;

	public ResolvedMacros(Macros origin) {
		this.origin = origin;
	}

	@Override
	public Optional<String> get(String name, boolean release) {
		return resolveName(name, release, new ArrayList<>());
	}
	
	public String resolveValue(String value, boolean release) {
		return resolveValue(value, release, new ArrayList<>());
	}

	private Optional<String> resolveName(String name, boolean release, List<String> inProgressNames) {
		if (inProgressNames.contains(name)) {
			throw new AppException("There is a loop when resolving the macro: " + name);
		}
		inProgressNames.add(name);
		final Optional<String> result = origin.get(name, release)
			.map(value -> resolveValue(value, release, inProgressNames));
		inProgressNames.remove(name);
		return result;
	}

	private String resolveValue(String value, boolean release, List<String> inProgressNames) {
		final int len = value.length();
		String res = "";
		for (int i = 0; i < len; i++) {
			final char ch = value.charAt(i);
			if (ch == '$' && i + 1 < len && value.charAt(i + 1) == '(') {
				int j = i + 2;
				while (j < len && value.charAt(j) != ')') j++;
				if (j >= len) {
					throw new AppException("Invalid macro syntax: " + value);
				}
				final String name = value.substring(i + 2, j);
				final Optional<String> opValue = resolveName(name, release, inProgressNames);
				if (!opValue.isPresent()) {
					throw new AppException(String.format(
						"Not found macro %s when resolving value: %s", name, value
					));
				}
				res += opValue.get();
				i = j;
			} else {
				res += ch;
			}
		}
		return res;
	}

}
