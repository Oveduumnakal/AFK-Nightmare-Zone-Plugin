/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/** Panel listing current, session and lifetime NMZ points plus the hourly rate, fed by {@link PointsTracker}. */
public class PointsOverlay extends OverlayPanel
{
	private final AfkNmzConfig config;

	private volatile boolean inNmz;
	private volatile int current;
	private volatile long session;
	private volatile long total;
	private volatile long perHour;

	@Inject
	PointsOverlay(AfkNmzConfig config)
	{
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	/**
	 * Stores the latest figures to draw.
	 *
	 * @param inNmz whether the player is in the dream or its lobby
	 * @param current points currently held
	 * @param session points gained this session
	 * @param total lifetime points gained
	 * @param perHour session points per hour
	 */
	void update(boolean inNmz, int current, long session, long total, long perHour)
	{
		this.inNmz = inNmz;
		this.current = current;
		this.session = session;
		this.total = total;
		this.perHour = perHour;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showPointsOverlay() || !inNmz)
			return null;

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("NMZ Points")
			.build());
		addLine("Current:", PointsMath.format(current), true);
		addLine("Session:", PointsMath.format(session), config.showSessionPoints());
		addLine("Total:", PointsMath.format(total), config.showTotalPoints());
		addLine("Points/hr:", PointsMath.format(perHour), config.showPointsPerHour());

		return super.render(graphics);
	}

	private void addLine(String left, String right, boolean enabled)
	{
		if (enabled)
			panelComponent.getChildren().add(LineComponent.builder()
				.left(left)
				.right(right)
				.build());
	}
}
