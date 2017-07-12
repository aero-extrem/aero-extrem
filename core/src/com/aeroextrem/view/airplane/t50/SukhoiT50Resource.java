package com.aeroextrem.view.airplane.t50;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import com.aeroextrem.engine.common3d.resource.WholePhysicsResource;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeX;
import org.jetbrains.annotations.NotNull;

import static com.aeroextrem.engine.common3d.resource.PhysicsInfo.GROUP_AIRPLANE;
import static com.aeroextrem.engine.common3d.resource.PhysicsInfo.GROUP_WORLD;

/** https://en.wikipedia.org/wiki/Sukhoi_PAK_FA
 *
 * @author terorie
 * @author oldfrizt */
public class SukhoiT50Resource implements WholePhysicsResource {

	private AssetManager assetMgr;
	private Model model;
	//private ArrayMap<String, PhysicsInfo> physicsNodes;
	private PhysicsInfo physics;

	@Override
	public void load() {
		assetMgr = new AssetManager();
		assetMgr.load("T-50.g3db", Model.class);

		/*physicsNodes = new ArrayMap<>();
		physicsNodes.put(BODY, new PhysicsInfo(
			new btCapsuleShapeX(0.5f, 8f),
			4f,
			GROUP_AIRPLANE, GROUP_WORLD
		));*/
		physics = new PhysicsInfo(
			new btCapsuleShapeX(0.5f, 8f),
			4f,
			GROUP_AIRPLANE, GROUP_WORLD
		);

		/*PhysicsInfo toDoShape = new PhysicsInfo(
			new btBoxShape(new Vector3(.1f, .1f, .1f)),
			1f,
			GROUP_AIRPLANE, GROUP_WORLD
		);

		physicsNodes.put(AILERON_LEFT, toDoShape);
		physicsNodes.put(AILERON_RIGHT, toDoShape);
		physicsNodes.put(RUDDER_LEFT, toDoShape);
		physicsNodes.put(RUDDER_RIGHT, toDoShape);
		physicsNodes.put(FLAPS_LEFT, toDoShape);
		physicsNodes.put(FLAPS_RIGHT, toDoShape);
		physicsNodes.put(LEAD_FLAPS_LEFT, toDoShape);
		physicsNodes.put(LEAD_FLAPS_RIGHT, toDoShape);
		physicsNodes.put(GEAR_ROD_BACKLEFT, toDoShape);
		physicsNodes.put(GEAR_ROD_BACKRIGHT, toDoShape);
		physicsNodes.put(GEAR_ROD_FRONT, toDoShape);
		physicsNodes.put(GEAR_WHEEL_BACKLEFT, toDoShape);
		physicsNodes.put(GEAR_WHEEL_BACKRIGHT, toDoShape);
		physicsNodes.put(GEAR_WHEEL_FRONT, toDoShape);*/
	}

	@Override
	public void commit() {
		// TODO Async loading
		assetMgr.finishLoading();
		model = assetMgr.get("T-50.g3db", Model.class);
		// Move 4 by X
		model.nodes.forEach(node -> node.translation.add(4, 0, 0));
	}

	@Override
	@NotNull
	public Model getModel() {
		return model;
	}

	/*@Override
	@NotNull
	public ArrayMap<String, PhysicsInfo> getPhysicsNodes() {
		return physicsNodes;
	}*/

	@Override
	@NotNull
	public PhysicsInfo getPhysicsInfo() {
		return physics;
	}

	@Override
	public void dispose() {
		assetMgr.dispose();
		model.dispose();
	}
}
