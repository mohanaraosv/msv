/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.relax.*;
import java.util.Iterator;
import java.util.Set;

/**
 * removes "none" type of RELAX from AGM.
 * 
 * "none" type is harmful for instance generation. This visitor changes
 * "none" type to nullSet.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NoneTypeRemover extends ExpressionCloner {
	
	/** set of visited ElementExps */
	private final Set visitedElements = new java.util.HashSet();
	
	public NoneTypeRemover( ExpressionPool pool ) { super(pool); }
	
	public Expression onElement( ElementExp exp ) {
		// this check is necessary to prevent infinite recursion.
		if( visitedElements.contains(exp) )	return exp;
		visitedElements.add(exp);
		exp.contentModel = exp.contentModel.visit(this);
		return exp;
	}
	
	public Expression onAttribute( AttributeExp exp ) {
		Expression content = exp.exp.visit(this);
		if( content==Expression.nullSet )
			return Expression.epsilon;
		else
			return pool.createAttribute( exp.nameClass, content );
	}
	
	public Expression onData( DataExp exp ) {
		if( exp.dt == NoneType.theInstance )	return Expression.nullSet;
		else									return exp;
	}
	public Expression onRef( ReferenceExp exp ) {
		exp.exp = exp.exp.visit(this);
		return exp;
	}
	public Expression onOther( OtherExp exp ) {
		return exp.exp.visit(this);
	}
}
