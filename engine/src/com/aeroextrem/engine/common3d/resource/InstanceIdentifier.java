package com.aeroextrem.engine.common3d.resource;

import com.aeroextrem.engine.resource.GameResource;
import org.jetbrains.annotations.NotNull;

/** Schlüssel zu einer Instanz */
public class InstanceIdentifier {
	public final GameResource resource;
	public final int index;

	public InstanceIdentifier(@NotNull GameResource resource, int index) {
		this.resource = resource;
		this.index = index;
	}

	@Override
	public boolean equals(Object obj) {
		return
			this == obj
				||
			obj instanceof InstanceIdentifier && resource.equals(((InstanceIdentifier) obj).resource) && index == ((InstanceIdentifier) obj).index;
	}

	@Override
	public int hashCode() {
		return resource.hashCode() + 19 * index;
	}

	@Override
	public String toString() {
		return resource.getClass().getName() + index;
	}
}
