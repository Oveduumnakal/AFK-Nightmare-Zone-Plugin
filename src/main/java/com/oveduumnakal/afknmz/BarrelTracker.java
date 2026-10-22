/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/** Tracks the lobby potion barrel objects and keeps {@link BarrelOverlay} supplied with them. */
@Singleton
public class BarrelTracker
{
	private final Client client;
	private final ClientThread clientThread;
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final BarrelOverlay overlay;

	@Inject
	BarrelTracker(Client client, ClientThread clientThread, EventBus eventBus, OverlayManager overlayManager,
		BarrelOverlay overlay)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.overlay = overlay;
	}

	/** Registers for events and shows the overlay. */
	public void start()
	{
		eventBus.register(this);
		overlayManager.add(overlay);
		clientThread.invoke(this::scanLoadedScene);
	}

	/** Unregisters from events, hides the overlay and drops tracked objects. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
		overlay.clear();
	}

	/** Picks up barrels that were already in the scene before the plugin was switched on. */
	private void scanLoadedScene()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
			return;

		SceneObjects.forEach(client, this::track);
	}

	private void track(GameObject object)
	{
		NmzBarrel.forObjectId(object.getId()).ifPresent(found -> overlay.add(object, found));
	}

	@Subscribe
	void onGameObjectSpawned(GameObjectSpawned event)
	{
		track(event.getGameObject());
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
