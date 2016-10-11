package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.Tuple;
import com.nghiabui.s2gparsing.macro.ResolvedMacros;
import com.nghiabui.s2gparsing.win32.Win32Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SolutionConfig {
	
	private final FinalDocument finalDocument;
	private final Win32Solution win32Solution;
	private final Path sln2gccFile;
	
	public SolutionConfig(FinalDocument finalDocument, Win32Solution win32Solution, Path sln2gccFile) {
		this.finalDocument = finalDocument;
		this.win32Solution = win32Solution;
		this.sln2gccFile = sln2gccFile;
	}
	
	public String sln2gccFileName() {
		return sln2gccFile.name();
	}
	
	public List<GlobalConfig> globalConfigs() {
		final List<GlobalConfig> result = new ArrayList<>();
		finalDocument.globalMacroses().forEach((name, macros) ->
			result.add(new GlobalConfig(name, macros, sln2gccFile.parentFolder()))
		);
		return result;
	}
	
	public Optional<GlobalConfig> globalConfig(String name) {
		return finalDocument.globalMacros(name)
			.map(macros -> new GlobalConfig(name, macros, sln2gccFile.parentFolder()));
	}
	
	public List<ProjectConfig> projectConfigs(Consumer<String> notFoundInWin32Consumer) {
		final List<ProjectConfig> result = new ArrayList<>();
		finalDocument.projectInfos().forEach((name, info) -> {
			final Optional<ProjectConfig> op = info2config(name, info);
			if (op.isPresent()) {
				result.add(op.get());
			} else {
				notFoundInWin32Consumer.accept(name);
			}
		});
		return result;
	}
	
	public Optional<ProjectConfig> projectConfig(String name) {
		return finalDocument.projectInfo(name).flatMap(info -> info2config(name, info));
	}
	
	private Optional<ProjectConfig> info2config(String name, Tuple<ResolvedMacros, NonmacConfig> info) {
		return win32Solution.project(name).map(win32Project ->
			new ProjectConfig(name, info.x, info.y, win32Project)
		);
	}
	
}
