package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.NameClass;

public class DifferenceNameClass implements NameClass
{
	public final NameClass nc1;
	public final NameClass nc2;
	
	public boolean accepts( String namespaceURI, String localPart )
	{
		return nc1.accepts(namespaceURI,localPart)
			&& !nc2.accepts(namespaceURI,localPart);
	}
	
	public DifferenceNameClass( NameClass nc1, NameClass nc2 )
	{
		this.nc1 = nc1;
		this.nc2 = nc2;
	}
}
