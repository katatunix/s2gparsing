package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.AppException;
import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.xml.DocumentFactory;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.Optional;

public class Win32Solution {

	private final Path slnFolder;
	private final Map<String, String> projectInfos;

	public Win32Solution(Path slnFolder, Map<String, String> projectInfos) {
		this.slnFolder = slnFolder;
		this.projectInfos = projectInfos;
	}

	public Optional<Path> projectFile(String name) {
		return projectInfos.containsKey(name) ?
			Optional.of(slnFolder.combination(projectInfos.get(name))) :
			Optional.empty();
	}

	public Optional<Win32Project> project(String name) {
		final Optional<Path> opProjFile = projectFile(name);
		if (!opProjFile.isPresent()) {
			return Optional.empty();
		}

		final Path projFile = opProjFile.get();
		final Path projFolder = projFile.parentFolder();

		final Optional<Document> opProjDocument = DocumentFactory.createDocument(projFile);
		if (!opProjDocument.isPresent()) {
			throw new AppException("Could not parse file: " + projFile.canonical());
		}
		final Document projDocument = opProjDocument.get();
		
		final Path filterFile = projFolder.combination(projFile.name() + ".filters");
		final Optional<Document> opFilterDocument = DocumentFactory.createDocument(filterFile);
		final Filter filter = opFilterDocument.isPresent() ?
			new Win32Filter(opFilterDocument.get()) :
			new StubFilter();
		
		final Win32Project win32Project = new Win32Project(projFolder, projDocument, filter);
		return Optional.of(win32Project);
	}

}
