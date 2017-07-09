package com.aeroextrem.engine.common3d.resource;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

/** Beschreibung der physikalischen Komponente
 *
 * Besteht aus Form, Masse und Kollisionseigenschaften */
public class PhysicsInfo implements Disposable {

	/** Kollisionsgruppen: Alle */
	public static final short GROUP_ALL = -1;
	/** Kollisionsgruppe: Flugzeug */
	public static final short GROUP_AIRPLANE = 1 << 9;
	/** Kollisionsgruppe: Umgebung */
	public static final short GROUP_WORLD = 1 << 8;

	/** Erstellt eine physikalische Komponente
	 *
	 * @param hitbox Form der Kollisionsbox („Hitbox“)
	 * @param mass Gewicht des Objekts
	 * @param group Gruppen, mit denen sich das Objekt identifiziert. (16 Gruppen, je ein Bit).
	 * @param mask Gruppen, mit denen das Objekt kollidieren kann. (16 Gruppen, je ein Bit). */
	public PhysicsInfo(@NotNull btCollisionShape hitbox, float mass, short group, short mask) {
		this.hitbox = hitbox;
		this.mass = mass;
		this.group = group;
		this.mask = mask;
	}

	/** Form der Kollisionsbox */
	public final btCollisionShape hitbox;

	/** Gewicht des Objektes
	 *
	 * Ist mass 0, wird das Objekt statisch und ignoriert alle Kräfte, die auf es einwirken. */
	public final float mass;

	/** Gruppen, mit denen sich das Objekt identifiziert.
	 *
	 * Es gibt 16 Gruppen insgesamt.<br>
	 * Jedes bit in dem short (16) stellt den Wahrheitswert für eine Gruppe dar.<br>
	 * Ein Wert von 0b0100000000000001 heißt z.B., dass das Objekt zu Gruppen 2 und 16 gehört.<br>
	 * Kollisionsgruppen sind definiert in den Konstanten "GROUP_". */
	public final short group;

	/** Gruppen, mit denen das Objekt kollidieren kann.
	 *
	 * Es gibt 16 Gruppen insgesamt.<br>
	 * Jedes bit in dem short (16) stellt den Wahrheitswert für eine Gruppe dar.<br>
	 * Ein Wert von 0b0100000000000001 heißt z.B., dass das Objekt zu Gruppen 2 und 16 gehört.<br>
	 * Wenn mask 0 ist, kollidiert das Objekt mit nichts und fällt durch den Boden. */
	public final short mask;

	/** Gibt den benutzten Speicher frei */
	@Override
	public void dispose() {
		hitbox.dispose();
	}

}
