/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.SplitComponent;
import net.runelite.client.util.QuantityFormatter;

/**
 * Shows each ticked reward as its item icon beside the reward name, the amount affordable and a progress bar, with a
 * combined bar underneath when more than one reward is ticked. It only draws while the tracker hands it a non-empty
 * goal, which happens inside the NMZ lobby and dream.
 */
@Singleton
public class RewardOverlay extends OverlayPanel
{
	private static final int PANEL_WIDTH = 250;
	private static final int BORDER_SIZE = 3;
	private static final int VERTICAL_GAP = 4;
	private static final int ICON_GAP = 5;
	private static final long MILLIS_PER_HOUR = 3_600_000L;
	private static final Color BAR_EDGE = new Color(0, 0, 12);
	private static final Color BAR_CENTER = new Color(18, 32, 175);
	private static final Color DONE_EDGE = new Color(10, 70, 25);
	private static final Color DONE_CENTER = new Color(25, 165, 55);
	private static final Color RUN_EDGE = new Color(35, 45, 190);
	private static final Color RUN_CENTER = new Color(140, 55, 205);
	private static final Color BAR_BACKGROUND = new Color(61, 56, 49);

	private final ItemManager itemManager;
	private final DefaultPlacement placement;

	private volatile RewardGoal goal;
	private volatile long runPoints;
	private volatile long pointsPerHour;

	/** Creates the overlay in the top left corner. */
	@Inject
	RewardOverlay(ItemManager itemManager, DefaultPlacement placement)
	{
		this.placement = placement;
		this.itemManager = itemManager;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_MED);
		panelComponent.setPreferredSize(new Dimension(PANEL_WIDTH, 0));
		panelComponent.setBorder(new Rectangle(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
		panelComponent.setGap(new Point(0, VERTICAL_GAP));
	}

	/** Sets the goal to draw, or null to hide the overlay. */
	void setGoal(RewardGoal goal)
	{
		this.goal = goal;
	}

	/** Sets the points earned so far in the current dream, drawn as its own colour on each bar. */
	void setPointsPerHour(long pointsPerHour)
	{
		this.pointsPerHour = pointsPerHour;
	}

	void setRunPoints(long runPoints)
	{
		this.runPoints = runPoints;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		RewardGoal current = goal;
		if (current == null || current.isEmpty())
			return null;

		placement.place(this, 2);
		graphics.setFont(FontManager.getRunescapeSmallFont());
		for (RewardGoal.Entry entry : current.getEntries())
			panelComponent.getChildren().add(entryRow(current, entry));

		if (current.getEntries().size() > 1)
		{
			panelComponent.getChildren().add(new VerticalSpacer());
			panelComponent.getChildren().add(totalRow(current));
		}

		return super.render(graphics);
	}

	private SplitComponent entryRow(RewardGoal current, RewardGoal.Entry entry)
	{
		String amount = "";
		if (entry.getQuantity() > 1)
			amount = current.getItemsAffordable(entry) + "/" + entry.getQuantity();

		String eta = timeToReach(entry.getTotalCost() - current.getPoints());
		if (!eta.isEmpty())
			amount = amount.isEmpty() ? eta : amount + "  " + eta;

		LineComponent nameLine = LineComponent.builder()
			.left(entry.getReward().getDisplayName())
			.leftFont(FontManager.getRunescapeFont())
			.right(amount)
			.rightFont(FontManager.getRunescapeFont())
			.build();

		OutlinedProgressBar bar = bar(current.getPoints(), entry.getTotalCost());

		SplitComponent text = SplitComponent.builder()
			.first(nameLine)
			.second(bar)
			.orientation(ComponentOrientation.VERTICAL)
			.build();

		return SplitComponent.builder()
			.first(new ImageComponent(itemManager.getImage(entry.getReward().getItemId())))
			.second(text)
			.orientation(ComponentOrientation.HORIZONTAL)
			.gap(new Point(ICON_GAP, 0))
			.build();
	}

	/** How long the points still needed take to earn at this dream's rate, or blank when it can't be estimated. */
	private String timeToReach(long pointsNeeded)
	{
		if (pointsNeeded <= 0 || pointsPerHour <= 0)
			return "";

		return PointsMath.formatDuration(pointsNeeded * MILLIS_PER_HOUR / pointsPerHour);
	}

	private OutlinedProgressBar totalRow(RewardGoal current)
	{
		return bar(current.getPoints(), current.getTotalCost());
	}

	/**
	 * Builds a bar for a cost: points banked before this dream in blue (green once they cover the cost), then the
	 * points earned in this dream in their own colour.
	 */
	private OutlinedProgressBar bar(long points, long cost)
	{
		long shown = Math.min(points, cost);
		long banked = Math.min(Math.max(0, points - runPoints), cost);
		double scale = cost <= 0 ? 0 : 100.0 / cost;

		OutlinedProgressBar bar = new OutlinedProgressBar();
		if (banked >= cost)
			bar.setForegroundGradient(DONE_EDGE, DONE_CENTER);
		else
			bar.setForegroundGradient(BAR_EDGE, BAR_CENTER);

		bar.setRunGradient(RUN_EDGE, RUN_CENTER);
		bar.setBackgroundColor(BAR_BACKGROUND);
		bar.setValue(cost <= 0 ? 100 : banked * scale);
		bar.setRunValue((shown - banked) * scale);
		bar.setLeftLabel(QuantityFormatter.quantityToStackSize(shown));
		bar.setRightLabel(QuantityFormatter.quantityToStackSize(cost));
		long tenths = cost <= 0 ? 1000 : shown * 1000 / cost;
		bar.setCenterLabel(tenths / 10 + "." + tenths % 10 + "%");

		return bar;
	}
}
