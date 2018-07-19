/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import org.apache.taglibs.xtags.util.JspVariableContext;

/** The add tag parses it's body (as an XML fragment) and appends the contents to the
  * current node. The current node must be an Element.
  *
  * @author James Elson
  */
public class AddTag extends AbstractBodyTag {
    
    private XPath beforeXPath;
    private XPath afterXPath;
    
    public AddTag() {
    }

    public int doEndTag() throws JspException  {
        Object element = TagHelper.getInputNodes(pageContext, this, false );
        if (element == null) {
            throw new JspException( "No current node to add content to" );
        }
        if (! (element instanceof Element) ) {
            throw new JspException( "Current node is not an Element" );
        }
        if (bodyContent != null) {
            try {
                StringReader sreader = new StringReader("<dummy>"+bodyContent.getString()+"</dummy>");
                SAXReader reader = new SAXReader();
                Document doc = reader.read(sreader);
                Element root = doc.getRootElement();
                List nodes = root.content();
                while (! nodes.isEmpty() ) {
                    Node node = (Node)nodes.remove(0);
                    node.detach();
                    ((Element)element).add( node );
                }
            } 
            catch (DocumentException e) { 
                handleException(e);
            }
        }
        
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        beforeXPath = null;
        afterXPath = null;
    }

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /** Sets an XPath expression used to determine a child element of the current element.
     *  The body contents will be inserted just before the first node that matches this
     *  XPath.
     */
    public void setAfter(String after) {
        this.afterXPath = createXPath( after );
    }

    /** Sets an XPath expression used to determine a child element of the current element.
     *  The body contents will be inserted just after the first node that matches this
     *  XPath.
     */
    public void setBefore(String before) {
        this.beforeXPath = createXPath( before );
    }
    
    /** A factory method to create new XPath instances */
    protected XPath createXPath(String xpathExpression) {
        XPath xpath = getDocumentFactory().createXPath( xpathExpression ); 
        xpath.setVariableContext( JspVariableContext.getInstance( pageContext ) );
        return xpath;
    }

    /** @return the factory used to create XPath instances */
    protected DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }
}
