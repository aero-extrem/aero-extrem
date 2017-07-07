package com.aeroextrem.view.airplane.test;

import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/** Bildet einen Teil (Node) eines Models */
interface PartVisual {

	void loadNode(ModelBuilder mb, Node node);

	default void putNode(ModelBuilder mb, String nodeName) {
		Node node = mb.node();
		node.id = nodeName;
		loadNode(mb, node);
	}

}
