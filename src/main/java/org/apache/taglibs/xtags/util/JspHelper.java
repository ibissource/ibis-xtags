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

import javax.servlet.jsp.PageContext;

/** A collection of helper methods for JSP tag implementators
  * 
  * @author James Strachan
  */

public class JspHelper {

    /** Catches wierd Exceptions on platforms such as WebLogic when
      * you try and lookup an attribute and its not there
      *
      * @return the value of the given attribute or null if its not found
      */
    public static Object findAttribute(PageContext pageContext, String name) {
        try {
            return pageContext.findAttribute( name );
        }
        catch (Exception e) {
            // shouldn't really cause an exception but
            // some servlet engines do for some wierd reason
            return null;
        }
    }
}
