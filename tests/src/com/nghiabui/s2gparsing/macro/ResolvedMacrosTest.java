package com.nghiabui.s2gparsing.macro;

import com.nghiabui.kommon.AppException;
import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.xml.DocumentFactory;
import com.nghiabui.kommon.xml.NodeUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

public class ResolvedMacrosTest {

	private ResolvedMacros create() {
		final Document document = DocumentFactory
			.createDocument(new Path("tests/res/SampleProject/sln2gcc.xml"))
			.get();
		final Element element = NodeUtil.toList(document.getElementsByTagName("GccConfig"))
			.stream()
			.filter(e -> e.getAttribute("Name").equals("armeabi-v7a"))
			.findFirst()
			.get();
		final Macros xmlMacros = new XmlMacros(NodeUtil.toList(element.getChildNodes()));
		final FakeSystemMacros systemMacros = new FakeSystemMacros();
		systemMacros.add("USER", "nghia");
		systemMacros.add("ANDROID_NDK_HOME", "/Users/nghia/DevTools/android-ndk-r12b");
		return new ResolvedMacros(new CompositeMacros(xmlMacros, systemMacros));
	}

	@Test
	public void test_NoNeedToResolve() {
		final Macros macros = create();
		assertEquals("main", macros.get("MAIN_PROJECT", true).get());
	}

	@Test
	public void test_Resolve_Local() {
		final Macros macros = create();
		assertEquals("True", macros.get("GENERATE_DSYM", true).get());
		assertTrue(macros.getAsBoolean("GENERATE_DSYM", true).get());
		assertEquals("/Users/nghia/DevTools/android-ndk-r12b", macros.get("ANDROID_NDK_PATH", true).get());
	}

	@Test
	public void test_Resolve_ReachSystemEnv() {
		final Macros macros = create();
		assertEquals("nghia", macros.get("SYSTEM_USER", true).get());
	}

	@Test(expected = AppException.class)
	public void testLoop() {
		final Macros macros = create();
		macros.get("LOOP1", true);
	}
	
	@Test(expected = AppException.class)
	public void testSelfLoop() {
		final Macros macros = create();
		macros.get("LOOP3", true);
	}
	
	@Test(expected = AppException.class)
	public void testInvalid() {
		final Macros macros = create();
		macros.get("INVALID_MACRO", true);
	}
	
	@Test
	public void testNotFound() {
		final Macros macros = create();
		assertFalse(macros.get("HELLO", false).isPresent());
	}
	
	@Test
	public void testNotFoundEmpty() {
		final Macros macros = create();
		assertFalse(macros.get("", true).isPresent());
	}

}
