package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.xml.DocumentFactory;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.List;

import static org.junit.Assert.*;

public class Win32FilterTest {

	@Test
	public void testAppleWin32Filters() {
		final Path path = new Path("tests/res/SampleProject/prj/apple/apple.vcxproj.filters");
		final Document document = DocumentFactory.createDocument(path).get();
		final Filter filter = new Win32Filter(document);

		final List<String> exFiles = filter.filesOf("ex");
		assertEquals(1, exFiles.size());
		assertEquals("..\\..\\src\\source4.cpp", exFiles.get(0));
		
		final List<String> exFiles_Long = filter.filesOf("Source Files\\ex");
		assertEquals(1, exFiles_Long.size());
		assertEquals("..\\..\\src\\source4.cpp", exFiles_Long.get(0));

		final List<String> sourceFiles = filter.filesOf("Source Files");
		assertEquals(13, sourceFiles.size());
		assertEquals("..\\..\\src\\source1.cpp", sourceFiles.get(0));
		assertEquals("..\\..\\src\\source2.cpp", sourceFiles.get(1));
	}
	
}
