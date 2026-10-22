/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Singleton;

import net.runelite.api.GameObject;
import net.runelite.api.NPC;

/**
 * Shared snapshot of the lobby: where Dominic's coffer stands, what it holds and who is in the Rumble party. Written
 * by {@link LobbyTracker} on the client thread and read by the lobby overlays.
 */
@Singleton
public class LobbyState
{
	private volatile GameObject coffer;
	private volatile NPC dominic;
	private final List<GameObject> vials = new CopyOnWriteArrayList<>();
	private volatile GameObject vial;
	private volatile boolean vialEmpty;
	private volatile boolean cofferLocked;
	private volatile int cofferCoins;
	private volatile List<String> party = Collections.emptyList();

	/** The coffer in the scene, or null when it is not loaded. */
	GameObject getCoffer()
	{
		return coffer;
	}

	/** Dominic in the scene, or null when he is not loaded. */
	NPC getDominic()
	{
		return dominic;
	}

	/** Every potion vial in the scene; the lobby has one in each cubicle. */
	List<GameObject> getVials()
	{
		return vials;
	}

	/** The potion vial nearest the player, or null when it is not loaded. */
	GameObject getVial()
	{
		return vial;
	}

	/** Whether no dream has been chosen, so the vial is still empty. */
	boolean isVialEmpty()
	{
		return vialEmpty;
	}

	/** Whether the coffer is still locked. */
	boolean isCofferLocked()
	{
		return cofferLocked;
	}

	/** Coins in the coffer. */
	int getCofferCoins()
	{
		return cofferCoins;
	}

	/** Names in the Rumble party, empty when there is nobody. */
	List<String> getParty()
	{
		return party;
	}

	void setCoffer(GameObject coffer)
	{
		this.coffer = coffer;
	}

	void setDominic(NPC dominic)
	{
		this.dominic = dominic;
	}

	void setVial(GameObject vial)
	{
		this.vial = vial;
	}

	void setVialEmpty(boolean vialEmpty)
	{
		this.vialEmpty = vialEmpty;
	}

	void setCofferLocked(boolean cofferLocked)
	{
		this.cofferLocked = cofferLocked;
	}

	void setCofferCoins(int cofferCoins)
	{
		this.cofferCoins = cofferCoins;
	}

	void setParty(List<String> party)
	{
		this.party = party;
	}

	/** Clears everything, for leaving the lobby or logging out. */
	void reset()
	{
		coffer = null;
		vials.clear();
		vial = null;
		dominic = null;
		vialEmpty = false;
		cofferLocked = false;
		cofferCoins = 0;
		party = Collections.emptyList();
	}
}
