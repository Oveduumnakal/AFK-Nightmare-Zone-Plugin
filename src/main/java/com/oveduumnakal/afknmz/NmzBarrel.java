/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.util.Optional;

import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

/**
 * The four potion barrels in the Nightmare Zone lobby.
 *
 * <p>Object ids ({@code NZONE_BARREL_1..4}) and dose varbits ({@code NZONE_POTION_1..4}) come from the
 * RuneLite gameval constants. The object-to-potion pairing was confirmed in game: barrel 1 is super ranging,
 * 2 is super magic, 3 is overload and 4 is absorption. The dose varbit is assumed to share the barrel's slot
 * number; confirm that the drawn counts match the stored doses.
 */
public enum NmzBarrel
{
	/** Overload barrel. */
	OVERLOAD("Overload", Color.BLACK, ObjectID.NZONE_BARREL_3, VarbitID.NZONE_POTION_3),
	/** Absorption barrel. */
	ABSORPTION("Absorption", Color.WHITE, ObjectID.NZONE_BARREL_4, VarbitID.NZONE_POTION_4),
	/** Super ranging barrel. */
	SUPER_RANGING("Super ranging", Color.GREEN, ObjectID.NZONE_BARREL_1, VarbitID.NZONE_POTION_1),
	/** Super magic barrel. */
	SUPER_MAGIC("Super magic", new Color(130, 180, 255), ObjectID.NZONE_BARREL_2, VarbitID.NZONE_POTION_2);

	private final String label;
	private final Color tint;
	private final int objectId;
	private final int varbitId;

	NmzBarrel(String label, Color tint, int objectId, int varbitId)
	{
		this.label = label;
		this.tint = tint;
		this.objectId = objectId;
		this.varbitId = varbitId;
	}

	/** Returns the potion name drawn above the dose count. */
	public String getLabel()
	{
		return label;
	}

	/** Returns the colour this barrel is tinted with. */
	public Color getTint()
	{
		return tint;
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

	/** Whether a dose count is low enough to flag; a threshold of 0 turns the flag off. */
	static boolean isLow(int doses, int threshold)
	{
		return threshold > 0 && doses <= threshold;
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
