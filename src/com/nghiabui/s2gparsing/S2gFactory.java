package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.AppException;
import com.nghiabui.kommon.Path;
import com.nghiabui.s2gparsing.macro.Macros;
import com.nghiabui.s2gparsing.win32.Win32Factory;
import com.nghiabui.s2gparsing.win32.Win32Solution;
import com.nghiabui.kommon.xml.DocumentFactory;
import org.w3c.dom.Document;

import java.util.Optional;

public class S2gFactory {
	
	public static SolutionConfig createSolutionConfig(Path sln2gccFile, Macros systemMacros) {
		final Optional<Document> opDocument = DocumentFactory.createDocument(sln2gccFile);
		if (!opDocument.isPresent()) {
			throw new AppException("Could not open and parse the sln2gcc file: " + sln2gccFile.absolute());
		}
		final Document document = opDocument.get();
		final BasicDocument basicDocument = new BasicDocument(document);
		final String win32SlnFile = basicDocument.solution();
		if (win32SlnFile.isEmpty()) {
			throw new AppException("Missing the Win32 solution path in the sln2gcc file");
		}
		final Path workFolder = sln2gccFile.parentFolder();
		final Win32Solution win32Solution = Win32Factory.createSolution(workFolder.combination(win32SlnFile));
		final ActualDocument actualDocument = new ActualDocument(basicDocument, workFolder, win32Solution);
		final FinalDocument finalDocument = new FinalDocument(actualDocument, systemMacros);
		return new SolutionConfig(finalDocument, win32Solution, sln2gccFile);
	}
	
}
