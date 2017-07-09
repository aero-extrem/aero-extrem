package com.aeroextrem.view.airplane.test;

import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.jetbrains.annotations.NotNull;

/** Bildet einen Teil (Node) eines Models */
interface PartVisual {

	void loadNode(@NotNull ModelBuilder mb, Node node);

	default void putNode(@NotNull ModelBuilder mb, @NotNull String nodeName) {
		Node node = mb.node();
		node.id = nodeName;
		loadNode(mb, node);
	}

}
