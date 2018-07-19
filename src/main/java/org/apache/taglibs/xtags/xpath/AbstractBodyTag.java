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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.xtags.util.JspNestedException;

/** Abstract base class for BodyTag implementations
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class AbstractBodyTag extends BodyTagSupport  {

    public AbstractBodyTag() {
    }

    
    /** Handles non-JspExceptions thrown in this instance
      */
    protected void handleException( Exception e ) throws JspException {
        if ( e instanceof JspException ) {
            throw (JspException) e;
        }
        else {
            logError( e );
            throw new JspNestedException( e );
        }
    }

    
    protected void logInfo(String message) {
        pageContext.getServletContext().log( "INFO: " + getClass().getName() + " : " + message );
    }
    
    protected void logError(Throwable t) {
        logError( t.getMessage() );
        t.printStackTrace();
    }
    
    protected void logError(String message) {
        pageContext.getServletContext().log( "Error: " + getClass().getName() + " : " + message );
    }
}
