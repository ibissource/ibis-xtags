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

import javax.servlet.jsp.PageContext;

import org.dom4j.Node;
import org.dom4j.io.XMLWriter;
import org.dom4j.rule.Action;

/** Outputs the given Node to the current JSP output
  *
  * @author James Strachan
  */
public class JspCopyOfAction implements Action {

    /** Holds value of property pageContext. */
    private PageContext pageContext;
    
    /** Holds value of property xmlWriter. */
    private XMLWriter xmlWriter;    
    

    public JspCopyOfAction() {
    }

    public JspCopyOfAction(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    
    /** Getter for property pageContext.
     * @return Value of property pageContext.
     */
    public PageContext getPageContext() {
        return pageContext;
    }
    
    /** Setter for property pageContext.
     * @param pageContext New value of property pageContext.
     */
    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    
    
    // Action interface
    //-------------------------------------------------------------------------     
    public void run( Node node ) throws Exception {
        if ( node != null ) {
            XMLWriter writer = getXMLWriter();
            writer.setWriter( pageContext.getOut() );
            writer.write( node );
            writer.flush();
        }
    }

    // Properties
    //------------------------------------------------------------------------- 
    
    /** Getter for property xmlWriter.
     * @return Value of property xmlWriter.
     */
    public XMLWriter getXMLWriter() {
        if ( xmlWriter == null ) {
            xmlWriter = TagHelper.createXMLWriter( pageContext );
        }
        return xmlWriter;
    }
    
    /** Setter for property xmlWriter.
     * @param xmlWriter New value of property xmlWriter.
     */
    public void setXMLWriter(XMLWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
    }
}
