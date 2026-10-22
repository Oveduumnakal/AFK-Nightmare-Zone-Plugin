/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Builds the reward goal from the ticked config entries each game tick, feeds it to the overlay and sends one
 * notification when the combined goal first becomes affordable.
 */
@Singleton
public class RewardTracker
{
	private final Client client;
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final Notifier notifier;
	private final AfkNmzConfig config;
	private final RewardOverlay overlay;
	private final DreamRate rate;

	private final AffordableAlert affordableAlert = new AffordableAlert();
	private final RunPoints runPoints = new RunPoints();

	/** Creates the tracker; call {@link #start()} to begin tracking. */
	@Inject
	public RewardTracker(
		Client client,
		EventBus eventBus,
		OverlayManager overlayManager,
		Notifier notifier,
		AfkNmzConfig config,
		RewardOverlay overlay,
		DreamRate rate)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.notifier = notifier;
		this.config = config;
		this.overlay = overlay;
		this.rate = rate;
	}

	/** Registers for events and adds the overlay. */
	public void start()
	{
		affordableAlert.reset();
		runPoints.reset();
		eventBus.register(this);
		overlayManager.add(overlay);
	}

	/** Unregisters from events and removes the overlay. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
		overlay.setGoal(null);
		overlay.setRunPoints(0);
	}

	/** Returns the ticked rewards with their wanted quantities, in enum order. */
	Map<NmzReward, Integer> selectedRewards()
	{
		Map<NmzReward, Integer> wanted = new EnumMap<>(NmzReward.class);
		for (NmzReward reward : NmzReward.values())
		{
			if (reward.isSelected(config))
				wanted.put(reward, reward.getQuantity(config));
		}

		return wanted;
	}

	/** Refreshes the goal and fires the affordable notification once. */
	@Subscribe
	public void onGameTick(GameTick event)
	{
		Map<NmzReward, Integer> wanted = selectedRewards();
		if (wanted.isEmpty() || !NmzArea.inNmz(client))
		{
			overlay.setGoal(null);
			return;
		}

		boolean inDream = NmzArea.inDream(client);
		int dreamPoints = inDream ? client.getVarbitValue(VarbitID.NZONE_CURRENTPOINTS) : 0;
		int points = client.getVarpValue(VarPlayerID.NZONE_REWARDPOINTS) + dreamPoints;
		overlay.setRunPoints(runPoints.update(inDream, dreamPoints));
		overlay.setPointsPerHour(rate.getPerHour());
		RewardGoal goal = RewardGoal.calculate(wanted, points, config.hardDiscount());
		overlay.setGoal(goal);

		if (affordableAlert.update(goal.isAffordable()))
			notifier.notify(config.rewardNotify(), "You have enough NMZ points for your selected rewards!");
	}
}
