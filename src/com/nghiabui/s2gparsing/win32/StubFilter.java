package com.nghiabui.s2gparsing.win32;

import java.util.Collections;
import java.util.List;

class StubFilter implements Filter {

	@Override
	public List<String> filesOf(String filterName) {
		return Collections.emptyList();
	}

}
