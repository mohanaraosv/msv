package com.sun.msv.schematron.grammar;

import org.apache.xml.utils.PrefixResolver;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.trex.ElementPattern;

/**
 * ElementPattern with schematron annotation
 */
public class SElementExp extends ElementPattern {
	
	public SElementExp( NameClass nc, Expression body ) {
		super(nc,body);
	}
	
	/** this object is used to resolve prefixes found in XPath. */
	public PrefixResolver prefixResolver;
	
	/** schematron rules. */
	public SRule[]	rules;
}
