/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal.afknmz;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

/** Draws the stored dose count centred on each potion barrel in the Nightmare Zone lobby. */
public class BarrelOverlay extends Overlay
{
	/** Region id of the dream arena; barrels are never drawn there. */
	static final int DREAM_REGION = 9033;

	private static final int TEXT_Z_OFFSET = 40;

	private final Client client;
	private final AfkNmzConfig config;
	private final Map<GameObject, NmzBarrel> barrels = new ConcurrentHashMap<>();

	@Inject
	BarrelOverlay(Client client, AfkNmzConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	/** Starts tracking a spawned barrel object. */
	void add(GameObject object, NmzBarrel barrel)
	{
		barrels.put(object, barrel);
	}

	/** Stops tracking a despawned barrel object. */
	void remove(GameObject object)
	{
		barrels.remove(object);
	}

	/** Forgets every tracked barrel. */
	void clear()
	{
		barrels.clear();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showBarrelDoses() || isInDream())
			return null;

		for (Map.Entry<GameObject, NmzBarrel> entry : barrels.entrySet())
		{
			NmzBarrel barrel = entry.getValue();
			String text = barrel.getLabel() + " " + client.getVarbitValue(barrel.getVarbitId());
			Point point = Perspective.getCanvasTextLocation(
				client, graphics, entry.getKey().getLocalLocation(), text, TEXT_Z_OFFSET);
			if (point != null)
				OverlayUtil.renderTextLocation(graphics, point, text, config.barrelTextColor());
		}

		return null;
	}

	private boolean isInDream()
	{
		for (int region : client.getTopLevelWorldView().getMapRegions())
		{
			if (region == DREAM_REGION)
				return true;
		}

		return false;
	}
}
