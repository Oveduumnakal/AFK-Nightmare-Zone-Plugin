/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import com.oveduumnakal.afknmz.EffectTransitions.AbsorptionEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests for {@link EffectTransitions}. */
public class EffectTransitionsTest
{
	private final EffectTransitions transitions = new EffectTransitions();

	@Test
	public void drinkStartsFiveMinuteCountdown()
	{
		transitions.drinkOverload();

		assertTrue(transitions.isOverloadActive());
		assertEquals(300, transitions.getOverloadSeconds());
	}

	@Test
	public void secondsRoundUp()
	{
		transitions.seedOverload(1);

		assertEquals(1, transitions.getOverloadSeconds());
	}

	@Test
	public void settlingEndsAfterSettleTicks()
	{
		transitions.drinkOverload();
		for (int i = 0; i < EffectTransitions.SETTLE_TICKS - 1; i++)
			transitions.tick();

		assertTrue(transitions.isSettling());
		transitions.tick();

		assertFalse(transitions.isSettling());
		assertTrue(transitions.isOverloadActive());
	}

	@Test
	public void settlingEndsOnceTheFifthSelfHitLands()
	{
		transitions.drinkOverload();
		for (int i = 0; i < EffectTransitions.SELF_HITS - 1; i++)
			transitions.noteHit(EffectTransitions.SELF_HIT_DAMAGE);

		assertTrue(transitions.isSettling());
		transitions.noteHit(EffectTransitions.SELF_HIT_DAMAGE);
		transitions.tick();

		assertFalse(transitions.isSettling());
		assertTrue(transitions.isOverloadActive());
	}

	@Test
	public void otherHitsDoNotCloseTheSettlingWindow()
	{
		transitions.drinkOverload();
		for (int i = 0; i < 2 * EffectTransitions.SELF_HITS; i++)
			transitions.noteHit(7);

		assertTrue(transitions.isSettling());
	}

	@Test
	public void hitsOutsideTheWindowAreIgnored()
	{
		transitions.seedOverload(100);
		for (int i = 0; i < EffectTransitions.SELF_HITS; i++)
			transitions.noteHit(EffectTransitions.SELF_HIT_DAMAGE);

		assertFalse(transitions.isSettling());
	}

	@Test
	public void expiresExactlyOnce()
	{
		transitions.seedOverload(2);

		assertFalse(transitions.tick());
		assertTrue(transitions.tick());
		assertFalse(transitions.tick());
		assertFalse(transitions.isOverloadActive());
	}

	@Test
	public void warningFiresOncePerPotion()
	{
		transitions.seedOverload(60);

		assertFalse(transitions.overloadWarningDue(30));
		transitions.seedOverload(50);
		assertTrue(transitions.overloadWarningDue(30));
		assertFalse(transitions.overloadWarningDue(30));

		transitions.drinkOverload();
		transitions.seedOverload(10);
		assertTrue(transitions.overloadWarningDue(30));
	}

	@Test
	public void noWarningWithoutOverload()
	{
		assertFalse(transitions.overloadWarningDue(30));
	}

	@Test
	public void wornOffMessageReportsOnlyWhenActive()
	{
		assertFalse(transitions.expireOverload());
		transitions.drinkOverload();

		assertTrue(transitions.expireOverload());
		assertFalse(transitions.isOverloadActive());
	}

	@Test
	public void absorptionLowFiresOnceAndRearms()
	{
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(900, 200));
		assertEquals(AbsorptionEvent.LOW, transitions.updateAbsorption(200, 200));
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(150, 200));
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(900, 200));
		assertEquals(AbsorptionEvent.LOW, transitions.updateAbsorption(100, 200));
	}

	@Test
	public void drinkingAbsorptionBelowTheThresholdStaysQuiet()
	{
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(50, 200));
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(100, 200));
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(150, 200));
		assertEquals(AbsorptionEvent.LOW, transitions.updateAbsorption(140, 200));
	}

	@Test
	public void absorptionDepletedNeedsPriorPositive()
	{
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(0, 200));
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(5, 200));
		assertEquals(AbsorptionEvent.DEPLETED, transitions.updateAbsorption(0, 200));
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(0, 200));
	}

	@Test
	public void resetClearsEverything()
	{
		transitions.drinkOverload();
		transitions.updateAbsorption(100, 200);
		transitions.reset();

		assertFalse(transitions.isOverloadActive());
		assertFalse(transitions.isSettling());
		assertEquals(AbsorptionEvent.NONE, transitions.updateAbsorption(0, 200));
	}

	@Test
	public void syncStartsACountdownWhenNoneIsRunning()
	{
		transitions.syncOverload(100, 25);

		assertTrue(transitions.isOverloadActive());
		assertEquals(100, transitions.getOverloadTicks());
		assertFalse(transitions.isSettling());
	}

	@Test
	public void syncExtendsOnlyWhenWellShort()
	{
		transitions.seedOverload(100);
		transitions.syncOverload(120, 25);
		assertEquals(100, transitions.getOverloadTicks());

		transitions.syncOverload(200, 25);
		assertEquals(200, transitions.getOverloadTicks());
	}

	@Test
	public void syncNeverShortensAndIgnoresZero()
	{
		transitions.seedOverload(100);
		transitions.syncOverload(25, 25);
		transitions.syncOverload(0, 25);

		assertEquals(100, transitions.getOverloadTicks());
	}
}
