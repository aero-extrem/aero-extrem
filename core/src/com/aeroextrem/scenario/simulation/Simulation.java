package com.aeroextrem.scenario.simulation;

import com.aeroextrem.engine.common3d.Common3D;
import com.aeroextrem.engine.common3d.resource.InstanceIdentifier;
import com.aeroextrem.engine.common3d.resource.PhysicsInstance;
import com.aeroextrem.engine.resource.ResourceManager;
import com.aeroextrem.engine.util.ChaseCameraController;
import com.aeroextrem.engine.util.EnvironmentCubemap;
import com.aeroextrem.util.AeroExtrem;
import com.aeroextrem.util.InputSwitch;
import com.aeroextrem.view.airplane.test.TestPlaneData;
import com.aeroextrem.view.airplane.test.TestPlaneInput;
import com.aeroextrem.view.airplane.test.TestPlanePhysics;
import com.aeroextrem.view.airplane.test.TestPlaneResource;
import com.aeroextrem.view.terrain.TerrainResource;
import com.aeroextrem.view.ui.IngameMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import static com.aeroextrem.view.airplane.test.TestPlaneResource.NODE_FUSELAGE;

/** Flugsimulation */
public class Simulation extends Common3D {

	protected static final String INPUT_PAUSE = "pauseSwitch";
	protected static final String INPUT_CHASE_CAM = "camController";
	protected static final String INPUT_SIM = "scenario";

	boolean showPauseMenu = false;
	boolean showDebug = true;

	// Menu
	protected InputSwitch pauseMenuInput;
	protected IngameMenu menu;
	protected MenuController menuController;

	// Simulation
	protected SimulationInput inputSim;
	protected ChaseCameraController inputCam;
	
	// Flugzeug
	protected PhysicsInstance plane;
	protected TestPlaneData planeData;
	protected Vector3 planePos = new Vector3();
	protected Quaternion planeRot = new Quaternion();

	// Skybox
	protected EnvironmentCubemap skybox;
	protected Pixmap skyPosX, skyNegX, skyPosY, skyNegY, skyPosZ, skyNegZ;

	// UI
	protected BitmapFont debugFont;

	// Audio
	protected Music music;

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

		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/musicSim.mp3"));
		music.setVolume(0.5f);
		music.setLooping(true);
	}

	@Override
	public void lateLoad() {
		ResourceManager.lateLoad(TerrainResource.class);
		ResourceManager.lateLoad(TestPlaneResource.class);

		super.lateLoad();

		createUI();

		skybox = new EnvironmentCubemap(skyPosX, skyNegX, skyPosY, skyNegY, skyPosZ, skyNegZ);

		debugFont = new BitmapFont();
		debugFont.setColor(0f, 0f, 0f, 1f);
		debugFont.getData().markupEnabled = true;

		music.play();

		spawnObjects();

		setupInput();
	}

	protected void createUI() {
		menu.create();
		menuController.createMenu(new PauseWindow());
	}

	protected void spawnObjects() {
		spawnTerrain();

		spawnPlane();
	}

	protected void spawnTerrain() {
		InstanceIdentifier terrainKey = spawn(ResourceManager.get(TerrainResource.class));
		PhysicsInstance terrain = getInstance(terrainKey);
		assert terrain != null;
	}

	protected void spawnPlane() {
		planeData = new TestPlaneData();
		InstanceIdentifier planeKey = spawn(ResourceManager.get(TestPlaneResource.class));
		plane = getInstance(planeKey);
		assert plane != null;
		addBehaviour(planeKey, "Physics", new TestPlanePhysics(planeData));
		addBehaviour(planeKey, "Input", new TestPlaneInput(planeData));
	}

	protected void setupInput() {
		inputProcessor.putProcessor(INPUT_PAUSE,		pauseMenuInput = new InputSwitch(menu.getStage()));
		Matrix4 planeTrans = plane.getNode(NODE_FUSELAGE).globalTransform;
		inputProcessor.putProcessor(INPUT_CHASE_CAM,	inputCam = new ChaseCameraController(cam, planeTrans));
		inputProcessor.putProcessor(INPUT_SIM,		inputSim = new SimulationInput(this, pauseMenuInput));
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
	protected void renderDebugScreen(SpriteBatch sb) {
		debugFont.draw(sb, String.format("[ORANGE]Camera:[] Theta: %.2f, Phi: %.2f", inputCam.theta, inputCam.phi), 20, 20);
		debugFont.draw(sb, String.format("[ORANGE]Cam Up:[] [[%.2f, %.2f, %.2f]]", cam.up.x, cam.up.y, cam.up.z), 20, 40);
		debugFont.draw(sb, String.format("[ORANGE]Cam Dir:[] [[%.2f, %.2f, %.2f]]", cam.direction.x, cam.direction.y, cam.direction.z), 20, 60);

		debugFont.draw(sb, String.format("[BLUE]Plane X:[] [[%.2f]]", planePos.x), 20, 120);
		debugFont.draw(sb, String.format("[BLUE]Plane Y:[] [[%.2f]]", planePos.y), 20, 100);
		debugFont.draw(sb, String.format("[BLUE]Plane Z:[] [[%.2f]]", planePos.z), 20, 80);
		debugFont.draw(sb, String.format("[BLUE]Plane Spd:[] [[%03.2f]]",
				plane.partMap.get(NODE_FUSELAGE).rb.getLinearVelocity().len()), 20, 140);

		debugFont.draw(sb, String.format("[GREEN]Thrust:[] [[%.2f]]", planeData.thrust), Gdx.graphics.getWidth() / 2, 80);
		debugFont.draw(sb, String.format("[GREEN]Pitch Input:[] [[%.2f]]", planeData.pitch), Gdx.graphics.getWidth() / 2, 60);
		debugFont.draw(sb, String.format("[GREEN]Yaw Input:[] [[%.2f]]", planeData.yaw), Gdx.graphics.getWidth() / 2, 40);
		debugFont.draw(sb, String.format("[GREEN]Roll Input:[] [[%.2f]]", planeData.roll), Gdx.graphics.getWidth() / 2, 20);

		if(AeroExtrem.isRecording) {
			debugFont.draw(sb, String.format("[RED]AUFNAHME LÄUFT…[]"), 0, Gdx.graphics.getHeight() - 20);
		}
	}

	@Override
	protected void render3D(@NotNull ModelBatch mb, @NotNull Environment env) {
		skybox.render(cam);
		plane.partMap.get(NODE_FUSELAGE).ms.transform.getTranslation(planePos);
		super.render3D(mb, env);

		if(AeroExtrem.isRecording) {
			AeroExtrem.recorder.addRow(
				Gdx.graphics.getDeltaTime(),
				planePos.x, planePos.y, planePos.z,
				planeRot.x, planeRot.y, planeRot.z, planeRot.w,
				planeData.pitch, planeData.yaw, planeData.roll,
				planeData.thrust
			);
		}
	}

	@Override
	@NotNull
	protected Camera createCamera() {
		Camera cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 7f, 10f);
		cam.lookAt(0, 10f, 0);
		cam.near = 1f;
		cam.far = 100000f;
		cam.update();
		return cam;
	}

	@Override
	protected void updateCamera() {
		inputCam.update();
	}

	@Override
	public void dispose() {
		ResourceManager.unload(TerrainResource.class);
		ResourceManager.unload(TestPlaneResource.class);

		if(AeroExtrem.isRecording) {
			AeroExtrem.isRecording = false;
			AeroExtrem.recorder.commit(true);
			AeroExtrem.recorder = null;
		}

		menu.dispose();
		skybox.dispose();
		/* Already disposed by EnvCubemap

		skyPosX.dispose();
		skyNegX.dispose();
		skyPosY.dispose();
		skyNegY.dispose();
		skyPosZ.dispose();
		skyNegZ.dispose();*/

		music.stop();
		music.dispose();
		super.dispose();
	}
}
