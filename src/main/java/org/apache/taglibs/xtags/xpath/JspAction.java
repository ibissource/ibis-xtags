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

import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentHelper;
import org.dom4j.rule.Action;
import org.dom4j.rule.Pattern;
import org.dom4j.rule.Rule;
import org.dom4j.rule.RuleManager;
import org.dom4j.rule.pattern.DefaultPattern;

/** An Action which includes a piece of JSP
  *
  * @author James Strachan
  */
public class JspAction implements Action {

    /** Holds value of property jsp. */
    private String jsp;
    
    /** Holds value of property pageContext. */
    private PageContext pageContext;
    

    public JspAction() {
    }

    public JspAction(PageContext pageContext, String jsp) {
        this.pageContext = pageContext;
        this.jsp = jsp;
    }

    
    /** Getter for property jsp.
     * @return Value of property jsp.
     */
    public String getJsp() {
        return jsp;
    }
    
    /** Setter for property jsp.
     * @param jsp New value of property jsp.
     */
    public void setJsp(String jsp) {
        this.jsp = jsp;
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
        if ( pageContext == null ) {
            throw new JspException( "No PageContext. Cannot process JSP: " + jsp );
        }
        else
        if ( jsp == null ) {
            throw new JspException( "No JSP! Cannot execute Action" );
        }
        else {
            Object oldContext = TagHelper.getInputNodes( pageContext );
            TagHelper.setInputNodes( pageContext, node );
            
            pageContext.include( jsp );            
            pageContext.getOut().flush();
            
            TagHelper.setInputNodes( pageContext, oldContext );
        }
    }
}
