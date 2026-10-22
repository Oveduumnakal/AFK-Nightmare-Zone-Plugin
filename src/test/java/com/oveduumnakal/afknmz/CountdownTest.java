/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/** Tests for {@link Countdown}. */
public class CountdownTest
{
	@Test
	public void noTicksIsTheNoneCountdown()
	{
		assertSame(Countdown.NONE, Countdown.of(0, 1000));
		assertSame(Countdown.NONE, Countdown.of(-3, 1000));
		assertFalse(Countdown.NONE.isRunning());
		assertEquals(0, Countdown.NONE.remainingMillis(5000));
	}

	@Test
	public void startsAtFullTicksOnTheAnchor()
	{
		Countdown countdown = Countdown.of(10, 1000);

		assertTrue(countdown.isRunning());
		assertEquals(10, countdown.getTicks());
		assertEquals(6000, countdown.remainingMillis(1000));
	}

	@Test
	public void interpolatesBetweenTicks()
	{
		Countdown countdown = Countdown.of(10, 1000);

		assertEquals(5900, countdown.remainingMillis(1100));
		assertEquals(5700, countdown.remainingMillis(1300));
		assertEquals(5400, countdown.remainingMillis(1600));
	}

	@Test
	public void neverGoesBelowTheNextTickWhenATickIsLate()
	{
		Countdown countdown = Countdown.of(10, 1000);

		assertEquals(5400, countdown.remainingMillis(1600));
		assertEquals(5400, countdown.remainingMillis(1900));
		assertEquals(5400, countdown.remainingMillis(9000));
	}

	@Test
	public void clockBeforeAnchorDoesNotRunAhead()
	{
		assertEquals(6000, Countdown.of(10, 1000).remainingMillis(900));
	}

	@Test
	public void lastTickCountsDownToZero()
	{
		Countdown countdown = Countdown.of(1, 1000);

		assertEquals(600, countdown.remainingMillis(1000));
		assertEquals(300, countdown.remainingMillis(1300));
		assertEquals(0, countdown.remainingMillis(1600));
		assertEquals(0, countdown.remainingMillis(2000));
	}

	@Test
	public void formatsMinutesSecondsAndTenths()
	{
		assertEquals("5:00.0", Countdown.format(300_000));
		assertEquals("4:12.3", Countdown.format(252_300));
		assertEquals("0:30.0", Countdown.format(30_000));
		assertEquals("0:00.0", Countdown.format(0));
	}

	@Test
	public void formatRoundsUpToTheNextTenth()
	{
		assertEquals("0:00.1", Countdown.format(1));
		assertEquals("0:00.1", Countdown.format(100));
		assertEquals("0:00.2", Countdown.format(101));
		assertEquals("0:01.0", Countdown.format(901));
	}

	@Test
	public void formatClampsNegativeToZero()
	{
		assertEquals("0:00.0", Countdown.format(-500));
	}

	@Test
	public void nowIsMonotonic()
	{
		long first = Countdown.now();

		assertTrue(Countdown.now() >= first);
	}
}
