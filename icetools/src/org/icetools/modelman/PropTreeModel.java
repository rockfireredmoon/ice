package org.icetools.modelman;

import java.io.File;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class PropTreeModel extends DefaultTreeModel {

	public PropTreeModel(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}

	public PropTreeModel(TreeNode root) {
		super(root);
	}

	public PropNode getNodeForFile(File file) {
		return getNodeForFile((PropNode) getRoot(), file);
	}

	PropNode getNodeForFile(PropNode node, File file) {
		if (node.getFile().equals(file)) {
			return node;
		} else {
			if (!node.isLeaf()) {
				for (int i = 0; i < node.getChildCount(); i++) {
					PropNode n = getNodeForFile((PropNode) node.getChildAt(i), file);
					if (n != null) {
						return n;
					}
				}
			}
		}
		return null;
	}

}
