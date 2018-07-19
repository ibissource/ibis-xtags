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

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Node;
import org.dom4j.XPath;

/** A tag which defines a variable from an XPath expression 
  *
  * @author James Strachan
  */
public class VariableTag extends AbstractTag {

    /** Holds the XPath selection instance. */
    private XPath xpath;    
    /** Holds value of property id. */
    private String id;
    /** Holds value of property type. */
    private String type;
    
    
    //-------------------------------------------------------------------------                
    public VariableTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {        
        if ( xpath != null ) {    
            Object value = null;
            Object inputNodes = getInputNodes(false);
            if ( type == null ) {
                // default to string value
                value = xpath.valueOf( inputNodes );
            }
            else if ( type.equalsIgnoreCase( "string" ) || type.equals( "java.lang.String" ) ) {
                value = xpath.valueOf( inputNodes );
            }
            else if (type.equals( "node" ) || type.equals( "org.dom4j.Node") ) {
                value = xpath.selectSingleNode( inputNodes );
            }
            else if (type.equals( "list" ) || type.equals( "java.util.List") ) {
                value = xpath.selectNodes( inputNodes );
            }
            else if ( type.equalsIgnoreCase( "number" ) || type.equals( "java.lang.Number" ) || type.equals( "java.lang.Double" ) ) {
                Number n = xpath.numberValueOf( inputNodes );
                value = n;
                if ( type.equals( "java.lang.Double" ) && ! (value instanceof Double) ) {
                    value = new Double( n.doubleValue() );
                }
            }
            else {
                value = xpath.selectObject( inputNodes );
            }
            if ( value == null ) {
                pageContext.removeAttribute( getId() );
            }
            else {
                pageContext.setAttribute( getId(), value );
            }
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        xpath = null;
        id = null;
        type = null;
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
    
    /** Getter for property id.
     * @return Value of property id.
     */
    public String getId() {
        return id;
    }
    
    /** Setter for property id.
     * @param id New value of property id.
     */
    public void setId(String id) {
        this.id = id;
    }
    /** Getter for property type.
     * @return Value of property type.
     */
    public String getType() {
        return type;
    }
    /** Setter for property type.
     * @param type New value of property type.
     */
    public void setType(String type) {
        this.type = type;
    }
}
