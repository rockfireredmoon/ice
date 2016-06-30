package org.icetools.sq;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.icesquirrel.interpreter.SquirrelInterpretedTable;
import org.icesquirrel.runtime.SquirrelPrinter;
import org.icesquirrel.runtime.SquirrelTable;

import net.miginfocom.swing.MigLayout;

public class SqWindows extends JFrame {

	private JTextArea input;
	private JTextArea output;

	public SqWindows() {
		super("Sq");
		setLayout(new MigLayout("wrap 1", "[fill, grow]", "[][:200:,grow][][:200:,grow]"));
		add(new JLabel("Input"));
		input = new JTextArea(10, 80);
		input.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				changed();

			}

			void changed() {
				SquirrelTable t = SquirrelInterpretedTable.table(input.getText());
				output.setText(SquirrelPrinter.format(t, 0));
			}
		});
		add(new JScrollPane(input));
		output = new JTextArea(10, 80);
		add(new JLabel("Output"));
		add(new JScrollPane(output));
	}

	public static void main(String[] args) {
		SqWindows w = new SqWindows();
		w.pack();
		w.setVisible(true);
		w.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
