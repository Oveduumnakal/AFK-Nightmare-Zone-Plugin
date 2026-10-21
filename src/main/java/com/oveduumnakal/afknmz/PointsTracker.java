/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
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
	/** Region of the dream instance, as used by the built-in Nightmare Zone plugin. */
	private static final int NMZ_DREAM_REGION = 9033;

	/** Region of the Yanille building where the dream is entered. */
	private static final int NMZ_LOBBY_REGION = 10288;

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

		int gain = inDream() ? PointsMath.gain(lastPoints, points) : 0;
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
		long now = System.currentTimeMillis();
		boolean inDream = inDream();
		if (started && inNmzArea() && lastClockMillis > 0)
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
		overlay.update(inNmzArea(), current, sessionGain, persistedTotal + lifetimeGain,
			PointsMath.perHour(sessionGain, elapsedMillis));
	}

	private boolean inDream()
	{
		int[] regions = client.getTopLevelWorldView().getMapRegions();
		if (regions == null)
			return false;

		for (int region : regions)
			if (region == NMZ_DREAM_REGION)
				return true;

		return false;
	}

	private boolean inNmzArea()
	{
		if (inDream())
			return true;

		Player player = client.getLocalPlayer();
		if (player == null)
			return false;

		WorldPoint location = player.getWorldLocation();

		return location != null && location.getRegionID() == NMZ_LOBBY_REGION;
	}
}
