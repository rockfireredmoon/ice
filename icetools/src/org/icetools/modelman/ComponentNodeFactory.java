package org.icetools.modelman;

import java.io.File;

public class ComponentNodeFactory implements FileNodeFactory<ComponentNode> {

    @Override
    public ComponentNode create(Context context, PropNode parentNode, File file) {
        if (file.isFile() && file.getName().endsWith(".csm.xml")) {
            return new ComponentNode(context, file, parentNode);
        }
        return null;
    }
}
