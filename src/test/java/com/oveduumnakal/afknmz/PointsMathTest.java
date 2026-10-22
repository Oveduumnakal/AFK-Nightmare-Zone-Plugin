/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Covers the pure points arithmetic: gain, hourly rate and formatting. */
public class PointsMathTest
{
	@Test
	public void risingReadingIsGain()
	{
		assertEquals(150, PointsMath.gain(1000, 1150));
	}

	@Test
	public void spendingIsNotGainOrLoss()
	{
		assertEquals(0, PointsMath.gain(5000, 200));
	}

	@Test
	public void unknownPreviousReadingIsNotGain()
	{
		assertEquals(0, PointsMath.gain(-1, 40000));
	}

	@Test
	public void perHourScalesByElapsedTime()
	{
		assertEquals(60000, PointsMath.perHour(30000, 1_800_000L));
		assertEquals(1000, PointsMath.perHour(1000, 3_600_000L));
	}

	@Test
	public void perHourIsZeroWithoutElapsedTime()
	{
		assertEquals(0, PointsMath.perHour(500, 0));
	}

	@Test
	public void formatsThousandsSeparators()
	{
		assertEquals("0", PointsMath.format(0));
		assertEquals("999", PointsMath.format(999));
		assertEquals("1,234,567", PointsMath.format(1234567));
	}

	@Test
	public void formatsDurationsWithHoursOnlyWhenNeeded()
	{
		assertEquals("0:00", PointsMath.formatDuration(0));
		assertEquals("4:07", PointsMath.formatDuration(247_900));
		assertEquals("1:02:09", PointsMath.formatDuration(3_729_000));
		assertEquals("0:00", PointsMath.formatDuration(-5));
	}
}
