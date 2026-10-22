/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

/** One overlay row reading {@code Name [icon]: value}, all left-aligned, with the value in its own colour. */
class IconLine implements LayoutableRenderableEntity
{
	private static final int ICON_HEIGHT = 16;
	private static final int GAP = 3;

	private final String name;
	private final BufferedImage icon;
	private final String value;
	private final Color valueColour;
	private final Rectangle bounds = new Rectangle();

	private Point preferredLocation = new Point();
	private Dimension preferredSize = new Dimension(ComponentConstants.STANDARD_WIDTH, 0);

	IconLine(String name, BufferedImage icon, String value, Color valueColour)
	{
		this.name = name;
		this.icon = icon;
		this.value = value;
		this.valueColour = valueColour;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		int rowHeight = Math.max(metrics.getHeight(), ICON_HEIGHT);
		int x = preferredLocation.x;
		int y = preferredLocation.y;
		int baseline = y + (rowHeight - metrics.getHeight()) / 2 + metrics.getHeight();

		x += drawText(graphics, name, Color.WHITE, x, baseline) + GAP;
		if (icon != null && icon.getHeight() > 0)
		{
			int iconWidth = icon.getWidth() * ICON_HEIGHT / icon.getHeight();
			graphics.drawImage(icon, x, y + (rowHeight - ICON_HEIGHT) / 2, iconWidth, ICON_HEIGHT, null);
			x += iconWidth;
		}

		x += drawText(graphics, ":", Color.WHITE, x, baseline) + GAP;
		drawText(graphics, value, valueColour, x, baseline);

		bounds.setBounds(preferredLocation.x, y, preferredSize.width, rowHeight);
		return new Dimension(preferredSize.width, rowHeight);
	}

	private static int drawText(Graphics2D graphics, String text, Color colour, int x, int baseline)
	{
		TextComponent component = new TextComponent();
		component.setText(text);
		component.setColor(colour);
		component.setPosition(new Point(x, baseline));
		component.render(graphics);

		return graphics.getFontMetrics().stringWidth(text);
	}

	@Override
	public Rectangle getBounds()
	{
		return bounds;
	}

	@Override
	public void setPreferredLocation(Point preferredLocation)
	{
		this.preferredLocation = preferredLocation;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize)
	{
		this.preferredSize = preferredSize;
	}
}
