/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

/** Pure edge-detection helpers for the AFK alerts, kept free of client types so they can be unit tested. */
final class AlertLogic
{
	private AlertLogic()
	{
	}

	/** Fires once when a condition becomes true, and again only after it has been false in between. */
	static final class EdgeTrigger
	{
		private boolean active;

		/**
		 * Feeds the current state of the condition.
		 *
		 * @return true on the update where the condition turned true
		 */
		boolean update(boolean condition)
		{
			boolean fired = condition && !active;
			active = condition;

			return fired;
		}

		/** Forgets the last state, so a condition that is already true fires again. */
		void reset()
		{
			active = false;
		}
	}

	/** Fires once when a stat boost that was present drops to nothing. */
	static final class BoostFade
	{
		private boolean hadBoost;

		/**
		 * Feeds the current boost, which is boosted level minus real level.
		 *
		 * @return true on the update where the boost ran out
		 */
		boolean update(int boost)
		{
			if (boost > 0)
			{
				hadBoost = true;
				return false;
			}

			boolean faded = hadBoost;
			hadBoost = false;

			return faded;
		}

		/** Forgets any boost seen so far. */
		void reset()
		{
			hadBoost = false;
		}
	}

	/** Whether HP is low enough to warn about: at or below the threshold with no overload to explain it. */
	static boolean lowHp(int hp, int threshold, boolean overloadActive)
	{
		return !overloadActive && hp <= threshold;
	}
}
