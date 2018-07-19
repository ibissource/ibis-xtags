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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/** Causes the current iteration to be terminated rather like the Java 'break' statement.
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class BreakTag extends TagSupport  {
    
    // Tag interface
    //-------------------------------------------------------------------------
    public int doStartTag() throws JspException {
        ForEachTag tag = (ForEachTag) findAncestorWithClass( this, ForEachTag.class );
        if ( tag != null ) {
            tag.breakLoop();
        }
        return SKIP_BODY;
    }
}
