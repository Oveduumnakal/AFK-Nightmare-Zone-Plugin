/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.util.Text;

/**
 * Decides whether a click on the Locator orb or Dwarven rock cake is blocked. Pure functions, no client access.
 *
 * <p>Mechanics (OSRS wiki): the orb's Feel removes 10 HP but never below 1; the rock cake's Eat removes 1 HP while
 * above 2 HP and does nothing at 2 or less; Guzzle removes 10% of current HP rounded down plus 1, down to a floor of
 * 1 HP. Guzzle on the orb is treated as the worst case, dropping to 1 HP.
 *
 * <p>The two toggles combine in order. Rule 1 (require overload) applies first and is the stricter: when on, every
 * click is blocked unless an overload is active and settled, so rule 2 can only matter once rule 1 has allowed the
 * click or is off. Rule 2 (minimum HP) applies only when no overload is active; with an active overload the player is
 * deliberately at low HP and the rule is skipped. A click that would not lower HP at all is never blocked by rule 2.
 */
final class HpGuard
{
	/** What a guarded item click does. */
	enum Kind
	{
		/** Locator orb Feel. */
		ORB_FEEL,
		/** Locator orb Guzzle, assumed to drop to 1 HP. */
		ORB_GUZZLE,
		/** Dwarven rock cake Eat. */
		CAKE_EAT,
		/** Dwarven rock cake Guzzle. */
		CAKE_GUZZLE
	}

	private HpGuard()
	{
	}

	/** Whether the item id is a Locator orb. */
	static boolean isOrb(int itemId)
	{
		return itemId == ItemID.DS2_ORB;
	}

	/** Whether the item id is a Dwarven rock cake. */
	static boolean isCake(int itemId)
	{
		return itemId == ItemID.HUNDRED_DWARF_HOT_ROCKCAKE || itemId == ItemID.HUNDRED_DWARF_COOL_ROCKCAKE;
	}

	/** Whether the menu target names a Locator orb (fallback when the item id is unavailable). */
	static boolean isOrbName(String rawTarget)
	{
		return rawTarget != null && Text.removeTags(rawTarget).equals("Locator orb");
	}

	/** Whether the menu target names a Dwarven rock cake (fallback when the item id is unavailable). */
	static boolean isCakeName(String rawTarget)
	{
		return rawTarget != null && Text.removeTags(rawTarget).equals("Dwarven rock cake");
	}

	/**
	 * Maps an item and menu option to what the click does.
	 *
	 * @return the kind, or null when the click is not a guarded action
	 */
	static Kind kindOf(boolean orb, boolean cake, String option)
	{
		if (option == null)
			return null;

		if (orb && option.equals("Feel"))
			return Kind.ORB_FEEL;

		if (orb && option.equals("Guzzle"))
			return Kind.ORB_GUZZLE;

		if (cake && option.equals("Eat"))
			return Kind.CAKE_EAT;

		if (cake && option.equals("Guzzle"))
			return Kind.CAKE_GUZZLE;

		return null;
	}

	/** HP after the click, never below 1. */
	static int resultingHp(Kind kind, int hp)
	{
		switch (kind)
		{
			case ORB_FEEL:
				return Math.max(1, hp - 10);
			case ORB_GUZZLE:
				return Math.min(hp, 1);
			case CAKE_EAT:
				return hp > 2 ? hp - 1 : hp;
			default:
				return Math.max(1, Math.min(hp, hp - (hp / 10 + 1)));
		}
	}

	/** Rule 1: whether the click is blocked for lack of a settled overload. */
	static boolean blockedByOverloadRule(boolean requireOverload, boolean overloadActive, boolean overloadSettling)
	{
		return requireOverload && (!overloadActive || overloadSettling);
	}

	/** Rule 2: whether the click would take HP below the minimum with no overload active. */
	static boolean blockedByMinHp(boolean blockBelowMinHp, boolean overloadActive, Kind kind, int hp, int minHp)
	{
		if (!blockBelowMinHp || overloadActive)
			return false;

		int result = resultingHp(kind, hp);

		return result < hp && result < minHp;
	}
}
