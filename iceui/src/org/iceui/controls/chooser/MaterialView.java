package org.iceui.controls.chooser;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.MaterialList;

import icetone.controls.buttons.SelectableItem;
import icetone.core.BaseScreen;
import icetone.extras.chooser.AbstractButtonView;

public class MaterialView extends AbstractButtonView<String> {

	public MaterialView(BaseScreen screen) {
		super("material-view", screen);
	}

	@Override
	protected void configureButton(SelectableItem selectable, final String path) {
		super.configureButton(selectable, path);

		// Load the material to try and find a texture that can be used
		MaterialList materialList = getMaterialList(path);
		if (materialList != null) {
			Material mat = materialList.get(path);
			for (MatParam p : mat.getParams()) {
				if (p instanceof MatParamTexture) {
					final String texPath = ((MatParamTexture) p).getTextureValue().getName();
					if (texPath != null) {
						selectable.setButtonIcon(texPath);
						break;
					}
				}
			}
		}
	}

	protected MaterialList getMaterialList(String path) {
		throw new UnsupportedOperationException();
	}
}
