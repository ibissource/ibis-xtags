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

import javax.servlet.jsp.PageContext;

import org.dom4j.Node;
import org.dom4j.rule.Action;

/** Creates the string-value of the given Node (as defined by the XPath
  * specification) and passes it to the parent StylesheetTag for output
  * at the correct time.
  *
  * @author James Elson
  */
public class StylesheetValueOfAction implements Action {

    /** Holds value of property stylesheetTag */
    private StylesheetTag stylesheetTag;
    
    public StylesheetValueOfAction(StylesheetTag tag) {
        this.stylesheetTag = tag;
    }

    // Action interface
    //-------------------------------------------------------------------------     
    public void run( Node node ) throws Exception {
        String text = node.getStringValue();
        if ( text != null && text.length() > 0 ) {
            text = ValueOfTag.encode( text );
            
            this.stylesheetTag.addOutput( text );
        }
    }
}
