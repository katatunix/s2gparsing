package com.nghiabui.s2gparsing.macro;

import java.util.Optional;

public class CompositeMacros extends Macros {

	private final Macros current;
	private final Macros next;

	public CompositeMacros(Macros current, Macros next) {
		this.current = current;
		this.next = next;
	}

	@Override
	public Optional<String> get(String name, boolean release) {
		final Optional<String> opCurValue = current.get(name, release);
		return opCurValue.isPresent() ? opCurValue : next.get(name, release);
	}

}
