package org.icenet;

import java.util.logging.Logger;

public class CreatureEventReplyMessage extends SimulatorMessage {
	private static final Logger LOG = Logger.getLogger(CreatureEventReplyMessage.class.getName());

	public static final int SUBMSG_LOGOUT = 0;
	public static final int SUBMSG_AVATAR_CHANGED = 1;
	public static final int SUBMSG_JUMP = 3;
	public static final int SUBMSG_EFFECT = 4;
	public static final int SUBMSG_TARGET = 5;
	public static final int SUBMSG_DAMAGE = 7;
	public static final int SUBMSG_MISS = 8;
	public static final int SUBMSG_PARRY = 9;
	public static final int SUBMSG_COUNTDOWN = 11;
	public static final int SUBMSG_EFFECT_TARGET = 12;
	public static final int SUBMSG_LOOT = 13;
	public static final int SUBMSG_HENGE_LIST = 14;
	public static final int SUBMSG_HEAL = 15;
	public static final int SUBMSG_SILENCED = 16;
	public static final int SUBMSG_DISARMED = 17;
	public static final int SUBMSG_DODGE = 18;
	public static final int SUBMSG_BLOCK = 19;
	public static final int SUBMSG_SPELL_FAIL = 20;
	public static final int SUBMSG_XP = 21;
	public static final int SUBMSG_COOLDOWN = 22;
	public static final int SUBMSG_ABILITY_POINT_BOUGHT = 23;
	public static final int SUBMSG_PORTAL_REQUEST = 24;
	public static final int SUBMSG_VAULT = 25;
	public static final int SUBMSG_VELOCITY = 26;
	public static final int SUBMSG_REFLECT = 27;
	public static final int SUBMSG_REGENERATE = 28;

	private final long spawnId;
	private SimulatorMessage subMessage;
	private short type;

	public CreatureEventReplyMessage(Simulator sim, SimulatorMessage msg) {
		super(msg);
		setValidForProtocol(Simulator.ProtocolState.GAME);
		payload.rewind();
		spawnId = readUnsignedInt();
		switch (type = readUnsignedByte()) {
		case SUBMSG_LOGOUT:
			subMessage = new LogoutMessage(this);
			break;
		case SUBMSG_AVATAR_CHANGED:
			subMessage = new AvatarChangedMessage(this);
			break;
		case SUBMSG_JUMP:
			subMessage = new IncomingJumpMessage(this);
			break;
		case SUBMSG_EFFECT:
			subMessage = new EffectMessage(this);
			break;
		case SUBMSG_TARGET:
			subMessage = new TargetMessage(this);
			break;
		case SUBMSG_DAMAGE:
			subMessage = new DamageMessage(sim, this);
			break;
		case SUBMSG_MISS:
			subMessage = new MissMessage(this);
			break;
		case SUBMSG_COUNTDOWN:
			subMessage = new CountdownMessage(this);
			break;
		case SUBMSG_EFFECT_TARGET:
			subMessage = new EffectTargetMessage(this);
			break;
		case SUBMSG_LOOT:
			subMessage = new LootMessage(this);
			break;
		case SUBMSG_HENGE_LIST:
			subMessage = new HengeListMessage(this);
			break;
		case SUBMSG_HEAL:
			subMessage = new HealMessage(sim, this);
			break;
		case SUBMSG_SILENCED:
			subMessage = new SilencedMessage(this);
			break;
		case SUBMSG_DISARMED:
			subMessage = new DisarmedMessage(this);
			break;
		case SUBMSG_BLOCK:
			subMessage = new BlockMessage(this);
			break;
		case SUBMSG_SPELL_FAIL:
			subMessage = new SpellFailMessage(this);
			break;
		case SUBMSG_XP:
			subMessage = new XpMessage(this);
			break;
		case SUBMSG_COOLDOWN:
			subMessage = new CooldownMessage(sim, this);
			break;
		case SUBMSG_ABILITY_POINT_BOUGHT:
			subMessage = new AbilityPointBoughtMessage(this);
			break;
		case SUBMSG_PORTAL_REQUEST:
			subMessage = new PortalRequestMessage(sim, this);
			break;
		case SUBMSG_VAULT:
			subMessage = new VaultMessage(this);
			break;
		case SUBMSG_VELOCITY:
			subMessage = new VelocityMessage(this);
			break;
		case SUBMSG_REFLECT:
			subMessage = new ReflectMessage(this);
			break;
		default:
			LOG.warning(String.format("Unknown creature event message %d, %d bytes remaining", type, payload.remaining()));
			break;
		}

	}

	public short getType() {
		return type;
	}

	public long getSpawnId() {
		return spawnId;
	}

	public SimulatorMessage getSubMessage() {
		return subMessage;
	}

	@Override
	public String toString() {
		return "CreatureEventReplyMessage [spawnId=" + spawnId + ", subMessage=" + subMessage + ", type=" + type + "]";
	}

}
