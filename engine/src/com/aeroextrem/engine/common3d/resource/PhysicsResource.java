package com.aeroextrem.engine.common3d.resource;

import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.ArrayMap;
import org.jetbrains.annotations.NotNull;

/** Eine GameResource mit physikalischen Eigenschaften */
public interface PhysicsResource extends GameResource {

	/** Gibt eine Map aus, die physikalischen Komponenten (Nodes) IDs zuweist.
	 *
	 * @return Map mit IDs als Keys und physikalischen Komponenten als Values. */
	@NotNull
	ArrayMap<String, PhysicsInfo> getPhysicsNodes();

	/** Lädt die benötigten Objekte in den Speicher. */
	@Override void load();

	/** Gibt ein libgdx 3D-Modell mit den Resourcen aus. */
	@Override
	@NotNull Model getModel();

	/** Gibt den Speicher der Resource wieder frei. */
	@Override void dispose();

}
