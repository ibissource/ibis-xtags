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

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/** A Helper class of useful String methods.
  *
  * @author James Strachan
  */
public class StringHelper {

    /** Encodes the given XML / HTML so that it will appear in a HTML browser.
      *  so the "<" character is replaced with "&lt;" etc. 
      */
    public static String encodeHTML(String input) {
        int size = input.length();
        StringBuffer buffer = new StringBuffer(size);
        for (int i = 0; i < size; i++ ) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                default:
                    buffer.append(c);
            }
        }
        return buffer.toString();
    }
}    
