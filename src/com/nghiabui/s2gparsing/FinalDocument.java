package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.MapOperation;
import com.nghiabui.kommon.Tuple;
import com.nghiabui.s2gparsing.macro.CompositeMacros;
import com.nghiabui.s2gparsing.macro.Macros;
import com.nghiabui.s2gparsing.macro.ResolvedMacros;
import com.nghiabui.s2gparsing.macro.XmlMacros;
import com.nghiabui.kommon.xml.NodeUtil;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class FinalDocument {
	
	private final ActualDocument actualDocument;
	private final Macros systemMacros;
	
	public FinalDocument(ActualDocument actualDocument, Macros systemMacros) {
		this.actualDocument = actualDocument;
		this.systemMacros = systemMacros;
	}
	
	public Map<String, ResolvedMacros> globalMacroses() {
		return MapOperation.transform(actualDocument.globalElements(), (name, e) -> element2macros(e));
	}
	
	public Optional<ResolvedMacros> globalMacros(String name) {
		return actualDocument.globalElement(name).map(this::element2macros);
	}
	
	public Map<String, Tuple<ResolvedMacros, NonmacConfig>> projectInfos() {
		return MapOperation.transform(actualDocument.projectElements(), (name, e) -> element2info(e));
	}
	
	public Optional<Tuple<ResolvedMacros, NonmacConfig>> projectInfo(String name) {
		return actualDocument.projectElement(name).map(this::element2info);
	}
	
	//============================================================================================================
	
	private ResolvedMacros element2macros(Element parent) {
		return new ResolvedMacros(new CompositeMacros(
			new XmlMacros(macroChildren(parent)),
			systemMacros
		));
	}
	
	private Tuple<ResolvedMacros, NonmacConfig> element2info(Element e) {
		final ResolvedMacros macros = element2macros(e);
		return new Tuple<>(macros, new NonmacConfig(nonmacroChildren(e), macros));
	}
	
	private List<Element> macroChildren(Element parent) {
		return NodeUtil.toList(parent.getChildNodes()).stream().filter(this::isMacro).collect(Collectors.toList());
	}
	
	private List<Element> nonmacroChildren(Element parent) {
		return NodeUtil.toList(parent.getChildNodes()).stream().filter(e -> !isMacro(e)).collect(Collectors.toList());
	}
	
	private boolean isMacro(Element e) {
		return e.getTagName().equals("Macro");
	}
	
}
