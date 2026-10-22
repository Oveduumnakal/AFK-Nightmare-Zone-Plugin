/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Test;

import net.runelite.client.config.Notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Tests for {@link PowerUp}. */
public class PowerUpTest
{
	@Test
	public void parsesEachSpawnMessage()
	{
		assertEquals(PowerUp.POWER_SURGE, PowerUp.fromMessage("A power-up has spawned: Power surge"));
		assertEquals(PowerUp.RECURRENT_DAMAGE, PowerUp.fromMessage("A power-up has spawned: Recurrent damage"));
		assertEquals(PowerUp.ZAPPER, PowerUp.fromMessage("A power-up has spawned: Zapper"));
		assertEquals(PowerUp.ULTIMATE_FORCE, PowerUp.fromMessage("A power-up has spawned: Ultimate force"));
	}

	@Test
	public void ignoresOtherMessages()
	{
		assertNull(PowerUp.fromMessage(null));
		assertNull(PowerUp.fromMessage("You drink some of your overload potion."));
		assertNull(PowerUp.fromMessage("A power-up has spawned: Something else"));
		assertNull(PowerUp.fromMessage("The Zapper power-up has worn off."));
	}

	@Test
	public void everyPowerUpHasAMessage()
	{
		for (PowerUp powerUp : PowerUp.values())
			assertEquals(false, powerUp.getMessage().isEmpty());
	}

	@Test
	public void eachPowerUpReadsItsOwnSetting()
	{
		AfkNmzConfig config = mock(AfkNmzConfig.class);
		Notification surge = Notification.ON;
		Notification recurrent = Notification.OFF;
		when(config.powerSurge()).thenReturn(surge);
		when(config.recurrentDamage()).thenReturn(recurrent);
		when(config.zapper()).thenReturn(null);
		when(config.ultimateForce()).thenReturn(Notification.ON);

		assertSame(surge, PowerUp.POWER_SURGE.getNotification(config));
		assertSame(recurrent, PowerUp.RECURRENT_DAMAGE.getNotification(config));
		assertNull(PowerUp.ZAPPER.getNotification(config));
		assertSame(Notification.ON, PowerUp.ULTIMATE_FORCE.getNotification(config));
	}
}
