/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

/** The dream potions whose running out in the inventory is worth an alert. */
enum NmzSupply
{
	/** Overload (4) down to (1). */
	OVERLOAD(
		ItemID.NZONE4DOSEOVERLOADPOTION,
		ItemID.NZONE3DOSEOVERLOADPOTION,
		ItemID.NZONE2DOSEOVERLOADPOTION,
		ItemID.NZONE1DOSEOVERLOADPOTION),
	/** Absorption (4) down to (1). */
	ABSORPTION(
		ItemID.NZONE4DOSEABSORPTIONPOTION,
		ItemID.NZONE3DOSEABSORPTIONPOTION,
		ItemID.NZONE2DOSEABSORPTIONPOTION,
		ItemID.NZONE1DOSEABSORPTIONPOTION);

	private final int[] itemIds;

	NmzSupply(int... itemIds)
	{
		this.itemIds = itemIds;
	}

	/** How many potions of any dose size the container holds; 0 for a null container. */
	int count(ItemContainer container)
	{
		if (container == null)
			return 0;

		int total = 0;
		for (int itemId : itemIds)
			total += container.count(itemId);

		return total;
	}
}
