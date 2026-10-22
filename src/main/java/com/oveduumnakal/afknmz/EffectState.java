/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Singleton;

/**
 * Shared, thread-safe snapshot of the player's overload and absorption state. Written by the effect tracker on the
 * client thread and read by the click guards and overlays.
 */
@Singleton
public class EffectState
{
	private volatile boolean overloadActive;
	private volatile boolean overloadSettling;
	private volatile int overloadSecondsRemaining;
	private volatile int absorptionPoints;
	private volatile Countdown overloadCountdown = Countdown.NONE;

	/** Whether an overload potion's effect is currently running. */
	public boolean isOverloadActive()
	{
		return overloadActive;
	}

	/** Whether the overload's self-damage hits are still being applied, so the player is still losing health. */
	public boolean isOverloadSettling()
	{
		return overloadSettling;
	}

	/** Seconds of overload left, or 0 when none is active. */
	public int getOverloadSecondsRemaining()
	{
		return overloadSecondsRemaining;
	}

	/** Absorption points left. */
	public int getAbsorptionPoints()
	{
		return absorptionPoints;
	}

	/** The overload countdown, which can be evaluated at any moment for a smooth readout. */
	Countdown getOverloadCountdown()
	{
		return overloadCountdown;
	}

	void setOverloadActive(boolean overloadActive)
	{
		this.overloadActive = overloadActive;
	}

	void setOverloadSettling(boolean overloadSettling)
	{
		this.overloadSettling = overloadSettling;
	}

	void setOverloadSecondsRemaining(int overloadSecondsRemaining)
	{
		this.overloadSecondsRemaining = overloadSecondsRemaining;
	}

	void setAbsorptionPoints(int absorptionPoints)
	{
		this.absorptionPoints = absorptionPoints;
	}

	void setOverloadCountdown(Countdown overloadCountdown)
	{
		this.overloadCountdown = overloadCountdown;
	}

	/** Clears everything, for leaving the dream or logging out. */
	void reset()
	{
		overloadActive = false;
		overloadSettling = false;
		overloadSecondsRemaining = 0;
		absorptionPoints = 0;
		overloadCountdown = Countdown.NONE;
	}
}
