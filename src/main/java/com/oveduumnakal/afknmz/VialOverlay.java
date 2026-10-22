/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/** While no dream is chosen, outlines the lobby's potion vial in red with a label and Dominic in green. */
public class VialOverlay extends Overlay
{
	private static final String LABEL = "Select a Dream";
	private static final int LABEL_HEIGHT = 40;

	private final Client client;
	private final AfkNmzConfig config;
	private final LobbyState state;

	@Inject
	VialOverlay(Client client, AfkNmzConfig config, LobbyState state)
	{
		this.client = client;
		this.config = config;
		this.state = state;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		GameObject vial = state.getVial();
		if (!config.highlightEmptyVial() || vial == null || !state.isVialEmpty() || !NmzArea.inLobby(client))
			return null;

		Shape hull = vial.getClickbox();
		if (hull != null)
			OverlayUtil.renderPolygon(graphics, hull, Color.RED);

		NPC dominic = state.getDominic();
		Shape dominicHull = dominic == null ? null : dominic.getConvexHull();
		if (dominicHull != null)
			OverlayUtil.renderPolygon(graphics, dominicHull, Color.GREEN);

		Point label = vial.getCanvasTextLocation(graphics, LABEL, LABEL_HEIGHT);
		if (label != null)
			OverlayUtil.renderTextLocation(graphics, label, LABEL, Color.RED);

		return null;
	}
}
