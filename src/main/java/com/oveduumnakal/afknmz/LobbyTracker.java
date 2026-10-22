/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

/**
 * Tracks the lobby's coffer and Rumble party, hides the game's own readouts for them and feeds {@link LobbyState}
 * for the overlays that replace them.
 */
@Singleton
public class LobbyTracker
{
	private static final String PARTY_TITLE = "Rumble Party";
	private static final String EMPTY_SLOT = "-";
	private static final String DOMINIC = "Dominic";

	/** The coffer varbit counts thousands of coins: 271 in the varbit is 271,000 coins. */
	private static final long COFFER_UNIT = 1000;

	private final Client client;
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final AfkNmzConfig config;
	private final LobbyState state;
	private final CofferOverlay cofferOverlay;
	private final PartyOverlay partyOverlay;
	private final VialOverlay vialOverlay;

	private boolean partyHidden;
	private boolean cofferHidden;

	@Inject
	LobbyTracker(
		Client client,
		EventBus eventBus,
		OverlayManager overlayManager,
		AfkNmzConfig config,
		LobbyState state,
		CofferOverlay cofferOverlay,
		PartyOverlay partyOverlay,
		VialOverlay vialOverlay)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.config = config;
		this.state = state;
		this.cofferOverlay = cofferOverlay;
		this.partyOverlay = partyOverlay;
		this.vialOverlay = vialOverlay;
	}

	/** Starts listening for events and adds the lobby overlays. */
	public void start()
	{
		eventBus.register(this);
		overlayManager.add(cofferOverlay);
		overlayManager.add(partyOverlay);
		overlayManager.add(vialOverlay);
	}

	/** Stops listening, removes the overlays and restores the game's readouts. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(cofferOverlay);
		overlayManager.remove(partyOverlay);
		overlayManager.remove(vialOverlay);
		restoreBuiltIn();
		state.reset();
	}

	@Subscribe
	void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (isCoffer(event.getGameObject().getId()))
			state.setCoffer(event.getGameObject());
		else if (isVial(event.getGameObject().getId()))
			state.getVials().add(event.getGameObject());
	}

	@Subscribe
	void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (event.getGameObject() == state.getCoffer())
			state.setCoffer(null);
		else
			state.getVials().remove(event.getGameObject());
	}

	@Subscribe
	void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();
		if (npc.getName() != null && Text.removeTags(npc.getName()).contains(DOMINIC))
			state.setDominic(npc);
	}

	@Subscribe
	void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc() == state.getDominic())
			state.setDominic(null);
	}

	@Subscribe
	void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		if (gameState == GameState.LOADING || gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING)
		{
			state.reset();
			cofferHidden = false;
			partyHidden = false;
		}
	}

	@Subscribe
	void onGameTick(GameTick event)
	{
		if (!NmzArea.inLobby(client))
		{
			state.setParty(new ArrayList<>());
			return;
		}

		state.setCofferCoins((int) Math.min(Integer.MAX_VALUE,
			client.getVarbitValue(VarbitID.NZONE_CASH) * COFFER_UNIT));
		state.setVial(nearestVial());
		state.setCofferLocked(currentId(state.getCoffer()) == ObjectID.NZONE_LOBBY_MONEYPOUCH_LOCKED);
		state.setVialEmpty(currentId(state.getVial()) == ObjectID.NZONE_LOBBY_VIAL_EMPTY);
		state.setParty(readParty());
	}

	@Subscribe
	void onConfigChanged(ConfigChanged event)
	{
		if (AfkNmzConfig.GROUP.equals(event.getGroup()))
		{
			cofferHidden = true;
			partyHidden = true;
		}
	}

	@Subscribe
	void onBeforeRender(BeforeRender event)
	{
		boolean inLobby = NmzArea.inLobby(client);
		cofferHidden = apply(InterfaceID.NzoneLobby.RIGHT_CONTENT,
			inLobby && config.showCofferOverhead(), cofferHidden);
		partyHidden = apply(InterfaceID.NzoneLobby.LEFT_CONTENT,
			inLobby && config.showRumbleParty(), partyHidden);
	}

	private boolean apply(int widgetId, boolean hide, boolean hidden)
	{
		if (!hide && !hidden)
			return false;

		Widget widget = client.getWidget(widgetId);
		if (widget != null)
		{
			widget.setHidden(hide);
			widget.revalidate();
		}

		return hide;
	}

	private void restoreBuiltIn()
	{
		cofferHidden = apply(InterfaceID.NzoneLobby.RIGHT_CONTENT, false, cofferHidden);
		partyHidden = apply(InterfaceID.NzoneLobby.LEFT_CONTENT, false, partyHidden);
	}

	private List<String> readParty()
	{
		List<String> names = new ArrayList<>();
		Widget panel = client.getWidget(InterfaceID.NzoneLobby.LEFT_CONTENT);
		if (panel == null || panel.getDynamicChildren() == null)
			return names;

		for (Widget child : panel.getDynamicChildren())
		{
			String text = child.getText() == null ? "" : Text.removeTags(child.getText()).trim();
			if (!text.isEmpty() && !text.equals(PARTY_TITLE) && !text.equals(EMPTY_SLOT))
				names.add(text);
		}

		return names;
	}

	private GameObject nearestVial()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
			return null;

		GameObject nearest = null;
		int best = Integer.MAX_VALUE;
		for (GameObject vial : state.getVials())
		{
			int distance = vial.getLocalLocation().distanceTo(player.getLocalLocation());
			if (distance < best)
			{
				best = distance;
				nearest = vial;
			}
		}

		return nearest;
	}

	/** The id the object currently shows as, after any varbit-driven change, or -1 when there is no object. */
	private int currentId(GameObject object)
	{
		if (object == null)
			return -1;

		ObjectComposition definition = client.getObjectDefinition(object.getId());
		if (definition.getImpostorIds() != null && definition.getImpostor() != null)
			definition = definition.getImpostor();

		return definition.getId();
	}

	private static boolean isVial(int id)
	{
		return id == ObjectID.NZONE_LOBBY_VIAL_EMPTY
			|| id == ObjectID.NZONE_LOBBY_VIAL_FULL
			|| id == ObjectID.NZONE_LOBBY_VIAL_SPECTATOR
			|| id == ObjectID.NZONE_LOBBY_VIAL;
	}

	private static boolean isCoffer(int id)
	{
		return id == ObjectID.NZONE_LOBBY_MONEYPOUCH_LOCKED
			|| id == ObjectID.NZONE_LOBBY_MONEYPOUCH_UNLOCKED
			|| id == ObjectID.NZONE_LOBBY_MONEYPOUCH;
	}
}
