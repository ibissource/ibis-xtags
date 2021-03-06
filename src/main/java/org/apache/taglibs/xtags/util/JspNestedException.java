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

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.jsp.JspException;

/** A {@link JspException} which is nested to preserve stack traces.
  *
  * This class is not needed in JSP1.2 onwards.
  *
  * <pre>
  *    try {
  *        ...
  *    } catch (Exception e) {
  *        throw new RuntimeException(e);
  *    }
  * </pre>
  *
  * @author James Strachan
  */

public class JspNestedException extends JspException {
    
    private Throwable nestedException;

    //-------------------------------------------------------------------------    
    public JspNestedException(Throwable nestedException) {
        super( nestedException.getMessage() );
        this.nestedException = nestedException;
    }

    public JspNestedException(String message,Exception nestedException) {
        super( message );
        this.nestedException = nestedException;
    }

    public void printStackTrace( PrintStream s ) {
        nestedException.printStackTrace( s );
    }
    
    public void printStackTrace( PrintWriter w ) {
        nestedException.printStackTrace( w );
    }
    
    public void printStackTrace() {
        nestedException.printStackTrace();
    }
    
    public Throwable fillInStackTrace() {
        if ( nestedException == null ) {
            return super.fillInStackTrace(); 
        } else {
            return nestedException.fillInStackTrace();
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------    
    public Throwable getNestedException() {
        return nestedException;
    }
    
}
