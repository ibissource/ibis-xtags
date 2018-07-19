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

import javax.servlet.jsp.tagext.TagSupport;

/** Behaves like the equivalent XSLT tag.
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class ChooseTag extends TagSupport {
    
    private boolean evaluated;
    
    public ChooseTag() {
    }

    /** @return true if a when or otherwise block has been evaluted
      */
    public boolean isEvaluated() {
        return evaluated;
    }
    
    /** Called when an when or otherwise block has been evaluted
      */
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }
    
    // BodyTag interface
    //-------------------------------------------------------------------------
    public int doStartTag() {
        evaluated = false;
        return EVAL_BODY_INCLUDE;
    }
}
