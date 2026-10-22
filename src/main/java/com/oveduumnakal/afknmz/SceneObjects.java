/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.function.Consumer;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;

/** Walks the game objects already loaded in the scene, for trackers switched on after their objects spawned. */
final class SceneObjects
{
	private SceneObjects()
	{
	}

	/**
	 * Hands every game object in the top-level scene to the action. Call it on the client thread.
	 *
	 * @param client the client whose scene to walk
	 * @param action what to do with each object
	 */
	static void forEach(Client client, Consumer<GameObject> action)
	{
		for (Tile[][] plane : client.getTopLevelWorldView().getScene().getTiles())
		{
			for (Tile[] row : plane)
			{
				for (Tile tile : row)
				{
					if (tile == null)
						continue;

					for (GameObject object : tile.getGameObjects())
					{
						if (object != null)
							action.accept(object);
					}
				}
			}
		}
	}
}
