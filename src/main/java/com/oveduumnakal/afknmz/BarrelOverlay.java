/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/** Draws the stored dose count centred on each potion barrel in the Nightmare Zone lobby. */
public class BarrelOverlay extends Overlay
{
	private static final int TEXT_Z_OFFSET = 40;
	private static final int LINE_HEIGHT = 15;
	private static final int TINT_ALPHA = 55;

	private final Client client;
	private final AfkNmzConfig config;
	private final Map<GameObject, NmzBarrel> barrels = new ConcurrentHashMap<>();

	@Inject
	BarrelOverlay(Client client, AfkNmzConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	/** Starts tracking a spawned barrel object. */
	void add(GameObject object, NmzBarrel barrel)
	{
		barrels.put(object, barrel);
	}

	/** Stops tracking a despawned barrel object. */
	void remove(GameObject object)
	{
		barrels.remove(object);
	}

	/** Forgets every tracked barrel. */
	void clear()
	{
		barrels.clear();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showBarrelDoses() || NmzArea.inDream(client))
			return null;

		for (Map.Entry<GameObject, NmzBarrel> entry : barrels.entrySet())
		{
			NmzBarrel barrel = entry.getValue();
			LocalPoint location = entry.getKey().getLocalLocation();
			if (config.tintBarrels())
				tint(graphics, entry.getKey(), barrel.getTint());

			int doses = client.getVarbitValue(barrel.getVarbitId());
			Color doseColour = NmzBarrel.isLow(doses, config.lowDoseThreshold())
				? config.lowDoseColor()
				: config.barrelTextColor();
			drawCentred(graphics, location, barrel.getLabel(), config.barrelTextColor(), 0);
			drawCentred(graphics, location, String.valueOf(doses), doseColour, LINE_HEIGHT);
		}

		return null;
	}

	/** Fills the barrel's outline with a translucent shade of the given colour. */
	private void tint(Graphics2D graphics, GameObject object, Color colour)
	{
		Shape hull = object.getConvexHull();
		if (hull == null)
			return;

		graphics.setColor(new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), TINT_ALPHA));
		graphics.fill(hull);
	}

	/** Draws one line of text centred over the location, pushed down by the given number of pixels. */
	private void drawCentred(Graphics2D graphics, LocalPoint location, String text, Color colour, int yShift)
	{
		Point point = Perspective.getCanvasTextLocation(client, graphics, location, text, TEXT_Z_OFFSET);
		if (point != null)
			OverlayUtil.renderTextLocation(graphics, new Point(point.getX(), point.getY() + yShift), text, colour);
	}
}
