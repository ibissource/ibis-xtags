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

import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/** This tag is used to specify the output format of the XML
  *
  * @author James Strachan
  */
public class OutputTag extends TagSupport {

    /** The XMLWriter used by my child tags */
    private XMLWriter xmlWriter;
    
    /** Holds value of property method. */
    private String method;
    
    /** Holds value of property indent. */
    private String indent;
    
    /** Holds value of property omitXmlDeclaration. */
    private String omitXmlDeclaration;
    
    
    public OutputTag() {
    }

    
    public XMLWriter getXMLWriter() {
        return xmlWriter;
    }
    
    // Tag interface
    //-------------------------------------------------------------------------     
    public int doStartTag() throws JspException {
        OutputFormat format = null;
        if (indent != null) {
            format = OutputFormat.createPrettyPrint();
            format.setIndent(indent);
        }
        else {
            format = new OutputFormat();
        }
        format.setOmitEncoding( asBoolean( omitXmlDeclaration ) );

        Writer out = pageContext.getOut();
        if ( method != null && method.equalsIgnoreCase( "html" ) ) {
            xmlWriter = new HTMLWriter( out, format );
        }
        else {
            xmlWriter = new XMLWriter( out, format );
        }
        return EVAL_BODY_INCLUDE;
    }
    
    public void release() {
        super.release();
        xmlWriter = null;
        indent = null;
        omitXmlDeclaration = null;
        method = null;
    }

    // Properties
    //-------------------------------------------------------------------------                    
    public void setIndent(String indent) {
        this.indent = indent;
    }
    
    public void setOmitXmlDeclaration(String omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    

    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected boolean asBoolean( String text ) {
        if ( text != null ) {
            return indent.equalsIgnoreCase( "yes" ) || indent.equalsIgnoreCase( "true" );
        } 
        return false;
    }        
        
}
