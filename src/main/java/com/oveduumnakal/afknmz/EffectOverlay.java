/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Live readout of the overload timer and absorption points inside the dream. The timer is evaluated against the
 * clock on every frame, so it counts smoothly in tenths of a second instead of jumping once per game tick.
 */
public class EffectOverlay extends OverlayPanel
{
	private static final Color LOW_COLOR = new Color(255, 90, 90);
	private static final Color OFF_COLOR = Color.GRAY;

	private final AfkNmzConfig config;
	private final EffectState state;
	private final DefaultPlacement placement;
	private final ItemManager itemManager;

	@Inject
	EffectOverlay(AfkNmzConfig config, EffectState state, DefaultPlacement placement, ItemManager itemManager)
	{
		this.itemManager = itemManager;
		this.placement = placement;
		this.config = config;
		this.state = state;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showDreamOverlay() || !state.isInDream())
			return null;

		Graphics2D canvas = placement.place(this, 1, graphics);
		for (Line line : lines(Countdown.now()))
		{
			panelComponent.getChildren().add(new IconLine(line.name, itemManager.getImage(line.itemId), line.right,
				line.rightColour));
		}

		return super.render(canvas);
	}

	/** The lines to show at the given moment, according to the settings. */
	List<Line> lines(long now)
	{
		List<Line> lines = new ArrayList<>();
		if (config.showOverloadTimer())
			lines.add(overloadLine(now));

		if (config.showAbsorptionPoints())
			lines.add(absorptionLine());

		return lines;
	}

	private Line overloadLine(long now)
	{
		Countdown countdown = state.getOverloadCountdown();
		if (!countdown.isRunning())
			return new Line("Overload", ItemID.NZONE4DOSEOVERLOADPOTION, "Off", OFF_COLOR);

		long remaining = countdown.remainingMillis(now);
		Color colour = AlertLogic.overloadLow(remaining, config.overloadWarningSeconds()) ? LOW_COLOR : Color.WHITE;

		return new Line("Overload", ItemID.NZONE4DOSEOVERLOADPOTION, Countdown.format(remaining), colour);
	}

	private Line absorptionLine()
	{
		int points = state.getAbsorptionPoints();
		Color colour = AlertLogic.absorptionLow(points, config.absorptionThreshold()) ? LOW_COLOR : Color.WHITE;

		return new Line("Absorption", ItemID.NZONE4DOSEABSORPTIONPOTION, String.valueOf(points), colour);
	}

	/** One name, icon and value row of the readout. */
	static final class Line
	{
		final String name;
		final int itemId;
		final String right;
		final Color rightColour;

		Line(String name, int itemId, String right, Color rightColour)
		{
			this.name = name;
			this.itemId = itemId;
			this.right = right;
			this.rightColour = rightColour;
		}
	}
}
