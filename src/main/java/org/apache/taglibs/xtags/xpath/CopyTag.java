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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.XMLWriter;

/** A tag which performs a copy operation like the XSLT tag - a shallow copy
  *
  * @author James Strachan
  */
public class CopyTag extends AbstractTag {

    /** Holds the node selected for this tag */
    private Node node;
    
    /** Holds the current XMLWriter used in this tag */
    private XMLWriter writer;
    
    /** Holds value of property xpath. */
    private XPath xpath;
    

    public CopyTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException {
        node = getNode();
        if ( node != null ) {
            try {
                writer = TagHelper.getXMLWriter( pageContext, this );
                if ( node instanceof Element ) {
                    writer.writeOpen( (Element) node );
                }
                else {
                    writer.write( node );
                }
            }
            catch (IOException e) {
                handleException(e);
            }
        }
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspException {
        if ( node instanceof Element && writer != null ) {
            try {
                writer.writeClose( (Element) node );
            }
            catch (IOException e) {
                handleException(e);
            }
            finally {
                node = null;
                writer = null;
            }
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        node = null;
        writer = null;
        xpath = null;
    }

    // Properties
    //-------------------------------------------------------------------------                
    
    /** Setter for property select.
     * @param select New value of property select.
     */
    public void setSelect(String select) {
        if ( select != null && ! select.equals( "." ) ) {
            xpath = createXPath( select );
        }
        else {
            xpath = null;
        }
    }
    
    /** Returns the node selected for this tag
     */
    protected Node getNode() {
        Object input = getInputNodes();
        if ( xpath != null ) {
            input = xpath.selectNodes( input );
        }
        return getNode( input );
    }
    
    protected Node getNode(Object input) {
        if ( input instanceof Element ) {
            return (Element) input;
        }
        else if ( input instanceof Node ) {
            // don't output documents
            if ( !( input instanceof Document ) ) {
                return (Node) input;
            }
        }
        else if ( input instanceof List ) {
            List list = (List) input;
            if ( list.size() > 0 ) {
                return getNode( list.get(0) );
            }
        }
        return null;
    }

}
