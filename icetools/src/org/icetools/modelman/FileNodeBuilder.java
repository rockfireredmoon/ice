package org.icetools.modelman;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileNodeBuilder {

    private List<FileNodeFactory<?>> factories = new ArrayList<FileNodeFactory<?>>();
    private Context context;

    public FileNodeBuilder(Context context) {
        this.context = context;
        add(new DirectoryNodeFactory());
    }

    public void add(FileNodeFactory<?> factory) {
        factories.add(0, factory);
    }

    public PropNode create(PropNode parentNode, File file) {
        for (FileNodeFactory<?> factory : factories) {

            // Make sure file isn't already held in another node
            PropNode node = findForFile(file, parentNode);
            if (node == null) {
            	
            	try {
            		node = factory.create(context, parentNode, file);
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
                if (node != null) {
                    node.getChildCount();
                    return node;
                }
            }
        }
        return null;
    }

    PropNode findForFile(File f, PropNode n) {
        if (n == null || (n.getFile().equals(f) || (n instanceof ConvertableNode && ((ConvertableNode) n).getXML().equals(f)))) {
            return n;
        }
        if (!n.isLeaf()) {
            for (int i = 0; i < n.getChildCount(); i++) {
                PropNode fn = findForFile(f, (PropNode) n.getChildAt(i));
                if (fn != null) {
                    return fn;
                }
            }
        }
        return null;
    }

    class DirectoryNodeFactory implements FileNodeFactory<FolderNode> {

        @Override
        public FolderNode create(Context context, PropNode parentNode, File file) {
            if (file.isDirectory()) {
                return new FolderNode(context, file, parentNode);
            }
            return null;
        }
    }
}
