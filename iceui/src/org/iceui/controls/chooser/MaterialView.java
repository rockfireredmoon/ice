package org.iceui.controls.chooser;

import com.jme3.material.MaterialList;

import icetone.core.Element;
import icetone.core.ElementManager;

public class MaterialView extends AbstractButtonView {

	public MaterialView(ElementManager screen) {
        super(screen);
    }

    @Override
    protected Element createButton(final String path) {
        return new MaterialButton(chooser, previewSize, path, screen, getMaterialList(path));
    }

	protected MaterialList getMaterialList(String path) {
		throw new UnsupportedOperationException();
	}
}
