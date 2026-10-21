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

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

/**
 * Progress bar with outlined labels, so the text stays readable over the coloured fill. It takes whatever width its
 * parent layout gives it, which lets it sit beside an icon inside a split component.
 */
class OutlinedProgressBar implements LayoutableRenderableEntity
{
	private static final int BAR_HEIGHT = 16;
	private static final int TEXT_PADDING = 4;

	private double value;
	private double runValue;
	private String leftLabel = "";
	private String centerLabel = "";
	private String rightLabel = "";
	private Color foregroundColor = Color.GREEN;
	private Color runColor = Color.ORANGE;
	private Color backgroundColor = new Color(61, 56, 49);

	private Point preferredLocation = new Point();
	private Dimension preferredSize = new Dimension(ComponentConstants.STANDARD_WIDTH, BAR_HEIGHT);
	private final Rectangle bounds = new Rectangle();

	/** Sets the fill as a percentage, clamped to 0 to 100. */
	void setValue(double value)
	{
		this.value = Math.max(0, Math.min(100, value));
	}

	/** Sets the segment drawn after the main fill, as a percentage of the full bar, clamped to what room is left. */
	void setRunValue(double runValue)
	{
		this.runValue = Math.max(0, Math.min(100, runValue));
	}

	void setRunColor(Color color)
	{
		this.runColor = color;
	}

	void setLeftLabel(String label)
	{
		this.leftLabel = label;
	}

	void setCenterLabel(String label)
	{
		this.centerLabel = label;
	}

	void setRightLabel(String label)
	{
		this.rightLabel = label;
	}

	void setForegroundColor(Color color)
	{
		this.foregroundColor = color;
	}

	void setBackgroundColor(Color color)
	{
		this.backgroundColor = color;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		int x = preferredLocation.x;
		int y = preferredLocation.y;
		int width = preferredSize.width;
		int height = BAR_HEIGHT;

		graphics.setColor(backgroundColor);
		graphics.fillRect(x, y, width, height);
		graphics.setColor(foregroundColor);
		int mainWidth = (int) (width * value / 100.0);
		graphics.fillRect(x, y, mainWidth, height);
		int runWidth = (int) (width * Math.min(runValue, 100 - value) / 100.0);
		graphics.setColor(runColor);
		graphics.fillRect(x + mainWidth, y, runWidth, height);

		graphics.setFont(FontManager.getRunescapeSmallFont());
		FontMetrics metrics = graphics.getFontMetrics();
		int textY = y + (height - metrics.getAscent() - metrics.getDescent()) / 2 + metrics.getAscent();
		drawOutlined(graphics, leftLabel, x + TEXT_PADDING, textY);
		drawOutlined(graphics, centerLabel, x + (width - metrics.stringWidth(centerLabel)) / 2, textY);
		drawOutlined(graphics, rightLabel, x + width - metrics.stringWidth(rightLabel) - TEXT_PADDING, textY);

		bounds.setBounds(x, y, width, height);
		return new Dimension(width, height);
	}

	private void drawOutlined(Graphics2D graphics, String text, int x, int y)
	{
		if (text.isEmpty())
			return;

		TextComponent component = new TextComponent();
		component.setText(text);
		component.setColor(Color.WHITE);
		component.setOutline(true);
		component.setPosition(new Point(x, y));
		component.render(graphics);
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
