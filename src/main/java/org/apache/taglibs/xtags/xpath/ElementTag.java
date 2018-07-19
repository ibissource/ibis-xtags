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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;


/** A tag to produce an XML element which can contain other attributes 
  * or elements like the <code>&lt;xsl:element&gt;</code> tag.
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class ElementTag extends AbstractBodyTag  {

    /** Should attribute values be trimmed of whitespace? */
    protected static final boolean TRIM_VALUES = true;
    
    /** Holds value of property name. */
    private String name;
    private List attributeNames;
    private Map attributeValues;
    
    //-------------------------------------------------------------------------
    public ElementTag() {
    }

    public void addAttribute( String name ) {
        if ( attributeNames == null ) {
            attributeNames = new ArrayList();
        }
        attributeNames.add( name );
    }

    public void setAttributeValue( String name, String value ) {
        if ( attributeValues == null ) {
            attributeValues = new HashMap();
        }
        attributeValues.put( name, value );
    }

    // BodyTag interface
    //-------------------------------------------------------------------------
    public void release() {
        super.release();
        name = null;
        attributeNames = null;
        attributeValues = null;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_TAG;
    }
    
    public int doAfterBody() throws JspException {
        JspWriter out = bodyContent.getEnclosingWriter();
        //JspWriter out = pageContext.getOut();
        try {
            out.print( "<" + getName() );
            printAttributes( out );
            
            String content = bodyContent.getString();
            if ( content == null || content.length() <= 0 ) {
                out.print( "/>" );
            }
            else {
                out.print( ">" );
                out.print( content );
                out.print( "</" + getName() + ">" );
            }        
            bodyContent.clearBody();
        }
        catch ( IOException e ) {
            handleException( e );
        }
        return SKIP_BODY;
    }
    
    // Properties
    //-------------------------------------------------------------------------
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------    
    protected void printAttributes( JspWriter out ) throws IOException {
        if ( attributeNames != null ) {
            int size = attributeNames.size();
            for ( int i = 0; i < size; i++ ) {
                String attributeName = (String) attributeNames.get( i );
                printAttribute( out, attributeName );
            }
        }
    }
    
    protected void printAttribute( JspWriter out, String attributeName ) throws IOException {
        Object value = null;
        if ( attributeValues != null ) {
            value = attributeValues.get( attributeName );
        }
        String text = attributeName;
        if ( value != null ) {
            if ( TRIM_VALUES ) {
                text = value.toString().trim();
            }
            else {
                text = value.toString();
            }
        }
        out.print( " " + attributeName + "=\"" + text + "\"" );
    }
}
