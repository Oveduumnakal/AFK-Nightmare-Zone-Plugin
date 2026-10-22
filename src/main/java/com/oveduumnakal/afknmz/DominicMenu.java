/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

/** Makes "Dream" the default left-click option when Dominic's menu is open in the lobby. */
@Singleton
public class DominicMenu
{
	private static final String NPC_NAME = "Dominic";
	private static final String OPTION = "Dream";

	private final Client client;
	private final EventBus eventBus;
	private final AfkNmzConfig config;

	@Inject
	DominicMenu(Client client, EventBus eventBus, AfkNmzConfig config)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.config = config;
	}

	/** Starts listening for menus. */
	public void start()
	{
		eventBus.register(this);
	}

	/** Stops listening for menus. */
	public void stop()
	{
		eventBus.unregister(this);
	}

	@Subscribe
	void onPostMenuSort(PostMenuSort event)
	{
		if (!config.dominicDreamFirst() || !NmzArea.inLobby(client))
			return;

		MenuEntry[] entries = client.getMenu().getMenuEntries();
		int dream = -1;
		for (int i = 0; i < entries.length; i++)
		{
			if (entries[i].getNpc() != null && OPTION.equalsIgnoreCase(entries[i].getOption())
				&& Text.removeTags(entries[i].getTarget()).contains(NPC_NAME))
				dream = i;
		}

		if (dream < 0 || dream == entries.length - 1)
			return;

		MenuEntry chosen = entries[dream];
		System.arraycopy(entries, dream + 1, entries, dream, entries.length - dream - 1);
		entries[entries.length - 1] = chosen;
		client.getMenu().setMenuEntries(entries);
	}
}
