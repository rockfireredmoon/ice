package org.icenet.client;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class GameServer {

	public enum Access {
		PRIVATE, PUBLIC, DEVELOPER, LOCAL, UNKNOWN
	}

	private String name;
	private String description;
	private String owner;
	private String ownerEmail;
	private String url;
	private int capacity;
	private String assetUrl;
	private String routerAddress;
	private String simulatorAddress;
	private Access access;
	private boolean userDefined;
	private String info;

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isUserDefined() {
		return userDefined;
	}

	public void setUserDefined(boolean userDefined) {
		this.userDefined = userDefined;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getAssetUrl() {
		return assetUrl;
	}

	public void setAssetUrl(String assetUrl) {
		this.assetUrl = assetUrl;
	}

	public String getRouterAddress() {
		return routerAddress;
	}

	public void setRouterAddress(String routerAddress) {
		this.routerAddress = routerAddress;
	}

	public String getDisplayAddress() {
		if (StringUtils.isNotBlank(getRouterAddress())) {
			return getRouterAddress();
		} else if (StringUtils.isNotBlank(getSimulatorAddress())) {
			return getSimulatorAddress();
		} else {
			try {
				URL url = new URL(getAssetUrl());
				return url.getHost();
			} catch (Exception e) {
				return "Unknown";
			}
		}
	}

	public String getSimulatorAddress() {
		return simulatorAddress;
	}

	public void setSimulatorAddress(String simulatorAddress) {
		this.simulatorAddress = simulatorAddress;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	@Override
	public String toString() {
		return "GameServer [name=" + name + ", description=" + description + ", owner=" + owner + ", ownerEmail="
				+ ownerEmail + ", url=" + url + ", capacity=" + capacity + ", assetUrl=" + assetUrl + ", routerAddress="
				+ routerAddress + ", simulatorAddress=" + simulatorAddress + ", access=" + access + "]";
	}

}
