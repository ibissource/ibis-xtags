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

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.apache.taglibs.xtags.util.JspVariableContext;

/** The replace tag parses it's body (as an XML fragment) and replaces the contents to the
  * current node with this new XML fragment. The current node must be an Element.
  *
  * @author James Elson
  */
public class ReplaceTag extends AbstractBodyTag {
    
    public ReplaceTag() {
    }

    public int doEndTag() throws JspException  {
        Object context = TagHelper.getInputNodes(pageContext, this, false );
        if (context == null) {
            logInfo( "No current node to replace" );
            return EVAL_PAGE;
        }
        
        try {
            String xmlFragment = null;
            if (bodyContent != null) {
                xmlFragment = bodyContent.getString();
            }
            if (context instanceof List) {
                List els = (List )context;
                if (els.size() > 1) {
                    throw new JspException( "Current context contains more than one node");
                }
                if (els.size() == 1) {
                    context = els.get(0);
                }
            }
            if (context instanceof Document) {
                if (xmlFragment == null) {
                    throw new JspException( "Cannot replace document with empty body");
                }
                Document sourceDoc = (Document) context;
                Document newDoc = DocumentHelper.parseText( xmlFragment );

                // clear source doc contents
                sourceDoc.clearContent();
                for ( int i = 0, size = newDoc.nodeCount(); i < size; i++ ) {
                    Node node = newDoc.node(i);
                    // detach from new doc
                    node.detach();
                    // add to source
                    sourceDoc.add( node );
                }
            } else {
                if (! (context instanceof Element) ) {
                    throw new JspException( "Current node is not an Element: "+context.getClass().getName() );
                }
                Element element = (Element)context;

                SAXReader reader = new SAXReader();

                if (element.isRootElement()) {
                    if (xmlFragment == null) {
                        throw new JspException( "Cannot replace root element with empty body");
                    }
                    Document newDoc = DocumentHelper.parseText( xmlFragment );
                    Document sourceDoc = element.getDocument();
                    Element newRoot = newDoc.getRootElement();
                    newRoot.detach();
                    sourceDoc.setRootElement(newRoot);
                } else {
                    Element parent = element.getParent();
                    List parentContent = parent.content();
                    int index = parentContent.indexOf(element);
                    parentContent.remove(index);
                    if (xmlFragment != null) {
                        Document newDoc = DocumentHelper.parseText( "<dummy>"+xmlFragment+"</dummy>" );
                        parentContent.addAll(index, newDoc.getRootElement().content() );
                    }
                }
            }
        } catch (DocumentException e) { 
            handleException(e);
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
    }
}
