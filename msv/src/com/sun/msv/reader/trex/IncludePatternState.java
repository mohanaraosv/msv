package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.TREXGrammar;
import com.sun.tranquilo.reader.ExpressionWithoutChildState;
import com.sun.tranquilo.reader.ExpressionOwner;

/**
 * &lt;include&gt; element in the pattern.
 */
public class IncludePatternState extends ExpressionWithoutChildState implements ExpressionOwner
{
	protected Expression included = Expression.nullSet;
	// assign a default value just in case something goes wrong and onEndChild is not called.
	
	public void onEndChild( Expression included )
	{
		this.included = included;
	}
	
	protected Expression makeExpression()
	{
		final String href = startTag.getAttribute("href");
		
		if(href==null)
		{// name attribute is required.
			reader.reportError( TREXGrammarReader.ERR_MISSING_ATTRIBUTE,
				"include","href");
			// recover by returning something that can be interpreted as Pattern
			return Expression.nullSet;
		}
		
		
		reader.switchSource(href,new RootIncludedPatternState(this));
		
		// onEndChild method is called inside the above function call and
		// included will be set.
		
		return included;
	}
}
