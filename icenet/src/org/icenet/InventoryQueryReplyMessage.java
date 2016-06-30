package org.icenet;

import java.util.logging.Logger;

/**
 * Item query reply (game mode)
 */
public class InventoryQueryReplyMessage extends SimulatorMessage {

    //0000   // 46 00 15 ++ 01 31 ff ff 00 00 9c 5b 00 00 00 00 00  F...1.....[.....
    //0010   00 00 00 00 ff ff ff ff // 46 ++ 00 15 ++ 01 32 ff ff 00  ........F...2...
    //0020   00 9c 5c 00 00 00 00 00 00 00 00 00 ff ff ff ff //  ..\.............
    //0030   46 ++ 00 15 ++ 01 33 ff ff 00 00 9c 5d 00 00 00 00 00  F...3.....].....
    //0040   00 00 00 00 ff ff ff ff // 46 ++ 00 15 ++ 01 34 ff ff 00  ........F...4...
    //0050   00 9c 5e 00 00 00 00 00 00 00 00 00 ff ff ff ff //  ..^.............
    //0060   46 ++ 00 15 ++ 01 35 ff ff 00 00 9c 5f 00 00 00 00 00  F...5....._.....
    //0070   00 00 00 00 ff ff ff ff 46 00 15 01 36 ff ff 00  ........F...6...
    //0080   00 9c 60 00 00 00 00 00 00 00 00 00 ff ff ff ff  ..`.............
    //0090   46 00 15 01 37 ff ff 00 00 9c 61 00 00 00 00 00  F...7.....a.....
    //00a0   00 00 00 00 ff ff ff ff 46 00 15 01 38 ff ff 00  ........F...8...
    //00b0   00 9c 62 00 00 00 00 00 00 00 00 00 ff ff ff ff  ..b.............
    //00c0   46 00 15 01 41 ff ff 00 00 9c 64 00 00 00 00 00  F...A.....d.....
    //00d0   00 00 00 00 ff ff ff ff 46 00 15 01 45 ff ff 00  ........F...E...
    //00e0   00 9c 68 00 00 00 00 00 00 00 00 00 ff ff ff ff  ..h.............
    //00f0   46 00 15 01 46 ff ff 00 00 9c 69 00 00 00 00 00  F...F.....i.....
    //0100   00 00 00 00 ff ff ff ff 46 00 16 02 31 30 ff ff  ........F...10..
    //0110   00 00 9c 6a 00 00 00 00 00 00 00 00 00 ff ff ff  ...j............
    //0120   ff 46 00 16 02 31 31 ff ff 00 00 9c 6b 00 00 00  .F...11.....k...
    //0130   00 00 00 00 00 00 ff ff ff ff 46 00 16 02 31 32  ..........F...12
    //0140   ff ff 00 00 9c 6c 00 00 00 00 00 00 00 00 00 ff  .....l..........
    //0150   ff ff ff 46 00 16 02 31 33 ff ff 00 00 9c 6d 00  ...F...13.....m.
    //0160   00 00 00 00 00 00 00 00 ff ff ff ff 46 00 16 02  ............F...
    //0170   31 34 ff ff 00 00 9c 6e 00 00 00 00 00 00 00 00  14.....n........
    //0180   00 ff ff ff ff 46 00 16 02 31 35 ff ff 00 00 9c  .....F...15.....
    //0190   6f 00 00 00 00 00 00 00 00 00 ff ff ff ff 46 00  o.............F.
    //01a0   16 02 31 36 ff ff 00 00 9c 70 00 00 00 00 00 00  ..16.....p......
    //01b0   00 00 00 ff ff ff ff 46 00 16 02 31 37 ff ff 00  .......F...17...
    //01c0   00 9c 71 00 00 00 00 00 00 00 00 00 ff ff ff ff  ..q.............
    //01d0   46 00 15 01 39 ff ff 00 00 50 2a 00 00 00 00 00  F...9....P*.....
    //01e0   00 00 00 00 ff ff ff ff 46 00 15 01 42 ff ff 00  ........F...B...
    //01f0   00 50 95 00 00 00 00 00 00 00 00 00 ff ff ff ff  .P..............
    //0200   46 00 15 01 43 ff ff 00 0f 57 a6 00 00 00 00 00  F...C....W......
    //0210   00 00 00 00 ff ff ff ff // 46 00 15 ++ 01 44 ff ff 00  ........F...D...
    //0220   2c 40 74 00 00 00 00 00 00 00 00 00 ff ff ff ff  ,@t.............                                         .
    private static final Logger LOG = Logger.getLogger(InventoryQueryReplyMessage.class.getName());
    
    private int slot;
    private short unknown1;
    private short unknown2;
    private long itemId;
    private long unknown4;
    private int qty;
    private short unknown5;
    private short unknown6;
    private short unknown7;
    private long unknown8;
    

    public InventoryQueryReplyMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload.rewind();
        slot = Integer.valueOf(readString(), 16) - 1;
        unknown1 = readUnsignedByte();
        unknown2 = readUnsignedByte();
        itemId = readUnsignedInt();        
        unknown4 = readUnsignedInt();
        qty = readUnsignedShort() + 1;
        
        unknown5 = readUnsignedByte();
        unknown6 = readUnsignedByte();
        unknown7 = readUnsignedByte();
        unknown8 = readUnsignedInt();

        if (payload.remaining() > 0) {
            LOG.warning(String.format("%d remaining bytes for %s", payload.remaining(), this));
        }

    }

    public int getSlot() {
        return slot;
    }

    public short getUnknown1() {
        return unknown1;
    }

    public short getUnknown2() {
        return unknown2;
    }

    public long getItemId() {
        return itemId;
    }

    public long getUnknown4() {
        return unknown4;
    }

    public int getQty() {
        return qty;
    }

    public short getUnknown5() {
        return unknown5;
    }

    public short getUnknown6() {
        return unknown6;
    }

    public short getUnknown7() {
        return unknown7;
    }

    public long getUnknown8() {
        return unknown8;
    }

    @Override
    public String toString() {
        return "InventoryQueryReplyMessage{" + "slot=" + slot + ", unknown1=" + unknown1 + ", unknown2=" + unknown2 + ", itemId=" + itemId + ", unknown4=" + unknown4 + ", qty=" + qty + ", unknown5=" + unknown5 + ", unknown6=" + unknown6 + ", unknown7=" + unknown7 + ", unknown8=" + unknown8 + '}';
    }


}
