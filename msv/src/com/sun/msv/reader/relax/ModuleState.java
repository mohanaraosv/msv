package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.RELAXModule;

/**
 * parses &lt;module&gt; element.
 * 
 * this state is used to parse "head module", which is not included by
 * any other modules.
 * 
 * modules merged by include element are handled by MergeModuleState.
 * 
 * <p>
 * this class switchs RELAXReader.currentModule so that successive declarations
 * are placed in the proper module.
 */
public class ModuleState extends ModuleMergeState
{
	protected ModuleState(String expectedNamespace) { super(expectedNamespace); }

	protected void onModuleDetermined( RELAXModule m )
	{
		// switch current module to new one.
		getReader().currentModule = m;
		getReader().markAsInitialized(m);
	}
}