/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

/**
 * Decides when to tell the player their selected rewards became affordable. A goal that is already affordable the
 * first time it is seen stays quiet, so logging in with enough points does not repeat an old alert.
 */
final class AffordableAlert
{
	private boolean seen;
	private boolean notified;

	/**
	 * Feeds the current state of the goal.
	 *
	 * @return true if the player should be notified now
	 */
	boolean update(boolean affordable)
	{
		boolean firstLook = !seen;
		seen = true;
		if (!affordable)
		{
			notified = false;
			return false;
		}

		if (firstLook)
			notified = true;

		boolean notify = !notified;
		notified = true;

		return notify;
	}

	/** Forgets everything, so the next goal seen counts as a first look. */
	void reset()
	{
		seen = false;
		notified = false;
	}
}
