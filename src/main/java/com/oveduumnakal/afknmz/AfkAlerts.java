/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

/** Sends the AFK safety alerts: low HP, a faded ranged or magic boost, and the dream ending. */
@Singleton
public class AfkAlerts
{
	private final Client client;
	private final EventBus eventBus;
	private final Notifier notifier;
	private final AfkNmzConfig config;
	private final EffectState effectState;

	private final AlertLogic.EdgeTrigger lowHp = new AlertLogic.EdgeTrigger();
	private final AlertLogic.BoostFade rangedBoost = new AlertLogic.BoostFade();
	private final AlertLogic.BoostFade magicBoost = new AlertLogic.BoostFade();

	private boolean wasInDream;

	@Inject
	AfkAlerts(Client client, EventBus eventBus, Notifier notifier, AfkNmzConfig config, EffectState effectState)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.notifier = notifier;
		this.config = config;
		this.effectState = effectState;
	}

	/** Starts listening for events. */
	public void start()
	{
		eventBus.register(this);
	}

	/** Stops listening and forgets all state. */
	public void stop()
	{
		eventBus.unregister(this);
		reset();
	}

	@Subscribe
	void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
			reset();
	}

	@Subscribe
	void onGameTick(GameTick event)
	{
		boolean inDream = NmzArea.inDream(client);
		if (wasInDream && !inDream)
		{
			notifier.notify(config.dreamEnded(), "Your Nightmare Zone dream has ended.");
			resetInDreamState();
		}

		wasInDream = inDream;
		if (!inDream)
			return;

		int hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
		if (lowHp.update(AlertLogic.lowHp(hp, config.lowHpThreshold(), effectState.isOverloadActive())))
			notifier.notify(config.lowHpAlert(), "Your HP is low: " + hp);

		if (rangedBoost.update(boost(Skill.RANGED)))
			notifier.notify(config.rangedBoostFaded(), "Your ranged boost has worn off.");

		if (magicBoost.update(boost(Skill.MAGIC)))
			notifier.notify(config.magicBoostFaded(), "Your magic boost has worn off.");
	}

	private int boost(Skill skill)
	{
		return client.getBoostedSkillLevel(skill) - client.getRealSkillLevel(skill);
	}

	private void reset()
	{
		wasInDream = false;
		resetInDreamState();
	}

	private void resetInDreamState()
	{
		lowHp.reset();
		rangedBoost.reset();
		magicBoost.reset();
	}
}
