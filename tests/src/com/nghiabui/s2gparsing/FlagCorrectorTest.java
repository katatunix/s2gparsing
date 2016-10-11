package com.nghiabui.s2gparsing;

import com.nghiabui.kommon.SetOperation;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class FlagCorrectorTest {
	
	@Test
	public void whenNoNeedCorrect() {
		final Set<String> corrected = FlagCorrector.correct(Arrays.asList("-target", "-abc", "-haha"));
		assertTrue(SetOperation.areEqual(
			corrected,
			SetOperation.newSet("-target", "-abc", "-haha")
		));
	}
	
	@Test
	public void whenOneDependant() {
		final Set<String> corrected = FlagCorrector.correct(Arrays.asList("-target", "abc", "-haha"));
		assertTrue(SetOperation.areEqual(
			corrected,
			SetOperation.newSet("-target abc", "-haha")
		));
	}
	
	@Test
	public void whenManyDependant() {
		final Set<String> corrected = FlagCorrector.correct(Arrays.asList("-target", "abc", "xyz", "-haha"));
		assertTrue(SetOperation.areEqual(
			corrected,
			SetOperation.newSet("-target abc xyz", "-haha")
		));
	}
	
	@Test(expected = Exception.class)
	public void whenAbnormalFlags() {
		FlagCorrector.correct(Arrays.asList("target", "-abc", "-haha"));
	}
	
}
