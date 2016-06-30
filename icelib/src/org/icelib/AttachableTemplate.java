/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.icelib.beans.MappedList;
import org.icelib.beans.MappedMap;

public class AttachableTemplate extends AbstractTemplate<EntityKey, AttachableTemplate, AttachableTemplate> {

	private static final long serialVersionUID = 1L;
	private List<AttachmentPoint> attachPoints = new MappedList<AttachmentPoint>(AttachmentPoint.class);
	private Map<AttachmentPoint, String> attachAliases = new MappedMap<AttachmentPoint, String>(
			new EnumMap<AttachmentPoint, String>(AttachmentPoint.class), AttachmentPoint.class, String.class);
	private boolean particle;
	private AttachmentRibbon ribbon;
	private List<String> particles = new MappedList<String>(String.class);
	private boolean illuminated;
	private boolean animated;
	private String mesh;
	private String entity;

	public AttachableTemplate() {
		super();
	}

	public AttachableTemplate(EntityKey key) {
		super(key);
	}

	@Override
	protected void configureClone(AttachableTemplate d) {
		super.configureClone(d);
		d.entity = entity;
		d.mesh = mesh;
		d.animated = animated;
		d.illuminated = illuminated;
		d.particles.addAll(particles);
		d.ribbon = ribbon == null ? null : (AttachmentRibbon) ribbon.clone();
		d.particle = particle;
		d.attachAliases.putAll(attachAliases);
		d.attachPoints.addAll(attachPoints);
	}

	public AttachableTemplate clone() {
		AttachableTemplate t = new AttachableTemplate();
		configureClone(t);
		return t;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getMesh() {
		return mesh;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	public boolean isIlluminated() {
		return illuminated;
	}

	public void setIlluminated(boolean illuminated) {
		this.illuminated = illuminated;
	}

	public List<String> getParticles() {
		return particles;
	}

	public void setParticles(List<String> particles) {
		this.particles = particles;
	}

	public AttachmentRibbon getRibbon() {
		return ribbon;
	}

	public void setRibbon(AttachmentRibbon ribbon) {
		this.ribbon = ribbon;
	}

	public void setParticle(boolean particle) {
		this.particle = particle;
	}

	public boolean isParticle() {
		return particle;
	}

	public Map<AttachmentPoint, String> getAttachAliases() {
		return attachAliases;
	}

	public List<AttachmentPoint> getAttachPoints() {
		return attachPoints;
	}

	@Override
	public int compareTo(AttachableTemplate o) {
		return getKey().compareTo(o.getKey());
	}
}