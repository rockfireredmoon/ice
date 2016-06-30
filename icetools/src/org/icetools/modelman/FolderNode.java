package org.icetools.modelman;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FolderNode extends PropNode {

    public FolderNode(Context context, File root) {
        this(context, root, null);
    }

    public FolderNode(Context context, File file, PropNode parent) {
        super(context, file, parent);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    void checkChildren() {
        if (!file.isDirectory()) {
            throw new IllegalStateException("Not a directory");
        }
        if (children == null) {
            children = new ArrayList<>();
            for (File f : file.listFiles()) {
                PropNode create = context.getBuilder().create(this, f);
                if (create != null) {
                    children.add(create);
                }
            }
            Collections.sort(children);
        }
    }
}
