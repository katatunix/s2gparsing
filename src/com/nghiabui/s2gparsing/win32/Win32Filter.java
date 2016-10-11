package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.xml.NodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.stream.Collectors;

class Win32Filter implements Filter {

	private final Document document;

	public Win32Filter(Document document) {
		this.document = document;
	}

	public List<String> filesOf(String filterPath) {
		return NodeUtil.toList(document.getElementsByTagName("Filter")).stream()
			.filter(e -> matched(filterPath, e.getTextContent()))
			.filter(e -> {
				final Node p = e.getParentNode();
				if (p == null || !NodeUtil.isElement(p)) return false;
				final Element parent = (Element) p;
				final String tag = parent.getTagName();
				return tag.equals("ClInclude") || tag.equals("ClCompile");
			})
			.map(e -> ((Element) e.getParentNode()).getAttribute("Include"))
			.collect(Collectors.toList());
	}

	private boolean matched(String parent, String child) {
		parent = parent.replace('\\', '/');
		child = child.replace('\\', '/');
		
		if (parent.contains("/")) {
			final int childLen = child.length();
			final int parentLen = parent.length();
			return child.startsWith(parent) &&
				(childLen == parentLen || child.charAt(parentLen) == '/');
		}
		for (String realParent : child.split("/")) {
			if (realParent.equals(parent)) return true;
		}
		return false;
	}

}
