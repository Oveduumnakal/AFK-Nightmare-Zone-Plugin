/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.EnumMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests the reward goal arithmetic and the reward table. */
public class RewardGoalTest
{
	private static Map<NmzReward, Integer> wanted(Object... pairs)
	{
		Map<NmzReward, Integer> map = new EnumMap<>(NmzReward.class);
		for (int i = 0; i < pairs.length; i += 2)
			map.put((NmzReward) pairs[i], (Integer) pairs[i + 1]);

		return map;
	}

	/** An empty selection is never affordable and has no progress. */
	@Test
	public void emptyGoalIsNeverAffordable()
	{
		RewardGoal goal = RewardGoal.calculate(wanted(), 5_000_000, false);

		assertTrue(goal.isEmpty());
		assertFalse(goal.isAffordable());
		assertEquals(0.0, goal.getProgress(), 0.0);
	}

	/** Costs of several ticked rewards are summed with their quantities. */
	@Test
	public void sumsCostTimesQuantity()
	{
		RewardGoal goal = RewardGoal.calculate(
			wanted(NmzReward.HERB_BOX, 3, NmzReward.BERSERKER_RING_I, 1), 0, false);

		assertEquals(3 * 9500 + 650_000, goal.getTotalCost());
	}

	/** Progress is the fraction of the combined cost, capped at 1. */
	@Test
	public void progressIsFractionCappedAtOne()
	{
		Map<NmzReward, Integer> one = wanted(NmzReward.GRANITE_RING_I, 1);

		assertEquals(0.5, RewardGoal.calculate(one, 250_000, false).getProgress(), 1e-9);
		assertEquals(1.0, RewardGoal.calculate(one, 9_000_000, false).getProgress(), 0.0);
		assertEquals(250_000, RewardGoal.calculate(one, 250_000, false).getPointsRemaining());
		assertEquals(0, RewardGoal.calculate(one, 9_000_000, false).getPointsRemaining());
	}

	/** Affordable exactly at the combined cost, not one point before. */
	@Test
	public void affordableAtExactCost()
	{
		Map<NmzReward, Integer> one = wanted(NmzReward.OVERLOAD_DOSE, 2);

		assertFalse(RewardGoal.calculate(one, 2999, false).isAffordable());
		assertTrue(RewardGoal.calculate(one, 3000, false).isAffordable());
	}

	/** The Hard Combat Achievements discount halves imbues but not shop items. */
	@Test
	public void hardDiscountHalvesImbuesOnly()
	{
		RewardGoal goal = RewardGoal.calculate(
			wanted(NmzReward.SLAYER_HELM_I, 1, NmzReward.HERB_BOX, 1), 0, true);

		assertEquals(625_000 + 9500, goal.getTotalCost());
	}

	/** Items affordable is capped at the wanted quantity. */
	@Test
	public void itemsAffordableIsCapped()
	{
		RewardGoal goal = RewardGoal.calculate(wanted(NmzReward.HERB_BOX, 4), 30_000, false);
		RewardGoal.Entry entry = goal.getEntries().get(0);

		assertEquals(3, goal.getItemsAffordable(entry));
		RewardGoal rich = RewardGoal.calculate(wanted(NmzReward.HERB_BOX, 4), 1_000_000, false);
		assertEquals(4, rich.getItemsAffordable(entry));
	}

	/** Large quantities do not overflow an int. */
	@Test
	public void largeTotalsUseLong()
	{
		RewardGoal goal = RewardGoal.calculate(wanted(NmzReward.OVERLOAD_DOSE, 100_000), 0, false);

		assertEquals(150_000_000L, goal.getTotalCost());
	}

	/** Every reward has a positive cost, an item id and a name; only reward chest items repeat. */
	@Test
	public void rewardTableIsSane()
	{
		for (NmzReward reward : NmzReward.values())
		{
			assertTrue(reward.getCost() > 0);
			assertTrue(reward.getItemId() > 0);
			assertFalse(reward.getDisplayName().isEmpty());
			assertEquals(reward.getCategory() == NmzReward.Category.REWARD_CHEST, reward.isRepeatable());
		}
	}

	/** With the default config nothing is ticked. */
	@Test
	public void defaultConfigSelectsNothing()
	{
		AfkNmzConfig config = new AfkNmzConfig()
		{
		};

		for (NmzReward reward : NmzReward.values())
		{
			assertFalse(reward.isSelected(config));
			assertTrue(reward.getQuantity(config) >= 1);
		}
	}
}
