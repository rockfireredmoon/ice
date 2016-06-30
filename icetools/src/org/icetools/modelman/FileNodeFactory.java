package org.icetools.modelman;

import java.io.File;

public interface FileNodeFactory<T extends PropNode> {

    T create(Context context, PropNode parentNode, File file);
}
