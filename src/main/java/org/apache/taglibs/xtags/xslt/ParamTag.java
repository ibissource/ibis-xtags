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

package org.apache.taglibs.xtags.xslt;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/** A tag which declares a parameter for use in an XSLT stylesheet
  *
  * @author James Strachan
  */
public class ParamTag extends BodyTagSupport {

    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property value. */
    private Object value;
    
    
    public ParamTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------     
    public int doStartTag() throws JspException {
        if ( name == null ) {
            throw new JspException( "No value specified for attribute 'name'" );
        }
        if ( value != null ) {
            defineParameter(value);
            return SKIP_BODY;
        }
        return EVAL_BODY_TAG;
    }
    
    public int doAfterBody() throws JspException {        
        if ( value == null ) {
            BodyContent body = getBodyContent();
            String text = body.getString();
            body.clearBody();
            defineParameter(text);
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        name = null;
        value = null;
    }

    // Properties
    //-------------------------------------------------------------------------                    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected void defineParameter(Object value) throws JspException {        
        ParameterAcceptingTag tag = (ParameterAcceptingTag) findAncestorWithClass( 
            this, ParameterAcceptingTag.class 
        );
        if ( tag == null ) {
            throw new JspException( "<param> tag must be used inside a <style> tag!" );
        }
        tag.setParameter( name, value );
    }
}
