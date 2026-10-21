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
	public void boostFadeNeedsABoostFirst()
	{
		AlertLogic.BoostFade fade = new AlertLogic.BoostFade();

		assertFalse(fade.update(0));
		assertFalse(fade.update(10));
		assertFalse(fade.update(4));
		assertTrue(fade.update(0));
		assertFalse(fade.update(0));
	}

	@Test
	public void boostFadeRearmsOnNewBoost()
	{
		AlertLogic.BoostFade fade = new AlertLogic.BoostFade();
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
}
