/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;

import com.google.inject.Provides;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

/** Makes AFK Nightmare Zone runs safer and easier to track. */
@PluginDescriptor(
	name = "AFK Nightmare Zone",
	description = "Overload/absorption alerts, barrel dose counts, points tracking and orb/cake safety",
	tags = {"nmz", "nightmare", "zone", "overload", "absorption", "afk"},
	conflicts = {"Nightmare Zone"}
)
public class AfkNmzPlugin extends Plugin
{
	@Inject
	private EffectTracker effectTracker;

	@Inject
	private PointsTracker pointsTracker;

	@Inject
	private BarrelTracker barrelTracker;

	@Inject
	private ClickGuard clickGuard;

	@Inject
	private RewardTracker rewardTracker;

	@Inject
	private AfkAlerts afkAlerts;

	@Override
	protected void startUp()
	{
		effectTracker.start();
		pointsTracker.start();
		barrelTracker.start();
		clickGuard.start();
		rewardTracker.start();
		afkAlerts.start();
	}

	@Override
	protected void shutDown()
	{
		afkAlerts.stop();
		rewardTracker.stop();
		clickGuard.stop();
		barrelTracker.stop();
		pointsTracker.stop();
		effectTracker.stop();
	}

	@Provides
	AfkNmzConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AfkNmzConfig.class);
	}
}
