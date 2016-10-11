package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.AppException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class FlagCorrector {
	
	public static Set<String> correct(List<String> flags) {
		final List<String> result = new ArrayList<>();
		final int size = flags.size();
		int count = 0;
		for (int i = 0; i < size; ++i) {
			final String thisFlag = flags.get(i);
			if (thisFlag.startsWith("-")) {
				result.add(thisFlag);
				++count;
			} else if (i > 0) {
				final String updated = result.get(count - 1) + " " + thisFlag;
				result.set(count - 1, updated);
			} else {
				throw new AppException("Something went wrong with the flag list: " + flags.toString());
			}
		}
		return new HashSet<>(result);
	}
	
}
