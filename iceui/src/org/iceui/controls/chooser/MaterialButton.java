package org.iceui.controls.chooser;

import org.icelib.Icelib;

import com.jme3.font.LineWrapMode;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.MaterialList;

import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;

public class MaterialButton extends Element {

	private final ChooserButton selectButton;
	private final String path;

	public MaterialButton(final ChooserPanel chooser, float previewSize, final String path, ElementManager screen,
			MaterialList materialList) {
		super(screen);

		String imagePath = screen.getStyle("Common").getString("materialIcon");

		// Load the material to try and find a texture that can be used
		if (materialList != null) {
			Material mat = materialList.get(path);
			for (MatParam p : mat.getParams()) {
				if (p instanceof MatParamTexture) {
					final String texPath = ((MatParamTexture) p).getTextureValue().getName();
					if (texPath != null) {
						imagePath = texPath;
					}
				}
			}
		}

		this.path = path;

		//
		Label xLabel = new Label(Icelib.getBaseFilename(path), screen);
		xLabel.setTextWrap(LineWrapMode.Character);
		selectButton = new ChooserButton(path, imagePath, screen, chooser, previewSize);

		//
		setLayoutManager(new MigLayout(screen, "wrap 1", "[]", "[][]"));
		addChild(xLabel);
		addChild(selectButton, "ax 50%");
	}

	public String getPath() {
		return path;
	}
}
