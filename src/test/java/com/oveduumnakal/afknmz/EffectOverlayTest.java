/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.runelite.api.Client;
import net.runelite.api.WorldView;
import net.runelite.client.game.ItemManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Tests for {@link EffectOverlay}. */
public class EffectOverlayTest
{
	private Client client;
	private WorldView worldView;
	private AfkNmzConfig config;
	private EffectState state;
	private EffectOverlay overlay;
	private Graphics2D graphics;

	@Before
	public void setUp()
	{
		client = mock(Client.class);
		worldView = mock(WorldView.class);
		config = mock(AfkNmzConfig.class);
		state = new EffectState();
		when(client.getTopLevelWorldView()).thenReturn(worldView);
		when(worldView.getMapRegions()).thenReturn(new int[] {9033});
		when(config.showDreamOverlay()).thenReturn(true);
		when(config.showOverloadTimer()).thenReturn(true);
		when(config.showAbsorptionPoints()).thenReturn(true);
		when(config.overloadWarningSeconds()).thenReturn(30);
		when(config.absorptionThreshold()).thenReturn(200);
		overlay = new EffectOverlay(client, config, state, new DefaultPlacement(client),
			mock(ItemManager.class));
		graphics = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB).createGraphics();
	}

	private String readout(long now)
	{
		EffectOverlay.Line line = overlay.lines(now).get(0);

		return line.right;
	}

	private List<EffectOverlay.Line> lines()
	{
		return overlay.lines(Countdown.now());
	}

	@Test
	public void hiddenWhenDisabled()
	{
		when(config.showDreamOverlay()).thenReturn(false);

		assertNull(overlay.render(graphics));
	}

	@Test
	public void hiddenOutsideTheDream()
	{
		when(worldView.getMapRegions()).thenReturn(new int[] {1234});

		assertNull(overlay.render(graphics));
	}

	@Test
	public void drawsInsideTheDream()
	{
		assertNotNull(overlay.render(graphics));
	}

	@Test
	public void showsOverloadOffAndAbsorptionPoints()
	{
		state.setAbsorptionPoints(640);
		List<EffectOverlay.Line> lines = lines();

		assertEquals(2, lines.size());
		assertEquals("Overload", lines.get(0).name);
		assertEquals("Off", lines.get(0).right);
		assertEquals(Color.GRAY, lines.get(0).rightColour);
		assertEquals("Absorption", lines.get(1).name);
		assertEquals("640", lines.get(1).right);
		assertEquals(Color.WHITE, lines.get(1).rightColour);
	}

	@Test
	public void showsRunningOverloadInTenths()
	{
		state.setOverloadCountdown(Countdown.of(500, Countdown.now() + 60_000));
		EffectOverlay.Line line = lines().get(0);

		assertEquals("Overload", line.name);
		assertEquals("5:00.0", line.right);
		assertEquals(Color.WHITE, line.rightColour);
	}

	@Test
	public void overloadReadoutMovesBetweenTicks()
	{
		long anchor = Countdown.now();
		state.setOverloadCountdown(Countdown.of(500, anchor));

		assertEquals("5:00.0", readout(anchor));
		assertEquals("4:59.8", readout(anchor + 200));
		assertEquals("4:59.5", readout(anchor + 500));
	}

	@Test
	public void respectsTheIndividualToggles()
	{
		when(config.showOverloadTimer()).thenReturn(false);
		List<EffectOverlay.Line> lines = lines();

		assertEquals(1, lines.size());
		assertEquals("Absorption", lines.get(0).name);

		when(config.showAbsorptionPoints()).thenReturn(false);
		assertEquals(0, lines().size());
	}

	@Test
	public void lowValuesUseTheWarningColour()
	{
		state.setAbsorptionPoints(100);
		state.setOverloadCountdown(Countdown.of(10, Countdown.now() + 60_000));
		List<EffectOverlay.Line> lines = lines();

		assertEquals(new Color(255, 90, 90), lines.get(0).rightColour);
		assertEquals(new Color(255, 90, 90), lines.get(1).rightColour);
	}
}
