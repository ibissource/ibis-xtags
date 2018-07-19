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

import org.dom4j.XPath;

/** A tag which changes the current context for tags used inside its body.
  * It is similar to the <forEach> tag except no looping is performed.
  *
  * @author James Strachan
  */
public class ContextTag extends ForEachTag {
    
    private boolean firstIteration;
    
    public ContextTag() {
    }

    
    // BodyTag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {        
        firstIteration = true;
        return super.doStartTag();
    }

    /** Performs an iteration and defines a variable of the current object */
    protected boolean hasNext() {
        if ( firstIteration ) {
            firstIteration = false;
            super.hasNext();
            return true;
        }
        return false;
    }
    
}
