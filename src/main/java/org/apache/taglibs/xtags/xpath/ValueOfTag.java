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

/** A tag which performs an XPath expression on the current context Node
  *
  * @author James Strachan
  */
public class ValueOfTag extends AbstractTag {

    /** Holds value of property xpath. */
    private XPath xpath;    
    

    public ValueOfTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {        
        if ( xpath != null ) {
            try {
                String text = xpath.valueOf( getInputNodes() );
                text = encode( text );
                pageContext.getOut().print( text );
            }
            catch (IOException e) {
                handleException(e);
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

    
    // Helper methods
    //-------------------------------------------------------------------------                    

    /** Encodes the standard entities in the given string
     */
    public static String encode(String text) {
        StringBuffer buffer = new StringBuffer();
        char[] block = text.toCharArray();
        int size = block.length;
        int last = 0;
        for ( int i = 0; i < size; i++) {
            char ch = block[i];
            
            String entity = null;            
            switch ( ch ) {
                case '<' :
                    entity = "&lt;";
                    break;
                case '>' :
                    entity = "&gt;";
                    break;
                case '&' :
                    entity = "&amp;";
                    break;
                default :
                    /* no-op */ ;
            }
            if (entity != null) {
                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }
        if ( last < size ) {
            buffer.append(block, last, size - last);
        }
        return buffer.toString();
    }    
}
