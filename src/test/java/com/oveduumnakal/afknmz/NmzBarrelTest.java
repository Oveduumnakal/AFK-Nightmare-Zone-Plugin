/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests the barrel mapping table. */
public class NmzBarrelTest
{
	@Test
	public void idsAreDistinctAndResolve()
	{
		Set<Integer> objects = new HashSet<>();
		Set<Integer> varbits = new HashSet<>();
		for (NmzBarrel barrel : NmzBarrel.values())
		{
			objects.add(barrel.getObjectId());
			varbits.add(barrel.getVarbitId());
			assertEquals(barrel, NmzBarrel.forObjectId(barrel.getObjectId()).get());
		}

		assertEquals(4, objects.size());
		assertEquals(4, varbits.size());
	}

	@Test
	public void unknownObjectIsEmpty()
	{
		assertFalse(NmzBarrel.forObjectId(-1).isPresent());
	}

	@Test
	public void lowDoseFlagRespectsThresholdAndOff()
	{
		assertTrue(NmzBarrel.isLow(3, 3));
		assertTrue(NmzBarrel.isLow(0, 3));
		assertFalse(NmzBarrel.isLow(4, 3));
		assertFalse(NmzBarrel.isLow(0, 0));
	}
}
