package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.Path;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Win32ProjectTest {

	@Test
	public void testAppleWin32Project() {
		final Win32Solution win32Solution = Win32Factory.createSolution(new Path("tests/res/SampleProject/win32.sln"));
		final Win32Project appleProj = win32Solution.project("apple").get();

		final List<Path> debugIncludePaths = appleProj.includePaths("Debug|Win32");
		assertEquals(1, debugIncludePaths.size());
		assertEquals("tests/res/SampleProject/prj/apple/../../inc", debugIncludePaths.get(0).toString());

		final List<Path> releaseIncludePaths = appleProj.includePaths("Release|Win32");
		assertEquals(0, releaseIncludePaths.size());

		final List<Path> filesOfFilterEx = appleProj.filesOfFilter("ex");
		assertEquals(1, filesOfFilterEx.size());
		assertEquals("tests/res/SampleProject/prj/apple/../../src/source4.cpp", filesOfFilterEx.get(0).toString());

		final List<Path> filesOfFilterAllSources = appleProj.filesOfFilter("Source Files");
		assertEquals(13, filesOfFilterAllSources.size());

		final List<Path> fullSources = appleProj.fullSources();
		assertEquals(13, fullSources.size());
		
		final List<Path> releaseExSources = appleProj.excludedSources("Release|Win32");
		assertEquals(0, releaseExSources.size());

		final List<Path> debugExSources = appleProj.excludedSources("Debug|Win32");
		assertEquals(1, debugExSources.size());
		assertEquals("tests/res/SampleProject/prj/apple/../../src/source1.cpp", debugExSources.get(0).toString());
	}
	
}
