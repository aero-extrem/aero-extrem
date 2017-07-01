package com.aeroextrem.scenario.simulation;

import com.aeroextrem.engine.common3d.Common3D;
import com.aeroextrem.engine.common3d.resource.InstanceIdentifier;
import com.aeroextrem.engine.resource.ResourceManager;
import com.aeroextrem.engine.util.ChaseCameraController;
import com.aeroextrem.engine.util.EnvironmentCubemap;
import com.aeroextrem.scenario.view.skybox.SkyboxResource;
import com.aeroextrem.scenario.view.terrain.TerrainResource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/** Platzhalter für die Simulation */
public class Simulation extends Common3D {

	ChaseCameraController chaseCam;
	BitmapFont debugFont;
	EnvironmentCubemap skybox;
	private Pixmap skyPosX, skyNegX, skyPosY, skyNegY, skyPosZ, skyNegZ;

	private boolean debug = true;

	@Override
	public void load() {
		super.load();
		ResourceManager.load(TerrainResource.class);
		ResourceManager.load(SkyboxResource.class);

		skyPosX = new Pixmap(Gdx.files.internal("texture/skybox/CloudyLightRaysLeft2048.png"));
		skyNegX = new Pixmap(Gdx.files.internal("texture/skybox/CloudyLightRaysRight2048.png"));
		skyPosY = new Pixmap(Gdx.files.internal("texture/skybox/CloudyLightRaysUp2048.png"));
		skyNegY = new Pixmap(Gdx.files.internal("texture/skybox/CloudyLightRaysDown2048.png"));
		skyPosZ = new Pixmap(Gdx.files.internal("texture/skybox/CloudyLightRaysFront2048.png"));
		skyNegZ = new Pixmap(Gdx.files.internal("texture/skybox/CloudyLightRaysBack2048.png"));
	}

	@Override
	public void lateLoad() {
		ResourceManager.lateLoad(TerrainResource.class);
		ResourceManager.lateLoad(SkyboxResource.class);

		super.lateLoad();

		skybox = new EnvironmentCubemap(skyPosX, skyNegX, skyPosY, skyNegY, skyPosZ, skyNegZ);

		//InstanceIdentifier terrain = spawn(ResourceManager.get(TerrainResource.class));
		InstanceIdentifier sky = spawn(ResourceManager.get(SkyboxResource.class));

		chaseCam = new ChaseCameraController(cam, ((ModelInstance)instances.get(sky).instance).transform);
		Gdx.input.setInputProcessor(chaseCam);

		debugFont = new BitmapFont();
		debugFont.setColor(0f, 0f, 0f, 1f);
		debugFont.getData().markupEnabled = true;
	}

	@Override
	protected void renderUI(SpriteBatch sb) {
		sb.begin();
		if(debug)
			renderDebugScreen(sb);
		sb.end();
	}

	/** Zeigt Entwicklerinformationen an
	 *
	 * @param sb SpriteBatch, nach begin() */
	private void renderDebugScreen(SpriteBatch sb) {
		debugFont.draw(sb, String.format("[ORANGE]Camera:[] Theta: %.2f, Phi: %.2f", chaseCam.theta, chaseCam.phi), 20, 20);
	}

	@Override
	protected void render3D(ModelBatch mb, Environment env) {
		skybox.render(cam);
		super.render3D(mb, env);
	}

	@Override
	protected void updateCamera() {
		chaseCam.update();
	}

	@Override
	public void dispose() {
		ResourceManager.unload(TerrainResource.class);
		ResourceManager.unload(SkyboxResource.class);

		skybox.dispose();
		skyPosX.dispose();
		skyNegX.dispose();
		skyPosY.dispose();
		skyNegY.dispose();
		skyPosZ.dispose();
		skyNegZ.dispose();
	}
}
