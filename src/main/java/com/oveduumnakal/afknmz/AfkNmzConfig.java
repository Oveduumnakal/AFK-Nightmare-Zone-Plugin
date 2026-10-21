/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

/** Settings for the AFK Nightmare Zone plugin. */
@ConfigGroup(AfkNmzConfig.GROUP)
public interface AfkNmzConfig extends Config
{
	String GROUP = "afknmz";

	/** Config key holding the lifetime points total; not shown in the settings panel. */
	String TOTAL_POINTS_KEY = "totalPoints";

	@ConfigSection(
		name = "Notifications",
		description = "Alerts for overload and absorption expiring",
		position = 0
	)
	String NOTIFICATIONS = "notifications";

	@ConfigItem(
		keyName = "overloadWarning",
		name = "Overload expiring",
		description = "Notify shortly before your overload runs out",
		section = NOTIFICATIONS,
		position = 0
	)
	default boolean overloadWarning()
	{
		return true;
	}

	@Range(min = 5, max = 120)
	@ConfigItem(
		keyName = "overloadWarningSeconds",
		name = "Overload warning (seconds)",
		description = "How many seconds before overload expires to notify",
		section = NOTIFICATIONS,
		position = 1
	)
	default int overloadWarningSeconds()
	{
		return 30;
	}

	@ConfigItem(
		keyName = "overloadExpired",
		name = "Overload expired",
		description = "Notify when your overload has run out",
		section = NOTIFICATIONS,
		position = 2
	)
	default boolean overloadExpired()
	{
		return true;
	}

	@ConfigItem(
		keyName = "absorptionWarning",
		name = "Absorption low",
		description = "Notify when your absorption points drop to the threshold",
		section = NOTIFICATIONS,
		position = 3
	)
	default boolean absorptionWarning()
	{
		return true;
	}

	@Range(min = 1, max = 1000)
	@ConfigItem(
		keyName = "absorptionThreshold",
		name = "Absorption threshold",
		description = "Absorption points at or below which to notify",
		section = NOTIFICATIONS,
		position = 4
	)
	default int absorptionThreshold()
	{
		return 200;
	}

	@ConfigItem(
		keyName = "absorptionExpired",
		name = "Absorption expired",
		description = "Notify when your absorption points reach zero",
		section = NOTIFICATIONS,
		position = 5
	)
	default boolean absorptionExpired()
	{
		return true;
	}

	@ConfigSection(
		name = "Barrels",
		description = "Dose counts on the potion barrels in the lobby",
		position = 1
	)
	String BARRELS = "barrels";

	@ConfigItem(
		keyName = "showBarrelDoses",
		name = "Show barrel doses",
		description = "Draw the stored dose count on each barrel before entering a dream",
		section = BARRELS,
		position = 0
	)
	default boolean showBarrelDoses()
	{
		return true;
	}

	@ConfigItem(
		keyName = "barrelTextColor",
		name = "Dose text colour",
		description = "Colour of the dose count text",
		section = BARRELS,
		position = 1
	)
	default Color barrelTextColor()
	{
		return Color.WHITE;
	}

	@ConfigSection(
		name = "Points",
		description = "Nightmare Zone points overlay",
		position = 2
	)
	String POINTS = "points";

	@ConfigItem(
		keyName = "showPointsOverlay",
		name = "Show points overlay",
		description = "Show current, session and total points and the points per hour rate",
		section = POINTS,
		position = 0
	)
	default boolean showPointsOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSessionPoints",
		name = "Session points",
		description = "Points earned since the plugin started or the session was reset",
		section = POINTS,
		position = 1
	)
	default boolean showSessionPoints()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showTotalPoints",
		name = "Total points",
		description = "Lifetime points earned while this plugin was running",
		section = POINTS,
		position = 2
	)
	default boolean showTotalPoints()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPointsPerHour",
		name = "Points per hour",
		description = "Session points divided by session time",
		section = POINTS,
		position = 3
	)
	default boolean showPointsPerHour()
	{
		return true;
	}

	@ConfigItem(
		keyName = "resetSession",
		name = "Reset session",
		description = "Tick to reset the session points and timer; it untoggles itself",
		section = POINTS,
		position = 4
	)
	default boolean resetSession()
	{
		return false;
	}

	@ConfigSection(
		name = "Click Guards",
		description = "Block locator orb and dwarven rock cake clicks that would be unsafe",
		position = 3
	)
	String GUARDS = "guards";

	@ConfigItem(
		keyName = "requireOverload",
		name = "Require active overload",
		description = "Block locator orb and rock cake clicks unless an overload is active and its self-damage is done",
		section = GUARDS,
		position = 0
	)
	default boolean requireOverload()
	{
		return true;
	}

	@ConfigItem(
		keyName = "blockBelowMinHp",
		name = "Block below minimum HP",
		description = "Without an active overload, block clicks that would drop you below the minimum HP",
		section = GUARDS,
		position = 1
	)
	default boolean blockBelowMinHp()
	{
		return true;
	}

	@Range(min = 2, max = 99)
	@ConfigItem(
		keyName = "minHp",
		name = "Minimum HP",
		description = "Hitpoints you must stay at or above when no overload is active",
		section = GUARDS,
		position = 2
	)
	default int minHp()
	{
		return 51;
	}

	@ConfigSection(
		name = "Rewards",
		description = "Track NMZ points toward reward shop purchases and imbues",
		position = 4
	)
	String REWARDS = "rewards";

	@ConfigItem(
		keyName = "rewardNotify",
		name = "Notify when affordable",
		description = "Notify once when your points cover everything ticked below",
		section = REWARDS,
		position = 0
	)
	default boolean rewardNotify()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hardDiscount",
		name = "Hard CA reward claimed",
		description = "Imbues cost half the points once the Hard Combat Achievements reward is claimed",
		section = REWARDS,
		position = 1
	)
	default boolean hardDiscount()
	{
		return false;
	}

	@ConfigItem(
		keyName = "rewardHerbBox",
		name = "Shop: Herb box",
		description = "Track herb box (9,500 points each)",
		section = REWARDS,
		position = 2
	)
	default boolean rewardHerbBox()
	{
		return false;
	}

	@Range(min = 1, max = 100000)
	@ConfigItem(
		keyName = "rewardHerbBoxQuantity",
		name = "Shop: Herb box amount",
		description = "How many herb box to save for",
		section = REWARDS,
		position = 3
	)
	default int rewardHerbBoxQuantity()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "rewardRedirection",
		name = "Shop: Scroll of redirection",
		description = "Track scroll of redirection (775 points each)",
		section = REWARDS,
		position = 4
	)
	default boolean rewardRedirection()
	{
		return false;
	}

	@Range(min = 1, max = 100000)
	@ConfigItem(
		keyName = "rewardRedirectionQuantity",
		name = "Shop: Scroll of redirection amount",
		description = "How many scroll of redirection to save for",
		section = REWARDS,
		position = 5
	)
	default int rewardRedirectionQuantity()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "rewardSuperRanging",
		name = "Shop: Super ranging dose",
		description = "Track super ranging dose (250 points each)",
		section = REWARDS,
		position = 6
	)
	default boolean rewardSuperRanging()
	{
		return false;
	}

	@Range(min = 1, max = 100000)
	@ConfigItem(
		keyName = "rewardSuperRangingQuantity",
		name = "Shop: Super ranging dose amount",
		description = "How many super ranging dose to save for",
		section = REWARDS,
		position = 7
	)
	default int rewardSuperRangingQuantity()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "rewardSuperMagic",
		name = "Shop: Super magic dose",
		description = "Track super magic dose (250 points each)",
		section = REWARDS,
		position = 8
	)
	default boolean rewardSuperMagic()
	{
		return false;
	}

	@Range(min = 1, max = 100000)
	@ConfigItem(
		keyName = "rewardSuperMagicQuantity",
		name = "Shop: Super magic dose amount",
		description = "How many super magic dose to save for",
		section = REWARDS,
		position = 9
	)
	default int rewardSuperMagicQuantity()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "rewardOverload",
		name = "Shop: Overload dose",
		description = "Track overload dose (1,500 points each)",
		section = REWARDS,
		position = 10
	)
	default boolean rewardOverload()
	{
		return false;
	}

	@Range(min = 1, max = 100000)
	@ConfigItem(
		keyName = "rewardOverloadQuantity",
		name = "Shop: Overload dose amount",
		description = "How many overload dose to save for",
		section = REWARDS,
		position = 11
	)
	default int rewardOverloadQuantity()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "rewardAbsorption",
		name = "Shop: Absorption dose",
		description = "Track absorption dose (1,000 points each)",
		section = REWARDS,
		position = 12
	)
	default boolean rewardAbsorption()
	{
		return false;
	}

	@Range(min = 1, max = 100000)
	@ConfigItem(
		keyName = "rewardAbsorptionQuantity",
		name = "Shop: Absorption dose amount",
		description = "How many absorption dose to save for",
		section = REWARDS,
		position = 13
	)
	default int rewardAbsorptionQuantity()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "imbueBlackMask",
		name = "Imbue: Black mask (i)",
		description = "Track imbuing the black mask (1,250,000 points)",
		section = REWARDS,
		position = 14
	)
	default boolean imbueBlackMask()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSlayerHelm",
		name = "Imbue: Slayer helmet (i)",
		description = "Track imbuing the slayer helmet (1,250,000 points)",
		section = REWARDS,
		position = 15
	)
	default boolean imbueSlayerHelm()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSalve",
		name = "Imbue: Salve amulet (i)",
		description = "Track imbuing the salve amulet (800,000 points)",
		section = REWARDS,
		position = 16
	)
	default boolean imbueSalve()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSalveE",
		name = "Imbue: Salve amulet (ei)",
		description = "Track imbuing the elite (e) salve amulet (800,000 points)",
		section = REWARDS,
		position = 17
	)
	default boolean imbueSalveE()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSuffering",
		name = "Imbue: Ring of suffering (i)",
		description = "Track imbuing the ring of suffering (725,000 points)",
		section = REWARDS,
		position = 18
	)
	default boolean imbueSuffering()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueGods",
		name = "Imbue: Ring of the gods (i)",
		description = "Track imbuing the ring of the gods (650,000 points)",
		section = REWARDS,
		position = 19
	)
	default boolean imbueGods()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueBerserker",
		name = "Imbue: Berserker ring (i)",
		description = "Track imbuing the berserker ring (650,000 points)",
		section = REWARDS,
		position = 20
	)
	default boolean imbueBerserker()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueWarrior",
		name = "Imbue: Warrior ring (i)",
		description = "Track imbuing the warrior ring (650,000 points)",
		section = REWARDS,
		position = 21
	)
	default boolean imbueWarrior()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueArchers",
		name = "Imbue: Archers ring (i)",
		description = "Track imbuing the archers ring (650,000 points)",
		section = REWARDS,
		position = 22
	)
	default boolean imbueArchers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSeers",
		name = "Imbue: Seers ring (i)",
		description = "Track imbuing the seers ring (650,000 points)",
		section = REWARDS,
		position = 23
	)
	default boolean imbueSeers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueTyrannical",
		name = "Imbue: Tyrannical ring (i)",
		description = "Track imbuing the tyrannical ring (650,000 points)",
		section = REWARDS,
		position = 24
	)
	default boolean imbueTyrannical()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueTreasonous",
		name = "Imbue: Treasonous ring (i)",
		description = "Track imbuing the treasonous ring (650,000 points)",
		section = REWARDS,
		position = 25
	)
	default boolean imbueTreasonous()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueGranite",
		name = "Imbue: Granite ring (i)",
		description = "Track imbuing the granite ring (500,000 points)",
		section = REWARDS,
		position = 26
	)
	default boolean imbueGranite()
	{
		return false;
	}
}
