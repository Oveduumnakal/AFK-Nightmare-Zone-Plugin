/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;

/**
 * Gives the plugin's panels a default home on the left edge, with the whole stack centred vertically on the screen
 * and the panels one under another in the order they were registered. A panel the player has dragged is left where
 * they put it.
 *
 * <p>A panel's spot depends on the heights the other panels had on the previous frame, so a panel that has just
 * appeared takes a few frames to settle. Until it stops moving it is drawn onto an off-screen canvas: it is still
 * laid out and measured, but the player only sees it once it is in place. The panels take their data from the
 * trackers on the game tick, so panels that appear together arrive on the same frame and settle hidden together.
 */
@Singleton
public class DefaultPlacement
{
	private static final int LEFT_MARGIN = 5;
	private static final int GAP = 4;

	/** A panel not placed for this long counts as newly appearing the next time it is. */
	private static final long ABSENT_MILLIS = 250;

	/** Off-screen image a settling panel is drawn onto, so none of it reaches the game screen. */
	private static final BufferedImage SCRATCH = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	private final Client client;
	private final Map<Integer, Overlay> slots = new TreeMap<>();
	private final Map<Overlay, Point> applied = new ConcurrentHashMap<>();
	private final Map<Overlay, Long> lastPlacedMillis = new HashMap<>();
	private final Set<Overlay> settling = new HashSet<>();

	@Inject
	DefaultPlacement(Client client)
	{
		this.client = client;
	}

	/**
	 * Gives a panel that was just added a provisional spot on the left edge. RuneLite clears a panel's position when it
	 * is added, and a panel without one is stacked into the top-left corner on its first frame, which pushes other
	 * plugins' panels there down for that frame even though this one draws off-screen. A position the player saved by
	 * dragging is kept.
	 *
	 * @param overlay the panel just added to the overlay manager
	 */
	void prime(Overlay overlay)
	{
		if (overlay.getPreferredLocation() != null)
			return;

		Point spot = new Point(LEFT_MARGIN, 0);
		overlay.setPreferredLocation(spot);
		applied.put(overlay, spot);
	}

	/**
	 * Moves the overlay to its default spot unless the player has placed it themselves. Call it every frame the overlay
	 * renders, before drawing, and draw with the graphics it returns.
	 *
	 * @param overlay the panel to place
	 * @param order its position in the stack, lowest first
	 * @param graphics the graphics the panel would draw with
	 * @return the given graphics, or an off-screen canvas while the panel is still settling after appearing
	 */
	Graphics2D place(Overlay overlay, int order, Graphics2D graphics)
	{
		long now = System.currentTimeMillis();
		Long last = lastPlacedMillis.put(overlay, now);
		if (last == null || now - last > ABSENT_MILLIS)
			settling.add(overlay);

		slots.put(order, overlay);
		if (move(overlay, order) || !settling.contains(overlay))
			return graphics;

		return offscreen(graphics);
	}

	/** A canvas that draws nowhere but measures text the same way, so a settling panel still gets its real size. */
	private static Graphics2D offscreen(Graphics2D graphics)
	{
		Graphics2D canvas = SCRATCH.createGraphics();
		canvas.setFont(graphics.getFont());
		canvas.setRenderingHints(graphics.getRenderingHints());

		return canvas;
	}

	/**
	 * Moves a defaulted overlay to its spot in the stack.
	 *
	 * @return true when the overlay is settled: dragged by the player, or already at its spot
	 */
	private boolean move(Overlay overlay, int order)
	{
		if (!isDefaulted(overlay))
		{
			settling.remove(overlay);
			return true;
		}

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
		if (target.equals(overlay.getPreferredLocation()))
		{
			settling.remove(overlay);
			return true;
		}

		overlay.setPreferredLocation(target);
		applied.put(overlay, target);
		return false;
	}

	private boolean isDefaulted(Overlay overlay)
	{
		Point location = overlay.getPreferredLocation();
		return location == null || location.equals(applied.get(overlay));
	}
}
