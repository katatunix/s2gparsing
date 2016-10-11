package com.nghiabui.s2gparsing.macro;

import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.xml.DocumentFactory;
import com.nghiabui.kommon.xml.NodeUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class XmlMacrosTest {

	private XmlMacros create() {
		final Document document = DocumentFactory
			.createDocument(new Path("tests/res/SampleProject/sln2gcc.xml"))
			.get();
		final Element element = NodeUtil.toList(document.getElementsByTagName("GccConfig"))
			.stream()
			.filter(e -> e.getAttribute("Name").equals("armeabi-v7a"))
			.findFirst()
			.get();
		return new XmlMacros(NodeUtil.toList(element.getChildNodes()));
	}

	@Test
	public void whenMacroIsFound_Fine() {
		final XmlMacros macros = create();
		assertEquals("main", macros.get("MAIN_PROJECT", true).get());
		assertEquals("$(USER)", macros.get("SYSTEM_USER", true).get());
	}

	@Test
	public void whenMacroIsNotFound_Exception() {
		final XmlMacros macros = create();
		assertFalse(macros.get("DUMMY", true).isPresent());
	}

	@Test
	public void testValType() {
		final XmlMacros macros = create();
		assertEquals("common1 common2 release", macros.get("KATA", true).get());
		assertEquals("common1 common2 debug", macros.get("KATA", false).get());
	}
	
}
