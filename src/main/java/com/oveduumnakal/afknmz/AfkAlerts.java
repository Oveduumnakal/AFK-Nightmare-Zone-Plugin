/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

/**
 * Sends the AFK safety alerts: low HP, a faded ranged or magic boost, running out of overload or absorption doses,
 * spawned power-ups, and the dream ending.
 */
@Singleton
public class AfkAlerts
{
	private final Client client;
	private final EventBus eventBus;
	private final Notifier notifier;
	private final AfkNmzConfig config;
	private final EffectState effectState;

	private final AlertLogic.EdgeTrigger lowHp = new AlertLogic.EdgeTrigger();
	private final AlertLogic.DropToZero rangedBoost = new AlertLogic.DropToZero();
	private final AlertLogic.DropToZero magicBoost = new AlertLogic.DropToZero();
	private final AlertLogic.DropToZero overloadSupply = new AlertLogic.DropToZero();
	private final AlertLogic.DropToZero absorptionSupply = new AlertLogic.DropToZero();

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
	void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
			return;

		if (!NmzArea.inDream(client))
			return;

		PowerUp powerUp = PowerUp.fromMessage(Text.removeTags(event.getMessage()));
		if (powerUp != null)
			notifier.notify(powerUp.getNotification(config), powerUp.getMessage());
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

		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (overloadSupply.update(NmzSupply.OVERLOAD.count(inventory)))
			notifier.notify(config.outOfOverload(), "You have run out of overload doses.");

		if (absorptionSupply.update(NmzSupply.ABSORPTION.count(inventory)))
			notifier.notify(config.outOfAbsorption(), "You have run out of absorption doses.");
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
		overloadSupply.reset();
		absorptionSupply.reset();
	}
}
