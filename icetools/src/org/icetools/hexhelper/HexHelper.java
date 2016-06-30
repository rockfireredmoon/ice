package org.icetools.hexhelper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;

public class HexHelper extends JFrame {

    private final JTextField hexString;
    private ByteBuffer buffer;
    private final JPanel panel;

    public HexHelper() {
        super("HexHelper");
        setLayout(new BorderLayout());

        hexString = new JTextField();
        hexString.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                stringUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stringUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stringUpdate();
            }
        });
        add(hexString, BorderLayout.NORTH);

        panel = new JPanel(new MigLayout("wrap 2", "[][]", "[]"));
        panel.setPreferredSize(new Dimension(500, 500));
        add(panel, BorderLayout.CENTER);

    }

    private void stringUpdate() {
        StringTokenizer t = new StringTokenizer(hexString.getText());
        List<Byte> b = new ArrayList<Byte>();
        try {
            while (t.hasMoreTokens()) {
                int v = Integer.parseInt(t.nextToken(), 16);
                b.add(Byte.valueOf((byte) v));
            }
        } catch (NumberFormatException nfe) {
            return;
        }
        buffer = ByteBuffer.allocate(b.size());
        for (Byte a : b) {
            buffer.put(a);
        }

        panel.invalidate();
        panel.removeAll();

        // Unsigned Ints
        buffer.rewind();
        boolean first = true;
        while (buffer.remaining() > 3) {
            panel.add(new JLabel(first ? "Unsigned int" : ""));
            panel.add(new JLabel(String.valueOf(readUnsignedInt())));
            first = false;
        }


        // Unsigned Shorts
        buffer.rewind();
        first = true;
        while (buffer.remaining() > 1) {
            panel.add(new JLabel(first ? "Unsigned short" : ""));
            panel.add(new JLabel(String.valueOf(readUnsignedShort())));
            first = false;
        }

        // Bytes
        buffer.rewind();
        first = true;
        while (buffer.remaining() > 0) {
            panel.add(new JLabel(first ? "Byte" : ""));
            panel.add(new JLabel(String.valueOf(readUnsignedByte())));
            first = false;
        }
        //

        panel.validate();
        panel.repaint();
    }

    protected long readUnsignedInt() {
        return (long) buffer.getInt() & 0xffffffff;
    }

    protected int readUnsignedShort() {
        return (int) buffer.getShort() & 0xffff;
    }

    protected short readUnsignedByte() {
        return (short) (buffer.get() & 0xff);
    }

    protected String readString() {
        int w = buffer.get() & 0xff;
        byte[] arr;
        if (w == 0xff) {
            w = buffer.getShort();
            System.err.println("WIDE STRING OF: " + w);
        }
        arr = new byte[w];
        buffer.get(arr);
        return new String(arr);
    }

    public static void main(String[] args) {
        HexHelper hh = new HexHelper();
        hh.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        hh.pack();
        hh.setVisible(true);

    }
}
