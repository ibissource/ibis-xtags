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

package org.apache.taglibs.xtags.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.jaxen.VariableContext;

/** A Servlet to display the result of an XPath expression as XML
  * 
  * @author James Strachan
  */

public class JspVariableContext implements VariableContext {

    public static final String KEY_REQUEST_VARIABLE_CONTEXT = "org.apache.taglibs.xtags.jsp.VariableContext";
    
    /** Stores the page context */
    private PageContext pageContext;


    /** A static helper method to return the DOM4J {@link VariableContext}
      * for the current request, lazily creating an instance if one does not
      * currently exist.
      */
    public static JspVariableContext getInstance(PageContext pageContext) {
        JspVariableContext answer = (JspVariableContext) JspHelper.findAttribute( 
                pageContext,  
                KEY_REQUEST_VARIABLE_CONTEXT 
        );
        if ( answer == null ) {
            answer = new JspVariableContext( pageContext );
            pageContext.setAttribute( 
                KEY_REQUEST_VARIABLE_CONTEXT, 
                answer,
                PageContext.PAGE_SCOPE 
            );
        }
        return answer;
    }
    
    public JspVariableContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public Object getVariableValue(String name) {
        Object answer = JspHelper.findAttribute( pageContext, name );
        if ( answer == null ) {
            answer = pageContext.getRequest().getParameter( name );
            if ( answer == null ) {
                answer = pageContext.getServletContext().getInitParameter( name );
            }
        }
        return answer;
    }
    
    public Object getVariableValue(String prefix, String name) {
        if ( prefix != null && prefix.length() > 0 ) {
            if ( "app".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.APPLICATION_SCOPE );
            }
            else if ( "session".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.SESSION_SCOPE );
            }
            else if ( "request".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.REQUEST_SCOPE );
            }
            else if ( "page".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.PAGE_SCOPE );
            }
            else if ( "param".equals( prefix ) ) {
                return pageContext.getRequest().getParameter( name );
            }
            else if ( "initParam".equals( prefix ) ) {
                return pageContext.getServletContext().getInitParameter( name );
            }
            else if ( "header".equals( prefix ) ) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                return request.getHeader( name );
            }
            else if ( "cookie".equals( prefix ) ) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                Cookie[] cookies = request.getCookies();
                if ( cookies != null ) {
                    for ( int i = 0, size = cookies.length; i < size; i++ ) {
                        Cookie cookie = cookies[i];
                        if ( name.equals( cookie.getName() ) ) {
                            return cookie.getValue();  
                        }
                    }
                }
                return null;
            }
        }
        Object answer = JspHelper.findAttribute( pageContext, name );
        if ( answer == null ) {
            answer = pageContext.getRequest().getParameter( name );
            if ( answer == null ) {
                answer = pageContext.getServletContext().getInitParameter( name );
            }
        }
        return answer;
    }
    
    public Object getVariableValue(String namespaceURI, String prefix, String name) {
        return getVariableValue(prefix, name);
    }
}
