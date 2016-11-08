package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.MapOperation;
import com.nghiabui.kommon.io.MatchingFolder;
import com.nghiabui.kommon.io.WildcardFolder;
import com.nghiabui.s2gparsing.macro.Macros;
import com.nghiabui.s2gparsing.win32.Win32Project;
import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.SetOperation;
import com.nghiabui.kommon.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectConfig {
	
	private final String name;
	private final Macros macros;
	private final NonmacConfig nonmacConfig;
	private final Win32Project win32Project;
	
	public ProjectConfig(String name, Macros macros, NonmacConfig nonmacConfig, Win32Project win32Project) {
		this.name = name;
		this.macros = macros;
		this.nonmacConfig = nonmacConfig;
		this.win32Project = win32Project;
	}
	
	public String name() {
		return name;
	}
	
	public String outputName(boolean release) {
		return macros.get("USE_SPECIFIC_OUTPUT_NAME", release).orElse(name());
	}
	
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
	
	private Map<Path, Set<String>> cachedSpecFlags = null;
	
	public Map<Path, Set<String>> specFlags(boolean release) {
		if (cachedSpecFlags == null) {
			final Map<Path, Set<String>> map = new HashMap<>();
			for (Tuple<String, Set<String>> tuple : nonmacConfig.specFlags(release)) {
				final String        pattern = tuple.x;
				final Set<String>   cflags  = tuple.y;
				for (Path matched : matchingFolder().matchedPaths(pattern, fullSources())) {
					map.put(matched, cflags);
				}
			}
			cachedSpecFlags = map;
		}
		return cachedSpecFlags;
	}
	
	private Set<Path> fullSources() {
		return SetOperation.union(sources(true), sources(false));
	}
	
	public Set<Path> includePaths(boolean release) {
		final Set<Path> s2gPaths = paths("INCLUDE_PATHS", release);
		if (!useWin32IncludePaths(release)) return s2gPaths;
		
		final Set<Path> win32Paths = new HashSet<>(
			win32Project.includePaths(nonmacConfig.msvcConfiguration(release))
		);
		return SetOperation.union(s2gPaths, win32Paths);
	}
	
	private boolean useWin32IncludePaths(boolean release) {
		return macros.getAsBoolean("USE_ADDITIONAL_INCLUDE_DIRECTORIES_FROM_VS", release).orElse(false);
	}
	
	private Set<Path> paths(String name, boolean release) {
		final Path win32Folder = win32Project.folderPath();
		return macros.getAsSet(name, release).stream()
			.map(win32Folder::combination)
			.collect(Collectors.toSet());
	}
	
	private Set<Path> cachedReleaseSources = null;
	private Set<Path> cachedDebugSources = null;
	
	public Set<Path> sources(boolean release) {
		if (cachedReleaseSources == null) {
			final Set<Path> full = new HashSet<>();
			
			full.addAll(win32Project.fullSources());
			full.addAll(wildcardFolder().matchedFiles(nonmacConfig.additionalPatterns(release)));
			
			full.removeAll(matchingFolder().matchedPaths(nonmacConfig.ignoredPatterns(release), full));
			for (String filterPath : nonmacConfig.ignoredFilters(release)) {
				full.removeAll(win32Project.filesOfFilter(filterPath));
			}
			
			final boolean useWin32ExcludeFlag =
				macros.getAsBoolean("USE_EXCLUDEFROMBUILD_VS_FLAG", release).orElse(false);
			
			final Set<Path> releaseExc = useWin32ExcludeFlag ?
				new HashSet<>(win32Project.excludedSources(nonmacConfig.msvcConfiguration(true))) :
				Collections.emptySet();
			
			final Set<Path> debugExc = useWin32ExcludeFlag ?
				new HashSet<>(win32Project.excludedSources(nonmacConfig.msvcConfiguration(false))) :
				Collections.emptySet();
			
			final Set<Path> releaseSources  = SetOperation.subtract(full, releaseExc);
			final Set<Path> debugSources    = SetOperation.subtract(full, debugExc);
			
			cachedReleaseSources    = releaseSources;
			cachedDebugSources      = debugSources;
		}
		
		if (release) return cachedReleaseSources;
		return cachedDebugSources;
	}
	
	public int ubNumber() {
		return nonmacConfig.ubNumber();
	}
	
	public Set<Path> ubExcludedSources(boolean release) {
		final List<Path> result = matchingFolder().matchedPaths(
			nonmacConfig.ubExcludedPatterns(release), sources(release)
		);
		return new HashSet<>(result);
	}
	
	public List<String> ldlibs(boolean release) {
		return macros.getAsList("LDLIBS", release);
	}
	
	public Set<String> ldflags(boolean release) {
		return flags("LDFLAGS", release);
	}
	
	//=========================================================================================================
	
	private WildcardFolder cachedWildcardFolder = null;
	private MatchingFolder cachedMatchingFolder = null;
	
	private WildcardFolder wildcardFolder() {
		if (cachedWildcardFolder == null) {
			cachedWildcardFolder = new WildcardFolder(win32Project.folderPath());
		}
		return cachedWildcardFolder;
	}
	
	private MatchingFolder matchingFolder() {
		if (cachedMatchingFolder == null) {
			cachedMatchingFolder = new MatchingFolder(wildcardFolder());
		}
		return cachedMatchingFolder;
	}
	
}
