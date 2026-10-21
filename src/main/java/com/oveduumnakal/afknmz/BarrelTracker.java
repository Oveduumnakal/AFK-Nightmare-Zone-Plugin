/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/** Tracks the lobby potion barrel objects and keeps {@link BarrelOverlay} supplied with them. */
@Singleton
public class BarrelTracker
{
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final BarrelOverlay overlay;

	@Inject
	BarrelTracker(EventBus eventBus, OverlayManager overlayManager, BarrelOverlay overlay)
	{
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.overlay = overlay;
	}

	/** Registers for events and shows the overlay. */
	public void start()
	{
		eventBus.register(this);
		overlayManager.add(overlay);
	}

	/** Unregisters from events, hides the overlay and drops tracked objects. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
		overlay.clear();
	}

	@Subscribe
	void onGameObjectSpawned(GameObjectSpawned event)
	{
		Optional<NmzBarrel> barrel = NmzBarrel.forObjectId(event.getGameObject().getId());
		barrel.ifPresent(found -> overlay.add(event.getGameObject(), found));
	}

	@Subscribe
	void onGameObjectDespawned(GameObjectDespawned event)
	{
		overlay.remove(event.getGameObject());
	}

	@Subscribe
	void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
			overlay.clear();
	}
}
