package org.icetools.modelman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public abstract class Console extends JPanel implements UncaughtExceptionHandler, Output {

	private JTextPane text;
	private StyledDocument doc;
	private Style errorStyle;
	private Style stdStyle;

	public Console() {
		super(new BorderLayout());
		text = new JTextPane();
		text.setFont(new Font("Monospaced", Font.PLAIN, 12));
		doc = text.getStyledDocument();
		add(new JScrollPane(text), BorderLayout.CENTER);

		errorStyle = text.addStyle("Error", null);
		StyleConstants.setForeground(errorStyle, Color.red);

		stdStyle = text.addStyle("Std", null);
		StyleConstants.setForeground(stdStyle, Color.blue);
	}

	public void error(String text, Throwable exception) {
		if (text != null) {
			error(text);
		}
		if (exception != null) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			error(sw.toString());
		}
	}

	public void message(String text) {
		try {
			doc.insertString(doc.getLength(), text + "\n", stdStyle);
		} catch (BadLocationException e) {
		}
	}

	public void error(String text) {
		try {
			doc.insertString(doc.getLength(), text + "\n", errorStyle);
		} catch (BadLocationException e) {
		}
	}

	public OutputStream getStandardStream() {
		return getStream(stdStyle);
	}

	OutputStream getStream(final Style style) {
		return new OutputStream() {
			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							doc.insertString(doc.getLength(), new String(b, off, len), style);
						} catch (BadLocationException e) {
						}
						text.setCaretPosition(doc.getLength());
						text.scrollRectToVisible(text.getVisibleRect());
					}
				});
			}

			@Override
			public void write(final int b) throws IOException {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							doc.insertString(doc.getLength(), String.valueOf((char) b), style);
						} catch (BadLocationException e) {
						}
						text.setCaretPosition(doc.getLength());
						text.scrollRectToVisible(text.getVisibleRect());
					}
				});
			}
		};
	}

	public OutputStream getErrorStream() {
		return getStream(errorStyle);
	}

	@Override
	public void uncaughtException(Thread t, final Throwable e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				error(null, e);
			}
		});
	}
}
