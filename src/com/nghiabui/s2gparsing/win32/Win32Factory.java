package com.nghiabui.s2gparsing.win32;

import com.nghiabui.kommon.Path;
import com.nghiabui.kommon.io.FileReader;

public class Win32Factory {

	public static Win32Solution createSolution(Path slnFile) {
		final ProjectInfos projectInfos = new ProjectInfos(FileReader.readAllLines(slnFile.value()));
		return new Win32Solution(slnFile.parentFolder(), projectInfos.get());
	}

}
