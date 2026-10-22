/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

/**
 * Consumes Locator orb and Dwarven rock cake clicks inside the Nightmare Zone dream when {@link HpGuard} says they
 * are unsafe, and tells the player why.
 */
@Singleton
public class ClickGuard
{
	private static final long MESSAGE_INTERVAL_MS = 1000;

	private final Client client;
	private final EventBus eventBus;
	private final EffectState effectState;
	private final AfkNmzConfig config;

	private long lastMessageMs;

	/** Creates the guard; call {@link #start()} to begin listening. */
	@Inject
	public ClickGuard(Client client, EventBus eventBus, EffectState effectState, AfkNmzConfig config)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.effectState = effectState;
		this.config = config;
	}

	/** Starts listening for clicks. */
	public void start()
	{
		eventBus.register(this);
	}

	/** Stops listening for clicks. */
	public void stop()
	{
		eventBus.unregister(this);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!NmzArea.inDream(client))
			return;

		String target = event.getMenuTarget();
		boolean orb = HpGuard.isOrb(event.getItemId()) || HpGuard.isOrbName(target);
		boolean cake = HpGuard.isCake(event.getItemId()) || HpGuard.isCakeName(target);
		HpGuard.Kind kind = HpGuard.kindOf(orb, cake, event.getMenuOption());
		if (kind == null)
			return;

		if (HpGuard.blockedByOverloadRule(config.requireOverload(), effectState.isOverloadActive(),
				effectState.isOverloadSettling()))
		{
			event.consume();
			warn(effectState.isOverloadActive()
				? "AFK NMZ: blocked - overload is still damaging you."
				: "AFK NMZ: blocked - overload not active.");
			return;
		}

		int hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
		if (HpGuard.blockedByMinHp(config.blockBelowMinHp(), effectState.isOverloadActive(), kind, hp,
				config.minHp()))
		{
			event.consume();
			warn("AFK NMZ: blocked - would drop below " + config.minHp() + " HP.");
		}
	}

	private void warn(String message)
	{
		long now = System.currentTimeMillis();
		if (now - lastMessageMs < MESSAGE_INTERVAL_MS)
			return;

		lastMessageMs = now;
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
	}
}
