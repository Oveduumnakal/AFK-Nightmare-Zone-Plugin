/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/** Lists the Rumble party in the lobby, above the points panel, once someone besides the player has joined. */
public class PartyOverlay extends OverlayPanel
{
	private final Client client;
	private final AfkNmzConfig config;
	private final LobbyState state;
	private final DefaultPlacement placement;

	@Inject
	PartyOverlay(Client client, AfkNmzConfig config, LobbyState state, DefaultPlacement placement)
	{
		this.client = client;
		this.config = config;
		this.state = state;
		this.placement = placement;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		List<String> party = state.getParty();
		if (!config.showRumbleParty() || party.size() < 2 || !NmzArea.inLobby(client))
			return null;

		placement.place(this, -1);
		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Rumble Party")
			.build());
		for (String name : party)
			panelComponent.getChildren().add(LineComponent.builder()
				.left(name)
				.build());

		return super.render(graphics);
	}
}
