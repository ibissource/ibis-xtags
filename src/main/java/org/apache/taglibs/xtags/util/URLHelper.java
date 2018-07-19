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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/** Some helper methods for creating URLs that can handle relative or absolute
  * URIs or full URLs.
  * 
  * @author <a href="mailto:james.strachan@metastuff.com">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class URLHelper {

    /** @return the URL for the given URI. If the uri contains a ':' then
      * it is assumed to be a URL, otherwise a local URI resource is used.
      */
    public static URL createURL(String uri, PageContext pageContext) throws MalformedURLException {
        if ( uri.indexOf( ":" ) >= 0 ) {
            return new URL( uri );
        }
        else {
            return getResourceURL( uri, pageContext );
        }
    }
    
    /** @return the URL object for the given resource URI using the
      * ServletContext.getResource(String) method. 
      * If the path does not start with a '/' character then a relative
      * URI is calculated.
      */
    public static URL getResourceURL(String uri, PageContext pageContext) throws MalformedURLException {
        if ( uri.charAt(0) != '/' ) {
            // calculate a URI relative to the current JSP page
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            String path = request.getServletPath();
            if ( path.length() > 0 ) {
                int index = path.lastIndexOf( '/' );
                if ( index >= 0 ) {
                    String prefix = path.substring(0, index + 1);
                    uri = prefix + uri;
                }
            }
        }
        return pageContext.getServletContext().getResource( uri );
    }
}
