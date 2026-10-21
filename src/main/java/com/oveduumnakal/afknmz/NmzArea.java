/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.Arrays;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

/** Where the player is relative to the Nightmare Zone: inside a dream, in the Yanille lobby, or neither. */
final class NmzArea
{
	/** Map region of the inside of a dream, as used by the built-in Nightmare Zone plugin. */
	static final int DREAM_REGION = 9033;

	/** Region of the Yanille lobby where the dream is entered; computed from its coordinates, not seen in game. */
	static final int LOBBY_REGION = 10288;

	private static final int[] DREAM_REGIONS = {DREAM_REGION};

	private NmzArea()
	{
	}

	/** Whether the player is inside a dream. */
	static boolean inDream(Client client)
	{
		return Arrays.equals(client.getTopLevelWorldView().getMapRegions(), DREAM_REGIONS);
	}

	/** Whether the player is in the lobby where the barrels and reward shop are. */
	static boolean inLobby(Client client)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
			return false;

		return WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID() == LOBBY_REGION;
	}

	/** Whether the player is in the dream or the lobby. */
	static boolean inNmz(Client client)
	{
		return inDream(client) || inLobby(client);
	}
}
