package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.AttributeExp;
import com.sun.tranquilo.grammar.SimpleNameClass;
import com.sun.tranquilo.grammar.trex.TypedString;
import com.sun.tranquilo.grammar.xmlschema.AttributeDeclExp;
import com.sun.tranquilo.grammar.xmlschema.XMLSchemaSchema;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.ExpressionWithChildState;

/**
 * used to parse &lt;attribute &gt; element.
 */
public class AttributeState extends ExpressionWithChildState {

	protected final boolean isGlobal;
	
	protected AttributeState( boolean isGlobal ) {
		this.isGlobal = isGlobal;
	}
	
	protected State createChildState( StartTagInfo tag ) {
		if( tag.localName.equals("simpleType") )
			return ((XMLSchemaReader)reader).sfactory.simpleType(this,tag);
		
		return super.createChildState(tag);
	}
	
	protected Expression initialExpression() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( startTag.containsAttribute("ref") ) {
			// this this tag has @ref.
			Expression exp = reader.resolveQNameRef(
				startTag, "ref",
				new XMLSchemaReader.RefResolver() {
					public ReferenceContainer get( XMLSchemaSchema g ) {
						return g.attributeDecls;
					}
				} );
			if( exp==null )		return Expression.epsilon;	// couldn't resolve QName.
			return exp;
		}
		
		if( !startTag.containsAttribute("type") )
			// return null to indicate that no type definition is given.
			return null;
		
		// if <attribute> element has @type, then
		// it shall be used as content type.
		Expression exp = reader.resolveQNameRef(
			startTag, "type",
			new XMLSchemaReader.RefResolver() {
				public ReferenceContainer get( XMLSchemaSchema g ) {
					return g.simpleTypes;
				}
			} );
		if( exp==null )		return Expression.epsilon;	// couldn't resolve QName.
		return exp;
	}

	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		if( halfCastedExpression!=null )
			// only one child is allowed.
			// recover by ignoring previously found child expressions.
			reader.reportError( reader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
		
		return newChildExpression;
	}
																												   
	protected Expression annealExpression(Expression contentType) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		String name = startTag.getAttribute("name");

		// TODO: form attribute is prohibited in several occasions.
		String targetNamespace;
		
		if( isGlobal )	targetNamespace = reader.currentSchema.targetNamespace;
		else
			// in local attribute declaration,
			// targetNamespace is affected by @form and schema's @attributeFormDefault.
			targetNamespace = ((XMLSchemaReader)reader).resolveNamespaceOfAttributeDecl(
				startTag.getAttribute("form") );
		
		// TODO: this doesn't work: super class signals an error
		// if contentType is null at this moment.
		
		if( contentType==null )
			// type attribute is not present, and no <simpleType> is given as a child.
			// so it is assumed as "ur-type".
			contentType = Expression.anyString;
		
		String fixed = startTag.getAttribute("fixed");
		if( fixed!=null )
			// TODO: is this 'fixed' value should be added through enumeration facet?
			contentType = reader.pool.createTypedString( new TypedString(fixed,false) );
		
		Expression exp = reader.pool.createAttribute(
			new SimpleNameClass( targetNamespace, name ),
			contentType );
		
		if( isGlobal ) {
			// register this expression as a global attribtue declaration.
			AttributeDeclExp decl = reader.currentSchema.attributeDecls.getOrCreate(name);
			reader.setDeclaredLocationOf(decl);
			decl.exp = exp;
			
			// TODO: @use is prohibited in global
			
		} else {
			// handle @use
			
			String use = startTag.getAttribute("use");
			if( "prohibited".equals(use) )
				// in case of 'prohibit', the declaraion is simply ignored.
				return Expression.epsilon;
		
			if( "optional".equals(use) || use==null )
				exp = reader.pool.createOptional(exp);
			else
			if( !"required".equals(use) )
				reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "use", use );
				// recover by assuming "required" (i.e., do nothing)
		}
		
		return exp;
	}
}