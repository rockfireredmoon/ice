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

public class MeshNode extends ConvertableNode {
	public final static Icon MESH = new ImageIcon(MeshNode.class.getResource("/png/small/mesh.png"));
	public final static Icon MESH_XML = new ImageIcon(MeshNode.class.getResource("/png/small/mesh-xml.png"));
	public final static Icon MESH_NEEDS_CONVERT = new ImageIcon(MeshNode.class.getResource("/png/small/mesh-needs-convert.png"));

	private boolean hasXML;
	private boolean needsConvert;
	private File xmlFile;
	private MeshHandler meshHandler;

	public MeshNode(Context context, File root) {
		this(context, root, null);
	}

	public MeshNode(Context context, File file, PropNode parent) {
		super(context, file, parent);

		// Do we have an XML file for this Mesh?
		xmlFile = new File(file.getParentFile(), file.getName() + ".xml");
		hasXML = xmlFile.exists();

		// Analyse file, see if needs a convert
		if (!hasXML) {
                    if(file.exists()) {
			needsConvert = Fixer.isNeedsFix(file);
                    }
		} else {
			try {
				readXML();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void mouseClick(MouseEvent event) {
		if (event.getClickCount() == 2 && hasXML) {
			try {
				XDesktop.getDesktop().edit(xmlFile);
			} catch (IOException e) {
				context.getConsole().error("Failed to open editor.", e);
			} catch (UnsupportedOperationException uoe) {
				try {
					XDesktop.getDesktop().open(xmlFile);
				} catch (IOException e) {
					context.getConsole().error("Failed to open view.", e);
				}
			}

			// Stop the expand
			event.consume();
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
		xmlReader.setContentHandler(meshHandler = new MeshHandler());

		// Set an ErrorHandler before parsing
		xmlReader.setErrorHandler(new ConsoleErrorHandler(context.getConsole()));

		// Tell the XMLReader to parse the XML document
		InputSource is = new InputSource(new FileReader(xmlFile));
		xmlReader.parse(is);
	}

	public Icon getIcon() {
		if (hasXML) {
			return MESH_XML;
		} else if (needsConvert) {
			return MESH_NEEDS_CONVERT;
		} else {
			return MESH;
		}
	}

	@Override
	void checkChildren() {
		if (children == null && meshHandler != null) {
			children = new ArrayList<PropNode>();
			for (String t : meshHandler.textures) {
				File tf = new File(file.getParentFile(), t);
				if (tf.exists()) {
					children.add(new TextureNode(context, tf, this));
				}
			}
			if(meshHandler.skeleton != null) {
				File tf = new File(file.getParentFile(), meshHandler.skeleton);
				children.add(new SkeletonNode(context, tf, this));
			}
		}
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	public boolean isNeedsConvert() {
		return needsConvert;
	}

	public boolean isHasXML() {
		return hasXML;
	}
        
        public File getXML() {
            return xmlFile;
        }

	class MeshHandler extends DefaultHandler {
		private List<String> textures = new ArrayList<String>();
		private String skeleton;

		public void startDocument() throws SAXException {
		}

		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			String key = localName;
			if (key.equals("texture")) {
				textures.add(atts.getValue("name"));
			}

			if (key.equals("skeletonlink")) {
				skeleton = atts.getValue("name");
			}

		}

		public void endDocument() throws SAXException {
		}
	}
}
