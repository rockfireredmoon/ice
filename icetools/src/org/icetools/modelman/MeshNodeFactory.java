package org.icetools.modelman;

import java.io.File;

public class MeshNodeFactory implements FileNodeFactory<MeshNode> {

    @Override
    public MeshNode create(Context context, PropNode parentNode, File file) {
        if (file.isFile() && file.getName().endsWith(".mesh")) {
            // Make sure this file isn't dealt with by another child node at the
            // same level
            final int indexOf = file.getName().indexOf('.');
            if (indexOf != -1) {
                String bn = file.getName().substring(0, indexOf);
                File cn = new File(file.getParentFile(), bn + ".csm.xml");
                if (!cn.exists()) {
                    return new MeshNode(context, file, parentNode);
                }
            }
        }
        return null;
    }

}
