/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Tracks NMZ reward points: the current balance, the points gained in the dream in progress (positive changes
 * only), its running time and the points-per-hour rate, and pushes them to {@link PointsOverlay}. The dream figures
 * start from zero on entering a dream and are cleared when the player leaves it, whether by exiting or dying.
 */
@Singleton
public class PointsTracker
{
	private final Client client;
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final PointsOverlay overlay;

	private long dreamGain;
	private long dreamStartMillis;
	private int lastPoints = -1;
	private boolean wasInDream;

	@Inject
	PointsTracker(Client client, EventBus eventBus, OverlayManager overlayManager, PointsOverlay overlay)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.overlay = overlay;
	}

	/** Begins tracking: subscribes to events and shows the overlay. */
	public void start()
	{
		resetDream();
		lastPoints = -1;
		wasInDream = false;
		eventBus.register(this);
		overlayManager.add(overlay);
	}

	/** Stops tracking, hides the overlay. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int points = client.getVarbitValue(VarbitID.NZONE_CURRENTPOINTS);
		if (points == lastPoints)
			return;

		int gain = NmzArea.inDream(client) ? PointsMath.gain(lastPoints, points) : 0;
		lastPoints = points;
		if (gain <= 0)
			return;

		dreamGain += gain;
		publish();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		boolean inDream = NmzArea.inDream(client);
		if (inDream && !wasInDream)
		{
			resetDream();
			dreamStartMillis = System.currentTimeMillis();
		}
		else if (!inDream && wasInDream)
		{
			resetDream();
		}

		wasInDream = inDream;
		publish();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();
		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING)
		{
			lastPoints = -1;
			wasInDream = false;
			resetDream();
		}
	}

	private void resetDream()
	{
		dreamGain = 0;
		dreamStartMillis = 0;
	}

	private void publish()
	{
		int current = Math.max(0, client.getVarpValue(VarPlayerID.NZONE_REWARDPOINTS));
		long elapsed = dreamStartMillis > 0 ? System.currentTimeMillis() - dreamStartMillis : 0;
		overlay.update(NmzArea.inNmz(client), NmzArea.inDream(client), current, dreamGain, dreamStartMillis,
			PointsMath.perHour(dreamGain, elapsed));
	}
}
