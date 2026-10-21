/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

/**
 * Tracks overload and absorption inside the Nightmare Zone dream, mirrors them into {@link EffectState} and fires
 * the configured expiry notifications. Overload has no readable timer, so it is counted down from the drink chat
 * message; absorption is read from {@link VarbitID#NZONE_ABSORB_POTION_EFFECTS} every tick.
 */
@Singleton
public class EffectTracker
{
	/** Map region of the inside of an NMZ dream. */
	static final int[] NMZ_MAP_REGION = {9033};

	/** Game message sent when an overload dose is drunk. */
	static final String DRINK_MESSAGE = "You drink some of your overload potion.";

	/** Game message sent when overload runs out. */
	static final String WORN_OFF_MESSAGE = "The effects of overload have worn off, and you feel normal again.";

	/** The overload refresh varbit counts down in steps of this many ticks (20 refreshes over 5 minutes). */
	static final int TICKS_PER_REFRESH = 25;

	private final Client client;
	private final EventBus eventBus;
	private final Notifier notifier;
	private final AfkNmzConfig config;
	private final EffectState state;
	private final EffectTransitions transitions = new EffectTransitions();

	private boolean inDream;

	@Inject
	EffectTracker(Client client, EventBus eventBus, Notifier notifier, AfkNmzConfig config, EffectState state)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.notifier = notifier;
		this.config = config;
		this.state = state;
	}

	/** Starts listening for events. */
	public void start()
	{
		eventBus.register(this);
	}

	/** Stops listening and clears all state. */
	public void stop()
	{
		eventBus.unregister(this);
		leaveDream();
	}

	@Subscribe
	void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
			leaveDream();
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
		if (!inDream)
			return;

		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
			return;

		if (DRINK_MESSAGE.equals(event.getMessage()))
			transitions.drinkOverload();
		else if (WORN_OFF_MESSAGE.equals(event.getMessage()) && transitions.expireOverload())
			overloadExpired();
	}

	@Subscribe
	void onGameTick(GameTick event)
	{
		if (!isInDream())
		{
			leaveDream();
			return;
		}

		if (!inDream)
		{
			inDream = true;
			seedFromVarbit();
		}

		if (transitions.tick())
			overloadExpired();

		if (config.overloadWarning() && transitions.overloadWarningDue(config.overloadWarningSeconds()))
			notifier.notify("Your overload potion is about to expire!");

		int points = client.getVarbitValue(VarbitID.NZONE_ABSORB_POTION_EFFECTS);
		EffectTransitions.AbsorptionEvent absorption =
			transitions.updateAbsorption(points, config.absorptionThreshold());
		if (absorption == EffectTransitions.AbsorptionEvent.LOW && config.absorptionWarning())
			notifier.notify("Absorption points below: " + config.absorptionThreshold());
		else if (absorption == EffectTransitions.AbsorptionEvent.DEPLETED && config.absorptionExpired())
			notifier.notify("Your absorption points have run out!");

		publish(points);
	}

	private void overloadExpired()
	{
		if (config.overloadExpired())
			notifier.notify("Your overload has worn off");
	}

	/** Picks up an overload already running when the plugin starts mid-dream, from the refreshes-remaining varbit. */
	private void seedFromVarbit()
	{
		int refreshes = client.getVarbitValue(VarbitID.NZONE_OVERLOAD_POTION_EFFECTS);
		if (refreshes > 0 && !transitions.isOverloadActive())
			transitions.seedOverload(refreshes * TICKS_PER_REFRESH);
	}

	private void publish(int points)
	{
		state.setOverloadActive(transitions.isOverloadActive());
		state.setOverloadSettling(transitions.isSettling());
		state.setOverloadSecondsRemaining(transitions.getOverloadSeconds());
		state.setAbsorptionPoints(points);
	}

	private boolean isInDream()
	{
		return client.getGameState() == GameState.LOGGED_IN
			&& Arrays.equals(client.getTopLevelWorldView().getMapRegions(), NMZ_MAP_REGION);
	}

	private void leaveDream()
	{
		inDream = false;
		transitions.reset();
		state.reset();
	}
}
