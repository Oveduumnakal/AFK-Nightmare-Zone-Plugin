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
import net.runelite.api.WorldView;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.EventBus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Tests for {@link EffectTracker}. */
public class EffectTrackerTest
{
	private Client client;
	private WorldView worldView;
	private Notifier notifier;
	private EffectState state;
	private EffectTracker tracker;

	@Before
	public void setUp()
	{
		client = mock(Client.class);
		worldView = mock(WorldView.class);
		when(client.getTopLevelWorldView()).thenReturn(worldView);
		notifier = mock(Notifier.class);
		state = new EffectState();
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(worldView.getMapRegions()).thenReturn(new int[] {9033});
		tracker = new EffectTracker(client, mock(EventBus.class), notifier, new AfkNmzConfig()
		{
		}, state);
	}

	private void drink()
	{
		tracker.onChatMessage(new ChatMessage(null, ChatMessageType.GAMEMESSAGE, "", EffectTracker.DRINK_MESSAGE,
			null, 0));
	}

	@Test
	public void drinkInDreamActivatesOverload()
	{
		tracker.onGameTick(new GameTick());
		drink();
		tracker.onGameTick(new GameTick());

		assertTrue(state.isOverloadActive());
		assertTrue(state.isOverloadSettling());
		assertEquals(300, state.getOverloadSecondsRemaining());
	}

	@Test
	public void drinkOutsideDreamIgnored()
	{
		when(worldView.getMapRegions()).thenReturn(new int[] {1234});
		drink();
		tracker.onGameTick(new GameTick());

		assertFalse(state.isOverloadActive());
	}

	@Test
	public void absorptionNotifiesAndPublishes()
	{
		when(client.getVarbitValue(VarbitID.NZONE_ABSORB_POTION_EFFECTS)).thenReturn(150);
		tracker.onGameTick(new GameTick());

		assertEquals(150, state.getAbsorptionPoints());
		verify(notifier).notify("Absorption points below: 200");

		when(client.getVarbitValue(VarbitID.NZONE_ABSORB_POTION_EFFECTS)).thenReturn(0);
		tracker.onGameTick(new GameTick());

		verify(notifier).notify("Your absorption points have run out!");
	}

	@Test
	public void leavingDreamResetsState()
	{
		when(client.getVarbitValue(VarbitID.NZONE_ABSORB_POTION_EFFECTS)).thenReturn(500);
		tracker.onGameTick(new GameTick());
		when(worldView.getMapRegions()).thenReturn(new int[] {1234});
		tracker.onGameTick(new GameTick());

		assertEquals(0, state.getAbsorptionPoints());
		verify(notifier, never()).notify("Your absorption points have run out!");
	}

	@Test
	public void noNotificationsWhenIdle()
	{
		tracker.onGameTick(new GameTick());

		verify(notifier, never()).notify(anyString());
	}
}
