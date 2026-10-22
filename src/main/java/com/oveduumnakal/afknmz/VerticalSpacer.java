/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

/**
 * Draws nothing and takes no height. Inside a vertical panel it adds one more gap between its neighbours, which
 * doubles the spacing at that spot.
 */
class VerticalSpacer implements LayoutableRenderableEntity
{
	private final Rectangle bounds = new Rectangle();

	@Override
	public Dimension render(Graphics2D graphics)
	{
		return new Dimension(0, 0);
	}

	@Override
	public Rectangle getBounds()
	{
		return bounds;
	}

	@Override
	public void setPreferredLocation(Point preferredLocation)
	{
		bounds.setLocation(preferredLocation);
	}

	@Override
	public void setPreferredSize(Dimension preferredSize)
	{
	}
}
