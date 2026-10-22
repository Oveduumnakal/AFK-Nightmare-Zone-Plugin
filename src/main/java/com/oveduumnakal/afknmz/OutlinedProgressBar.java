/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

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

	/** Pixels to move the labels down, because the overlay draws this font 2px above where its metrics place it. */
	private static final int TEXT_DROP = 2;

	private double value;
	private double runValue;
	private String leftLabel = "";
	private String centerLabel = "";
	private String rightLabel = "";
	private Color foregroundEdge = Color.GREEN;
	private Color foregroundCenter = Color.GREEN;
	private Color runEdge = Color.ORANGE;
	private Color runCenter = Color.ORANGE;
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

	/** Sets the run segment's top and bottom colour and the colour it fades to through the middle. */
	void setRunGradient(Color edge, Color center)
	{
		this.runEdge = edge;
		this.runCenter = center;
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
		setForegroundGradient(color, color);
	}

	/** Sets the fill's top and bottom colour and the colour it fades to through the middle. */
	void setForegroundGradient(Color edge, Color center)
	{
		this.foregroundEdge = edge;
		this.foregroundCenter = center;
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
		int mainWidth = (int) (width * value / 100.0);
		fillFaded(graphics, x, y, mainWidth, height, foregroundEdge, foregroundCenter);
		int runWidth = (int) (width * Math.min(runValue, 100 - value) / 100.0);
		fillFaded(graphics, x + mainWidth, y, runWidth, height, runEdge, runCenter);
		if (runWidth > 0)
		{
			graphics.setColor(Color.BLACK);
			graphics.fillRect(x + mainWidth + runWidth - 1, y, 1, height);
		}

		graphics.setFont(FontManager.getRunescapeSmallFont());
		FontMetrics metrics = graphics.getFontMetrics();
		int[] ink = inkRows(graphics.getFont());
		int textY = y + (height - ink[1]) / 2 + ink[0] + TEXT_DROP;
		drawOutlined(graphics, leftLabel, x + TEXT_PADDING, textY);
		drawOutlined(graphics, centerLabel, x + (width - metrics.stringWidth(centerLabel)) / 2, textY);
		drawOutlined(graphics, rightLabel, x + width - metrics.stringWidth(rightLabel) - TEXT_PADDING, textY);

		bounds.setBounds(x, y, width, height);
		return new Dimension(width, height);
	}

	private static void fillFaded(Graphics2D graphics, int x, int y, int width, int height, Color edge, Color center)
	{
		if (width <= 0)
			return;

		Paint previous = graphics.getPaint();
		graphics.setPaint(new LinearGradientPaint(new Point2D.Float(0, y), new Point2D.Float(0, y + height),
			new float[] {0f, 0.5f, 1f}, new Color[] {edge, center, edge}));
		graphics.fillRect(x, y, width, height);
		graphics.setPaint(previous);
	}

	/**
	 * Measures the pixels the font really paints for digits and a percent sign, because its reported metrics leave
	 * the text sitting off-centre.
	 *
	 * @return the rows between the top of the ink and the baseline, then the height of the ink
	 */
	private static int[] inkRows(Font font)
	{
		int baseline = 30;
		BufferedImage image = new BufferedImage(120, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D canvas = image.createGraphics();
		canvas.setFont(font);
		canvas.setColor(Color.WHITE);
		canvas.drawString("0123456789%", 0, baseline);
		canvas.dispose();

		int top = baseline;
		int bottom = baseline;
		for (int row = 0; row < image.getHeight(); row++)
		{
			for (int column = 0; column < image.getWidth(); column++)
			{
				if ((image.getRGB(column, row) >>> 24) != 0)
				{
					top = Math.min(top, row);
					bottom = Math.max(bottom, row);
				}
			}
		}

		return new int[] {baseline - top, bottom - top + 1};
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
