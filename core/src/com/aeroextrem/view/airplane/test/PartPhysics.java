package com.aeroextrem.view.airplane.test;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import org.jetbrains.annotations.NotNull;

/** Bildet ein Glied (RigidBody) */
interface PartPhysics {

	@NotNull
	PhysicsInfo loadPhysicsPart();

}
