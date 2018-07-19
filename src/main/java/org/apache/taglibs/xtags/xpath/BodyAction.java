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

import org.dom4j.Node;
import org.dom4j.rule.Action;

/** An Action which tells the Stylesheet tag which template
  * body to execute.
  *
  * @author James Elson
  */
public class BodyAction implements Action {

    /** Holds value of property stylesheetTag */
    private StylesheetTag stylesheetTag;
    
    private String match;

    private Node contextNode;
    
    public BodyAction() {
    }

    public BodyAction(StylesheetTag stylesheetTag, String match) {
        this.stylesheetTag = stylesheetTag;
        this.match = match;
    }
    
    public String getMatch() {
        return this.match;
    }

    public Node getContextNode() {
        return this.contextNode;
    }
    
    
    // Action interface
    //-------------------------------------------------------------------------     
    public void run( Node node ) throws Exception {
        if ( this.stylesheetTag == null ) {
            throw new JspException( "No StylesheetTag. Cannot execute Action");
        }
        else {
            TemplateExecution te = new TemplateExecution(node, this.match);
            stylesheetTag.addTemplateExecution(te);
        }
    }
}
