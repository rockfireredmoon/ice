package org.icetools.js;

import java.io.File;
import java.io.FileInputStream;

import org.icesquirrel.interpreter.SquirrelInterpretedScript;
import org.icesquirrel.runtime.SquirrelRuntime;
import org.icesquirrel.runtime.SquirrelTable;

public class ArmorJS {

	public ArmorJS() {

	}

	public static void main(String[] args) throws Exception {
		ArmorJS js = new ArmorJS();
		js.process(new File(args[0]));
	}

	private void process(File file) throws Exception {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				process(f);
			}
		} else {
			if (file.getName().endsWith(".nut")) {
				System.out.println(file);
				SquirrelInterpretedScript sis = new SquirrelInterpretedScript();
				sis.getRootTable().insert("ClothingDef", new SquirrelTable());
				sis.getRootTable().insert("AttachableDef", new SquirrelTable());
				sis.getRootTable().insert("AttachableTemplates", new SquirrelTable());
				try (FileInputStream in = new FileInputStream(file)) {
					sis.execute(in);
				}
			}
		}
	}
}
