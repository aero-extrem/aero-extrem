package com.aeroextrem.engine.common3d;

import com.aeroextrem.engine.ScenarioAdapter;
import com.aeroextrem.engine.common3d.behaviour.*;
import com.aeroextrem.engine.common3d.resource.*;
import com.aeroextrem.engine.resource.GameResource;
import com.aeroextrem.engine.util.InputMappedMultiplexer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Random;

import static com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.AmbientLight;

/** Basis für ein 3D Szenario */
@SuppressWarnings("WeakerAccess")
public abstract class Common3D extends ScenarioAdapter {

	protected Random rand = new Random();

	// Instance map
	protected HashMap<InstanceIdentifier, BehavingInstance> instances;
	// Count of instances of type
	protected HashMap<GameResource, Integer> instanceCount;

	// Input
	protected InputMappedMultiplexer inputProcessor;

	// Behaviours
	private Array<BehaviourVisual> behavioursVisual;
	private Array<BehaviourPhysics> behavioursPhysics;
	private Array<BehaviourInput> behavioursInput;
	private InputMappedMultiplexer behavioursInputProcessors;
	protected static final String INPUT_BEHAVIOURS = "behaviours";

	// Anzeige
	protected ModelBatch modelBatch;
	protected SpriteBatch spriteBatch;
	protected Camera cam;
	protected Environment environment;

	// Physikunterstüzung
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private btBroadphaseInterface broadphase;
	protected btDynamicsWorld dynamicsWorld;
	private btConstraintSolver constraintSolver;

	// Disposables
	private Array<Disposable> despos;


	protected float bgColorR = 1f;
	protected float bgColorG = 1f;
	protected float bgColorB = 1f;
	protected float bgColorA = 1f;

	/** Bereitet das Szenario vor */
	@Override
	public void create() {
		// 3D Renderer
		modelBatch = new ModelBatch();
		cam = createCamera();
		environment = new Environment();

		// 2D Renderer
		spriteBatch = new SpriteBatch();
	}

	@Override
	public void load() {
		// Load Bullet Phys Engine
		Bullet.init();

		instances = new HashMap<>(10);
		instanceCount = new HashMap<>(10);

		// Input
		inputProcessor = new InputMappedMultiplexer();

		// Behaviours
		despos = new Array<>();
		behavioursVisual = new Array<>();
		behavioursPhysics = new Array<>();
		behavioursInput = new Array<>();
		behavioursInputProcessors = new InputMappedMultiplexer();
		inputProcessor.putProcessor(INPUT_BEHAVIOURS, behavioursInputProcessors);

		// Physik
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
	}

	@Override
	public void lateLoad() {
		// Visuell
		environment = createEnvironment();

		Gdx.input.setInputProcessor(inputProcessor);
	}

	/** Rendert einen Frame */
	@Override
	public void render() {
		// Hintergrund
		Gdx.gl.glClearColor(bgColorR, bgColorG, bgColorB, bgColorA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		handleInput();

		updateCamera();

		modelBatch.begin(cam);
		render3D(modelBatch, environment);
		modelBatch.end();

		renderUI(spriteBatch);
		calcPhysics();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

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

	protected void handleInput() {
		// TODO Extend InputProcessor
		for(BehaviourInput b : behavioursInput)
			b.onInputUpdate();
	}

	/** Rendert 3D Sicht
	 *
	 * @param mb 3D Renderer
	 * @param env Belichtungsinformationen */
	protected void render3D(@NotNull ModelBatch mb, @NotNull Environment env) {
		for(BehavingInstance bi : instances.values()) {
			if(bi.instance instanceof ModelInstance) {
				ModelInstance model = (ModelInstance) bi.instance;
				mb.render(model, env);
			}
		}
	}

	/** Rendert 2D Overlay
	 *
	 * @param sb 2D Renderer */
	protected abstract void renderUI(@NotNull SpriteBatch sb);

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

		for(InstanceIdentifier i : instances.keySet())
			kill(i);

		for(Disposable d : despos)
			d.dispose();
	}

	/** Gibt alle Instanzen dieser Ressource zurück
	 *
	 * @param resource Klasse der Ressource */
	@NotNull
	public Array<InstanceIdentifier> getInstances(@NotNull GameResource resource) {
		Array<InstanceIdentifier> array = new Array<>();
		instances.keySet().forEach(identifier -> {
			if(identifier.resource == resource) {
				array.add(identifier);
			}
		});
		return array;
	}

	/** Setzt eine Ressource in die Welt
	 *
	 * @param res Ressource */
	@NotNull
	public InstanceIdentifier spawn(@NotNull GameResource res) {
		// Neue ID erstellen
		InstanceIdentifier id = new InstanceIdentifier(res, rand.nextInt());
		// Neue Instanz erstellen
		ModelInstance modelInstance = new ModelInstance(res.getModel());

		// Änderungen schreiben
		instances.put(id, new BehavingInstance<>(modelInstance));

		int count = getInstanceCount(res);
		instanceCount.put(res, count+1);

		return id;
	}

	/** Setzt eine physikalische Ressource in die Welt
	 *
	 * @param res Ressource, die erstellt werden soll. */
	@NotNull
	public InstanceIdentifier spawn(@NotNull PhysicsResource res) {
		// Neue ID erstellen
		InstanceIdentifier id = new InstanceIdentifier(res, rand.nextInt());
		// Neue Instanz erstellen
		PhysicsInstance physInstance = new PhysicsInstance(res, dynamicsWorld);

		// Änderungen schreiben
		instances.put(id, new BehavingInstance<>(physInstance));

		int count = getInstanceCount(res);

		instanceCount.put(res, count+1);

		return id;
	}

	public int getInstanceCount(@NotNull GameResource res) {
		Integer count = instanceCount.get(res);
		if(count == null)
			return 0;
		else
			return count;
	}

	/** Entfernt die Instanz.
	 *
	 * @param identifier ID der Instanz */
	public boolean kill(@NotNull InstanceIdentifier identifier) {
		// Gibt es Instanzen des Typs?
		int count = getInstanceCount(identifier.resource);
		if(count <= 0)
			return false;

		// Gibt es Instanzen mit dieser ID?
		BehavingInstance instance = instances.get(identifier);
		if(instance == null)
			return false;

		// Behaviours entfernen
		clearBehaviours(identifier);

		// Entfernen
		instanceCount.put(identifier.resource, count-1);
		instances.remove(identifier);
		if(instance.instance instanceof Disposable) {
			((Disposable) instance.instance).dispose();
		}
		return true;
	}

	/** Versucht, ein Behaviour hinzuzufügen
	 *
	 * Fügt kein Behaviour hinzu, falls bereits eins vorhanden ist.
	 *
	 * @param key Instanz
	 * @param name Name des Behaviours
	 * @param behaviour Behaviour
	 * @return Erfolgreich? */
	public <T extends ModelInstance> boolean addBehaviour(
			@NotNull InstanceIdentifier key,
			@NotNull String name,
			@NotNull BehaviourBase behaviour
	) {
		// Gibt es Instanzen mit dieser ID?
		BehavingInstance<T> instance = get(key);
		if(instance == null)
			return false;

		behaviour.onCreate(key.resource);
		// FIXME Hässlicher Code
		if(behaviour instanceof BehaviourVisual) {
			((BehaviourVisual) behaviour).onCreateVisuals(instance.instance);
			behavioursVisual.add((BehaviourVisual) behaviour);
		}
		if(behaviour instanceof BehaviourPhysics) {
			if(!(instance.instance instanceof PhysicsInstance)) {
				System.err.println("Engine: Tried to apply a physical behaviour on a non-physical instance!");
				return false;
			}
			((BehaviourPhysics) behaviour).onBindPhysics(dynamicsWorld, (PhysicsInstance) instance.instance);
			behavioursPhysics.add((BehaviourPhysics) behaviour);
		}
		if(behaviour instanceof BehaviourInput) {
			InputProcessor processor = ((BehaviourInput) behaviour).onBindInput();
			behavioursInput.add((BehaviourInput) behaviour);
			behavioursInputProcessors.putProcessor(key.toString() + name, processor);
		}

		instance.behaviours.put(name, behaviour);
		return true;
	}

	/** Versucht, ein Behaviour zu entfernen
	 *
	 * @param key Instanz
	 * @param name Name des Behaviours
	 * @return true falls entfernt, false falls nicht gefunden */
	public <T extends ModelInstance> boolean removeBehaviour(
			@NotNull InstanceIdentifier key,
			@NotNull String name
	) {
		// Gibt es Instanzen mit dieser ID?
		BehavingInstance<T> instance = get(key);
		if(instance == null)
			return false;

		// Gibt es Behaviours mit dieser ID?
		BehaviourBase behaviour = instance.behaviours.get(name);
		behaviour.dispose();
		instance.behaviours.removeKey(name);

		if(behaviour instanceof BehaviourVisual)
			behavioursVisual.removeValue((BehaviourVisual) behaviour, true);
		if(behaviour instanceof BehaviourPhysics)
			behavioursPhysics.removeValue((BehaviourPhysics) behaviour, true);
		if(behaviour instanceof BehaviourInput) {
			behavioursInput.removeValue((BehaviourInput) behaviour, true);
			behavioursInputProcessors.removeProcessor(key.toString() + name);
		}

		return true;
	}

	/** Entfernt alle Behaviours
	 *
	 * @param key Instanz */
	public <T extends ModelInstance> boolean clearBehaviours(@NotNull InstanceIdentifier key) {
		// Gibt es Instanzen mit dieser ID?
		BehavingInstance<T> instance = get(key);
		if(instance == null)
			return false;

		for(String b : instance.behaviours.keys())
			removeBehaviour(key, b);

		return true;
	}


	/** Versucht, die zugehörige Instanz zu finden */
	@Nullable
	public <T extends ModelInstance> T getInstance(@NotNull InstanceIdentifier key) {
		return (T) instances.get(key).instance;
	}

	/** Versucht, die zugehörige BehavingInstance zu finden */
	@Nullable
	public <T extends ModelInstance> BehavingInstance<T> get(@NotNull InstanceIdentifier key) {
		return (BehavingInstance<T>) instances.get(key);
	}

}
