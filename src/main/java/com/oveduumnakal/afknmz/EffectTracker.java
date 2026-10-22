/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Tracks overload and absorption inside the Nightmare Zone dream, mirrors them into {@link EffectState} and fires
 * the configured expiry notifications. Overload has no readable timer, so it is counted down from the drink chat
 * message; absorption is read from {@link VarbitID#NZONE_ABSORB_POTION_EFFECTS} every tick.
 */
@Singleton
public class EffectTracker
{
	/** Game message sent when an overload dose is drunk. */
	static final String DRINK_MESSAGE = "You drink some of your overload potion.";

	/** Game message sent when overload runs out. */
	static final String WORN_OFF_MESSAGE = "The effects of overload have worn off, and you feel normal again.";

	/** The overload refresh varbit counts down in steps of this many ticks (20 refreshes over 5 minutes). */
	static final int TICKS_PER_REFRESH = 25;

	private final Client client;
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final Notifier notifier;
	private final AfkNmzConfig config;
	private final EffectState state;
	private final EffectOverlay overlay;
	private final EffectTransitions transitions = new EffectTransitions();

	private boolean inDream;
	private int lastRefreshes = -1;
	private boolean builtInHidden;

	@Inject
	EffectTracker(
		Client client,
		EventBus eventBus,
		OverlayManager overlayManager,
		Notifier notifier,
		AfkNmzConfig config,
		EffectState state,
		EffectOverlay overlay)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.notifier = notifier;
		this.config = config;
		this.state = state;
		this.overlay = overlay;
	}

	/** Starts listening for events and adds the dream overlay. */
	public void start()
	{
		eventBus.register(this);
		overlayManager.add(overlay);
	}

	/** Stops listening, removes the overlay and clears all state. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
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
		if (client.getGameState() != GameState.LOGGED_IN || !NmzArea.inDream(client))
		{
			leaveDream();
			return;
		}

		inDream = true;
		syncOverloadFromVarbit();

		if (transitions.tick())
			overloadExpired();

		if (transitions.overloadWarningDue(config.overloadWarningSeconds()))
			notifier.notify(config.overloadWarning(), "Your overload potion is about to expire!");

		int points = client.getVarbitValue(VarbitID.NZONE_ABSORB_POTION_EFFECTS);
		EffectTransitions.AbsorptionEvent absorption =
			transitions.updateAbsorption(points, config.absorptionThreshold());
		if (absorption == EffectTransitions.AbsorptionEvent.LOW)
			notifier.notify(config.absorptionWarning(),
				"Absorption points at or below " + config.absorptionThreshold());
		else if (absorption == EffectTransitions.AbsorptionEvent.DEPLETED)
			notifier.notify(config.absorptionExpired(), "Your absorption points have run out!");

		publish(points);
	}

	private void overloadExpired()
	{
		notifier.notify(config.overloadExpired(), "Your overload has worn off");
	}

	/**
	 * Keeps the countdown in step with the refreshes-remaining varbit, so an overload that was already running when the
	 * plugin started is picked up and a missed drink message cannot leave the guard thinking it has run out.
	 */
	private void syncOverloadFromVarbit()
	{
		int refreshes = client.getVarbitValue(VarbitID.NZONE_OVERLOAD_POTION_EFFECTS);
		if (lastRefreshes >= 0 && refreshes > lastRefreshes)
			transitions.drinkOverload();

		lastRefreshes = refreshes;
		transitions.syncOverload(refreshes * TICKS_PER_REFRESH, TICKS_PER_REFRESH);
	}

	private void publish(int points)
	{
		state.setOverloadActive(transitions.isOverloadActive());
		state.setOverloadSettling(transitions.isSettling());
		state.setOverloadSecondsRemaining(transitions.getOverloadSeconds());
		state.setOverloadCountdown(Countdown.of(transitions.getOverloadTicks(), Countdown.now()));
		state.setAbsorptionPoints(points);
	}

	@Subscribe
	void onHitsplatApplied(HitsplatApplied event)
	{
		if (inDream && event.getActor() == client.getLocalPlayer())
			transitions.noteHit(event.getHitsplat().getAmount());
	}

	@Subscribe
	void onBeforeRender(BeforeRender event)
	{
		boolean hide = inDream && config.showDreamOverlay() && config.hideBuiltInAbsorption();
		if (hide || builtInHidden)
			setBuiltInHidden(hide);
	}

	/** Hides or restores the game's own NMZ readout, which holds the absorption display. */
	private void setBuiltInHidden(boolean hidden)
	{
		Widget widget = client.getWidget(InterfaceID.NzoneGame.UNIVERSE);
		if (widget != null)
			widget.setHidden(hidden);

		builtInHidden = hidden;
	}

	private void leaveDream()
	{
		if (builtInHidden)
			setBuiltInHidden(false);

		inDream = false;
		lastRefreshes = -1;
		transitions.reset();
		state.reset();
	}
}
