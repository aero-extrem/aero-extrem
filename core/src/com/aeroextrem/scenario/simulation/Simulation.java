package com.aeroextrem.scenario.simulation;

import com.aeroextrem.engine.common3d.Common3D;
import com.aeroextrem.engine.common3d.resource.InstanceIdentifier;
import com.aeroextrem.engine.common3d.resource.PhysicsInstance;
import com.aeroextrem.engine.resource.ResourceManager;
import com.aeroextrem.engine.util.ChaseCameraController;
import com.aeroextrem.engine.util.EnvironmentCubemap;
import com.aeroextrem.util.InputSwitch;
import com.aeroextrem.view.airplane.test.TestPlanePhysics;
import com.aeroextrem.view.airplane.test.TestPlaneResource;
import com.aeroextrem.view.terrain.TerrainResource;
import com.aeroextrem.view.ui.IngameMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import org.jetbrains.annotations.NotNull;

/** Flugsimulation */
public class Simulation extends Common3D {

	protected static final String INPUT_PAUSE = "pauseSwitch";
	protected static final String INPUT_CHASE_CAM = "camController";
	protected static final String INPUT_SIM = "scenario";

	boolean showPauseMenu = false;
	boolean showDebug = true;

	// Menu
	private InputSwitch pauseMenuInput;
	private IngameMenu menu;
	private MenuController menuController;

	// Simulation
	private SimulationInput inputSim;
	private ChaseCameraController inputCam;

	// Skybox
	private EnvironmentCubemap skybox;
	private Pixmap skyPosX, skyNegX, skyPosY, skyNegY, skyPosZ, skyNegZ;

	// UI
	private BitmapFont debugFont;

	@Override
	public void load() {
		super.load();
		ResourceManager.load(TerrainResource.class);
		ResourceManager.load(TestPlaneResource.class);

		menu = new IngameMenu();
		menuController = new MenuController(menu);

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
		ResourceManager.lateLoad(TestPlaneResource.class);

		super.lateLoad();
		menu.create();
		menuController.createMenu();

		skybox = new EnvironmentCubemap(skyPosX, skyNegX, skyPosY, skyNegY, skyPosZ, skyNegZ);

		InstanceIdentifier terrainKey = spawn(ResourceManager.get(TerrainResource.class));
		PhysicsInstance terrain = getInstance(terrainKey);
		assert terrain != null;
		terrain.partMap.get(TerrainResource.PART_GROUND).rb.translate(new Vector3(0, -50f, 0));

		InstanceIdentifier planeKey = spawn(ResourceManager.get(TestPlaneResource.class));
		PhysicsInstance plane = getInstance(planeKey);
		assert plane != null;
		Matrix4 planePos = plane.getNode(TestPlaneResource.NODE_FUSELAGE).globalTransform;
		addBehaviour(planeKey, "Physics", new TestPlanePhysics());

		inputProcessor.putProcessor(INPUT_PAUSE,		pauseMenuInput = new InputSwitch(menu.getStage()));
		inputProcessor.putProcessor(INPUT_CHASE_CAM,	inputCam = new ChaseCameraController(cam, planePos));
		inputProcessor.putProcessor(INPUT_SIM,		inputSim = new SimulationInput(this, pauseMenuInput));

		debugFont = new BitmapFont();
		debugFont.setColor(0f, 0f, 0f, 1f);
		debugFont.getData().markupEnabled = true;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		menu.resize(width, height);
	}

	@Override
	protected void renderUI(@NotNull SpriteBatch sb) {
		sb.begin();
		if(showDebug)
			renderDebugScreen(sb);
		if(showPauseMenu)
			menu.render();
		sb.end();
	}

	/** Zeigt Entwicklerinformationen an
	 *
	 * @param sb SpriteBatch, nach begin() */
	private void renderDebugScreen(SpriteBatch sb) {
		debugFont.draw(sb, String.format("[ORANGE]Camera:[] Theta: %.2f, Phi: %.2f", inputCam.theta, inputCam.phi), 20, 20);
		debugFont.draw(sb, String.format("[ORANGE]Cam Up:[] [[%.2f, %.2f, %.2f]]", cam.up.x, cam.up.y, cam.up.z), 20, 40);
		debugFont.draw(sb, String.format("[ORANGE]Cam Dir:[] [[%.2f, %.2f, %.2f]]", cam.direction.x, cam.direction.y, cam.direction.z), 20, 60);
	}

	@Override
	protected void render3D(@NotNull ModelBatch mb, @NotNull Environment env) {
		skybox.render(cam);
		super.render3D(mb, env);
	}

	@Override
	protected void updateCamera() {
		inputCam.update();
	}

	@Override
	public void dispose() {
		ResourceManager.unload(TerrainResource.class);
		ResourceManager.unload(TestPlaneResource.class);

		menu.dispose();
		skybox.dispose();
		skyPosX.dispose();
		skyNegX.dispose();
		skyPosY.dispose();
		skyNegY.dispose();
		skyPosZ.dispose();
		skyNegZ.dispose();
	}
}
