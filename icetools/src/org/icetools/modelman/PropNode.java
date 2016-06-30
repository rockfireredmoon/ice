package org.icetools.modelman;

import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

public abstract class PropNode implements TreeNode, Comparable<PropNode> {
    
    protected List<PropNode> children = null;
    protected File file;
    protected TreeNode parent;
    protected Context context;
    
    public PropNode(Context fileNodeBuilder, File root) {
        this(fileNodeBuilder, root, null);
    }
    
    public void mouseClick(MouseEvent event) {
    }
    
    public File getFile() {
        return file;
    }
    
    public PropNode(Context context, File file, PropNode parent) {
        this.parent = parent;
        setFile(file);
        this.context = context;
    }
    
    protected void setFile(File file) {
        this.file = file;
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        checkChildren();
        return children.get(childIndex);
    }
    
    @Override
    public int getChildCount() {
        checkChildren();
        return children == null ? 0 : children.size();
    }
    
    @Override
    public TreeNode getParent() {
        return parent;
    }
    
    @Override
    public int getIndex(TreeNode node) {
        checkChildren();
        return children.indexOf(node);
    }
    
    @Override
    public boolean getAllowsChildren() {
        return !isLeaf();
    }
    
    @Override
    public Enumeration<PropNode> children() {
        checkChildren();
        return new IteratingEnumeration<PropNode>(children.iterator());
    }
    
    @Override
    public int compareTo(PropNode o) {
        return toString().compareTo(o.toString());
    }
    
    @Override
    public String toString() {
        return file.getName();
    }
    
    public Context getContext() {
        return context;
    }
    
    public Icon getIcon() {
        // Default file/folder icon gets used
        return null;
    }
    
    public void reload() {
        children = null;
    }
    
    abstract void checkChildren();
}
