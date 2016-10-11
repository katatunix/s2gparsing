package com.nghiabui.s2gparsing.macro;

import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;

public class XmlMacros extends Macros {

	private final List<Element> elements;

	public XmlMacros(List<Element> elements) {
		this.elements = elements;
	}

	@Override
	public Optional<String> get(String name, boolean release) {
		final Optional<Element> op = elements.stream()
			.filter(e -> e.getAttribute("Name").equals(name))
			.findFirst();
		if (!op.isPresent()) {
			return Optional.empty();
		}

		final Element macroElement = op.get();
		String macroValue = "";
		String temp;
		if (!(temp = macroElement.getAttribute("Value")).isEmpty()) {
			if (!macroValue.isEmpty()) macroValue += " ";
			macroValue += temp;
		}
		if (!(temp = macroElement.getAttribute("CommonValue")).isEmpty()) {
			if (!macroValue.isEmpty()) macroValue += " ";
			macroValue += temp;
		}

		if (release) {
			if (!(temp = macroElement.getAttribute("ReleaseValue")).isEmpty()) {
				if (!macroValue.isEmpty()) macroValue += " ";
				macroValue += temp;
			}
		} else {
			if (!(temp = macroElement.getAttribute("DebugValue")).isEmpty()) {
				if (!macroValue.isEmpty()) macroValue += " ";
				macroValue += temp;
			}
		}

		return Optional.of(macroValue.trim());
	}

}
