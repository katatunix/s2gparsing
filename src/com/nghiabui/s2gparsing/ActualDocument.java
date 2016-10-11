package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.MapOperation;
import com.nghiabui.kommon.Path;
import com.nghiabui.s2gparsing.win32.Win32Solution;
import com.nghiabui.kommon.xml.DocumentFactory;
import com.nghiabui.kommon.xml.NodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.Optional;

class ActualDocument {
	
	private final BasicDocument basicDocument;
	private final Path workFolder;
	private final Win32Solution win32Solution;
	
	public ActualDocument(BasicDocument basicDocument, Path workFolder, Win32Solution win32Solution) {
		this.basicDocument = basicDocument;
		this.workFolder = workFolder;
		this.win32Solution = win32Solution;
	}
	
	public Map<String, Element> globalElements() {
		return basicDocument.globalElements();
	}
	
	public Optional<Element> globalElement(String name) {
		return basicDocument.globalElement(name);
	}
	
	public Map<String, Element> projectElements() {
		return MapOperation.transform(basicDocument.projectElements(), this::actualProjectElement);
	}
	
	public Optional<Element> projectElement(String name) {
		return basicDocument.projectElement(name).map(e -> actualProjectElement(name, e));
	}
	
	private Element actualProjectElement(String name, Element origin) {
		final String useS2GFile = origin.getAttribute("UseS2GFile");
		final String low = useS2GFile.toLowerCase();
		if (low.isEmpty() || low.equals("false") || low.equals("no")) {
			return origin;
		}
		
		final Path actualPath;
		if (low.equals("true") || low.equals("yes")) {
			final Optional<Path> op = win32Solution.projectFile(name);
			if (!op.isPresent()) return origin;
			final Path win32ProjFile = op.get();
			actualPath = win32ProjFile.parentFolder().combination(win32ProjFile.baseName() + ".s2g");
		} else {
			actualPath = workFolder.combination(useS2GFile);
		}
		
		if (!actualPath.exists()) {
			return origin;
		}
		final Optional<Document> opDocument = DocumentFactory.createDocument(actualPath);
		if (!opDocument.isPresent()) {
			return origin;
		}
		final Optional<Element> opElement = NodeUtil.toList(opDocument.get().getElementsByTagName("Project"))
			.stream()
			.findFirst();
		return !opElement.isPresent() ? origin : opElement.get();
	}
	
}
