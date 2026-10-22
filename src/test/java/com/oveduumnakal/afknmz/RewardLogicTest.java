/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests for {@link AffordableAlert}. */
public class RewardLogicTest
{
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
