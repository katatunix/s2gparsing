package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.io.FileReader;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ProjectInfosTest {

	@Test
	public void testSample_Win32Stuff() {
		final List<String> lines = FileReader.readAllLines("tests/res/SampleProject/win32.sln");
		final Map<String, String> infos = new ProjectInfos(lines).get();
		assertEquals(2, infos.size());
		assertEquals("prj\\apple\\apple.vcxproj", infos.get("apple"));
		assertEquals("prj\\banana\\banana.vcxproj", infos.get("banana"));
	}
	
}
