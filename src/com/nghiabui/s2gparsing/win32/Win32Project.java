package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.AppException;
import com.nghiabui.kommon.ListOperation;
import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.xml.DocumentFactory;
import com.nghiabui.kommon.xml.NodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Win32Project {

	private final Path projFolder;
	private final Document document;
	private final Filter filter;

	public Win32Project(Path projFolder, Document document, Filter filter) {
		this.projFolder = projFolder;
		this.document = document;
		this.filter = filter;
	}

	public Path folderPath() {
		return projFolder;
	}
	
	public List<Path> includePaths(String configName) {
		final List<Element> mainElements = aidElements(document).stream()
			.filter(e -> {
				final Node parent = e.getParentNode();
				if (parent == null) return false;
				final Node grand = parent.getParentNode();
				return grand != null && NodeUtil.isElement(grand) && conditionPassed((Element) grand, configName);
			}).collect(Collectors.toList());
		
		final String PROJECT = "Project";
		final List<String> propsFiles = NodeUtil.toList(document.getElementsByTagName("ImportGroup")).stream()
			.filter(e -> "PropertySheets".equals(e.getAttribute("Label")) && conditionPassed(e, configName))
			.flatMap(e -> NodeUtil.toList(e.getChildNodes()).stream())
			.filter(e -> {
				if (!e.getTagName().equals("Import")) return false;
				final String project = e.getAttribute(PROJECT);
				return project != null && !project.contains("$");
			})
			.map(e -> e.getAttribute(PROJECT))
			.collect(Collectors.toList());
		
		final List<String> fullPropsFiles = new ArrayList<>();
		final String COMMON_PROPS = "common.props";
		if (projFolder.combination(COMMON_PROPS).exists()) {
			fullPropsFiles.add(COMMON_PROPS);
		}
		fullPropsFiles.addAll(propsFiles);
		
		final List<Element> externalElements = fullPropsFiles.stream()
			.flatMap(file -> {
				final Path propsFilePath = projFolder.combination(file);
				final Optional<Document> op = DocumentFactory.createDocument(propsFilePath);
				if (!op.isPresent()) {
					throw new AppException("Could not read and parse: \"" + propsFilePath.absolute() + "\"");
				}
				return aidElements(op.get()).stream();
			})
			.collect(Collectors.toList());
		
		return aidElements2Paths(ListOperation.concat(mainElements, externalElements));
	}
	
	private static List<Element> aidElements(Document doc) {
		return NodeUtil.toList(doc.getElementsByTagName("AdditionalIncludeDirectories"));
	}
	
	private List<Path> aidElements2Paths(List<Element> elements) {
		return elements.stream()
			.map(Node::getTextContent)
			.flatMap(paths -> Arrays.stream(paths.split(";")))
			.filter(path -> !path.contains("%"))
			.map(projFolder::combination)
			.collect(Collectors.toList());
	}

	public List<Path> fullSources() {
		return sources(e -> true);
	}

	public List<Path> excludedSources(String configName) {
		return sources(e ->
			NodeUtil.toList(e.getChildNodes()).stream()
				.anyMatch(child ->
					child.getTagName().equals("ExcludedFromBuild") &&
					conditionPassed(child, configName) &&
					child.getTextContent().equals("true")
				)
		);
	}

	private List<Path> sources(Predicate<Element> filterFunc) {
		return NodeUtil.toList(document.getElementsByTagName("ClCompile")).stream()
			.filter(filterFunc)
			.map(e -> e.getAttribute("Include"))
			.filter(path -> !path.isEmpty())
			.map(projFolder::combination)
			.collect(Collectors.toList());
	}

	public List<Path> filesOfFilter(String filterPath) {
		return filter.filesOf(filterPath).stream()
			.map(projFolder::combination)
			.collect(Collectors.toList());
	}

	private static boolean conditionPassed(Element element, String configName) {
		final String condition = element.getAttribute("Condition");
		if (condition.isEmpty()) return false;
		if (!condition.startsWith("'$(Configuration)|$(Platform)'==")) {
			throw new AppException("Not support this condition: " + condition);
		}
		return condition.contains("'" + configName + "'");
	}

}
