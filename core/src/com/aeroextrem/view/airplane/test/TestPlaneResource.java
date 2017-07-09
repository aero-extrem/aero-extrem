package com.aeroextrem.view.airplane.test;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import com.aeroextrem.engine.common3d.resource.PhysicsResource;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.ArrayMap;
import org.jetbrains.annotations.NotNull;

/** Hitboxen und Modell des Konzept-Flugzeugs */
public class TestPlaneResource implements PhysicsResource {

	public static final String NODE_WINGS = "wings";
	public static final String NODE_FUSELAGE = "fuselage";
	public static final String NODE_FRONTWHEEL = "front wheel";
	public static final String NODE_BACKWHEEL_L = "back wheel left";
	public static final String NODE_BACKWHEEL_R = "back wheel right";

	private Model model;
	private ArrayMap<String, PhysicsInfo> physicsNodes;

	@Override
	public void load() {

	}

	@Override
	public void commit() {
		// MODEL
		Wings wings = new Wings();
		Fuselage fuselage = new Fuselage();
		Wheel wheel = new Wheel();

		ModelBuilder mb = new ModelBuilder();
		mb.begin();
			wings.putNode(mb, NODE_WINGS);
			fuselage.putNode(mb, NODE_FUSELAGE);
			wheel.putNode(mb, NODE_FRONTWHEEL);
			wheel.putNode(mb, NODE_BACKWHEEL_L);
			wheel.putNode(mb, NODE_BACKWHEEL_R);
		model = mb.end();

		// PHYSICS
		physicsNodes = new ArrayMap<>(5);
		physicsNodes.put(NODE_WINGS, wings.loadPhysicsPart());
		physicsNodes.put(NODE_FUSELAGE, fuselage.loadPhysicsPart());

		PhysicsInfo wheelShape = wheel.loadPhysicsPart();
		physicsNodes.put(NODE_FRONTWHEEL,	wheelShape);
		physicsNodes.put(NODE_BACKWHEEL_L,	wheelShape);
		physicsNodes.put(NODE_BACKWHEEL_R,	wheelShape);
	}
	
	@Override
	@NotNull
	public Model getModel() {
		return model;
	}
	
	@Override
	@NotNull
	public ArrayMap<String, PhysicsInfo> getPhysicsNodes() {
		return physicsNodes;
	}

	@Override
	public void dispose() {

	}
}
