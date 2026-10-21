/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Pure goal arithmetic for the ticked NMZ rewards: the combined cost, how far the current points get toward it and how
 * many of each reward are affordable. Holds no client state so it can be unit tested directly.
 */
public final class RewardGoal
{
	/** One ticked reward with the quantity wanted and the unit cost it was calculated with. */
	public static final class Entry
	{
		private final NmzReward reward;
		private final int quantity;
		private final int unitCost;

		Entry(NmzReward reward, int quantity, int unitCost)
		{
			this.reward = reward;
			this.quantity = quantity;
			this.unitCost = unitCost;
		}

		/** Returns the reward. */
		public NmzReward getReward()
		{
			return reward;
		}

		/** Returns how many units are wanted. */
		public int getQuantity()
		{
			return quantity;
		}

		/** Returns the points for one unit. */
		public int getUnitCost()
		{
			return unitCost;
		}

		/** Returns the points for all wanted units. */
		public long getTotalCost()
		{
			return (long) unitCost * quantity;
		}
	}

	private final List<Entry> entries;
	private final long points;
	private final long totalCost;

	private RewardGoal(List<Entry> entries, long points)
	{
		this.entries = Collections.unmodifiableList(entries);
		this.points = Math.max(0, points);

		long sum = 0;
		for (Entry entry : entries)
			sum += entry.getTotalCost();

		this.totalCost = sum;
	}

	/**
	 * Builds the goal for the wanted rewards.
	 *
	 * @param wanted reward to quantity wanted, in the order the overlay should list them
	 * @param points the player's current NMZ points
	 * @param hardDiscount whether imbues are half price
	 */
	public static RewardGoal calculate(Map<NmzReward, Integer> wanted, long points, boolean hardDiscount)
	{
		List<Entry> entries = new ArrayList<>();
		for (Map.Entry<NmzReward, Integer> want : wanted.entrySet())
		{
			int quantity = Math.max(1, want.getValue());
			entries.add(new Entry(want.getKey(), quantity, want.getKey().costFor(hardDiscount)));
		}

		return new RewardGoal(entries, points);
	}

	/** Returns the ticked rewards. */
	public List<Entry> getEntries()
	{
		return entries;
	}

	/** Returns whether nothing is ticked. */
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	/** Returns the current points the goal was calculated with. */
	public long getPoints()
	{
		return points;
	}

	/** Returns the combined cost of everything ticked. */
	public long getTotalCost()
	{
		return totalCost;
	}

	/** Returns progress toward the combined cost from 0 to 1; an empty goal counts as 0. */
	public double getProgress()
	{
		if (totalCost <= 0)
			return 0;

		return Math.min(1.0, (double) points / totalCost);
	}

	/** Returns whether the points cover the whole combined cost of a non-empty goal. */
	public boolean isAffordable()
	{
		return !entries.isEmpty() && points >= totalCost;
	}

	/** Returns how many more points are needed for the combined goal; 0 once affordable. */
	public long getPointsRemaining()
	{
		return Math.max(0, totalCost - points);
	}

	/** Returns how many units of the entry the current points could buy on their own, capped at the quantity wanted. */
	public int getItemsAffordable(Entry entry)
	{
		if (entry.getUnitCost() <= 0)
			return entry.getQuantity();

		return (int) Math.min(entry.getQuantity(), points / entry.getUnitCost());
	}

	/** Returns progress of the points toward one entry's own cost from 0 to 1. */
	public double getProgress(Entry entry)
	{
		if (entry.getTotalCost() <= 0)
			return 0;

		return Math.min(1.0, (double) points / entry.getTotalCost());
	}
}
