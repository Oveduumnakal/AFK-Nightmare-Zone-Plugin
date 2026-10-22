/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import org.junit.Before;
import org.junit.Test;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.WorldView;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.Notifier;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.EventBus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Tests for {@link AfkAlerts}. */
public class AfkAlertsTest
{
	private static final int[] DREAM = {9033};
	private static final int[] ELSEWHERE = {1234};

	private Client client;
	private WorldView worldView;
	private Notifier notifier;
	private EventBus eventBus;
	private EffectState state;
	private ItemContainer inventory;
	private AfkAlerts alerts;

	@Before
	public void setUp()
	{
		client = mock(Client.class);
		worldView = mock(WorldView.class);
		notifier = mock(Notifier.class);
		eventBus = mock(EventBus.class);
		inventory = mock(ItemContainer.class);
		state = new EffectState();
		when(client.getTopLevelWorldView()).thenReturn(worldView);
		when(worldView.getMapRegions()).thenReturn(DREAM);
		when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(99);
		when(client.getItemContainer(InventoryID.INV)).thenReturn(inventory);
		alerts = new AfkAlerts(client, eventBus, notifier, new AfkNmzConfig()
		{
		}, state);
	}

	private void message(String text)
	{
		alerts.onChatMessage(new ChatMessage(null, ChatMessageType.GAMEMESSAGE, "", text, null, 0));
	}

	private void tick()
	{
		alerts.onGameTick(new GameTick());
	}

	private void verifyNotified(String text, int times)
	{
		verify(notifier, times(times)).notify(any(Notification.class), eq(text));
	}

	@Test
	public void startAndStopRegisterWithTheEventBus()
	{
		alerts.start();
		verify(eventBus).register(alerts);

		alerts.stop();
		verify(eventBus).unregister(alerts);
	}

	@Test
	public void lowHpAlertsOnceWithoutOverload()
	{
		when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(10);
		tick();
		tick();

		verifyNotified("Your HP is low: 10", 1);
	}

	@Test
	public void lowHpIsQuietWhileOverloadIsActive()
	{
		state.setOverloadActive(true);
		when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(1);
		tick();

		verify(notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	public void rangedBoostFadeAlertsOnce()
	{
		when(client.getRealSkillLevel(Skill.RANGED)).thenReturn(99);
		when(client.getBoostedSkillLevel(Skill.RANGED)).thenReturn(112);
		tick();
		when(client.getBoostedSkillLevel(Skill.RANGED)).thenReturn(99);
		tick();
		tick();

		verifyNotified("Your ranged boost has worn off.", 1);
	}

	@Test
	public void magicBoostFadeAlertsOnce()
	{
		when(client.getRealSkillLevel(Skill.MAGIC)).thenReturn(99);
		when(client.getBoostedSkillLevel(Skill.MAGIC)).thenReturn(110);
		tick();
		when(client.getBoostedSkillLevel(Skill.MAGIC)).thenReturn(99);
		tick();

		verifyNotified("Your magic boost has worn off.", 1);
	}

	@Test
	public void runningOutOfOverloadAlertsAfterHavingSome()
	{
		when(inventory.count(ItemID.NZONE4DOSEOVERLOADPOTION)).thenReturn(1);
		tick();
		verifyNotified("You have run out of overload doses.", 0);

		when(inventory.count(ItemID.NZONE4DOSEOVERLOADPOTION)).thenReturn(0);
		tick();
		tick();
		verifyNotified("You have run out of overload doses.", 1);
	}

	@Test
	public void runningOutOfAbsorptionAlertsAfterHavingSome()
	{
		when(inventory.count(ItemID.NZONE2DOSEABSORPTIONPOTION)).thenReturn(3);
		tick();
		when(inventory.count(ItemID.NZONE2DOSEABSORPTIONPOTION)).thenReturn(0);
		tick();

		verifyNotified("You have run out of absorption doses.", 1);
		verifyNotified("You have run out of overload doses.", 0);
	}

	@Test
	public void startingWithoutPotionsStaysQuiet()
	{
		tick();
		tick();

		verify(notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	public void missingInventoryIsTolerated()
	{
		when(client.getItemContainer(InventoryID.INV)).thenReturn(null);
		tick();

		verify(notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	public void eachPowerUpSpawnAlerts()
	{
		message("A power-up has spawned: Power surge");
		message("A power-up has spawned: Recurrent damage");
		message("A power-up has spawned: Zapper");
		message("A power-up has spawned: Ultimate force");

		verifyNotified("A power surge has spawned!", 1);
		verifyNotified("A recurrent damage power-up has spawned!", 1);
		verifyNotified("A zapper power-up has spawned!", 1);
		verifyNotified("An ultimate force power-up has spawned!", 1);
	}

	@Test
	public void powerUpMessageWithColourTagsStillAlerts()
	{
		message("<col=ef1020>A power-up has spawned: Zapper</col>");

		verifyNotified("A zapper power-up has spawned!", 1);
	}

	@Test
	public void powerUpOutsideTheDreamIgnored()
	{
		when(worldView.getMapRegions()).thenReturn(ELSEWHERE);
		message("A power-up has spawned: Zapper");

		verify(notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	public void unrelatedChatTypeIgnored()
	{
		alerts.onChatMessage(new ChatMessage(null, ChatMessageType.PUBLICCHAT, "", "A power-up has spawned: Zapper",
			null, 0));

		verify(notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	public void unrelatedGameMessageIgnored()
	{
		message("You drink some of your overload potion.");

		verify(notifier, never()).notify(any(Notification.class), anyString());
	}

	@Test
	public void leavingTheDreamAlertsOnce()
	{
		tick();
		when(worldView.getMapRegions()).thenReturn(ELSEWHERE);
		tick();
		tick();

		verifyNotified("Your Nightmare Zone dream has ended.", 1);
	}

	@Test
	public void leavingTheDreamDoesNotCountPotionsAsRunOut()
	{
		when(inventory.count(ItemID.NZONE4DOSEOVERLOADPOTION)).thenReturn(2);
		tick();
		when(worldView.getMapRegions()).thenReturn(ELSEWHERE);
		when(inventory.count(ItemID.NZONE4DOSEOVERLOADPOTION)).thenReturn(0);
		tick();

		verifyNotified("You have run out of overload doses.", 0);
	}

	@Test
	public void loggingOutForgetsTheDream()
	{
		tick();
		alerts.onGameStateChanged(new GameStateChanged());
		GameStateChanged logout = new GameStateChanged();
		logout.setGameState(GameState.LOGIN_SCREEN);
		alerts.onGameStateChanged(logout);
		when(worldView.getMapRegions()).thenReturn(ELSEWHERE);
		tick();

		verifyNotified("Your Nightmare Zone dream has ended.", 0);
	}
}
