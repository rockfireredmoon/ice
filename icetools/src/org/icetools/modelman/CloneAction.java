package org.icetools.modelman;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class CloneAction extends AbstractAction {

	private PropTreeModel treeModel;
	private Context context;

	CloneAction(Context context, PropTreeModel treeModel) {
		super("Clone");
		this.context = context;
		this.treeModel = treeModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ComponentNode cn = (ComponentNode) context.getSelectedPropNode();
		String newName = JOptionPane.showInputDialog("Enter the new name for component", cn.getFile().getName());
		if (newName != null) {
			String baseName = newName;
			if (!newName.endsWith(".csm.xml")) {
				newName += ".csm.xml";
			} else {
				baseName = newName.substring(0, newName.lastIndexOf(".csm.xml"));
			}
			final File dir = cn.getFile().getParentFile();
			File nn = new File(dir, newName);
			if (nn.exists()) {
				JOptionPane.showMessageDialog((Component) e.getSource(), "Component already exists", "Error",
					JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					doConvert(cn, baseName, dir, nn);
				} catch (Exception ex) {
					this.context.getConsole().error("Failed to clone component.", ex);
				}

				// Update the parent node
				PropNode parent = (PropNode) cn.getParent();
				parent.reload();
				treeModel.reload(parent);
			}
		}
	}

	private void doConvert(ComponentNode cn, String baseName, final File dir, File nn) throws ParserConfigurationException,
			SAXException, IOException, TransformerFactoryConfigurationError, TransformerConfigurationException,
			FileNotFoundException, TransformerException {
		Document doc = readDocument(cn.getFile());
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("Component");
		Element el = (Element) nList.item(0);
		String currentComponentId = el.getAttribute("id");

		// Set the new component ID
		el.setAttribute("id", baseName);

		nList = doc.getElementsByTagName("Entity");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String mesh = eElement.getAttribute("mesh");
				String suffix = mesh.substring(currentComponentId.length());
				eElement.setAttribute("mesh", baseName + suffix);

				doMesh(baseName, dir, mesh, suffix);
			}
		}

		writeCsmXML(nn, doc);
	}

	private void doMesh(String baseName, final File dir, String mesh, String suffix) throws IOException,
			ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {

		// First convert to XML if we need
		File currentFile = new File(dir, mesh);
		File currentXmlFile = new File(dir, mesh + ".xml");
		if (!currentXmlFile.exists()) {
			new Converter(context.getConsole(), treeModel, Converter.Type.BINARY_TO_XML).addFile(currentFile).run();
		}

		// Copy the mesh XML to it's new name and set the
		// attribute
		File newXmlFile = new File(dir, baseName + suffix + ".xml");
		FileUtils.copyFile(currentXmlFile, newXmlFile);
		context.getConsole().message("Copied " + currentXmlFile.getName() + " to " + newXmlFile.getName());

		// Now we have XML, we can look for the textures it uses and copy them
		// to the new name
		Document doc = readDocument(newXmlFile);
		NodeList nList = doc.getElementsByTagName("texture");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String name = eElement.getAttribute("name");

				// Copy the texture file
				File currentTextureFile = new File(dir, name);
				String extension = name.substring(name.lastIndexOf('.'));
				File newTextureFile = new File(dir, baseName + extension);
				FileUtils.copyFile(currentTextureFile, newTextureFile);

				// Set the new element
				eElement.setAttribute("name", baseName + extension);
				writeCsmXML(newXmlFile, doc);

				context.getConsole().message("Copied " + currentTextureFile.getName() + " to " + newTextureFile.getName());
			}
		}

		// Convert the new XML back to the binary format
		new Converter(context.getConsole(), treeModel, Converter.Type.XML_TO_BINARY).addFile(newXmlFile).run();
		File newFile = new File(dir, baseName + suffix);
		context.getConsole().message("Converted " + newXmlFile.getName() + " to " + newFile.getName());
	}

	private Document readDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		return doc;
	}

	private void writeCsmXML(File nn, Document doc) throws TransformerFactoryConfigurationError, TransformerConfigurationException,
			FileNotFoundException, TransformerException, IOException {
		// Write out the new .csm.xml
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream fout = new FileOutputStream(nn);
		try {
			StreamResult result = new StreamResult(fout);
			transformer.transform(source, result);
		} finally {
			fout.close();
		}
		context.getConsole().message("Created new component " + nn.getName());
	}
}