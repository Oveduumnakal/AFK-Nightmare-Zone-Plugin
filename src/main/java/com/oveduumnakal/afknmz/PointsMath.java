/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.text.NumberFormat;
import java.util.Locale;

/** Pure arithmetic for the points overlay: gain accumulation, hourly rate and display formatting. */
final class PointsMath
{
	private static final long MILLIS_PER_HOUR = 3_600_000L;

	private PointsMath()
	{
	}

	/**
	 * Points gained between two readings. Spending points (a drop) and an unknown previous reading count as zero.
	 *
	 * @param previous the earlier reading, or a negative number when none is known
	 * @param current the new reading
	 * @return the positive difference, or zero
	 */
	static int gain(int previous, int current)
	{
		if (previous < 0)
			return 0;

		return Math.max(0, current - previous);
	}

	/**
	 * Gained points scaled to one hour.
	 *
	 * @param gained points gained in the period
	 * @param elapsedMillis length of the period in milliseconds
	 * @return points per hour, or zero when no time has passed
	 */
	static long perHour(long gained, long elapsedMillis)
	{
		if (elapsedMillis <= 0)
			return 0;

		return Math.round(gained * (double) MILLIS_PER_HOUR / elapsedMillis);
	}

	/**
	 * Formats a number with thousands separators.
	 *
	 * @param value the number to format
	 * @return the text, for example {@code 1,234,567}
	 */
	static String format(long value)
	{
		return NumberFormat.getIntegerInstance(Locale.US).format(value);
	}

	/** Whether the lifetime total should be seeded from the points already held: nothing saved yet, or only a zero. */
	static boolean needsSeed(Long saved)
	{
		return saved == null || saved == 0;
	}
}
