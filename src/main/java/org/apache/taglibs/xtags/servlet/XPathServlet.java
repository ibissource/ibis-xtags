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

package org.apache.taglibs.xtags.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/** A Servlet to display the result of an XPath expression as XML
  * 
  * @author James Strachan
  */

public class XPathServlet extends HttpServlet {
    
    private SAXReader reader = new SAXReader();
    private OutputFormat outputFormat = new OutputFormat( "  ", true );
    private XMLWriter writer;
    
    protected static final String XML_MIME_TYPE = "application/xml";
    //private static final String XML_MIME_TYPE = "text/xml";
    
    //-------------------------------------------------------------------------
    public XPathServlet() {
    }
    
    public void init() throws ServletException {
    }
        
    public void destroy() {
        log( "Destroyed" );
    }

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        response.setContentType( XML_MIME_TYPE );
        try {
            if ( writer == null ) {
                writer = new XMLWriter( outputFormat );
            }
            writer.setOutputStream( response.getOutputStream() );
            writer.write( createDocument(request) );
            writer.flush();
        }
        catch (ServletException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

        
    // Implementation methods
    //-------------------------------------------------------------------------
    protected Document createDocument( HttpServletRequest request ) throws ServletException {
        Document document = DocumentHelper.createDocument();
        Element element = document.addElement( "results" );

        try {
            URL url = getDocumentURL(request);
            if ( url != null ) {
                String path = request.getParameter("path");
                if ( path != null && path.length() > 0 ) {
                    Document source = reader.read( url );            
                    XPath xpath = source.createXPath( path );
                    
                    String contextPath = request.getParameter("contextPath");
                    if ( contextPath == null ) {
                        contextPath = ".";
                    }
                    List context = source.selectNodes( contextPath );

                    List results = null;
                    if ( ! getBoolean( request, "sort" ) ) {
                        results = xpath.selectNodes( context );
                    }
                    else {
                        String sortPath = request.getParameter("sortPath");
                        if ( sortPath == null ) {
                            sortPath = ".";
                        }
                        boolean distinct = getBoolean( request, "distinct" );
                        XPath sortXPath = source.createXPath( sortPath );
                        results = xpath.selectNodes( context, sortXPath, distinct );
                    }
                    appendResults( element, results );
                }
            }
        }
        catch (DocumentException e) {
            e.printStackTrace();
            throw new ServletException( "Error parsing document: " + e, e );
        }
        return document;
    }
        
    protected void appendResults( Element element, List results ) {
        for ( int i = 0, size = results.size(); i < size; i++ ) {
            Object result = results.get(i);
            if ( result instanceof String ) {
                element.addText( (String) result );
            }
            else if ( result instanceof Node ) {
                Node node = (Node) result;
                node.detach();
                element.add( node );
            }
            else if ( result != null ) {
                element.addText( result.toString() );
            }
        }
    }
    
    protected boolean getBoolean( HttpServletRequest request, String parameterName ) {
        String text = request.getParameter( parameterName );
        boolean answer = false;
        if ( text != null && text.equalsIgnoreCase( "true" ) ) {
            answer = true;
        }
        return answer;
    }
    
    protected URL getDocumentURL( HttpServletRequest request ) throws ServletException {
        String uri = request.getParameter("uri");
        if ( uri != null && uri.length() > 0 ) {
            try {
                return new URL( uri );
            }
            catch (MalformedURLException e) {
                try {
                    return getServletContext().getResource( uri );
                }
                catch (MalformedURLException e2) {
                    throw new ServletException( "Cannot resolve URI: " + uri, e );
                }
            }
        }
        return null;
    }    
}
