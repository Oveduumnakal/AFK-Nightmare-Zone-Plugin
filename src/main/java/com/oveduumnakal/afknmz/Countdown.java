/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * An immutable countdown anchored to the moment a game tick was seen. Between ticks the remaining time is
 * interpolated from the clock so a readout can count smoothly in tenths of a second, but it is clamped to the
 * current tick so it never runs ahead of the game or backwards when a tick arrives late.
 */
final class Countdown
{
	/** Length of one game tick in milliseconds. */
	static final long TICK_MILLIS = 600;

	/** A countdown that is not running. */
	static final Countdown NONE = new Countdown(0, 0);

	private final int ticks;
	private final long anchorMillis;

	private Countdown(int ticks, long anchorMillis)
	{
		this.ticks = ticks;
		this.anchorMillis = anchorMillis;
	}

	/**
	 * Creates a countdown for the ticks left as of a given moment.
	 *
	 * @param ticks game ticks remaining at that moment
	 * @param nowMillis the moment the tick was seen, from {@link #now()}
	 */
	static Countdown of(int ticks, long nowMillis)
	{
		return ticks <= 0 ? NONE : new Countdown(ticks, nowMillis);
	}

	/** A monotonic clock reading in milliseconds, suitable for {@link #of} and {@link #remainingMillis}. */
	static long now()
	{
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
	}

	/** Whole game ticks remaining as of the anchor. */
	int getTicks()
	{
		return ticks;
	}

	/** Whether the countdown is running. */
	boolean isRunning()
	{
		return ticks > 0;
	}

	/**
	 * Time left at the given moment, between the previous tick's value and this tick's value.
	 *
	 * @param nowMillis the moment to evaluate, from {@link #now()}
	 */
	long remainingMillis(long nowMillis)
	{
		if (ticks <= 0)
			return 0;

		long elapsed = Math.max(0, nowMillis - anchorMillis);
		long floor = (ticks - 1) * TICK_MILLIS;

		return Math.max(floor, ticks * TICK_MILLIS - elapsed);
	}

	/**
	 * Formats a duration as minutes, seconds and tenths, such as {@code 4:12.3}, rounding up so it only reads
	 * {@code 0:00.0} once the time has really run out.
	 */
	static String format(long millis)
	{
		long tenths = (Math.max(0, millis) + 99) / 100;

		return String.format(Locale.ROOT, "%d:%02d.%d", tenths / 600, tenths / 10 % 60, tenths % 10);
	}
}
