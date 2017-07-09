package com.aeroextrem.engine.resource;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.ArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Referenziert alle geladenen Resourcen. */
public class ResourceManager {

	/** Map die geladene Klassen auf ihre Resourcenobjekte zuweist. */
	private static final ArrayMap<Class<? extends GameResource>, GameResource> resources;

	// Statischer Konstruktur (wird beim Laden der Klasse aufgerufen)
	static {
		resources = new ArrayMap<>();
	}

	/** Lädt eine Resource in den Hauptspeicher
	 *
	 * @param key Klasse der Resource
	 *
	 * @throws IllegalStateException Falls die Klasse nicht geladen werden konnte */
	public static void load(@NotNull Class<? extends GameResource> key) throws IllegalStateException {
		GameResource res;

		// Versuche, eine Instanz der Klasse zu erstellen
		try {
			res = key.newInstance();
		} catch (InstantiationException|IllegalAccessException e) {
			String error = String.format("Resource %s konnte nicht geladen werden!", key.toGenericString());
			System.err.printf(error);
			e.printStackTrace();
			throw new IllegalStateException(error);
		}

		// Die Resource kann zu diesem Zeitpunkt nicht null sein
		assert res != null;

		// Versuche, die Resource zu laden
		res.load();
		resources.put(res.getClass(), res);
	}

	/** Lädt eine Resource in den Grafikspeicher
	 *
	 * @param key Klasse der Resource
	 */
	public static void lateLoad(@NotNull Class<? extends GameResource> key) throws IllegalStateException {
		GameResource res = get(key);

		// Die Resource kann zu diesem Zeitpunkt nicht null sein
		assert res != null;

		// Versuche, die Resource zu laden
		res.commit();
	}

	/** Entlädt eine Resource
	 *
	 * Falls die angebene Klasse nicht bekannt ist, geschieht nichts.
	 *
	 * @param key Klasse der Resource */
	public static void unload(@NotNull Class<? extends GameResource> key) {
		GameResource res = resources.get(key);
		if(res == null)
			return;

		// Das Modell muss manuell entsorgt werden
		res.getModel().dispose();
		resources.removeKey(key);
	}

	/** Gibt eine Resource aus, die im Speicher ist.
	 *
	 * @param key Klasse der Resource
	 * @param <T> GameResource type
	 * @return Die Resource, falls sie im Speicher ist, andernfalls null. */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends GameResource> T get(@NotNull Class<T> key) {
		return (T) resources.get(key);
	}

	/** Erstellt eine Instanz einer Resource
	 *
	 * Gemeint ist hier, dass aus der Defintion eines 3D-Modells
	 * eine ModelInstance mit diesen Eigenschaften erstellt wird.
	 *
	 * @param key Klasse der Resource
	 * @return Die ModelInstance, falls die Resource im Speicher ist, andernfalls null. */
	@Nullable
	public static ModelInstance create(@NotNull Class<? extends GameResource> key) {
		GameResource res = resources.get(key);
		if(res == null)
			return null;

		return new ModelInstance(res.getModel());
	}

}
