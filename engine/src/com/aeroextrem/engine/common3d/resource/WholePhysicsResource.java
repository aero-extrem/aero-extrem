package com.aeroextrem.engine.common3d.resource;

import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.graphics.g3d.Model;
import org.jetbrains.annotations.NotNull;

/** Eine GameResource mit physikalischen Eigenschaften */
public interface WholePhysicsResource extends GameResource {

	/** Gibt eine einzelne physikalische Komponente aus */
	@NotNull
	PhysicsInfo getPhysicsInfo();

	/** Lädt die benötigten Objekte in den Speicher. */
	@Override void load();

	/** Gibt ein libgdx 3D-Modell mit den Resourcen aus. */
	@Override
	@NotNull Model getModel();

	/** Gibt den Speicher der Resource wieder frei. */
	@Override void dispose();

}
