package org.iceui.controls.chooser;

import icetone.core.Element;
import icetone.core.ElementManager;

/**
 * {@link  ChooserDialog.ChooserView} that lists resources as image thumbnails.
 * Appropriate for any image type supported by JME3.
 */
public class ImageThumbView extends AbstractButtonView {


    public ImageThumbView(ElementManager screen) {
        super(screen);
    }

    @Override
    protected Element createButton(String path) {
        return new ChooserButton(path, path, screen, chooser, previewSize);
    }

}
