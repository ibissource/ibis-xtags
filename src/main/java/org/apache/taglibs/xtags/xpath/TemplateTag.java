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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentHelper;
import org.dom4j.rule.Action;
import org.dom4j.rule.Pattern;
import org.dom4j.rule.Rule;
import org.dom4j.rule.RuleManager;

/** The body of this tag defines a stylesheet which is implemented via calling
  * a JSP include.
  *
  * @author James Strachan
  */
public class TemplateTag extends AbstractBodyTag {

    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property mode. */
    private String mode;    
    
    /** Holds value of property priority. */
    private double priority;
    
    /** Holds value of property jsp. */
    private String jsp;
    
    /** Holds value of property rule. */
    private Rule rule;    

    /** Holds value of property action. */
    private Action action;

    /** Holds value of property avt (attribute value template). */
    private boolean avt;    

    /** The pattern to match */
    private Pattern pattern;

    /** The xpath expression to match */
    private String match;
    
    private StylesheetTag stylesheetTag;
    private Object oldContext = null;
    
    //-------------------------------------------------------------------------                
    public TemplateTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException {
        StylesheetTag tag  = getStylesheetTag();
        if (tag.getCurrentState() == StylesheetTag.INITIALISE_STYLESHEET) {
            Rule rule = getRule();
            if ( rule != null ) {
                if ( tag != null ) {
                    tag.addTemplate( rule );
                }
            }
            return SKIP_BODY;
        } else {
            TemplateExecution te = tag.getTemplateExecution(match);
            if ( te != null ) {
                oldContext = TagHelper.getInputNodes( pageContext );
                TagHelper.setInputNodes( pageContext, te.getNode() );

                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        }
    }

    public int doAfterBody() throws JspException {
        try {
            String text = bodyContent.getString();
            if (isAvt() == true) {
                text = getStylesheetTag().processAVTs(text);
            }
            getStylesheetTag().addOutput( text );
            bodyContent.clearBody();
            if (oldContext != null) {
                TagHelper.setInputNodes( pageContext, oldContext );
                oldContext = null;
            }
        } catch (IOException e) {
            handleException(e);
        }
        return SKIP_BODY;   
    }    
    
    
    public void release() {
        super.release();
        name = null;
        jsp = null;
        match = null;
        rule = null;
        action = null;
        pattern = null;
        oldContext = null;
    }

    
    void preApplyTemplates() throws IOException {
        // An applyTemplates tag is about to be run, so we need to stick our body in the Stylesheets output
        // list at this point (as it needs to be output just before the applyTemplates actions are output).
        String text = bodyContent.getString();
        if (isAvt() == true) {
            text = getStylesheetTag().processAVTs(text);
        }
        getStylesheetTag().addOutput( text );
        bodyContent.clearBody();
    }
    
    // Properties
    //-------------------------------------------------------------------------                

    /** Getter for property avt.
     * @return Value of property avt.
     */
    public boolean isAvt() {
        return avt;
    }
    
    /** Setter for property avt.
     * @param avt New value of property avt.
     */
    public void setAvt(boolean avt) {
        this.avt = avt;
    }   
    
    public void setMatch( String match ) {
        // XXXX: can we do this at compile time? / Class load?
        this.match = match;
        
        StylesheetTag tag  = getStylesheetTag();
        if (tag.getCurrentState() == StylesheetTag.INITIALISE_STYLESHEET ) {
            pattern = createPattern( match );
        }
    }
    
    /** Getter for property priority.
     * @return Value of property priority.
     */
    public double getPriority() {
        return priority;
    }
    
    /** Setter for property priority.
     * @param priority New value of property priority.
     */
    public void setPriority(double priority) {
        this.priority = priority;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Getter for property stylesheetTag.
     * @return Value of property stylesheetTag.
     */
    public StylesheetTag getStylesheetTag() {
        if ( stylesheetTag == null ) {
            stylesheetTag = (StylesheetTag) findAncestorWithClass( 
                this, StylesheetTag.class 
            );
        }
        return stylesheetTag;
    }

    
    /** Getter for property action.
     * @return Value of property action.
     */
    public Action getAction() {
        if ( action == null ) {
            action = createAction();
        }
        return action;
    }
    
    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(Action action) {
        this.action = action;
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    /** Getter for property jsp.
     * @return Value of property jsp.
     */
    public String getJsp() {
        return jsp;
    }
    
    /** Setter for property jsp.
     * @param jsp New value of property jsp.
     */
    public void setJsp(String jsp) {
        this.jsp = jsp;
    }
    
    /** Getter for property rule.
     * @return Value of property rule.
     */
    public Rule getRule() {
        if ( rule == null ) {
            rule = createRule();
        }
        return rule;
    }
    
    /** Setter for property rule.
     * @param rule New value of property rule.
     */
    public void setRule(Rule rule) {
        this.rule = rule;
    }
    
    
    
    
    // Implementation methods
    //------------------------------------------------------------------------- 
    protected Rule createRule() {
        Rule rule = new Rule( pattern, getAction() );
        rule.setMode( mode );
        return rule;
    }
    
    protected Pattern createPattern( String xpath ) {
        return DocumentHelper.createPattern( xpath );
    }
    
    // Action interface
    //-------------------------------------------------------------------------     
    protected Action createAction() {
        if ( jsp != null ) {
            return new JspAction( pageContext, jsp );
        }
        else { 
            return new BodyAction( getStylesheetTag(), this.match );
        }
    }
}
