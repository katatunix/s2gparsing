package com.nghiabui.s2gparsing.macro;

import com.nghiabui.kommon.System;

import java.util.Optional;

public class SystemMacros extends Macros {

	@Override
	public Optional<String> get(String name, boolean release) {
		return System.getEnv(name);
	}

}
