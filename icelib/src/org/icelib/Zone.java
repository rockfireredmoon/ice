/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

public class Zone {

	private String name;
	private int id;
	private String description;
	private String terrainConfig;
	private String environmentType;
	private String regions;
	private String shardName;
	private String mapName;
	private String warpName;
	private boolean persist;
	private Point3D defaultLocation;
	private int pageSize = 1000;

	public Zone() {
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTerrainConfig(String terrainConfig) {
		this.terrainConfig = terrainConfig;
	}

	public void setRegions(String regions) {
		this.regions = regions;
	}

	public void setShardName(String shardName) {
		this.shardName = shardName;
	}

	public void setWarpName(String warpName) {
		this.warpName = warpName;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public void setDefaultLocation(Point3D defaultLocation) {
		this.defaultLocation = defaultLocation;
	}

	public Zone(String name, int id, String description, String terrainConfig, String environmentType, String regions,
			String shardName, String warpName, boolean persist, Point3D defaultLocation) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.terrainConfig = terrainConfig;
		this.environmentType = environmentType;
		this.regions = regions;
		this.shardName = shardName;
		this.warpName = warpName;
		this.persist = persist;
		this.defaultLocation = defaultLocation;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTerrainConfig() {
		return terrainConfig;
	}

	public String getEnvironmentType() {
		return environmentType;
	}

	public String getRegions() {
		return regions;
	}

	public String getShardName() {
		return shardName;
	}

	public String getWarpName() {
		return warpName;
	}

	public boolean isPersist() {
		return persist;
	}

	public Point3D getDefaultLocation() {
		return defaultLocation;
	}

	public void setEnvironmentType(String environmentType) {
		this.environmentType = environmentType;
	}

	@Override
	public String toString() {
		return "Zone{" + "name=" + name + ", id=" + id + ", description=" + description + ", terrainConfig=" + terrainConfig
				+ ", environmentType=" + environmentType + ", regions=" + regions + ", shardName=" + shardName + ", warpName="
				+ warpName + ", persist=" + persist + ", defaultLocation=" + defaultLocation + '}';
	}
}
