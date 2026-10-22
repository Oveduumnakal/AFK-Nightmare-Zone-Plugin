/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Singleton;

/** The points-per-hour rate of the dream in progress, shared between the points tracker and the reward overlay. */
@Singleton
public class DreamRate
{
	private volatile long perHour;

	/** Points per hour in the current dream, or 0 when there is no dream or nothing earned yet. */
	long getPerHour()
	{
		return perHour;
	}

	void setPerHour(long perHour)
	{
		this.perHour = perHour;
	}
}
