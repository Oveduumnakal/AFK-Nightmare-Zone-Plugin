/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

/**
 * Shows progress toward each ticked reward as "points / cost" lines, plus a bar for the combined goal. It only draws
 * while the tracker hands it a non-empty goal, which happens inside the NMZ lobby and dream.
 */
@Singleton
public class RewardOverlay extends OverlayPanel
{
	private static final Color BAR_COLOR = new Color(60, 120, 200);
	private static final Color BAR_DONE_COLOR = new Color(40, 150, 60);
	private static final Color BAR_BACKGROUND = new Color(61, 56, 49);

	private volatile RewardGoal goal;

	/** Creates the overlay in the top left corner. */
	@Inject
	RewardOverlay()
	{
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_MED);
	}

	/** Sets the goal to draw, or null to hide the overlay. */
	void setGoal(RewardGoal goal)
	{
		this.goal = goal;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		RewardGoal current = goal;
		if (current == null || current.isEmpty())
			return null;

		TitleComponent title = TitleComponent.builder()
			.text("NMZ rewards")
			.build();
		panelComponent.getChildren().add(title);

		for (RewardGoal.Entry entry : current.getEntries())
		{
			long shown = Math.min(current.getPoints(), entry.getTotalCost());
			String name = entry.getReward().getDisplayName();
			if (entry.getQuantity() > 1)
				name = entry.getQuantity() + " x " + name;

			panelComponent.getChildren().add(LineComponent.builder()
				.left(name)
				.right(QuantityFormatter.quantityToStackSize(shown) + " / "
					+ QuantityFormatter.quantityToStackSize(entry.getTotalCost()))
				.rightColor(current.getProgress(entry) >= 1 ? Color.GREEN : Color.WHITE)
				.build());
		}

		ProgressBarComponent bar = new ProgressBarComponent();
		bar.setMinimum(0);
		bar.setMaximum(current.getTotalCost());
		bar.setValue(Math.min(current.getPoints(), current.getTotalCost()));
		bar.setLabelDisplayMode(ProgressBarComponent.LabelDisplayMode.FULL);
		bar.setForegroundColor(current.isAffordable() ? BAR_DONE_COLOR : BAR_COLOR);
		bar.setBackgroundColor(BAR_BACKGROUND);
		panelComponent.getChildren().add(bar);

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Total")
			.right(QuantityFormatter.formatNumber(current.getPoints()) + " / "
				+ QuantityFormatter.formatNumber(current.getTotalCost()))
			.rightColor(current.isAffordable() ? Color.GREEN : Color.WHITE)
			.build());

		return super.render(graphics);
	}
}
