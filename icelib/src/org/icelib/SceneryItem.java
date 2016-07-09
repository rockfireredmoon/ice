/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.LinkedHashMap;
import java.util.Map;

public class SceneryItem {

	public enum Type {
		BUILDING, PROP, DUNGEON, CAVE, MANIPULATOR, EFFECT, CL;

		public static Type fromPath(String asset) {
			if (asset.startsWith("Bldg")) {
				return Type.BUILDING;
			} else if (asset.startsWith("Cav")) {
				return Type.CAVE;
			} else if (asset.startsWith("Dng")) {
				return Type.DUNGEON;
			} else if (asset.startsWith("Prop")) {
				return Type.PROP;
			} else if (asset.startsWith("Par")) {
				return Type.EFFECT;
			} else if (asset.startsWith("CL")) {
				return Type.CL;
			} else if (asset.startsWith("Manipulator")) {
				return Type.MANIPULATOR;
			}
			throw new IllegalArgumentException("Unknown type. " + asset);
		}

		public boolean hasSubDir() {
			switch (this) {
			case BUILDING:
			case DUNGEON:
			case CAVE:
			case CL:
			case PROP:
				return true;
			default:
				return false;
			}
		}

		public String assetPath() {
			switch (this) {
			case BUILDING:
				return "Bldg";
			case DUNGEON:
				return "Dng";
			case CAVE:
				return "Cav";
			case CL:
				return "CL";
			case EFFECT:
				return "Effects";
			case MANIPULATOR:
				return "Manipulator";
			default:
				return "Prop";
			}
		}
	}

	private Point3D location = new Point3D();
	private Point3D scale = new Point3D(1, 1, 1);
	private Point4D rotation = new Point4D(0, 0, 0, 1);
	private String asset;
	private long id;
	private boolean locked;
	private boolean primary;
	private int layer;
	// TODO not sure ... name doesn't seem to work with server i have
	private String name = "UnknownMaybeBugInServer";
	private Map<String, String> variables;
	private Map<String, String> defaultVariables = new LinkedHashMap<>();
	private Type type = Type.PROP;

	public SceneryItem() {
		this(new LinkedHashMap<String, String>());
	}

	public SceneryItem(String assetWithParameters) {
		setAsset(assetWithParameters);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setAsset(String assetWithParameters) {
		asset = assetWithParameters;
		int idx = asset.indexOf('#');
		String assetName = idx == -1 ? asset : asset.substring(idx + 1);
		type = Type.fromPath(assetName);
		variables = new LinkedHashMap<String, String>();
		idx = asset.indexOf('?');
		if (idx != -1) {
			String[] vars = asset.substring(idx + 1).split("\\&+");
			asset = asset.substring(0, idx);
			for (String v : vars) {
				idx = v.indexOf('=');
				String key = v;
				String val = null;
				if (idx != -1) {
					key = v.substring(0, idx);
					val = Icelib.decodeAssetUrlValue(v.substring(idx + 1));
				}
				variables.put(key, val);
			}
		}
	}

	protected SceneryItem(Map<String, String> variables) {
		this.variables = variables;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public Point3D getLocation() {
		return location;
	}

	public void setLocation(Point3D location) {
		this.location = location;
	}

	public Point3D getScale() {
		return scale;
	}

	public void setScale(Point3D scale) {
		this.scale = scale;
	}

	public Point4D getRotation() {
		return rotation;
	}

	public void setRotation(Point4D rotation) {
		this.rotation = rotation;
	}

	public void setDefaultVariable(String variableName, String value) {
		if (!variables.containsKey(variableName)) {
			variables.put(variableName, value);
		}
		defaultVariables.put(variableName, value);
	}

	/**
	 * Get the full asset including encoded parameters
	 * 
	 * @return encoded asset with parameters
	 */
	public String getAsset() {
		StringBuilder bui = new StringBuilder();
		bui.append(getAssetName());
		if (!variables.isEmpty()) {
			bui.append("?");
			boolean first = false;
			for (Map.Entry<String, String> en : variables.entrySet()) {
				if (defaultVariables.containsKey(en.getKey()) && defaultVariables.get(en.getKey()).equals(en.getValue())) {
					// Same as default, so skip it
					continue;
				}
				if (first) {
					bui.append("&");
				}
				first = true;
				bui.append(en.getKey());
				if (en.getValue() != null) {
					bui.append("=");
					bui.append(Icelib.encodeAssetUrlValue(en.getValue()));
				}
			}
		}
		return bui.toString();
	}

	/**
	 * Get the asset name (without any parameters)
	 * 
	 * @return asset name
	 */
	public String getAssetName() {
		return asset;
	}

	/**
	 * Set the asset name (without any parameters)
	 * 
	 * @param name
	 *            name
	 */
	public void setAssetName(String name) {
		this.asset = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SceneryItem other = (SceneryItem) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SceneryItem [location=" + location + ", scale=" + scale + ", rotation=" + rotation + ", asset=" + getAsset()
				+ ", id=" + id + ", locked=" + locked + ", primary=" + primary + ", layer=" + layer + ", name=" + name + ", type="
				+ type + "]";
	}

}
