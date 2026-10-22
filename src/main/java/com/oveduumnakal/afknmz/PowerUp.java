/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import net.runelite.client.config.Notification;

/** The power-ups that can spawn in a dream, recognised from the game message announcing them. */
enum PowerUp
{
	/** Doubles the points earned for a while. */
	POWER_SURGE("Power surge", "A power surge has spawned!"),
	/** Makes every monster damaged so they can be finished quickly. */
	RECURRENT_DAMAGE("Recurrent damage", "A recurrent damage power-up has spawned!"),
	/** Damages every monster that attacks you. */
	ZAPPER("Zapper", "A zapper power-up has spawned!"),
	/** Heals the boss through its special attack. */
	ULTIMATE_FORCE("Ultimate force", "An ultimate force power-up has spawned!");

	/** Prefix of the game message announcing a spawned power-up. */
	static final String SPAWN_PREFIX = "A power-up has spawned:";

	private final String gameName;
	private final String message;

	PowerUp(String gameName, String message)
	{
		this.gameName = gameName;
		this.message = message;
	}

	/** The notification text for this power-up. */
	String getMessage()
	{
		return message;
	}

	/** The notification setting for this power-up. */
	Notification getNotification(AfkNmzConfig config)
	{
		switch (this)
		{
			case POWER_SURGE:
				return config.powerSurge();
			case RECURRENT_DAMAGE:
				return config.recurrentDamage();
			case ZAPPER:
				return config.zapper();
			default:
				return config.ultimateForce();
		}
	}

	/**
	 * Finds the power-up named by a game message.
	 *
	 * @param message the chat message with colour tags already removed
	 * @return the power-up, or null if the message is not a power-up spawn
	 */
	static PowerUp fromMessage(String message)
	{
		if (message == null || !message.contains(SPAWN_PREFIX))
			return null;

		for (PowerUp powerUp : values())
		{
			if (message.contains(powerUp.gameName))
				return powerUp;
		}

		return null;
	}
}
