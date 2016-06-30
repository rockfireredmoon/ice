package org.icenet;

import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import org.icelib.Armed;
import org.icelib.ChannelType;
import org.icelib.PageLocation;
import org.icelib.Persona;
import org.icelib.Point3D;
import org.icelib.SceneryItem;
import org.icelib.Zone;
import org.icenet.client.Client;
import org.icenet.client.ClientListener;
import org.icenet.client.Spawn;
import org.icenet.client.SpawnListenerAdapter;

public class TestClient {

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
	}
	private static final Logger LOG = Logger.getLogger(TestClient.class.getName());
	private Client client;
	private List<Persona> personas;

	void connect(String user, char[] pass) throws Exception {
		client = new Client(new URI("http://213.138.112.253"));

		client.connect();
		client.login(user, pass);
	}

	void lobby() throws NetworkException, ParseException {
		LOG.info("Test Lobby Mode");
		personas = client.getPersonas();
		for (Persona p : personas) {
			LOG.info("Persona: " + p);
		}
	}

	void await() throws InterruptedException {
		client.await();
		LOG.info("Exiting");
	}

	void select() throws NetworkException {
		final Persona get = personas.get(0);
		LOG.info("Selected: " + client.select(get));
		client.setClientLoading(false);
		for (Spawn s : client.getSpawns()) {
			LOG.info("Initial spawn: " + s);
		}
		client.addListener(new ClientListener() {
			@Override
			public void zoneChanged(Zone zone) {
				LOG.info("Zone update: " + zone);
			}

			@Override
			public void emote(long id, String sender, String emote) {
			}

			@Override
			public void disconnected(Exception e) {
			}

			@Override
			public void spawned(Spawn spawn) {
				LOG.info("Spawned! " + spawn);
				spawn.addListener(new SpawnListenerAdapter() {
					@Override
					public void equipmentChanged(Spawn spawn) {
						LOG.info("Equipment changed: " + spawn.toString());
					}

					@Override
					public void serverLocationChanged(Spawn spawn, Point3D oldLocation, boolean warpTo) {
						LOG.info("Direction " + spawn.getDirection());
					}

					@Override
					public void moved(Spawn spawn, Point3D oldLocation, int oldRotation, int oldHeading, int oldSpeed) {
						// int ang = spawn.getRotation() < 32768 ? (65535 -
						// spawn.getRotation()) - 32768 : 65535 -
						// (spawn.getRotation() - 32768);

						int ang = spawn.getRotation();

						int deg = (int) (((float) ang / 65536f) * 360f);
						LOG.info("Spawn rotated to: " + spawn.getRotation() + " = " + ang + " deg = " + deg);
					}

					@Override
					public void destroyed(Spawn spawn) {
						LOG.info("Despawned: " + spawn);
						spawn.removeListener(this);
					}

					@Override
					public void appearanceChange(Spawn spawn) {
						LOG.info("Appearance change: " + spawn);
					}

					@Override
					public void statsChanged(Spawn spawn) {
						LOG.info("Stats change: " + spawn);
					}

					@Override
					public void armedChanged(Spawn spawn, Armed armed) {
						LOG.info("Armed change: " + spawn + " = " + armed);
					}

				});
			}

			@Override
			public void chatMessage(String sender, String recipient, ChannelType channel, String text) {
			}

			@Override
			public void message(String message) {
			}

			@Override
			public void inventoryUpdate() {
				LOG.info("Inventory update");
			}

			@Override
			public void propAddedOrUpdated(SceneryItem prop) {
				LOG.info("Prop added or updated: " + prop);
			}

			@Override
			public void propDeleted(SceneryItem prop) {
				LOG.info("Prop deleted: " + prop);
			}

			@Override
			public void playerLoggedIn(String name) {
			}

			@Override
			public void statusChanged(String name, String status) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void shardChanged(String name, String status) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void friendAdded(String name) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void playerLoggedOut(String name) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void popup(String message) {
				// TODO Auto-generated method stub
				
			}
		});

		client.updateInventory();

		InventoryAndEquipment inv = new InventoryAndEquipment(client, get);
		inv.rebuild();
		for (InventoryAndEquipment.EquipmentItem s : inv.getEquipment()) {
			LOG.info(String.format("Adding %s to %s", s.getItem(), s.getSlot()));
		}

		for (HengeListMessage.Henge g : client.getGroves()) {
			LOG.info("Grove: " + g);
		}

		PageLocation pl = new PageLocation(1, 1);
		for (SceneryItem p : client.listScenery(pl, client.getZone())) {
			int tx = (int) (p.getLocation().x / 1920);
			int tz = (int) (p.getLocation().z / 1920);
			LOG.info("Prop: " + p + " tiles: " + tx + "," + tz);
			if (tx == pl.x && tz == pl.y) {
				LOG.info("  YES!");
			}
		}
	}

	void close() throws NetworkException {
		if(client.isConnected()) {
			client.close();
		}
	}

	public static void main(String[] args) throws Exception {
		TestClient r = new TestClient();

		try {
			r.connect(args[0], args[1].toCharArray());
			r.lobby();
			r.select();
			r.await();
		} finally {
			r.close();
		}

	}
}
