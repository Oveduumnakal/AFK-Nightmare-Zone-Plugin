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
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Tracks NMZ reward points: the session gain (positive changes only), the persisted lifetime total and the
 * points-per-hour rate, and pushes them to {@link PointsOverlay}.
 */
@Singleton
public class PointsTracker
{
	private final Client client;
	private final EventBus eventBus;
	private final ConfigManager configManager;
	private final AfkNmzConfig config;
	private final OverlayManager overlayManager;
	private final PointsOverlay overlay;

	private long persistedTotal;
	private long lifetimeGain;
	private long sessionGain;
	private long elapsedMillis;
	private long lastClockMillis;
	private int lastPoints = -1;
	private boolean started;
	private boolean wasInDream;
	private boolean needsSeed;

	@Inject
	PointsTracker(Client client, EventBus eventBus, ConfigManager configManager, AfkNmzConfig config,
		OverlayManager overlayManager, PointsOverlay overlay)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.configManager = configManager;
		this.config = config;
		this.overlayManager = overlayManager;
		this.overlay = overlay;
	}

	/** Begins tracking: loads the lifetime total, subscribes to events and shows the overlay. */
	public void start()
	{
		Long saved = configManager.getConfiguration(AfkNmzConfig.GROUP, AfkNmzConfig.TOTAL_POINTS_KEY, Long.class);
		persistedTotal = saved == null ? 0 : saved;
		needsSeed = PointsMath.needsSeed(saved);
		lifetimeGain = 0;
		resetSession();
		lastPoints = -1;
		wasInDream = false;
		eventBus.register(this);
		overlayManager.add(overlay);
	}

	/** Stops tracking, hides the overlay and persists the lifetime total. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
		persist();
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

		sessionGain += gain;
		lifetimeGain += gain;
		started = true;
		publish();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		seedFromExistingPoints();
		long now = System.currentTimeMillis();
		boolean inDream = NmzArea.inDream(client);
		if (started && NmzArea.inNmz(client) && lastClockMillis > 0)
			elapsedMillis += now - lastClockMillis;

		lastClockMillis = now;
		if (wasInDream && !inDream)
			persist();

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
			lastClockMillis = 0;
			wasInDream = false;
			persist();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!AfkNmzConfig.GROUP.equals(event.getGroup()) || !"resetSession".equals(event.getKey()))
			return;

		if (config.resetSession())
		{
			resetSession();
			configManager.setConfiguration(AfkNmzConfig.GROUP, "resetSession", false);
			publish();
		}
	}

	/**
	 * On the first run there is no saved total, or only a zero, so the points the player already holds become the
	 * starting total. Without this the total would ignore everything earned before the plugin was installed.
	 */

	private void seedFromExistingPoints()
	{
		if (!needsSeed || client.getGameState() != GameState.LOGGED_IN)
			return;

		needsSeed = false;
		persistedTotal = Math.max(0, client.getVarbitValue(VarbitID.NZONE_CURRENTPOINTS));
		persist();
	}

	private void resetSession()
	{
		sessionGain = 0;
		elapsedMillis = 0;
		lastClockMillis = 0;
		started = false;
	}

	private void persist()
	{
		configManager.setConfiguration(AfkNmzConfig.GROUP, AfkNmzConfig.TOTAL_POINTS_KEY,
			persistedTotal + lifetimeGain);
	}

	private void publish()
	{
		int current = Math.max(0, client.getVarbitValue(VarbitID.NZONE_CURRENTPOINTS));
		overlay.update(NmzArea.inNmz(client), current, sessionGain, persistedTotal + lifetimeGain,
			PointsMath.perHour(sessionGain, elapsedMillis));
	}
}
