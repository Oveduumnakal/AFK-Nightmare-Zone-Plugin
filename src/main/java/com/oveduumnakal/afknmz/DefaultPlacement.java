/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;

/**
 * Gives the plugin's panels a default home on the left edge, with the whole stack centred vertically on the screen
 * and the panels one under another in the order they were registered. A panel the player has dragged is left where
 * they put it.
 */
@Singleton
public class DefaultPlacement
{
	private static final int LEFT_MARGIN = 5;
	private static final int GAP = 4;

	private final Client client;
	private final Map<Integer, Overlay> slots = new TreeMap<>();
	private final Map<Overlay, Point> applied = new HashMap<>();

	@Inject
	DefaultPlacement(Client client)
	{
		this.client = client;
	}

	/**
	 * Moves the overlay to its default spot unless the player has placed it themselves. Call it every frame the
	 * overlay renders.
	 *
	 * @param overlay the panel to place
	 * @param order its position in the stack, lowest first
	 */
	void place(Overlay overlay, int order)
	{
		slots.put(order, overlay);
		if (!isDefaulted(overlay))
			return;

		int total = 0;
		int above = 0;
		int shown = 0;
		for (Map.Entry<Integer, Overlay> slot : slots.entrySet())
		{
			Overlay other = slot.getValue();
			int height = other.getBounds() == null ? 0 : other.getBounds().height;
			if (!isDefaulted(other) || (height <= 0 && other != overlay))
				continue;

			total += height + (shown > 0 ? GAP : 0);
			if (slot.getKey() < order)
				above = total + GAP;

			shown++;
		}

		int y = client.getCanvasHeight() / 2 - total / 2 + above;
		Point target = new Point(LEFT_MARGIN, y);
		if (!target.equals(overlay.getPreferredLocation()))
		{
			overlay.setPreferredLocation(target);
			applied.put(overlay, target);
		}
	}

	private boolean isDefaulted(Overlay overlay)
	{
		Point location = overlay.getPreferredLocation();
		return location == null || location.equals(applied.get(overlay));
	}
}
