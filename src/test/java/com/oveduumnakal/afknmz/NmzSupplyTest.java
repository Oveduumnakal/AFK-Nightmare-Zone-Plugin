/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Tests for {@link NmzSupply}. */
public class NmzSupplyTest
{
	@Test
	public void nullContainerHasNothing()
	{
		assertEquals(0, NmzSupply.OVERLOAD.count(null));
		assertEquals(0, NmzSupply.ABSORPTION.count(null));
	}

	@Test
	public void countsEveryDoseSizeOfOverload()
	{
		ItemContainer inventory = mock(ItemContainer.class);
		when(inventory.count(ItemID.NZONE4DOSEOVERLOADPOTION)).thenReturn(2);
		when(inventory.count(ItemID.NZONE1DOSEOVERLOADPOTION)).thenReturn(1);

		assertEquals(3, NmzSupply.OVERLOAD.count(inventory));
		assertEquals(0, NmzSupply.ABSORPTION.count(inventory));
	}

	@Test
	public void countsEveryDoseSizeOfAbsorption()
	{
		ItemContainer inventory = mock(ItemContainer.class);
		when(inventory.count(ItemID.NZONE3DOSEABSORPTIONPOTION)).thenReturn(4);
		when(inventory.count(ItemID.NZONE2DOSEABSORPTIONPOTION)).thenReturn(1);

		assertEquals(5, NmzSupply.ABSORPTION.count(inventory));
		assertEquals(0, NmzSupply.OVERLOAD.count(inventory));
	}
}
