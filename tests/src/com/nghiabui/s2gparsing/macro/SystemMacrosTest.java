package com.nghiabui.s2gparsing.macro;

import com.nghiabui.kommon.System;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SystemMacrosTest {

	@Test
	public void whenMacroIsFound_Fine() {
		final SystemMacros macros = new SystemMacros();
		if (System.IS_UNIX) {
			assertEquals("nghia", macros.get("USER", true).get());
		} else {
			assertEquals("C:\\WINDOWS", macros.get("windir", true).get());
		}
	}

	@Test
	public void whenMacroIsNotFound_Exception() {
		final SystemMacros macros = new SystemMacros();
		assertFalse(macros.get("DUMMY", true).isPresent());
	}
	
}
