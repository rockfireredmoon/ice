package org.icetools.modelman;

import java.io.File;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;

public class Converter extends AbstractBatchFileProcessor {

	public enum Type {

		BINARY_TO_XML, XML_TO_BINARY
	}

	private boolean removeOther;
	private PropTreeModel treeModel;
	private Type type;

	public Converter(Output console, PropTreeModel treeModel, Type type) {
		super(console);
		this.treeModel = treeModel;
		this.type = type;
	}

	public static void main(String[] args) {
		if (args.length >= 2) {
			Type type = Type.valueOf(args[0]);
			Converter fixer = new Converter(new DumbOutput(), null, type);
			boolean ok = true;
			for (int i = 1; i < args.length; i++) {
				if (!fixer.doFile(new File(args[i]))) {
					ok = false;
				}
			}
			System.exit(ok ? 0 : 1);
		} else {
			System.err.println(Fixer.class.getName() + ": <" + Type.BINARY_TO_XML + "|" + Type.XML_TO_BINARY + "> <filename>");
			System.exit(2);
		}
	}

	public void setRemoveOther(boolean removeOther) {
		this.removeOther = removeOther;
	}

	@Override
	protected boolean doFile(final File file) {
		console.message("Converting " + file.getName());
		ProcessBuilder pb = new ProcessBuilder("OgreXMLConverter", "-d3d", file.getAbsolutePath());
		try {
			final Process p = pb.start();
			Thread errThread = new Thread() {
				@Override
				public void run() {
					try {
						IOUtils.copy(p.getErrorStream(), console.getErrorStream());
					} catch (Exception e) {
					}
				}
			};
			errThread.start();
			IOUtils.copy(p.getInputStream(), console.getStandardStream());
			int ret = p.waitFor();
			errThread.join(10000);
			if (ret == 0) {
				if (type.equals(Type.BINARY_TO_XML)) {
					console.message("Converted " + file.getName() + " to XML");
				} else {
					console.message("Converted " + file.getName() + " to Binary");
				}
			} else {
				throw new RuntimeException("OgreXMLConvert returned " + ret);
			}

			if (removeOther) {
				file.delete();
				console.message("Removed " + file.getName());
			}

			// Update the parent node
			if (treeModel != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						PropNode parent = (PropNode) treeModel.getNodeForFile(file.getParentFile());
						parent.reload();
						treeModel.reload(parent);
					}
				});
			}
			return true;

		} catch (Exception ex) {
			throw new RuntimeException("Failed to convert to XML", ex);
		}
	}
}
