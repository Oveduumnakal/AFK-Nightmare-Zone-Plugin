/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

/**
 * Pure edge-detection logic for overload and absorption, kept free of client types so it can be unit tested.
 * Overload runs on a tick countdown started by the drink message; absorption is a plain point count.
 */
public class EffectTransitions
{
	/** Length of one game tick in seconds. */
	static final double SECONDS_PER_TICK = 0.6;

	/** An overload potion lasts 5 minutes, which is 500 game ticks. */
	static final int OVERLOAD_TICKS = 500;

	/** Ticks after drinking during which the 5 self-damage hits are still landing (5 hits plus one tick margin). */
	static final int SETTLE_TICKS = 6;

	/** What changed about absorption on an update. */
	public enum AbsorptionEvent
	{
		/** Nothing worth notifying. */
		NONE,
		/** Points fell to or below the threshold. */
		LOW,
		/** Points fell to zero after having been above zero. */
		DEPLETED
	}

	private int overloadTicks;
	private int settleTicks;
	private boolean warned;
	private boolean absorptionLowSent;
	private boolean absorptionSeenPositive;

	/** Starts (or restarts) the overload countdown and the settling window and re-arms the warning. */
	public void drinkOverload()
	{
		overloadTicks = OVERLOAD_TICKS;
		settleTicks = SETTLE_TICKS;
		warned = false;
	}

	/** Starts a countdown with a known remaining tick count, without a settling window. */
	public void seedOverload(int ticks)
	{
		overloadTicks = Math.max(0, ticks);
		settleTicks = 0;
		warned = false;
	}

	/**
	 * Advances one game tick.
	 *
	 * @return true if the overload ran out on this tick
	 */
	public boolean tick()
	{
		if (settleTicks > 0)
			settleTicks--;

		if (overloadTicks <= 0)
			return false;

		overloadTicks--;
		if (overloadTicks > 0)
			return false;

		settleTicks = 0;
		return true;
	}

	/**
	 * Ends the overload early, for the worn-off chat message.
	 *
	 * @return true if an overload was active
	 */
	public boolean expireOverload()
	{
		boolean wasActive = overloadTicks > 0;
		overloadTicks = 0;
		settleTicks = 0;
		return wasActive;
	}

	/** Whether an overload is running. */
	public boolean isOverloadActive()
	{
		return overloadTicks > 0;
	}

	/** Whether the self-damage hits are still landing. */
	public boolean isSettling()
	{
		return settleTicks > 0;
	}

	/** Remaining overload ticks. */
	public int getOverloadTicks()
	{
		return overloadTicks;
	}

	/** Remaining overload time in whole seconds, rounded up, or 0 when none is active. */
	public int getOverloadSeconds()
	{
		return (int) Math.ceil(overloadTicks * SECONDS_PER_TICK);
	}

	/**
	 * Whether the one-per-potion expiry warning is due now. Returns true at most once until the potion is re-drunk.
	 *
	 * @param warnSeconds seconds before expiry at which to warn
	 */
	public boolean overloadWarningDue(int warnSeconds)
	{
		if (warned || overloadTicks <= 0 || getOverloadSeconds() > warnSeconds)
			return false;

		warned = true;
		return true;
	}

	/**
	 * Feeds the current absorption points and reports a threshold crossing, once per crossing.
	 *
	 * @param points absorption points now
	 * @param threshold points at or below which to report {@link AbsorptionEvent#LOW}
	 */
	public AbsorptionEvent updateAbsorption(int points, int threshold)
	{
		if (points > threshold)
			absorptionLowSent = false;

		if (points <= 0)
		{
			boolean depleted = absorptionSeenPositive;
			absorptionSeenPositive = false;
			return depleted ? AbsorptionEvent.DEPLETED : AbsorptionEvent.NONE;
		}

		absorptionSeenPositive = true;
		if (points <= threshold && !absorptionLowSent)
		{
			absorptionLowSent = true;
			return AbsorptionEvent.LOW;
		}

		return AbsorptionEvent.NONE;
	}

	/** Clears all state. */
	public void reset()
	{
		overloadTicks = 0;
		settleTicks = 0;
		warned = false;
		absorptionLowSent = false;
		absorptionSeenPositive = false;
	}
}
