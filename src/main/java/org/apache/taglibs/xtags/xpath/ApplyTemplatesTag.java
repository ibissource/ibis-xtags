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
import org.dom4j.DocumentHelper;
import org.dom4j.rule.Stylesheet;


/** The body of this tag defines a stylesheet which is implemented via calling
  * a JSP include.
  *
  * @author James Strachan
  */
public class ApplyTemplatesTag extends AbstractTag {

    /** Holds value of property mode. */
    private String mode;    

    /** Holds the XPath object */
    private XPath xpath;
    

    public ApplyTemplatesTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException {
        flush();
        Stylesheet stylesheet = getStylesheet();
        if ( stylesheet == null ) {
            throw new JspException( 
                "applytemplates tag requires a Stylesheet to be in scope" 
            );
        }
        else {
            try {
                // Tell the current TemplateTag to put its output in the Stylesheet's output list,
                // otherwise the order of the output will be incorrect.
                TemplateTag templateTag = (TemplateTag) findAncestorWithClass( 
                    this, TemplateTag.class 
                );
                templateTag.preApplyTemplates();
                
                Object context = getInputNodes();
                if ( xpath != null ) {
                    stylesheet.applyTemplates( context, xpath );
                }
                else {
                    stylesheet.applyTemplates( context );
                }
                // restore the context back to what it was
                setInputNodes( context );
            }
            catch (JspException e) {
                throw e;
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        xpath = null;
        mode = null;
    }

    // Properties
    //-------------------------------------------------------------------------                

    public void setSelect( String select ) {
        xpath = createXPath( select );
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }    
}
