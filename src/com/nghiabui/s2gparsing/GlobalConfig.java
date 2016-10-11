package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.Path;
import com.nghiabui.s2gparsing.macro.Macros;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalConfig {
	
	private final String name;
	private final Macros macros;
	private final Path workFolder;
	
	public GlobalConfig(String name, Macros macros, Path workFolder) {
		this.name = name;
		this.macros = macros;
		this.workFolder = workFolder;
	}
	
	public String name() {
		return name;
	}
	
	public Optional<String> mainProject() {
		return macros.get("MAIN_PROJECT", true);
	}
	
	//========================================================================================================
	
	public Set<String> defines(boolean release) {
		return macros.getAsSet("DEFINES", release);
	}
	
	public Set<String> cflags(boolean release) {
		return flags("CFLAGS", release);
	}
	
	private Set<String> flags(String name, boolean release) {
		return FlagCorrector.correct(macros.getAsList(name, release));
	}
	
	public Set<String> cppflags(boolean release) {
		return flags("CPPFLAGS", release);
	}
	
	public Set<Path> includePaths(boolean release) {
		return paths("INCLUDE_PATHS", release);
	}
	
	private Set<Path> paths(String name, boolean release) {
		return macros.getAsSet(name, release).stream()
			.map(workFolder::combination)
			.collect(Collectors.toSet());
	}
	
	//========================================================================================================
	
	public List<String> ldlibs(boolean release) {
		return macros.getAsList("LDLIBS", release);
	}
	
	public Set<String> ldflags(boolean release) {
		return flags("LDFLAGS", release);
	}
	
	public Set<Path> linkPaths(boolean release) {
		return paths("LINK_PATHS", release);
	}
	
	public Optional<Path> archiver(boolean release) {
		return path("LS", release);
	}
	
	private Optional<Path> path(String name, boolean release) {
		return macros.get(name, release).map(workFolder::combination);
	}
	
	public Optional<Path> linker(boolean release) {
		return path("LD", release);
	}
	
	public Optional<Path> dsymer(boolean release) {
		return path("OBJCOPY", release);
	}
	
	public Optional<Path> stripper(boolean release) {
		return path("STRIP", release);
	}
	
	//========================================================================================================
	
	public Optional<Path> pathWithKey(String key, boolean release) {
		return macros.get(key, release).map(workFolder::combination);
	}
	
	public Optional<String> stringWithKey(String key, boolean release) {
		return macros.get(key, release);
	}
	
}
