package org.icetools.convertfnt;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

public class ConvertFnt {

	private String name;
	private String type;
	private String source;
	private List<Glyph> glyphs = new ArrayList<Glyph>();
	private boolean unicode;
	private File fontDef;
	private int trim;
	
	// fnt to fontdef
	private Properties info;
	private Properties page;
	private Properties common;

	enum State {

		START, GOT_NAME, SECTION, END
	}

	class Glyph {

		char glyph;
		float u1;
		float v1;
		float u2;
		float v2;
		Properties p;
		
		Glyph(Properties p) {
			this.p = p;
		}

		Glyph(String[] args) throws ParseException {
			glyph = parseGlyph(args[1]);
			u1 = Float.parseFloat(args[2]);
			v1 = Float.parseFloat(args[3]);
			u2 = Float.parseFloat(args[4]);
			v2 = Float.parseFloat(args[5]);
		}

		private char parseGlyph(String glyph) throws ParseException {
			if (glyph.startsWith("u") && glyph.length() == 5) {
				unicode = true;
				return (char) (Integer.parseInt(glyph.substring(1)));
			} else if (glyph.length() == 1) {
				return glyph.charAt(0);
			} else {
				throw new ParseException("Failed to parse glyph '" + glyph + "'", 0);
			}
		}
	}

	void saveFontdef(File file) throws IOException {

		PrintWriter pw = new PrintWriter(file);
		try {
			pw.println(info.getProperty("face"));
			pw.println("{");
			pw.println();
			pw.println("\ttype\timage");
			pw.println("\tsource\t" + page.get("file"));
			pw.println();
			
			int sw = Integer.parseInt(common.getProperty("scaleW"));
			int sh = Integer.parseInt(common.getProperty("scaleH"));
			
			int row = 0;
			
			for(Glyph g : glyphs) {
				float x = Integer.parseInt(g.p.getProperty("x"));
				float y = Integer.parseInt(g.p.getProperty("y"));
				float width = Integer.parseInt(g.p.getProperty("width"));
				float height = Integer.parseInt(g.p.getProperty("height"));
				float xoff = Integer.parseInt(g.p.getProperty("xoffset"));
				float yoff = Integer.parseInt(g.p.getProperty("yoffset"));
				
				//char ch = Character.valueOf((char)Integer.parseInt(g.p.getProperty("id")));
				String ch = String.format("u%04d", Integer.parseInt(g.p.getProperty("id")));
				//char ch = (char)('A' + row);
				
				float ax = Math.min(1, Math.max(0,  ( x + xoff ) / (float)sw));
				float ay = Math.min(1, Math.max(0,  ( y + yoff ) / (float)sh));
				float ax2 = Math.min(1, Math.max(0,  ax + ( width / (float)sw )));
				float ay2 = Math.min(1, Math.max(0,  ay + ( height / (float)sh )));
				
				pw.println(String.format("\tglyph %s %.6f %.6f %.6f %.6f", ch, ax, ay, ax2, ay2));
				
				row++;
			}
			
			pw.println("}");
			pw.flush();
		}
		finally {
			pw.close();
		}
	}

	void saveFnt(File file) throws IOException {
		// TODO determine these somehow
		int size = 0;
		boolean bold = false;
		boolean italic = false;
		String charset = "ASCII";
		int stretchH = 100;
		boolean smooth = false;
		boolean aa = false;
		String padding = "0,0,0,0";
		String spacing = "0,0";
		int lineHeight = 0;
		int base = 0;
		int pages = 1;
		int chnl = 0;
		boolean packed = false;
		int charCount = 255;

		// Per glyph
		int xoffset = 0;
		int yoffset = 0;
		int xadvance = 1;

		// Load the image (because of the way relative paths are defined we
		// search up
		// till the source is found)
		if (source == null) {
			throw new IOException("No source found in fontdef file.");
		}
		File dir = fontDef.getParentFile();
		File sourceFile = null;
		while (dir != null) {
			sourceFile = new File(dir, source);
			if (sourceFile.exists()) {
				break;
			}
			dir = dir.getParentFile();
		}
		if (sourceFile == null || !sourceFile.exists()) {
			throw new IOException("Could not find source image.");
		}
		BufferedImage img = ImageIO.read(sourceFile);

		// Can now work out base
		int maxTrimTop = Integer.MAX_VALUE;
		int maxTrimBottom = Integer.MAX_VALUE;

		for (Glyph g : glyphs) {
			int x1 = (int) (img.getWidth() * g.u1);
			int y1 = (int) (img.getHeight() * g.v1);
			int x2 = (int) (Math.ceil(img.getWidth() * g.u2));
			int y2 = (int) (Math.ceil(img.getHeight() * g.v2));
			int h = y2 - y1;
			int w = x2 - x1;
			base = lineHeight = Math.max(base, h);
			size = (int) ((float) base * 0.5f);

			if (h < 1 || w < 1) {
				System.out.println("WARNING: zero size glyph " + g.glyph);
			} else {

				// Now work out what we can trim from the top and bottom
				BufferedImage charImg = img.getSubimage(x1, y1, w, h);
				int[] pixel = new int[4];
				final WritableRaster alphaRaster = charImg.getAlphaRaster();

				// Find how much to trim from top
				int thisTrimTop = 0;
				for (int y = 0; y < h; y++) {
					boolean hasPixel = false;
					for (int i = 0; i < w && !hasPixel; i++) {
						alphaRaster.getPixel(i, y, pixel);
						if (pixel[0] != 0) {
							hasPixel = true;
							break;
						}
					}
					if (hasPixel) {
						break;
					}
					thisTrimTop = Math.max(thisTrimTop, y);
				}

				int thisTrimBottom = 0;
				for (int y = h - 1; y >= 0; y--) {
					boolean hasPixel = false;
					for (int i = 0; i < w && !hasPixel; i++) {
						alphaRaster.getPixel(i, y, pixel);
						if (pixel[0] != 0) {
							hasPixel = true;
							break;
						}
					}
					if (hasPixel) {
						break;
					}
					thisTrimBottom = Math.max(thisTrimBottom, h - y);
				}

				System.out.println("Trim " + thisTrimTop + " from top, " + thisTrimBottom + " from bottom, from char " + g.glyph);
				if (thisTrimBottom > 0) {
					maxTrimBottom = Math.min(maxTrimBottom, thisTrimBottom);
				}
				if (thisTrimTop > 0) {
					maxTrimTop = Math.min(maxTrimTop, thisTrimTop);
				}
			}

		}

		if (maxTrimBottom == Integer.MAX_VALUE) {
			maxTrimBottom = 0;
		}
		if (maxTrimTop == Integer.MAX_VALUE) {
			maxTrimTop = 0;
		}
		System.out.println("Final Trim " + maxTrimTop + " from top, " + maxTrimBottom + " from bottom");

		// Subtract trimming from line height
		lineHeight -= (maxTrimBottom + maxTrimTop);

		System.out.println("Writing angelcode format " + file);
		FileOutputStream out = new FileOutputStream(file);
		try {
			PrintWriter pw = new PrintWriter(file);

			// Info
			StringBuilder info = new StringBuilder();
			info.append("info face=null size=");
			info.append(size);
			info.append(" bold=");
			info.append(bold ? 1 : 0);
			info.append(" italic=");
			info.append(italic ? 1 : 0);
			info.append(" charset=");
			info.append(charset);
			info.append(" unicode=");
			info.append(unicode ? 1 : 0);
			info.append(" stretchH=");
			info.append(stretchH);
			info.append(" smooth=");
			info.append(smooth ? 1 : 0);
			info.append(" aa=");
			info.append(aa ? 1 : 0);
			info.append(" padding=");
			info.append(padding);
			info.append(" spacing=");
			info.append(spacing);
			pw.println(info.toString());

			// Commons
			StringBuilder common = new StringBuilder();
			common.append("common lineHeight=");
			common.append(lineHeight);
			common.append(" base=");
			common.append(base);
			common.append(" scaleW=");
			common.append(img.getWidth());
			common.append(" scaleH=");
			common.append(img.getHeight());
			common.append(" pages=");
			common.append(pages);
			common.append(" packed=");
			common.append(packed ? 1 : 0);
			pw.println(common.toString());

			// TODO work out image base better
			int idx = source.lastIndexOf("/");
			String imgFile = idx == -1 ? source : source.substring(idx + 1);

			// TODO multiple pages
			StringBuilder page = new StringBuilder();
			page.append("page id=0 file=\"");
			page.append(imgFile);
			page.append("\"");
			pw.println(page.toString());

			// TODO not entirely certain what this means - investigate
			StringBuilder chars = new StringBuilder();
			chars.append("chars count=");
			chars.append(charCount);
			pw.println(chars.toString());

			for (Glyph g : glyphs) {
				int x1 = (int) (img.getWidth() * g.u1);
				int y1 = (int) (img.getHeight() * g.v1);
				int x2 = (int) (Math.ceil(img.getWidth() * g.u2));
				int y2 = (int) (Math.ceil(img.getHeight() * g.v2));

				int w = x2 - x1;
				int h = y2 - y1;

				y1 += maxTrimTop;
				h -= (maxTrimBottom + maxTrimTop);

				StringBuilder glyph = new StringBuilder("char id=");
				glyph.append((int) g.glyph);
				glyph.append("    x=");
				glyph.append(x1);
				glyph.append("    y=");
				glyph.append(y1);
				glyph.append("    width=");
				glyph.append(w);
				glyph.append("    height=");
				glyph.append(h);
				glyph.append("    xoffset=");
				glyph.append(xoffset);
				glyph.append("    yoffset=");
				glyph.append(yoffset);
				glyph.append("    xadvance=");
				glyph.append(w + xadvance);
				glyph.append("    page=0 chnl=0");

				pw.println(glyph.toString());
			}
			pw.flush();

		} finally {
			out.close();
		}
	}
	
	void loadFnt(File f) throws IOException, ParseException {
		fontDef = f;
		FileInputStream in = new FileInputStream(f);
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = r.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					String[] words = line.split("\\s+");
					Properties p = new Properties();
					String op = words[0];
					for(int i = 1 ; i < words.length; i++) {
						int idx = words[i].indexOf('=');
						String n = words[i].substring(0, idx);
						String v = words[i].substring(idx + 1);
						p.setProperty(n, v);
					}
					if(op.equals("info")) {
						info = p;
					}
					else if(op.equals("page")) {
						page = p;
					}
					else if(op.equals("common")) {
						common = p;
					}
					else if(op.equals("char")) {
						glyphs.add(new Glyph(p));	
					}
				}
			}
		} finally {
			in.close();
		}
	}

	void loadFontdef(File f) throws IOException, ParseException {
		fontDef = f;
		FileInputStream in = new FileInputStream(f);
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			State state = State.START;
			int lineNo = 0;
			while ((line = r.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					switch (state) {
					case START:
						name = line;
						state = State.GOT_NAME;
						break;
					case GOT_NAME:
						if (line.equals("{")) {
							state = State.SECTION;
						} else {
							throw new ParseException("Expected {", lineNo);
						}
						break;
					case SECTION:
						if (line.equals("}")) {
							state = State.END;
						} else {
							String[] args = line.split("[ \\t]+");
							if (args[0].equals("type")) {
								if (args.length == 2) {
									type = args[1];
								} else {
									throw new ParseException("Expected single argument for type item", lineNo);

								}
							} else if (args[0].equals("source")) {
								if (args.length == 2) {
									source = args[1];
								} else {
									throw new ParseException("Expected single argument for source item", lineNo);
								}
							} else if (args[0].equals("glyph")) {
								if (args.length == 6) {
									glyphs.add(new Glyph(args));
								} else {
									throw new ParseException("Expected six arguments for glyph item at line " + lineNo + ", got "
											+ args.length, lineNo);
								}
							}
						}
						break;
					case END:
						throw new ParseException("Didn't expect anything after the }", lineNo);
					}
				}
				lineNo++;
			}
		} finally {
			in.close();
		}
	}

	public static void main(String[] args) throws Exception {
		JPanel p = new JPanel();
		p.setLayout(new MigLayout("wrap 2", "[][grow, fill]", "[]"));
		p.add(new JLabel("Trim"));
		JTextField trim = new JTextField();
		trim.setText("0");
		p.add(trim);

		JFileChooser c = new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "EEStuff"
				+ File.separator + "ModelSamples" + File.separator + "DefaultSkin");
		c.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".fontdef");
			}

			@Override
			public String getDescription() {
				return "Fontdef files (*.fontdef)";
			}
		});
		c.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".fnt");
			}

			@Override
			public String getDescription() {
				return "FNT files (*.fnt)";
			}
		});
		if (c.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			ConvertFnt cnv = new ConvertFnt();
			File f = c.getSelectedFile();
			String n = f.getName();
			int idx = n.indexOf(".fontdef");

			if (idx == -1) {
				idx = n.indexOf(".fnt");
				if (idx == -1) {
					throw new Exception("Unknown extension.");
				} else {
					String fn = idx == -1 ? n + ".fontdef" : n.substring(0, idx) + ".fontdef";
					cnv.loadFnt(f);
					cnv.saveFontdef(new File(f.getParentFile(), fn));
				}
			} else {
				String fn = idx == -1 ? n + ".fnt" : n.substring(0, idx) + ".fnt";
				cnv.loadFontdef(f);
				cnv.saveFnt(new File(f.getParentFile(), fn));
			}
		}
	}
}
