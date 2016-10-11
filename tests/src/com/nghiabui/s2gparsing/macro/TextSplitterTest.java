package com.nghiabui.s2gparsing.macro;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TextSplitterTest {

	@Test
	public void testEmpty() {
		final List<String> parts = TextSplitter.split("");
		assertTrue(parts.isEmpty());
	}

	@Test
	public void testEmptySome() {
		final List<String> parts = TextSplitter.split(" \t\r\n\n");
		assertTrue(parts.isEmpty());
	}

	@Test
	public void testSingle() {
		final List<String> parts = TextSplitter.split("nghia");
		assertEquals(1, parts.size());
		assertEquals("nghia", parts.get(0));
	}

	@Test
	public void testNormal() {
		final List<String> parts = TextSplitter.split("nghia bui\tvan\ncong\r\nhoa");
		assertEquals(5, parts.size());
		assertEquals("nghia", parts.get(0));
		assertEquals("bui", parts.get(1));
		assertEquals("van", parts.get(2));
		assertEquals("cong", parts.get(3));
		assertEquals("hoa", parts.get(4));
	}

	@Test
	public void testMultipleSpaces() {
		final List<String> parts = TextSplitter.split("\t nghia \n\n bui \r\n ");
		assertEquals(2, parts.size());
		assertEquals("nghia", parts.get(0));
		assertEquals("bui", parts.get(1));
	}
	
}
