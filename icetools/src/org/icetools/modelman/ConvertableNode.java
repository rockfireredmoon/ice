package org.icetools.modelman;

import java.io.File;

public abstract class ConvertableNode extends PropNode {

    public ConvertableNode(Context fileNodeBuilder, File root) {
        super(fileNodeBuilder, root);
    }

    public ConvertableNode(Context context, File file, PropNode parent) {
        super(context, file, parent);
    }

    abstract boolean isHasXML();

    abstract File getXML();

    abstract boolean isNeedsConvert();
}
