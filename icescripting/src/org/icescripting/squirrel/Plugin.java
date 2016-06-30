package org.icescripting.squirrel;

import org.icesquirrel.runtime.SquirrelPlugin;
import org.icesquirrel.runtime.SquirrelRuntime;

public class Plugin implements SquirrelPlugin {

	@Override
	public void init(SquirrelRuntime runtime) {
		runtime.getBuiltInFunctions().add(BuiltIn.class);
	}

}
