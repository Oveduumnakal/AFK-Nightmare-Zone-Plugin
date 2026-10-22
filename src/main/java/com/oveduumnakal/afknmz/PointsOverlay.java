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

/** Panel listing total points, the dream's points and time, and the hourly rate, fed by {@link PointsTracker}. */
public class PointsOverlay extends OverlayPanel
{
	private final AfkNmzConfig config;
	private final DefaultPlacement placement;

	private volatile boolean inNmz;
	private volatile boolean inDream;
	private volatile int current;
	private volatile long dream;
	private volatile long dreamStartMillis;
	private volatile long perHour;

	@Inject
	PointsOverlay(AfkNmzConfig config, DefaultPlacement placement)
	{
		this.config = config;
		this.placement = placement;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	/**
	 * Stores the latest figures to draw.
	 *
	 * @param inNmz whether the player is in the dream or its lobby
	 * @param inDream whether the player is inside a dream
	 * @param current points held plus those earned so far in the dream
	 * @param dream points gained in the dream in progress
	 * @param dreamStartMillis clock time the dream began, or 0 when none is running
	 * @param perHour the dream's points per hour
	 */
	void update(boolean inNmz, boolean inDream, int current, long dream, long dreamStartMillis, long perHour)
	{
		this.inNmz = inNmz;
		this.inDream = inDream;
		this.current = current;
		this.dream = dream;
		this.dreamStartMillis = dreamStartMillis;
		this.perHour = perHour;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showPointsOverlay() || !inNmz)
			return null;

		placement.place(this, 0);
		panelComponent.getChildren().add(TitleComponent.builder()
			.text("NMZ Points")
			.build());
		addLine("Total:", PointsMath.format(current), true);
		addLine("Dream:", PointsMath.format(dream), inDream && config.showSessionPoints());
		addLine("Points/hr:", PointsMath.format(perHour), inDream && config.showPointsPerHour());
		long elapsed = dreamStartMillis > 0 ? System.currentTimeMillis() - dreamStartMillis : 0;
		addLine("Dream Time:", PointsMath.formatDuration(elapsed), inDream && config.showDreamTime());

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
