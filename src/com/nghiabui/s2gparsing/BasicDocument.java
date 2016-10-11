package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.xml.NodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

class BasicDocument {
	
	private final Document document;
	
	public BasicDocument(Document document) {
		this.document = document;
	}
	
	public Map<String, Element> globalElements() {
		return buildMap(globalElementList());
	}
	
	public Optional<Element> globalElement(String name) {
		return filterByName(globalElementList(), name);
	}
	
	private List<Element> globalElementList() {
		return getElementsByTagName("GccConfig");
	}
	
	//===========================================================================================================
	
	public Map<String, Element> projectElements() {
		return buildMap(projectElementList());
	}
	
	public Optional<Element> projectElement(String name) {
		return filterByName(projectElementList(), name);
	}
	
	private List<Element> projectElementList() {
		return getElementsByTagName("Project");
	}
	
	//===========================================================================================================
	
	public String solution() {
		final List<Element> solutions = getElementsByTagName("Solution");
		return solutions.isEmpty() ? "" : solutions.get(0).getAttribute("Path");
	}
	
	//===========================================================================================================
	
	private Map<String, Element> buildMap(Collection<Element> list) {
		final Map<String, Element> map = new HashMap<>();
		for (Element e : list) {
			map.put(getName(e), e);
		}
		return map;
	}
	
	private List<Element> getElementsByTagName(String tag) {
		return NodeUtil.toList(document.getElementsByTagName(tag));
	}
	
	private String getName(Element e) {
		return e.getAttribute("Name");
	}
	
	private Optional<Element> filterByName(Collection<Element> list, String name) {
		return list.stream().filter(e -> getName(e).equals(name)).findFirst();
	}
	
}
