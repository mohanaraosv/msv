/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

/**
 * 'scale' facet.
 *
 * this class holds these facet information and performs validation.
 * 
 * @author Kohsuke KAWAGUCHI
 */
class ScaleFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of fraction digits */
	protected final int scale;

	public ScaleFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_SCALE, facets );
		
		scale = facets.getNonNegativeInteger(FACET_SCALE);
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_SCALE);
		if(o!=null && ((ScaleFacet)o).scale < this.scale )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_SCALE, o.getName() );
		
		// consistency with precision is checked in DataTypeImpl.derive method.
	}

	protected boolean checkLexicalConstraint( String content )
	{
		return countScale(content)<=scale;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		final int cnt = countScale(content);
		if(cnt<=scale)		return null;
		
		return new DataTypeErrorDiagnosis( this, content, -1, 
			DataTypeErrorDiagnosis.ERR_TOO_MUCH_SCALE,
			new Integer(cnt), new Integer(scale) );
	}
	
	final private int countScale( String literal )
	{
		// count the number of digits.
		final int len = literal.length();
		boolean skipMode = true;
		char[] chs = literal.toCharArray();

		int count=0;
		
		for( int i=0; i<len; i++ )
			if( skipMode )
			{
				if( chs[i]=='.' )
					skipMode = false;
			}
			else
			{
				if( '0'<chs[i] && chs[i]<='9' )
					count++;
			}
		
		return count;
	}
}
