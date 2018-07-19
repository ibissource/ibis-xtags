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

import java.util.List;

import javax.servlet.jsp.JspException;

import org.dom4j.Node;
import org.dom4j.NodeFilter;
import org.dom4j.DocumentHelper;

/** Behaves like the equivalent XSLT tag.
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class IfTag extends AbstractTag {
    
    /** Stores the XPath filter to evaluate */
    private NodeFilter nodeFilter;
    

    public IfTag() {
    }
    
    // BodyTag interface
    //-------------------------------------------------------------------------
    public int doStartTag() throws JspException {
        if ( evaluate() ) {
            return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
    }
    
    // Properties
    //-------------------------------------------------------------------------    
    public boolean evaluate() throws JspException {
        if ( nodeFilter != null ) {    
            return matches( nodeFilter );
        }
        return false;
    }

    public void setTest( String test ) {
        nodeFilter = createXPathFilter( test );
    }   
    
    public void setFilter( NodeFilter nodeFilter ) {
        this.nodeFilter = nodeFilter;
    }   
}
