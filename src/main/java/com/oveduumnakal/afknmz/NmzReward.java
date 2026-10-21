/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.runelite.api.gameval.ItemID;

/**
 * Everything the Nightmare Zone reward shop sells that is worth saving points for: reward chest purchases and item
 * imbues. Point costs come from the OSRS wiki; imbue costs are the standard price (halved by the Hard Combat
 * Achievements reward, see {@link #costFor(boolean)}).
 */
public enum NmzReward
{
	HERB_BOX(
		"Herb box", ItemID.NZONE_HERBBOX, 9500,
		Category.REWARD_CHEST,
		config -> config.rewardHerbBoxQuantity() > 0,
		AfkNmzConfig::rewardHerbBoxQuantity),
	SCROLL_OF_REDIRECTION(
		"Scroll of redirection", ItemID.NZONE_REDIRECTION, 775,
		Category.REWARD_CHEST,
		config -> config.rewardRedirectionQuantity() > 0,
		AfkNmzConfig::rewardRedirectionQuantity),
	SUPER_RANGING_DOSE(
		"Super ranging (dose)", ItemID.NZONE1DOSE2RANGERSPOTION, 250,
		Category.REWARD_CHEST,
		config -> config.rewardSuperRangingQuantity() > 0,
		AfkNmzConfig::rewardSuperRangingQuantity),
	SUPER_MAGIC_DOSE(
		"Super magic (dose)", ItemID.NZONE1DOSE2MAGICPOTION, 250,
		Category.REWARD_CHEST,
		config -> config.rewardSuperMagicQuantity() > 0,
		AfkNmzConfig::rewardSuperMagicQuantity),
	OVERLOAD_DOSE(
		"Overload (dose)", ItemID.NZONE1DOSEOVERLOADPOTION, 1500,
		Category.REWARD_CHEST,
		config -> config.rewardOverloadQuantity() > 0,
		AfkNmzConfig::rewardOverloadQuantity),
	ABSORPTION_DOSE(
		"Absorption (dose)", ItemID.NZONE1DOSEABSORPTIONPOTION, 1000,
		Category.REWARD_CHEST,
		config -> config.rewardAbsorptionQuantity() > 0,
		AfkNmzConfig::rewardAbsorptionQuantity),
	FLAX(
		"Flax", ItemID.FLAX, 75,
		Category.REWARD_CHEST,
		config -> config.rewardFlaxQuantity() > 0,
		AfkNmzConfig::rewardFlaxQuantity),
	BUCKET_OF_SAND(
		"Bucket of sand", ItemID.BUCKET_SAND, 200,
		Category.REWARD_CHEST,
		config -> config.rewardSandQuantity() > 0,
		AfkNmzConfig::rewardSandQuantity),
	SEAWEED(
		"Seaweed", ItemID.SEAWEED, 200,
		Category.REWARD_CHEST,
		config -> config.rewardSeaweedQuantity() > 0,
		AfkNmzConfig::rewardSeaweedQuantity),
	COMPOST_POTION(
		"Compost potion(4)", ItemID.SUPERCOMPOST_POTION_4, 5000,
		Category.REWARD_CHEST,
		config -> config.rewardCompostPotionQuantity() > 0,
		AfkNmzConfig::rewardCompostPotionQuantity),
	AIR_RUNE(
		"Air rune", ItemID.AIRRUNE, 25,
		Category.REWARD_CHEST,
		config -> config.rewardAirRuneQuantity() > 0,
		AfkNmzConfig::rewardAirRuneQuantity),
	WATER_RUNE(
		"Water rune", ItemID.WATERRUNE, 25,
		Category.REWARD_CHEST,
		config -> config.rewardWaterRuneQuantity() > 0,
		AfkNmzConfig::rewardWaterRuneQuantity),
	EARTH_RUNE(
		"Earth rune", ItemID.EARTHRUNE, 25,
		Category.REWARD_CHEST,
		config -> config.rewardEarthRuneQuantity() > 0,
		AfkNmzConfig::rewardEarthRuneQuantity),
	FIRE_RUNE(
		"Fire rune", ItemID.FIRERUNE, 25,
		Category.REWARD_CHEST,
		config -> config.rewardFireRuneQuantity() > 0,
		AfkNmzConfig::rewardFireRuneQuantity),
	RUNE_ESSENCE(
		"Rune essence", ItemID.BLANKRUNE, 60,
		Category.REWARD_CHEST,
		config -> config.rewardRuneEssenceQuantity() > 0,
		AfkNmzConfig::rewardRuneEssenceQuantity),
	PURE_ESSENCE(
		"Pure essence", ItemID.BLANKRUNE_HIGH, 70,
		Category.REWARD_CHEST,
		config -> config.rewardPureEssenceQuantity() > 0,
		AfkNmzConfig::rewardPureEssenceQuantity),
	VIAL_OF_WATER(
		"Vial of water", ItemID.VIAL_WATER, 145,
		Category.REWARD_CHEST,
		config -> config.rewardVialOfWaterQuantity() > 0,
		AfkNmzConfig::rewardVialOfWaterQuantity),
	BLACK_MASK_I(
		"Black mask (i)", ItemID.NZONE_BLACK_MASK, 1250000,
		Category.IMBUE, AfkNmzConfig::imbueBlackMask, null),
	SLAYER_HELM_I(
		"Slayer helmet (i)", ItemID.SLAYER_HELM_I, 1250000,
		Category.IMBUE, AfkNmzConfig::imbueSlayerHelm, null),
	SALVE_AMULET_I(
		"Salve amulet (i)", ItemID.NZONE_SALVE_AMULET, 800000,
		Category.IMBUE, AfkNmzConfig::imbueSalve, null),
	SALVE_AMULET_EI(
		"Salve amulet (ei)", ItemID.NZONE_SALVE_AMULET_E, 800000,
		Category.IMBUE, AfkNmzConfig::imbueSalveE, null),
	RING_OF_SUFFERING_I(
		"Ring of suffering (i)", ItemID.NZONE_ZENYTE_RING_ENCHANTED, 725000,
		Category.IMBUE, AfkNmzConfig::imbueSuffering, null),
	RING_OF_THE_GODS_I(
		"Ring of the gods (i)", ItemID.NZONE_ROTG, 650000,
		Category.IMBUE, AfkNmzConfig::imbueGods, null),
	BERSERKER_RING_I(
		"Berserker ring (i)", ItemID.NZONE_BERZERKER_RING, 650000,
		Category.IMBUE, AfkNmzConfig::imbueBerserker, null),
	WARRIOR_RING_I(
		"Warrior ring (i)", ItemID.NZONE_WARRIOR_RING, 650000,
		Category.IMBUE, AfkNmzConfig::imbueWarrior, null),
	ARCHERS_RING_I(
		"Archers ring (i)", ItemID.NZONE_RANGER_RING, 650000,
		Category.IMBUE, AfkNmzConfig::imbueArchers, null),
	SEERS_RING_I(
		"Seers ring (i)", ItemID.NZONE_SEER_RING, 650000,
		Category.IMBUE, AfkNmzConfig::imbueSeers, null),
	TYRANNICAL_RING_I(
		"Tyrannical ring (i)", ItemID.NZONE_HEAVY_RING, 650000,
		Category.IMBUE, AfkNmzConfig::imbueTyrannical, null),
	TREASONOUS_RING_I(
		"Treasonous ring (i)", ItemID.NZONE_SHARP_RING, 650000,
		Category.IMBUE, AfkNmzConfig::imbueTreasonous, null),
	GRANITE_RING_I(
		"Granite ring (i)", ItemID.NZONE_GRANITE_RING, 500000,
		Category.IMBUE, AfkNmzConfig::imbueGranite, null);

	/** Where in the shop a reward is bought. */
	public enum Category
	{
		/** Bought as a stackable purchase from the rewards tabs; may be repeated. */
		REWARD_CHEST,
		/** An item imbued once at the shop. */
		IMBUE
	}

	private final String displayName;
	private final int itemId;
	private final int cost;
	private final Category category;
	private final Predicate<AfkNmzConfig> selected;
	private final ToIntFunction<AfkNmzConfig> quantity;

	NmzReward(
		String displayName,
		int itemId,
		int cost,
		Category category,
		Predicate<AfkNmzConfig> selected,
		ToIntFunction<AfkNmzConfig> quantity)
	{
		this.displayName = displayName;
		this.itemId = itemId;
		this.cost = cost;
		this.category = category;
		this.selected = selected;
		this.quantity = quantity;
	}

	/** Returns the name shown on the overlay. */
	public String getDisplayName()
	{
		return displayName;
	}

	/** Returns the id of the item received. */
	public int getItemId()
	{
		return itemId;
	}

	/** Returns the standard point cost of one unit. */
	public int getCost()
	{
		return cost;
	}

	/** Returns where the reward is bought. */
	public Category getCategory()
	{
		return category;
	}

	/** Returns whether the reward can be bought more than once. */
	public boolean isRepeatable()
	{
		return category == Category.REWARD_CHEST;
	}

	/**
	 * Returns the point cost of one unit.
	 *
	 * @param hardDiscount whether the Hard Combat Achievements reward (50% cheaper imbues) is claimed
	 */
	public int costFor(boolean hardDiscount)
	{
		if (hardDiscount && category == Category.IMBUE)
			return cost / 2;

		return cost;
	}

	/** Returns whether the player ticked this reward in the config. */
	public boolean isSelected(AfkNmzConfig config)
	{
		return selected.test(config);
	}

	/** Returns how many units the player wants; always at least 1, and 1 for non-repeatable rewards. */
	public int getQuantity(AfkNmzConfig config)
	{
		if (quantity == null)
			return 1;

		return Math.max(1, quantity.applyAsInt(config));
	}
}
