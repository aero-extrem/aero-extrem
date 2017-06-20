package com.aeroextrem.engine.common3d;

import com.aeroextrem.engine.common3d.behaviour.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.AmbientLight;

/** Basis für ein 3D Szenario */
public abstract class Common3D extends ApplicationAdapter {

	/** Umgebung / Belichtungsinformationen erstellen
	 *
	 * @return Environment-Objekt */
	@NotNull
	protected Environment createEnvironment() {
		Environment env = new Environment();
		env.set(new ColorAttribute(AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		return env;
	}

	/** Neue Kamera erstellen
	 *
	 * @return Camera-Objekt */
	@NotNull
	protected Camera createCamera() {
		Camera cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 7f, 10f);
		cam.lookAt(0, 10f, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		return cam;
	}

	/** Kameraposition ausrechnen */
	protected abstract void updateCamera();

	/** Rendert 3D Sicht
	 *
	 * @param mb 3D Renderer
	 * @param env Belichtungsinformationen */
	protected abstract void render3D(ModelBatch mb, Environment env);

	/** Rendert 2D Overlay
	 *
	 * @param sb 2D Renderer */
	protected abstract void renderUI(SpriteBatch sb);

	// Behaviours
	private Array<BehaviourVisual> behavioursVisual;
	private Array<BehaviourPhysics> behavioursPhysics;
	private Array<BehaviourInput> behavioursInput;

	// Anzeige
	private ModelBatch modelBatch;
	private SpriteBatch spriteBatch;
	private Camera cam;
	private Environment environment;

	// Physikunterstüzung
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	private btDynamicsWorld dynamicsWorld;
	private btConstraintSolver constraintSolver;

	// Disposables
	private Array<Disposable> despos;

	/** Bereitet das Szenario vor */
	@Override
	public void create() {
		// Load Bullet Phys Engine
		Bullet.init();

		// 3D Renderer
		modelBatch = new ModelBatch();
		cam = createCamera();
		environment = new Environment();

		// 2D Renderer
		spriteBatch = new SpriteBatch();

		// Behaviours
		despos = new Array<>();
		behavioursVisual = new Array<>();
		behavioursPhysics = new Array<>();
		behavioursInput = new Array<>();

		// Physik
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
	}

	/** Rendert einen Frame */
	@Override
	public void render() {
		// Hintergrund
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		updateCamera();

		modelBatch.begin(cam);
		render3D(modelBatch, environment);
		modelBatch.end();

		renderUI(spriteBatch);
		calcPhysics();
	}

	/** Berechnet einen Schritt der Physiksimulation */
	protected void calcPhysics() {
		final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
		for(BehaviourPhysics phys : behavioursPhysics)
			phys.physicsTick(delta);
		dynamicsWorld.stepSimulation(delta, 5, 1 / 60f);
	}

	/** Gibt den Speicher wieder frei */
	@Override
	public void dispose() {
		dynamicsWorld.dispose();
		constraintSolver.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();

		modelBatch.dispose();
		spriteBatch.dispose();

		for(Disposable d : despos)
			d.dispose();
	}

}
