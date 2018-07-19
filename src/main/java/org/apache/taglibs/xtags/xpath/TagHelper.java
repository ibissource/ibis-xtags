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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.rule.Stylesheet;

/** A number of helper methods
  *
  * @author James Strachan
  */
public class TagHelper {

    /** Request scope attribute name used to pass the context between
      * JSP files 
      */
    public static final String REQUEST_KEY_CONTEXT = "org.apache.taglibs.xtags.taglib.Context";
    
    /** Request scope attribute name used to pass the stylesheet between
      * JSP files */
    public static final String REQUEST_KEY_STYLESHEET = "org.apache.taglibs.xtags.taglib.Stylesheet";

    protected static final OutputFormat outputFormat;
    
    /** Request scope attribute name used to pass the XMLWriter between
      * JSP files 
      */
    public static final String REQUEST_KEY_XML_WRITER = "org.apache.taglibs.xtags.XMLWriter";
    
    static {
        outputFormat = new OutputFormat();
        outputFormat.setSuppressDeclaration(true);
    }
    
    
    public static OutputFormat getOutputFormat( PageContext pageContext ) {
        return outputFormat;
    }
   
    public static XMLWriter getXMLWriter(PageContext pageContext, Tag thisTag) {
        OutputTag tag = (OutputTag) TagSupport.findAncestorWithClass( 
            thisTag, OutputTag.class 
        );
        if ( tag != null ) {
            return tag.getXMLWriter();
        }
        return new XMLWriter( pageContext.getOut(), getOutputFormat( pageContext ) );
    }

    public static XMLWriter createXMLWriter(PageContext pageContext) {
        return new XMLWriter( pageContext.getOut(), getOutputFormat( pageContext ) );
    }

   
    
    /** @return the input node on which to make a selction
      */
    public static Object getInputNodes(PageContext pageContext) {
        Object nodes = pageContext.getAttribute( 
            REQUEST_KEY_CONTEXT, PageContext.PAGE_SCOPE 
        );
        if (nodes == null) {
            nodes = pageContext.getAttribute( 
                REQUEST_KEY_CONTEXT, PageContext.REQUEST_SCOPE 
            );
        }
        return nodes;
    }
    
    /** @return the input node on which to make a selction
      */
    public static Object getInputNodes(PageContext pageContext, Tag thisTag, boolean warn) {
        Object context = null;
        ContextNodeTag tag = (ContextNodeTag) TagSupport.findAncestorWithClass( 
            thisTag, ContextNodeTag.class 
        );
        if ( tag != null ) {
            context = tag.getContext();
        }
        if ( context == null ) {
            context = getInputNodes( pageContext );
        }
/*        
        if ( context == null && warn ) {
            pageContext.getServletContext().log( "WARNING: No Input Node found!" );
            Exception e = new Exception();
            e.printStackTrace();
        }
*/
        return context;
    }
    
    public static void setInputNodes( PageContext pageContext, Object inputNodes ) {
        if ( inputNodes == null ) {
            pageContext.removeAttribute( 
                REQUEST_KEY_CONTEXT,  
                PageContext.PAGE_SCOPE 
            );
            pageContext.removeAttribute( 
                REQUEST_KEY_CONTEXT,  
                PageContext.REQUEST_SCOPE 
            );
        }
        else {
            pageContext.setAttribute( 
                REQUEST_KEY_CONTEXT,  
                inputNodes, 
                PageContext.PAGE_SCOPE 
            );
            pageContext.setAttribute( 
                REQUEST_KEY_CONTEXT,  
                inputNodes, 
                PageContext.REQUEST_SCOPE 
            );
        }
    }
    
    public static Stylesheet getStylesheet(PageContext pageContext) {
        return (Stylesheet) pageContext.getAttribute( 
            REQUEST_KEY_STYLESHEET, 
            PageContext.REQUEST_SCOPE 
        );
    }
    
    public static void setStylesheet(PageContext pageContext, Stylesheet stylesheet) {
        pageContext.setAttribute( 
            REQUEST_KEY_STYLESHEET,  
            stylesheet, 
            PageContext.REQUEST_SCOPE 
        );
    }
    
    public static void defineVariable(PageContext pageContext, String id, Object value ) {
        if ( id != null ) {
            pageContext.setAttribute( id, value );
        }
        setInputNodes( pageContext, value );
    }
}
