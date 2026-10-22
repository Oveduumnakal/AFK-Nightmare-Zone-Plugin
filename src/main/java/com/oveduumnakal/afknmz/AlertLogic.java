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

	/** Fires once when a count that was positive drops to zero, such as a stat boost or a stack of potions. */
	static final class DropToZero
	{
		private boolean hadPositive;

		/**
		 * Feeds the current count.
		 *
		 * @return true on the update where the count fell to zero
		 */
		boolean update(int count)
		{
			if (count > 0)
			{
				hadPositive = true;
				return false;
			}

			boolean dropped = hadPositive;
			hadPositive = false;

			return dropped;
		}

		/** Forgets any positive count seen so far. */
		void reset()
		{
			hadPositive = false;
		}
	}

	/** Whether HP is low enough to warn about: at or below the threshold with no overload to explain it. */
	static boolean lowHp(int hp, int threshold, boolean overloadActive)
	{
		return !overloadActive && hp <= threshold;
	}

	/** Whether the overload countdown is at or under the warning lead time. */
	static boolean overloadLow(long remainingMillis, int warnSeconds)
	{
		return remainingMillis <= warnSeconds * 1000L;
	}

	/** Whether absorption points are at or under the warning threshold. */
	static boolean absorptionLow(int points, int threshold)
	{
		return points <= threshold;
	}
}
