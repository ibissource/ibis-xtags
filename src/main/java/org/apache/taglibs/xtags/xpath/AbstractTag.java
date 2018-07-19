/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.taglibs.xtags.xpath;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.NodeFilter;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.rule.Stylesheet;

import org.jaxen.VariableContext;

import org.apache.taglibs.xtags.util.JspNestedException;
import org.apache.taglibs.xtags.util.JspVariableContext;



/** A tag which performs an XPath expression on the current context Node
  *
  * @author James Strachan
  */
public abstract class AbstractTag extends TagSupport {

    protected static final Document EMPTY_DOCUMENT = DocumentHelper.createDocument();
    
    protected static final boolean ALLOW_FLUSH = false;
    
    protected Object context;
    
    
    public AbstractTag() {
    }

    public void release() {
        context = null;
    }

    public void flush() throws JspException {
        if ( ALLOW_FLUSH ) {
            try {
                pageContext.getOut().flush();
            }
            catch (IOException e) {
                handleException(e);
            }
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    public void setContext(Object context) {
        this.context = context;
    }

    
    // Helper methods
    //-------------------------------------------------------------------------                
    /** @return true if the given filter matches a node in the 
      * input nodes
      */
    public boolean matches(NodeFilter filter) {
        Object input = getInputNodes( false );
        if ( input == null ) {
            // use an empty document to support
            // filters that just use XPath variables
            // such as "$foo='bar'"
            input = EMPTY_DOCUMENT;
        }
        if ( input instanceof List ) {
            List list = (List) input;
            for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
                Object object = iter.next();
                if ( object instanceof Node ) {
                    Node node = (Node) object;
                    if ( filter.matches( node ) ) {
                        return true;
                    }
                }
            }
        }
        else if ( input instanceof Node ) {
            Node node = (Node) input;
            if ( filter.matches( node ) ) {
                return true;
            }
        }
        return false;
    }

    /** @return the input node on which to make a selction
      */
    public Object getInputNodes() {
        return getInputNodes( true );
    }
    
    public Object getInputNodes( boolean warn ) {
        if ( context == null ) {
            return TagHelper.getInputNodes( pageContext, this, warn );
        }
        return context;
    }
    
    public void setInputNodes( Object inputNodes ) {
        TagHelper.setInputNodes( pageContext, inputNodes );
    }
    
    public Stylesheet getStylesheet() {
        StylesheetTag tag  = (StylesheetTag) findAncestorWithClass( 
            this, StylesheetTag.class 
        );
        if ( tag != null ) {
            return tag.getStylesheet();
        }
        else {
            return TagHelper.getStylesheet( pageContext );
        }
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------                

    /** A factory method to create new XPath instances */
    protected XPath createXPath(String xpathExpression) {
        VariableContext variableContext = JspVariableContext.getInstance( pageContext );
        return getDocumentFactory().createXPath( xpathExpression, variableContext ); 
    }

    /** A factory method to create new XPath filter */
    protected NodeFilter createXPathFilter(String xpathExpression) {
        VariableContext variableContext = JspVariableContext.getInstance( pageContext );
        return getDocumentFactory().createXPathFilter( xpathExpression, variableContext ); 
    }

    /** @return the factory used to create XPath instances */
    protected DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }
    
    /** Handles non-JspExceptions thrown in this instance
      */
    protected void handleException( Exception e ) throws JspException {
        if ( e instanceof JspException ) {
            throw (JspException) e;
        }
        else {
            pageContext.getServletContext().log( e.getMessage(), e );
            throw new JspNestedException( e );
        }
    }    
}
