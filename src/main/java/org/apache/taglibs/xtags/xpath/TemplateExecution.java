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

import org.dom4j.Node;

/** Created by BodyAction objects when a Stylesheet has a template body
 *  that needs to be evaluated as JSP. It records the Node and the template
 *  match XPath expression, so that the StylesheetTag can evaluate it's body
 *  and tell which TemplateTag to fire.
 * 
 * @author James Elson
 */
class TemplateExecution extends java.lang.Object {

    private Node node;
    private String match;
    
    /** Creates new TemplateExecution */
    TemplateExecution(Node node, String match) {
        this.node = node;
        this.match = match;
    }
    
    String getMatch() {
        return this.match;
    }

    Node getNode() {
        return this.node;
    }
}
