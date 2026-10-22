/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests for {@link AlertLogic}. */
public class AlertLogicTest
{
	@Test
	public void edgeTriggerFiresOncePerRise()
	{
		AlertLogic.EdgeTrigger trigger = new AlertLogic.EdgeTrigger();

		assertFalse(trigger.update(false));
		assertTrue(trigger.update(true));
		assertFalse(trigger.update(true));
		assertFalse(trigger.update(false));
		assertTrue(trigger.update(true));
	}

	@Test
	public void edgeTriggerResetFiresAgain()
	{
		AlertLogic.EdgeTrigger trigger = new AlertLogic.EdgeTrigger();
		trigger.update(true);
		trigger.reset();

		assertTrue(trigger.update(true));
	}

	@Test
	public void dropToZeroNeedsAPositiveFirst()
	{
		AlertLogic.DropToZero fade = new AlertLogic.DropToZero();

		assertFalse(fade.update(0));
		assertFalse(fade.update(10));
		assertFalse(fade.update(4));
		assertTrue(fade.update(0));
		assertFalse(fade.update(0));
	}

	@Test
	public void dropToZeroRearmsOnNewPositive()
	{
		AlertLogic.DropToZero fade = new AlertLogic.DropToZero();
		fade.update(5);
		fade.update(0);
		fade.update(5);

		assertTrue(fade.update(0));
	}

	@Test
	public void lowHpIgnoredWhileOverloadIsActive()
	{
		assertTrue(AlertLogic.lowHp(10, 15, false));
		assertTrue(AlertLogic.lowHp(15, 15, false));
		assertFalse(AlertLogic.lowHp(16, 15, false));
		assertFalse(AlertLogic.lowHp(1, 15, true));
	}

	@Test
	public void dropToZeroTracksPotionStacks()
	{
		AlertLogic.DropToZero supply = new AlertLogic.DropToZero();

		assertFalse(supply.update(0));
		assertFalse(supply.update(3));
		assertFalse(supply.update(1));
		assertTrue(supply.update(0));
		assertFalse(supply.update(0));
	}

	@Test
	public void dropToZeroResetForgetsPositive()
	{
		AlertLogic.DropToZero supply = new AlertLogic.DropToZero();
		supply.update(2);
		supply.reset();

		assertFalse(supply.update(0));
	}

	@Test
	public void overloadLowAtOrUnderLeadTime()
	{
		assertTrue(AlertLogic.overloadLow(30_000, 30));
		assertTrue(AlertLogic.overloadLow(0, 30));
		assertFalse(AlertLogic.overloadLow(30_001, 30));
	}

	@Test
	public void absorptionLowAtOrUnderThreshold()
	{
		assertTrue(AlertLogic.absorptionLow(200, 200));
		assertTrue(AlertLogic.absorptionLow(0, 200));
		assertFalse(AlertLogic.absorptionLow(201, 200));
	}
}
