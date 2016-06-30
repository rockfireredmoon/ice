/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.icelib.beans.MappedMap;
import org.icesquirrel.runtime.SquirrelArray;
import org.icesquirrel.runtime.SquirrelTable;

@SuppressWarnings("serial")
public class ClothingTemplate extends AbstractTemplate<ClothingTemplateKey, ClothingTemplate, ClothingTemplate> {

	private BodyType bodyType = BodyType.NORMAL;
	private Map<String, String> regionLayers = new MappedMap<>(String.class, String.class);
	private Map<Region, String> regions = new MappedMap<>(new EnumMap<Region, String>(Region.class), Region.class, String.class);
	private Map<Region, String> regionPriority = new MappedMap<>(new EnumMap<Region, String>(Region.class), Region.class,
			String.class);

	private String greaveL;
	private String greaveR;
	private String gauntletL;
	private String gauntletR;

	public ClothingTemplate() {
		super();
	}

	public ClothingTemplate(ClothingTemplateKey key) {
		super(key);
	}

	@Override
	public ClothingTemplate clone() {
		ClothingTemplate d = new ClothingTemplate();
		configureClone(d);
		return d;
	}

	protected void configureClone(ClothingTemplate d) {
		super.configureClone(d);
		d.bodyType = bodyType;
		d.regions.putAll(regions);
		d.regionPriority.putAll(regions);
		d.greaveL = greaveL;
		d.gauntletL = gauntletL;
		d.greaveR = greaveR;
		d.gauntletR = gauntletR;
	}

	public String getGreaveL() {
		return greaveL;
	}

	public void setGreaveL(String greaveL) {
		this.greaveL = greaveL;
	}

	public String getGreaveR() {
		return greaveR;
	}

	public void setGreaveR(String greaveR) {
		this.greaveR = greaveR;
	}

	public String getGauntletL() {
		return gauntletL;
	}

	public void setGauntletL(String gauntletL) {
		this.gauntletL = gauntletL;
	}

	public String getGauntletR() {
		return gauntletR;
	}

	public void setGauntletR(String gauntletR) {
		this.gauntletR = gauntletR;
	}

	public Map<String, String> getRegionLayers() {
		return regionLayers;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}

	public SquirrelTable toSquirrel() {
		SquirrelTable ob = new SquirrelTable();

		// Body type
		if (bodyType != null) {
			ob.put("bodyType", Icelib.toEnglish(bodyType));
		}

		// Colors
		SquirrelArray colorsOb = new SquirrelArray();
		SquirrelArray paletteOb = new SquirrelArray();
		SquirrelArray plist = new SquirrelArray();
		SquirrelArray clist = new SquirrelArray();
		Iterator<ColourPalette> it = getPalette().iterator();
		// for (RGB c : colors) {
		// clist.add(Icelib.toHexNumber(c));
		// plist.add(it.next().getKey());
		// }
		ob.put("colors", clist);
		ob.put("palette", plist);

		// Regions
		if (!regionPriority.isEmpty()) {
			SquirrelTable regionsPriorityOb = new SquirrelTable();
			for (Map.Entry<Region, String> en : regionPriority.entrySet()) {
				regionsPriorityOb.put(en.getKey().name().toLowerCase(), en.getValue());
			}
			ob.put("region_priority", regionsPriorityOb);
		}

		// Regions
		SquirrelTable regionsOb = new SquirrelTable();
		for (Map.Entry<Region, String> en : regions.entrySet()) {
			regionsOb.put(en.getKey().name().toLowerCase(), en.getValue());
		}
		ob.put("regions", regionsOb);

		return ob;
	}

	public Map<Region, String> getRegions() {
		return regions;
	}

	public Map<Region, String> getRegionPriority() {
		return regionPriority;
	}

	public BodyType getBodyType() {
		return bodyType;
	}

}