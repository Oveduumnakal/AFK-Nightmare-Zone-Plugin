/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/** Draws the coffer's coins, or a locked notice with a key, above Dominic's coffer in the lobby. */
public class CofferOverlay extends Overlay
{
	private static final int HEIGHT_ABOVE_COFFER = 80;
	private static final int GAP = 4;

	private final Client client;
	private final AfkNmzConfig config;
	private final LobbyState state;
	private final ItemManager itemManager;

	@Inject
	CofferOverlay(Client client, AfkNmzConfig config, LobbyState state, ItemManager itemManager)
	{
		this.client = client;
		this.config = config;
		this.state = state;
		this.itemManager = itemManager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		GameObject coffer = state.getCoffer();
		if (!config.showCofferOverhead() || coffer == null || !NmzArea.inLobby(client))
			return null;

		Point anchor = coffer.getCanvasLocation(HEIGHT_ABOVE_COFFER);
		if (anchor == null)
			return null;

		boolean locked = state.isCofferLocked();
		String text = locked ? "Locked" : "";
		BufferedImage icon = locked
			? itemManager.getImage(ItemID.VARLAMORE_THIEVING_HOUSE_KEY)
			: itemManager.getImage(ItemID.COINS, Math.max(1, state.getCofferCoins()), true);

		FontMetrics metrics = graphics.getFontMetrics();
		int width = text.isEmpty() ? icon.getWidth() : metrics.stringWidth(text) + GAP + icon.getWidth();
		int left = anchor.getX() - width / 2;
		int top = anchor.getY() - icon.getHeight() / 2;
		graphics.drawImage(icon, left, top, null);
		if (!text.isEmpty())
			OverlayUtil.renderTextLocation(graphics,
				new Point(left + icon.getWidth() + GAP, top + (icon.getHeight() + metrics.getAscent()) / 2),
				text, Color.WHITE);

		return null;
	}
}
