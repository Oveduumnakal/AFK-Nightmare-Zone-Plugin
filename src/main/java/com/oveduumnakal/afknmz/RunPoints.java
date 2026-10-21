/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

/** Tracks the points earned in the current dream: the balance now minus the balance when the dream began. */
final class RunPoints
{
	private boolean wasInDream;
	private int startPoints;

	/**
	 * Feeds the current state.
	 *
	 * @return the points earned since the dream began, or 0 outside a dream
	 */
	long update(boolean inDream, int points)
	{
		if (inDream && !wasInDream)
			startPoints = points;

		wasInDream = inDream;

		return inDream ? Math.max(0, points - startPoints) : 0;
	}

	/** Forgets the dream in progress. */
	void reset()
	{
		wasInDream = false;
		startPoints = 0;
	}
}
