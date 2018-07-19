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

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.dom4j.Node;
import org.dom4j.XPath;

/** The remove tag removes nodes from the current document which matches
  * the given XPath expression.
  *
  * @author James Strachan
  */
public class RemoveTag extends AbstractTag {
    
    /** Holds the XPath selection instance. */
    private XPath xpath;
    
    public RemoveTag() {
    }

    
    // BodyTag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {        
        if ( xpath != null ) {
            List list = xpath.selectNodes( getInputNodes() );
            if ( list != null ) {
                for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
                    Object value = iter.next();
                    if ( value instanceof Node ) {
                        Node node = (Node) value;
                        node.detach();
                    }
                }
            }
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        xpath = null;
    }

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /** Sets the select XPath expression
      */
    public void setSelect(String select) {
        this.xpath = createXPath( select );
    }

    /** Sets the XPath selection expression
      */
    public void setSelectXPath(XPath xpath) {
        this.xpath = xpath;
    }
    
}
