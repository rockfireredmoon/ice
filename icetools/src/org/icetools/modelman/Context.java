package org.icetools.modelman;

public interface Context {

	Console getConsole();

	FileNodeBuilder getBuilder();

	PropNode getSelectedPropNode();

	PropTreeModel getTreeModel();
}
