/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.Optional;

import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

/**
 * The four potion barrels in the Nightmare Zone lobby.
 *
 * <p>Object ids ({@code NZONE_BARREL_1..4}) and dose varbits ({@code NZONE_POTION_1..4}) come from the
 * RuneLite gameval constants. The pairing of slot N to a potion (overload, absorption, super ranging,
 * super magic) follows the order the barrels are listed in, but could not be confirmed from the wiki or
 * the client jars: it needs an in-game check (fill one barrel, watch which varbit moves). If the order
 * is wrong, only the labels in this enum need to be swapped.
 */
public enum NmzBarrel
{
	/** Overload barrel. */
	OVERLOAD("OL", ObjectID.NZONE_BARREL_1, VarbitID.NZONE_POTION_1),
	/** Absorption barrel. */
	ABSORPTION("Abs", ObjectID.NZONE_BARREL_2, VarbitID.NZONE_POTION_2),
	/** Super ranging barrel. */
	SUPER_RANGING("Rng", ObjectID.NZONE_BARREL_3, VarbitID.NZONE_POTION_3),
	/** Super magic barrel. */
	SUPER_MAGIC("Mage", ObjectID.NZONE_BARREL_4, VarbitID.NZONE_POTION_4);

	private final String label;
	private final int objectId;
	private final int varbitId;

	NmzBarrel(String label, int objectId, int varbitId)
	{
		this.label = label;
		this.objectId = objectId;
		this.varbitId = varbitId;
	}

	/** Returns the short label drawn before the dose count. */
	public String getLabel()
	{
		return label;
	}

	/** Returns the object id of this barrel in the lobby. */
	public int getObjectId()
	{
		return objectId;
	}

	/** Returns the varbit holding this barrel's stored dose count. */
	public int getVarbitId()
	{
		return varbitId;
	}

	/** Returns the barrel with the given object id, if any. */
	public static Optional<NmzBarrel> forObjectId(int objectId)
	{
		for (NmzBarrel barrel : values())
		{
			if (barrel.objectId == objectId)
				return Optional.of(barrel);
		}

		return Optional.empty();
	}
}
