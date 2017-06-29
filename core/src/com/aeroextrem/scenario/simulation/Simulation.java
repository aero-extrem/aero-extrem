package com.aeroextrem.scenario.simulation;

import com.aeroextrem.engine.common3d.Common3D;
import com.aeroextrem.engine.resource.ResourceManager;
import com.aeroextrem.scenario.view.terrain.TerrainResource;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

/** Platzhalter für die Simulation */
public class Simulation extends Common3D {


	@Override
	public void load() {
		super.load();
		ResourceManager.load(TerrainResource.class);
	}

	@Override
	public void lateLoad() {
		super.lateLoad();
		ResourceManager.lateLoad(TerrainResource.class);
		spawn(ResourceManager.get(TerrainResource.class));
	}

	@Override
	protected void renderUI(SpriteBatch sb) {

	}

	@Override
	protected void updateCamera() {

	}

	@Override
	public void dispose() {
		ResourceManager.unload(TerrainResource.class);
	}
}
