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
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
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
	private static final int NMZ_LOBBY_REGION = 10288;
	private static final int NMZ_DREAM_REGION = 9033;

	private final Client client;
	private final EventBus eventBus;
	private final OverlayManager overlayManager;
	private final Notifier notifier;
	private final AfkNmzConfig config;
	private final RewardOverlay overlay;

	private boolean notified;

	/** Creates the tracker; call {@link #start()} to begin tracking. */
	@Inject
	public RewardTracker(
		Client client,
		EventBus eventBus,
		OverlayManager overlayManager,
		Notifier notifier,
		AfkNmzConfig config,
		RewardOverlay overlay)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.overlayManager = overlayManager;
		this.notifier = notifier;
		this.config = config;
		this.overlay = overlay;
	}

	/** Registers for events and adds the overlay. */
	public void start()
	{
		notified = false;
		eventBus.register(this);
		overlayManager.add(overlay);
	}

	/** Unregisters from events and removes the overlay. */
	public void stop()
	{
		eventBus.unregister(this);
		overlayManager.remove(overlay);
		overlay.setGoal(null);
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
		if (wanted.isEmpty() || !inNmz())
		{
			overlay.setGoal(null);
			return;
		}

		RewardGoal goal = RewardGoal.calculate(
			wanted, client.getVarbitValue(VarbitID.NZONE_CURRENTPOINTS), config.hardDiscount());
		overlay.setGoal(goal);

		if (!goal.isAffordable())
		{
			notified = false;
			return;
		}

		if (!notified && config.rewardNotify())
			notifier.notify("You have enough NMZ points for your selected rewards!");

		notified = true;
	}

	private boolean inNmz()
	{
		if (client.getLocalPlayer() == null)
			return false;

		LocalPoint local = client.getLocalPlayer().getLocalLocation();
		int region = WorldPoint.fromLocalInstance(client, local).getRegionID();
		return region == NMZ_LOBBY_REGION || region == NMZ_DREAM_REGION;
	}
}
