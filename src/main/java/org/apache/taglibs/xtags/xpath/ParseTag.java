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
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import org.apache.taglibs.xtags.util.URLHelper;

import org.xml.sax.SAXException;

/** A tag which parses its body as an XML Document and defines a variable
  *
  * @author James Strachan
  */
public class ParseTag extends AbstractBodyTag {

    /** Allow tracing to be disabled */
    private static final boolean TRACE = false;    
    
    /** Holds value of property id. */
    private String id;
    
    /** The URL of the document to parse. */
    private URL url;
    
    /** The Reader used to parse XML */
    private Reader reader;
    
    private Document document;
    
    /** Sets whether validation mode is on or off */
    private boolean validate;
    
    public ParseTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {
        document = null;        
        if ( url != null ) {
            try {
                // XXXX: check cache here...
                document = getSAXReader().read( url );
                defineVariable( document );
            }
            catch (Exception e) {
                handleException(e);
            }
            return SKIP_BODY;
        }
        return EVAL_BODY_TAG;
    }
    
    public int doAfterBody() throws JspException {
        if ( document == null ) {
            try {
                if ( reader != null ) {
                    document = getSAXReader().read( reader );
                    reader = null;
                }
                else {
                    BodyContent body = getBodyContent();
                    String text = body.getString().trim();
                    body.clearBody();
                    document = getSAXReader().read( new StringReader(text) );
                }
                defineVariable( document );
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        id = null;
        url = null;
        reader = null;
        document = null;
        validate = false;
    }

    // Properties
    //-------------------------------------------------------------------------                
    
    /** Getter for property id.
     * @return Value of property id.
     */
    public String getId() {
        return id;
    }
    
    /** Setter for property id.
     * @param id New value of property id.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    public void setURL( URL url ) {
        this.url = url;
        
        if ( TRACE ) {
            logInfo( "Set URL to: " + url );
        }
    }
    
    public void setUrl( String url ) throws IOException {
        if ( TRACE ) {
            logInfo( "Setting absolute URL to: " + url );
        }
        
        setURL( new URL( url ) );
    }
    
    public void setUri( String uri ) throws IOException {
        setURL( URLHelper.getResourceURL( uri, pageContext ) );
    }
    
    public void setReader(Reader reader) {
        this.reader = reader;
    }
    
    public boolean getValidate() {
        return validate;
    }
    
    public void setValidate(boolean validate) {
        this.validate = validate;
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    protected SAXReader getSAXReader() throws SAXException {
        // XXXX: special patch for Tomcat 4.0 beta 6
        // which for some reason sets the value of org.xml.sax.driver 
        // to be Xerces even though it ships with crimson by default.
        // So clearing the org.xml.sax.driver property will force JAXP to be
        // used instead - which is a much better idea for web apps anyway.
        //
        // Note that dom4j 0.8 can handle invalid settings of org.xml.sax.driver
        // gracefully and will use JAXP in preference to org.xml.sax.driver anyway
        // so this patch is not required for dom4j versions 0.8 or above
        try {
            System.setProperty( "org.xml.sax.driver", null );
        }
        catch (Throwable t) {
            // ignore any errors
        }
        return new SAXReader(validate);
    }
    
    protected void defineVariable( Document document ) {
        TagHelper.defineVariable( pageContext, getId(), document );
    }
}
