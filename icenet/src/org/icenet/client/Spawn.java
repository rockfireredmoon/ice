package org.icenet.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Armed;
import org.icelib.Icelib;
import org.icelib.Persona;
import org.icelib.Point3D;

public class Spawn {

	private static final Logger LOG = Logger.getLogger(Spawn.class.getName());
	short rotation;
	long id;
	Point3D location = null;
	Point3D serverLocation = new Point3D(0, Float.MIN_VALUE, 0);
	int direction;
	Persona persona;
	long zone;
	boolean ready;
	int instance;
	Armed armed = Armed.UNARMED;
	short speed;
	short heading;
	private List<SpawnListener> listeners = new ArrayList();
	private float height;
	public int elevation;

	public Spawn(long id) {
		this.id = id;
	}

	public int getElevation() {
		return elevation;
	}

	public short getSpeed() {
		return speed;
	}

	public void setSpeed(short speed) {
		this.speed = speed;
	}

	public short getHeading() {
		return heading;
	}

	public void setHeading(short heading) {
		this.heading = heading;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		LOG.info(String.format("Player height is %2.2f", height));
		this.height = height;
	}

	public Armed getArmed() {
		return armed;
	}

	public int getInstance() {
		return instance;
	}

	public boolean isReady() {
		return ready;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public void setLocation(Point3D location) {
		if (!location.equals(this.location)) {
			Point3D oldLoc = this.location == null ? null : this.location.clone();
			this.location = location;
			fireMoved(oldLoc, rotation, heading, speed);
		}
	}

	public void move(Point3D location, short rotation, short heading, short speed) {
		int oldRot = this.rotation;
		int oldHeading = this.heading;
		int oldSpeed = this.speed;
		Point3D oldLoc = this.location == null ? null : this.location.clone();

		if (!Objects.equals(oldLoc, location) || oldRot != rotation || oldHeading != heading || oldSpeed != speed) {
			LOG.info(String.format("Setting velocity rot: %d / %d  head: %d / %d  speed:  %d / %d", oldRot, rotation, oldHeading,
					heading, oldSpeed, speed));
			this.rotation = rotation;
			this.heading = heading;
			this.speed = speed;
			this.location = location;
			fireMoved(oldLoc, oldRot, oldHeading, oldSpeed);
		}
	}

	public void setVelocity(short rotation, short heading, short speed) {
		int oldRot = this.rotation;
		int oldHeading = this.heading;
		int oldSpeed = this.speed;
		if (oldRot != rotation || oldHeading != heading || oldSpeed != speed) {
			LOG.info(String.format("Setting velocity rot: %d / %d  head: %d / %d  speed:  %d / %d", oldRot, rotation, oldHeading,
					heading, oldSpeed, speed));
			this.rotation = rotation;
			this.heading = heading;
			this.speed = speed;
			fireMoved(this.location, oldRot, oldHeading, oldSpeed);
		}
	}

	public short getRotation() {
		return rotation;
	}

	public void addListener(SpawnListener l) {
		listeners.add(l);
	}

	public Point3D getLocation() {
		return location;
	}

	public Point3D getServerLocation() {
		return serverLocation;
	}

	public void removeListener(SpawnListener l) {
		listeners.add(l);
	}

	public long getId() {
		return id;
	}

	public long getZone() {
		return zone;
	}

	@Override
	public String toString() {
		return "Spawn{" + "rotation=" + rotation + ", id=" + id + ", location=" + location + ", serverLocation=" + serverLocation
				+ ", direction=" + direction + ", persona=" + persona + ", zone=" + zone + ", ready=" + ready + ", instance="
				+ instance + ", armed=" + armed + '}';
	}

	public void recalcElevation() {
		location.y = Float.MIN_VALUE;
		fireRecalcElevation();
	}

	void fireRecalcElevation() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).recalcElevation(this);
		}
	}

	void fireJump() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).jump(this);
		}
	}

	void fireMoved(Point3D oldLocation, int oldRotation, int oldHeading, int oldSpeed) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Firing moved of %s changed from %s to %s - %d,%d,%d to %d,%d,%d", getId(), oldLocation,
					location, oldRotation, oldHeading, oldSpeed, rotation, heading, speed));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).moved(this, oldLocation, oldRotation, oldHeading, oldSpeed);
		}
	}

	void fireServerLocationChanged(Point3D oldLocation, boolean warp) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Firing Location of %s changed from %s to %s", getId(), oldLocation, serverLocation));
		}

		Icelib.removeMe("Firing Location of %s changed from %s to %s", getId(), oldLocation, serverLocation);
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).serverLocationChanged(this, oldLocation, warp);
		}
	}

	void fireEquipmentChanged() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).equipmentChanged(this);
		}
	}

	void fireStatsChanged() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).equipmentChanged(this);
		}
	}

	void fireArmedChanged(Armed armed) {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).armedChanged(this, armed);
		}
	}

	void fireAppearanceChange() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).appearanceChange(this);
		}
	}

	void fireDestroyed() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).destroyed(this);
		}
	}
}
