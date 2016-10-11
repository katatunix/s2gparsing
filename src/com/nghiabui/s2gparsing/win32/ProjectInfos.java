package com.nghiabui.s2gparsing.win32;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ProjectInfos {

	private final List<String> linesInSlnFile;

	public ProjectInfos(List<String> linesInSlnFile) {
		this.linesInSlnFile = linesInSlnFile;
	}
	
	private static final String REGEX = "Project\\(\"\\{(.*)\\}\"\\) = \"(.*)\", \"(.*)\", \"\\{(.*)\\}\"";

	public Map<String, String> get() {
		final Pattern pattern = Pattern.compile(REGEX);

		final Map<String, String> infos = new HashMap<>();
		for (String line : linesInSlnFile) {
			final Matcher matcher = pattern.matcher(line);
			if (!matcher.find()) continue;
			final String projName = matcher.group(2);
			final String projPath = matcher.group(3);
			infos.put(projName, projPath);
		}
		return infos;
	}

}
