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

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/** A tag which performs a copy-of operation like the XSLT tag
  *
  * @author James Strachan
  */
public class CopyOfTag extends AbstractTag {

    /** Holds value of property xpath. */
    private XPath xpath;
    

    public CopyOfTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspException {
        Object input = getInputNodes();
        if ( xpath != null ) {
            input = xpath.selectNodes( input );
        }
        if ( input != null ) {
            try {
                XMLWriter writer = TagHelper.getXMLWriter( pageContext, this );
                writer.write( input );
            }
            catch (IOException e) {
                handleException(e);
            }
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
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
}
