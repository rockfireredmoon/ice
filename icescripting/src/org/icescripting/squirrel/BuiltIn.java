package org.icescripting.squirrel;

import org.apache.commons.io.FilenameUtils;
import org.icescripting.Scripts;
import org.icesquirrel.jsr223.BindingsAdapter;
import org.icesquirrel.runtime.Function;
import org.icesquirrel.runtime.SquirrelExecutionContext;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class BuiltIn {


	@Function
	public static Object require(String path) {
		String dir = FilenameUtils.getPath(path);
		Scripts scripts = Scripts.get();
		if (dir.equals("")) {
			path = String.format("%s%s", FilenameUtils.getPath(scripts.getCurrentPath()), path);
		}
		if (!scripts.isLoaded(path)) {
			SquirrelExecutionContext ctx = SquirrelExecutionContext.get();
			scripts.eval(path, new BindingsAdapter(ctx.getRoot()));
		}
		return Boolean.TRUE;
	}

}
