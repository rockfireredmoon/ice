package org.icetools.modelman;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.icelib.XDesktop;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ComponentNode extends PropNode {
	public final static Icon COMPONENT = new ImageIcon(ComponentNode.class.getResource("/png/small/component.png"));
	private ComponentHandler componentHandler;

	public ComponentNode(Context context, File root) {
		this(context, root, null);
	}

	public ComponentNode(Context context, File file, PropNode parent) {
		super(context, file, parent);
	}

	public Icon getIcon() {
		return COMPONENT;
	}
	@Override
	public void mouseClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			try {
				XDesktop.getDesktop().edit(file);
			} catch (IOException e) {
				context.getConsole().error("Failed to open editor.", e);
			} catch (UnsupportedOperationException uoe) {
				try {
					XDesktop.getDesktop().open(file);
				} catch (IOException e) {
					context.getConsole().error("Failed to open view.", e);
				}
			}

			// Stop the expand
			event.consume();
		}
	}

	@Override
	void checkChildren() {
		if (children == null) {

			children = new ArrayList<PropNode>();
			try {
				readXML();
				if (componentHandler != null) {
					for (Entity e : componentHandler.entities) {
						File f = new File(file.getParentFile(), e.mesh);
						children.add(new MeshNode(context, f, this));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				context.getConsole().error("Failed to read component XML.", e);
			}
		}

	}

	void readXML() throws Exception {
		// Create a JAXP SAXParserFactory and configure it
		SAXParserFactory spf = SAXParserFactory.newInstance();

		// Set namespaceAware to true to get a parser that corresponds to
		// the default SAX2 namespace feature setting. This is necessary
		// because the default value from JAXP 1.0 was defined to be false.
		spf.setNamespaceAware(true);

		// Validation part 1: set whether validation is on
		spf.setValidating(false);

		// Create a JAXP SAXParser
		SAXParser saxParser = spf.newSAXParser();

		// Get the encapsulated SAX XMLReader
		XMLReader xmlReader = saxParser.getXMLReader();

		// Set the ContentHandler of the XMLReader
		xmlReader.setContentHandler(componentHandler = new ComponentHandler());

		// Set an ErrorHandler before parsing
		xmlReader.setErrorHandler(new ConsoleErrorHandler(context.getConsole()));

		// Tell the XMLReader to parse the XML document
		InputSource is = new InputSource(new FileReader(file));
		xmlReader.parse(is);
	}

	private String getBasename(File file, String suffix) {
		String basename = file.getName().substring(0, file.getName().lastIndexOf(suffix));
		return basename;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	class Entity {
		String mesh;
		int qf;
		int vf;

		Entity(String mesh) {
			this(mesh, 0, 0);
		}

		Entity(String mesh, int qf, int vf) {
			this.mesh = mesh;
			this.qf = qf;
			this.vf = vf;
		}
	}

	class ComponentHandler extends DefaultHandler {
		private List<Entity> entities = new ArrayList<Entity>();

		public void startDocument() throws SAXException {
		}

		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			String key = localName;
			if (key.equals("Entity")) {
				String qf = atts.getValue("qf");
				String vf = atts.getValue("vf");
				entities.add(new Entity(atts.getValue("mesh"), qf == null ? 0 : Integer.parseInt(qf), vf == null ? 0 : Integer
					.parseInt(vf)));
			}
		}

		public void endDocument() throws SAXException {
		}
	}
}
