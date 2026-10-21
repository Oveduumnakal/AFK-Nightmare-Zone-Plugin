/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Tests for {@link HpGuard}. */
public class HpGuardTest
{
	@Test
	public void kindOfMapsItemAndOption()
	{
		assertEquals(HpGuard.Kind.ORB_FEEL, HpGuard.kindOf(true, false, "Feel"));
		assertEquals(HpGuard.Kind.ORB_GUZZLE, HpGuard.kindOf(true, false, "Guzzle"));
		assertEquals(HpGuard.Kind.CAKE_EAT, HpGuard.kindOf(false, true, "Eat"));
		assertEquals(HpGuard.Kind.CAKE_GUZZLE, HpGuard.kindOf(false, true, "Guzzle"));
		assertNull(HpGuard.kindOf(true, false, "Destroy"));
		assertNull(HpGuard.kindOf(false, true, "Drop"));
		assertNull(HpGuard.kindOf(false, false, "Eat"));
		assertNull(HpGuard.kindOf(true, false, null));
	}

	@Test
	public void resultingHpOrb()
	{
		assertEquals(90, HpGuard.resultingHp(HpGuard.Kind.ORB_FEEL, 100));
		assertEquals(1, HpGuard.resultingHp(HpGuard.Kind.ORB_FEEL, 5));
		assertEquals(1, HpGuard.resultingHp(HpGuard.Kind.ORB_GUZZLE, 99));
	}

	@Test
	public void resultingHpCake()
	{
		assertEquals(59, HpGuard.resultingHp(HpGuard.Kind.CAKE_EAT, 60));
		assertEquals(2, HpGuard.resultingHp(HpGuard.Kind.CAKE_EAT, 3));
		assertEquals(2, HpGuard.resultingHp(HpGuard.Kind.CAKE_EAT, 2));
		assertEquals(17, HpGuard.resultingHp(HpGuard.Kind.CAKE_GUZZLE, 20));
		assertEquals(1, HpGuard.resultingHp(HpGuard.Kind.CAKE_GUZZLE, 2));
		assertEquals(1, HpGuard.resultingHp(HpGuard.Kind.CAKE_GUZZLE, 1));
	}

	@Test
	public void overloadRuleNeedsActiveAndSettled()
	{
		assertTrue(HpGuard.blockedByOverloadRule(true, false, false));
		assertTrue(HpGuard.blockedByOverloadRule(true, true, true));
		assertFalse(HpGuard.blockedByOverloadRule(true, true, false));
		assertFalse(HpGuard.blockedByOverloadRule(false, false, false));
	}

	@Test
	public void minHpRuleBlocksDropBelowMinimum()
	{
		assertTrue(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.ORB_FEEL, 55, 51));
		assertFalse(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.ORB_FEEL, 61, 51));
		assertTrue(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.ORB_GUZZLE, 99, 51));
		assertTrue(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.CAKE_EAT, 51, 51));
		assertFalse(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.CAKE_EAT, 52, 51));
		assertTrue(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.CAKE_GUZZLE, 55, 51));
	}

	@Test
	public void minHpRuleIgnoresClicksThatLoseNothing()
	{
		assertFalse(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.CAKE_GUZZLE, 1, 51));
		assertFalse(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.ORB_FEEL, 1, 51));
		assertFalse(HpGuard.blockedByMinHp(true, false, HpGuard.Kind.CAKE_EAT, 2, 51));
	}

	@Test
	public void minHpRuleSkippedWithOverloadOrToggleOff()
	{
		assertFalse(HpGuard.blockedByMinHp(true, true, HpGuard.Kind.ORB_GUZZLE, 99, 51));
		assertFalse(HpGuard.blockedByMinHp(false, false, HpGuard.Kind.ORB_GUZZLE, 99, 51));
	}

	@Test
	public void itemMatching()
	{
		assertTrue(HpGuard.isOrb(22081));
		assertTrue(HpGuard.isCake(7509));
		assertTrue(HpGuard.isCake(7510));
		assertFalse(HpGuard.isCake(22081));
		assertTrue(HpGuard.isOrbName("<col=ff9040>Locator orb</col>"));
		assertTrue(HpGuard.isCakeName("<col=ff9040>Dwarven rock cake</col>"));
		assertFalse(HpGuard.isCakeName(null));
	}
}
