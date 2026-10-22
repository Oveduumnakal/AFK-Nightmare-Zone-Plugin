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

	/**
	 * Most ticks after drinking during which the self-damage hits can still be landing: 5 hits up to 3 ticks apart,
	 * plus margin, since a shorter window let rock cake clicks through mid-damage. The window normally closes sooner,
	 * once {@link #noteHit} has counted the fifth hit.
	 */
	static final int SETTLE_TICKS = 14;

	/** An overload drink hurts the player this many times. */
	static final int SELF_HITS = 5;

	/** Damage of each of those hits. */
	static final int SELF_HIT_DAMAGE = 10;

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
	private int selfHitsLeft;
	private boolean warned;
	private boolean absorptionLowSent;
	private int lastAbsorption = -1;
	private boolean absorptionSeenPositive;

	/** Starts (or restarts) the overload countdown and the settling window and re-arms the warning. */
	public void drinkOverload()
	{
		overloadTicks = OVERLOAD_TICKS;
		settleTicks = SETTLE_TICKS;
		selfHitsLeft = SELF_HITS;
		warned = false;
	}

	/**
	 * Counts a hit the player just took. Once the drink's last self-damage hit has landed the settling window closes
	 * on the next tick, instead of waiting out the full window. Only hits of the exact self-damage amount count, and
	 * only while the window is open.
	 *
	 * @param amount the damage of the hit
	 */
	public void noteHit(int amount)
	{
		if (settleTicks <= 0 || amount != SELF_HIT_DAMAGE)
			return;

		selfHitsLeft--;
		if (selfHitsLeft <= 0)
			settleTicks = Math.min(settleTicks, 1);
	}

	/** Starts a countdown with a known remaining tick count, without a settling window. */
	public void seedOverload(int ticks)
	{
		overloadTicks = Math.max(0, ticks);
		settleTicks = 0;
		warned = false;
	}

	/**
	 * Aligns the countdown with a remaining tick count read from the game. It starts a countdown when none is running,
	 * and extends one that has fallen short by more than the tolerance; it never shortens, so a stale reading right
	 * after a drink cannot cut the countdown.
	 *
	 * @param ticks remaining ticks according to the game
	 * @param tolerance how far the countdown may be below the reading before it is extended
	 */
	public void syncOverload(int ticks, int tolerance)
	{
		if (ticks <= 0)
			return;

		if (overloadTicks <= 0)
			seedOverload(ticks);
		else if (ticks > overloadTicks + tolerance)
			overloadTicks = ticks;
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
	 * Feeds the current absorption points and reports a threshold crossing, once per crossing. A low warning only fires
	 * while the points are falling, so drinking a dose that leaves them at or below the threshold stays quiet until
	 * they start to drop again.
	 *
	 * @param points absorption points now
	 * @param threshold points at or below which to report {@link AbsorptionEvent#LOW}
	 */
	public AbsorptionEvent updateAbsorption(int points, int threshold)
	{
		boolean falling = lastAbsorption >= 0 && points < lastAbsorption;
		lastAbsorption = points;
		if (points > threshold)
			absorptionLowSent = false;

		if (points <= 0)
		{
			boolean depleted = absorptionSeenPositive;
			absorptionSeenPositive = false;
			return depleted ? AbsorptionEvent.DEPLETED : AbsorptionEvent.NONE;
		}

		absorptionSeenPositive = true;
		if (points <= threshold && falling && !absorptionLowSent)
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
		lastAbsorption = -1;
	}
}
