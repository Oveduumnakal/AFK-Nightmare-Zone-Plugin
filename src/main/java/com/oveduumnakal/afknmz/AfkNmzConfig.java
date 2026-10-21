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
import net.runelite.client.config.Notification;
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
	default Notification overloadWarning()
	{
		return Notification.ON;
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
	default Notification overloadExpired()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "absorptionWarning",
		name = "Absorption low",
		description = "Notify when your absorption points drop to or below the threshold",
		section = NOTIFICATIONS,
		position = 3
	)
	default Notification absorptionWarning()
	{
		return Notification.ON;
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
	default Notification absorptionExpired()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "lowHpAlert",
		name = "Low HP",
		description = "Notify when your HP is at or below the threshold and no overload is active",
		section = NOTIFICATIONS,
		position = 6
	)
	default Notification lowHpAlert()
	{
		return Notification.ON;
	}

	@Range(min = 2, max = 99)
	@ConfigItem(
		keyName = "lowHpThreshold",
		name = "Low HP threshold",
		description = "Hitpoints at or below which to notify; ignored while an overload is active",
		section = NOTIFICATIONS,
		position = 7
	)
	default int lowHpThreshold()
	{
		return 15;
	}

	@ConfigItem(
		keyName = "rangedBoostFaded",
		name = "Ranged boost faded",
		description = "Notify when a ranging boost has worn off",
		section = NOTIFICATIONS,
		position = 8
	)
	default Notification rangedBoostFaded()
	{
		return Notification.OFF;
	}

	@ConfigItem(
		keyName = "magicBoostFaded",
		name = "Magic boost faded",
		description = "Notify when a magic boost has worn off",
		section = NOTIFICATIONS,
		position = 9
	)
	default Notification magicBoostFaded()
	{
		return Notification.OFF;
	}

	@ConfigItem(
		keyName = "dreamEnded",
		name = "Dream ended",
		description = "Notify when you leave the dream, whether it finished or you died",
		section = NOTIFICATIONS,
		position = 10
	)
	default Notification dreamEnded()
	{
		return Notification.ON;
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
		keyName = "tintBarrels",
		name = "Tint barrels",
		description = "Shade each barrel: green ranging, blue magic, black overload, white absorption",
		section = BARRELS,
		position = 2
	)
	default boolean tintBarrels()
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

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "lowDoseThreshold",
		name = "Low dose threshold",
		description = "Show the dose count in the low dose colour at or below this many doses; 0 to turn off",
		section = BARRELS,
		position = 3
	)
	default int lowDoseThreshold()
	{
		return 3;
	}

	@ConfigItem(
		keyName = "lowDoseColor",
		name = "Low dose colour",
		description = "Colour of the dose count when a barrel is running low",
		section = BARRELS,
		position = 4
	)
	default Color lowDoseColor()
	{
		return Color.RED;
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
		name = "Rewards - Shop",
		description = "Track NMZ points toward reward shop purchases; set an amount above 0 to track an item",
		position = 4
	)
	String REWARDS_SHOP = "rewardsShop";

	@ConfigSection(
		name = "Rewards - Imbue",
		description = "Track NMZ points toward imbuing items at the reward shop",
		position = 5
	)
	String REWARDS_IMBUE = "rewardsImbue";

	@ConfigItem(
		keyName = "rewardNotify",
		name = "Notify when affordable",
		description = "Notify once when your points cover everything ticked below",
		section = REWARDS_SHOP,
		position = 0
	)
	default Notification rewardNotify()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "hardDiscount",
		name = "Hard CA reward claimed",
		description = "Imbues cost half the points once the Hard Combat Achievements reward is claimed",
		section = REWARDS_IMBUE,
		position = 1
	)
	default boolean hardDiscount()
	{
		return false;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardHerbBoxQuantity",
		name = "Herb Boxes",
		description = "How many herb boxes to save for (9,500 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 3
	)
	default int rewardHerbBoxQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardRedirectionQuantity",
		name = "Scrolls of Redirection",
		description = "How many scrolls of redirection to save for (775 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 5
	)
	default int rewardRedirectionQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardSuperRangingQuantity",
		name = "Super Ranging Doses",
		description = "How many super ranging doses to save for (250 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 7
	)
	default int rewardSuperRangingQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardSuperMagicQuantity",
		name = "Super Magic Doses",
		description = "How many super magic doses to save for (250 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 9
	)
	default int rewardSuperMagicQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardOverloadQuantity",
		name = "Overload Doses",
		description = "How many overload doses to save for (1,500 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 11
	)
	default int rewardOverloadQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardAbsorptionQuantity",
		name = "Absorption Doses",
		description = "How many absorption doses to save for (1,000 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 13
	)
	default int rewardAbsorptionQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardFlaxQuantity",
		name = "Flax",
		description = "How many flax to save for (75 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 14
	)
	default int rewardFlaxQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardSandQuantity",
		name = "Buckets of Sand",
		description = "How many buckets of sand to save for (200 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 15
	)
	default int rewardSandQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardSeaweedQuantity",
		name = "Seaweed",
		description = "How many seaweed to save for (200 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 16
	)
	default int rewardSeaweedQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardCompostPotionQuantity",
		name = "Compost Potions",
		description = "How many compost potions to save for (5,000 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 17
	)
	default int rewardCompostPotionQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardAirRuneQuantity",
		name = "Air Runes",
		description = "How many air runes to save for (25 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 18
	)
	default int rewardAirRuneQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardWaterRuneQuantity",
		name = "Water Runes",
		description = "How many water runes to save for (25 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 19
	)
	default int rewardWaterRuneQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardEarthRuneQuantity",
		name = "Earth Runes",
		description = "How many earth runes to save for (25 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 20
	)
	default int rewardEarthRuneQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardFireRuneQuantity",
		name = "Fire Runes",
		description = "How many fire runes to save for (25 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 21
	)
	default int rewardFireRuneQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardRuneEssenceQuantity",
		name = "Rune Essence",
		description = "How many rune essence to save for (60 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 22
	)
	default int rewardRuneEssenceQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardPureEssenceQuantity",
		name = "Pure Essence",
		description = "How many pure essence to save for (70 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 23
	)
	default int rewardPureEssenceQuantity()
	{
		return 0;
	}

	@Range(min = 0, max = 100000)
	@ConfigItem(
		keyName = "rewardVialOfWaterQuantity",
		name = "Vials of Water",
		description = "How many vials of water to save for (145 points each); 0 to ignore",
		section = REWARDS_SHOP,
		position = 24
	)
	default int rewardVialOfWaterQuantity()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "imbueBlackMask",
		name = "Black mask (i)",
		description = "Track imbuing the black mask (1,250,000 points)",
		section = REWARDS_IMBUE,
		position = 25
	)
	default boolean imbueBlackMask()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSlayerHelm",
		name = "Slayer helmet (i)",
		description = "Track imbuing the slayer helmet (1,250,000 points)",
		section = REWARDS_IMBUE,
		position = 26
	)
	default boolean imbueSlayerHelm()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSalve",
		name = "Salve amulet (i)",
		description = "Track imbuing the salve amulet (800,000 points)",
		section = REWARDS_IMBUE,
		position = 27
	)
	default boolean imbueSalve()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSalveE",
		name = "Salve amulet (ei)",
		description = "Track imbuing the elite (e) salve amulet (800,000 points)",
		section = REWARDS_IMBUE,
		position = 28
	)
	default boolean imbueSalveE()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSuffering",
		name = "Ring of suffering (i)",
		description = "Track imbuing the ring of suffering (725,000 points)",
		section = REWARDS_IMBUE,
		position = 29
	)
	default boolean imbueSuffering()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueGods",
		name = "Ring of the gods (i)",
		description = "Track imbuing the ring of the gods (650,000 points)",
		section = REWARDS_IMBUE,
		position = 30
	)
	default boolean imbueGods()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueBerserker",
		name = "Berserker ring (i)",
		description = "Track imbuing the berserker ring (650,000 points)",
		section = REWARDS_IMBUE,
		position = 31
	)
	default boolean imbueBerserker()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueWarrior",
		name = "Warrior ring (i)",
		description = "Track imbuing the warrior ring (650,000 points)",
		section = REWARDS_IMBUE,
		position = 32
	)
	default boolean imbueWarrior()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueArchers",
		name = "Archers ring (i)",
		description = "Track imbuing the archers ring (650,000 points)",
		section = REWARDS_IMBUE,
		position = 33
	)
	default boolean imbueArchers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueSeers",
		name = "Seers ring (i)",
		description = "Track imbuing the seers ring (650,000 points)",
		section = REWARDS_IMBUE,
		position = 34
	)
	default boolean imbueSeers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueTyrannical",
		name = "Tyrannical ring (i)",
		description = "Track imbuing the tyrannical ring (650,000 points)",
		section = REWARDS_IMBUE,
		position = 35
	)
	default boolean imbueTyrannical()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueTreasonous",
		name = "Treasonous ring (i)",
		description = "Track imbuing the treasonous ring (650,000 points)",
		section = REWARDS_IMBUE,
		position = 36
	)
	default boolean imbueTreasonous()
	{
		return false;
	}

	@ConfigItem(
		keyName = "imbueGranite",
		name = "Granite ring (i)",
		description = "Track imbuing the granite ring (500,000 points)",
		section = REWARDS_IMBUE,
		position = 37
	)
	default boolean imbueGranite()
	{
		return false;
	}
}
