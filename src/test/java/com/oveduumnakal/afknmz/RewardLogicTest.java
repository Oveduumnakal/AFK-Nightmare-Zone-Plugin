/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests for {@link RunPoints} and {@link AffordableAlert}. */
public class RewardLogicTest
{
	@Test
	public void runPointsCountFromTheDreamStart()
	{
		RunPoints run = new RunPoints();

		assertEquals(0, run.update(false, 5000));
		assertEquals(0, run.update(true, 5000));
		assertEquals(250, run.update(true, 5250));
		assertEquals(250, run.update(true, 5250));
	}

	@Test
	public void runPointsResetOutsideTheDream()
	{
		RunPoints run = new RunPoints();
		run.update(true, 100);
		run.update(true, 400);

		assertEquals(0, run.update(false, 400));
		assertEquals(0, run.update(true, 400));
		assertEquals(50, run.update(true, 450));
	}

	@Test
	public void runPointsNeverGoNegativeWhenSpending()
	{
		RunPoints run = new RunPoints();
		run.update(true, 1000);

		assertEquals(0, run.update(true, 400));
	}

	@Test
	public void alertStaysQuietWhenAlreadyAffordableOnFirstLook()
	{
		AffordableAlert alert = new AffordableAlert();

		assertFalse(alert.update(true));
		assertFalse(alert.update(true));
	}

	@Test
	public void alertFiresWhenGoalBecomesAffordable()
	{
		AffordableAlert alert = new AffordableAlert();

		assertFalse(alert.update(false));
		assertTrue(alert.update(true));
		assertFalse(alert.update(true));
	}

	@Test
	public void alertRearmsAfterDroppingBelowTheCost()
	{
		AffordableAlert alert = new AffordableAlert();
		alert.update(false);
		alert.update(true);
		alert.update(false);

		assertTrue(alert.update(true));
	}

	@Test
	public void alertResetCountsNextGoalAsFirstLook()
	{
		AffordableAlert alert = new AffordableAlert();
		alert.update(false);
		alert.reset();

		assertFalse(alert.update(true));
	}
}
