package com.nghiabui.s2gparsing.macro;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextSplitter {

	public static List<String> split(String text) {
		final String[] parts = text
			.replace('\t', ' ')
			.replace("\r\n", " ")
			.replace('\n', ' ')
			.replace(';', ' ')
			.split(" ");

		return Arrays.stream(parts)
			.filter(part -> !part.trim().isEmpty())
			.collect(Collectors.toList());
	}

}
