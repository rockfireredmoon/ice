/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

public class Coin {
	private long coin;
	private short copper;
	private short silver;
	private short gold;

	public Coin() {
	}

	@Override
	public Coin clone() {
		Coin c = new Coin();
		c.copper = copper;
		c.gold = gold;
		c.silver = silver;
		c.copper = copper;
		return c;
	}

	public Coin(long coin) {
		this.coin = coin;
		recalc();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + (int) (this.coin ^ (this.coin >>> 32));
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
		final Coin other = (Coin) obj;
		if (this.coin != other.coin) {
			return false;
		}
		return true;
	}

	public void setCoin(long coin) {
		this.coin = coin;
		recalc();
	}

	public long getCoin() {
		return coin;
	}

	public short getCopper() {
		return copper;
	}

	public short getSilver() {
		return silver;
	}

	public short getGold() {
		return gold;
	}

	@Override
	public String toString() {
		return "Coin{" + "coin=" + coin + ", copper=" + copper + ", silver=" + silver + ", gold=" + gold + '}';
	}

	private void recalc() {
		gold = (short) (coin / 10000);
		silver = (short) ((coin - (gold * 10000)) / 100);
		copper = (short) ((coin - (gold * 10000) - (silver * 100)));
	}
}
