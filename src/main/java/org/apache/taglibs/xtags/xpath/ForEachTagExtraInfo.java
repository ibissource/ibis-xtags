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

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/** The extra info for the foreach tag
  *
  * @author James Strachan
  */
public class ForEachTagExtraInfo extends TagExtraInfo {

    public ForEachTagExtraInfo() {
    }
    
    public VariableInfo[] getVariableInfo(TagData data) {
        String instanceName = data.getAttributeString( "id" );
        if ( instanceName == null ) {
            return new VariableInfo[0]; 
        }
        String typeName = data.getAttributeString( "type" );
        if (typeName == null) {
            typeName = "java.lang.Object";
        }
        else if ("node".equalsIgnoreCase( typeName )) {
            typeName = "org.dom4j.Node";
        }
        else if ("string".equalsIgnoreCase( typeName ) ) {
            typeName = "java.lang.String";
        }
        else if ("object".equalsIgnoreCase( typeName ) ) {
            typeName = "java.lang.Object";
        }
        else if ("boolean".equalsIgnoreCase( typeName ) ) {
            typeName ="java.lang.Boolean";
        }
        else if ("number".equalsIgnoreCase( typeName ) ) {
            typeName ="java.lang.Number";
        }
        VariableInfo[] answer = {
            new VariableInfo( instanceName, typeName, true, VariableInfo.NESTED )
        };
        return answer;
    }
}
