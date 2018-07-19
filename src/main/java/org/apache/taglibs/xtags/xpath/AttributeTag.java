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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/** Adds an XML attribute to the parent element tag like 
  * the <code>&lt;xsl:attribute&gt;</code> tag.
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class AttributeTag extends AbstractBodyTag  {
    
    /** Holds attributeValue of property name. */
    private String name;
    private ElementTag elementTag;
    

    public AttributeTag() {
    }
    
    // BodyTag interface
    //-------------------------------------------------------------------------
    public void release() {
        super.release();
        name = null;
        elementTag = null;
    }

    public int doStartTag() throws JspTagException {
        getElementTag().addAttribute( getName() );
        return EVAL_BODY_TAG;
    }
    
    public int doAfterBody() throws JspTagException {
        getElementTag().setAttributeValue( getName(), bodyContent.getString() );
        bodyContent.clearBody();
        return SKIP_BODY;
    }
    
    // Properties
    //-------------------------------------------------------------------------
    
    /** Getter for property name.
     * @return AttributeValue of property name.
     */
    public String getName() {
        return name;
    }
    /** Setter for property name.
     * @param name New attributeValue of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------
    protected ElementTag getElementTag() throws JspTagException {
        if ( elementTag != null ) {            
            return elementTag;
        }        
        Tag tag = findAncestorWithClass( this, ElementTag.class );
        if ( tag != null ) {
            if ( tag instanceof ElementTag ) {
                elementTag = (ElementTag) tag;
                return elementTag;
            }
        }
        throw new JspTagException( "<attribute> tag must be enclosed inside an <element> tag" );
    }
}
