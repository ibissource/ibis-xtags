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



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.dom4j.DocumentException;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.rule.Action;
import org.dom4j.rule.Rule;
import org.dom4j.rule.Stylesheet;

import org.apache.taglibs.xtags.util.JspVariableContext;

/** The body of this tag defines a stylesheet which is implemented via calling
  * a JSP include.
  *
  * @author James Strachan
  */
public class StylesheetTag extends AbstractBodyTag {

    /** Holds the stylesheet which will be applied to the source context. */
    private Stylesheet stylesheet = new Stylesheet();
    
    /** Holds value of property mode. */
    private String mode;    

    /** Previous value of Stylesheet if nesting of stylesheet occurs */
    private Stylesheet previousStylesheet;
    
    /** The default action used to specify the value of a node */
    
    private Action valueOfAction;
    
    private Stack templateResultLists = new Stack();
    private Stack resultIterators = new Stack();
    
    private List openResultList;
    
    private int currentState;
    private TemplateExecution currentMatch;
    private StringBuffer avtOutput = new StringBuffer(256);
    private StringBuffer actionOutput = new StringBuffer(2048);
    
    static final int INITIALISE_STYLESHEET = 1;
    static final int RUN_TEMPLATES = 2;
    
    protected Object context;    
        
    public StylesheetTag() {
        // add default actions
        valueOfAction = new StylesheetValueOfAction(this);
        stylesheet.setValueOfAction( valueOfAction );
    }

    public Stylesheet getStylesheet() {
        return stylesheet;
    }
    
    public void addTemplate( Rule rule ) {
        stylesheet.addRule( rule );
    }

    int getCurrentState() {
        return this.currentState;
    }
    
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
    }
    
    public int doStartTag() throws JspException {
        previousStylesheet = TagHelper.getStylesheet( pageContext );
        TagHelper.setStylesheet( pageContext, stylesheet );
        stylesheet.clear();
        templateResultLists.clear();
        actionOutput.delete(0, actionOutput.length() );
        
        currentState = INITIALISE_STYLESHEET;
        
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspException {
        try {
            if (currentState == INITIALISE_STYLESHEET) {
                // Need to run the stylesheet
                runStylesheet();

                currentState = RUN_TEMPLATES;
            } else {
                // Make sure that any text in the output gets pushed into the result
                // list - this is necessary if the last action was not a template execution
                addOutputToResultList();
                // Get rid of the writespace between the templates.
                bodyContent.clear();
            }
            
            if (this.openResultList != null) {
                // Need suspend processing the current result list, and start on this one...
                this.templateResultLists.push(this.openResultList);
                this.resultIterators.push( this.openResultList.iterator() );
                this.openResultList = null;
            }
                
            return processResults();
        } catch (Exception e) {
            handleException(e);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        // restore the previous stylesheet value
        TagHelper.setStylesheet( pageContext, previousStylesheet );
        reset();
        return EVAL_PAGE;
    }

    public void runStylesheet() throws Exception {
        stylesheet.setModeName( getMode() );
        stylesheet.run( getInputNodes() );
    }
    
    /** This method is called once the stylesheet has been run, and the list of output strings
     *  and template executions has been collated. Each string in the list is output to the 
     *  BodyContent's writer, and whenever a template body needs to be run, the StylesheetTag will
     *  evaluate its body again evaluating the correct TemplateTag body. Then it resumes processing
     *  of the outputs and so on until the list is exhausted.
     */
    int processResults() throws IOException {
        while (! this.resultIterators.isEmpty()) {
            Iterator resultIter = (Iterator)this.resultIterators.peek();
            while (resultIter.hasNext()) {
                Object o = resultIter.next();
                if (o instanceof String) {
                    bodyContent.getEnclosingWriter().write( (String)o );
                } else{
                    currentMatch = (TemplateExecution)o;
                    return EVAL_BODY_TAG;
                }
            }
            this.resultIterators.pop();
            this.templateResultLists.pop();
        }
        return SKIP_BODY;
    }

    /** TemplateTag objects call this method to see if they should run their template
     *  bodies. If the template matches the first on our "execution list", then it should
     *  run.
     *  @returns a TemplateExecution object that tells the template which node to use as the
     *           context for its evaluation. This was recorded when the XLST action created the
     *           TemplateExecution object (see {@link BodyAction}). <br>
     *           -or-<br>
     *           <code>null</code> to tell the template body NOT to evaluate its body.
     */
    TemplateExecution getTemplateExecution(String match) {
        if (currentMatch.getMatch().equals(match)) {
            return currentMatch;
        } else {
            return null;
        }
    }
    
    public void release() {
        super.release();
        reset();
    }

    void reset() {
        stylesheet.clear();
        openResultList = null;
        currentMatch = null;
        templateResultLists.clear();
        resultIterators.clear();
        actionOutput.delete(0, actionOutput.length() );
        avtOutput.delete(0, actionOutput.length() );
    }
    // Properties
    //-------------------------------------------------------------------------                
    
    /** Getter for property mode.
     * @return Value of property mode.
     */
    public String getMode() {
        return mode;
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }   
    
    /* Adds a string into the output of the stylesheet.
     */
    void addOutput(String text){
        actionOutput.append(text);
    }

    void addOutputToResultList() {
        if (actionOutput.length() > 0) {
            getOpenResultList().add( actionOutput.toString() );
            actionOutput.delete(0, actionOutput.length() );
        }
    }
    
    /** 
     * Adds a request to execute a Stylesheet's template body.
     * Called by the XSLT template's Action when it matches such
     * a template rule.
     */
    void addTemplateExecution(TemplateExecution te) {
        // Add any text in the actionOutput to the result list, before we add 
        // the TemplateExecution
        addOutputToResultList();
        getOpenResultList().add(te);
    }
    
    List getOpenResultList() {
        if (this.openResultList == null) {
            this.openResultList = new ArrayList(256);
        }
        return this.openResultList;
    }

    String processAVTs(String text) throws IOException, InvalidXPathException {
        int marker = 0;
        int leftBracket;
        int rightBracket;
        XPath xpath;
        Object context = getInputNodes();
        
        avtOutput.delete(0, avtOutput.length() );

        while ( (leftBracket = text.indexOf('{', marker)) > 0) {
            // output all text up to the { 
            avtOutput.append(text.substring(marker,leftBracket));
            rightBracket = text.indexOf('}', leftBracket);
            if (rightBracket < 0) {
                marker = leftBracket; // It's not part of an xpath expression
                // No more valid {xpath} expressions
                break;
            }
            xpath = createXPath( text.substring(leftBracket+1, rightBracket) );
            if (xpath == null) {
                throw new InvalidXPathException( text.substring(leftBracket+1, rightBracket) );
            }
            avtOutput.append( xpath.valueOf( context ) );
            marker = rightBracket+1;
        }
        if (marker < text.length()) {
            avtOutput.append( text.substring(marker) );
        }
        
        return avtOutput.toString();
    }
    
    
    
    
    /** @return the input node on which to make a selction
      */
    public Object getInputNodes() {
        if ( context == null ) {
            return TagHelper.getInputNodes( pageContext, this, true );
        }
        return context;
    }
    
    /** A factory method to create new XPath instances */
    protected XPath createXPath(String xpathExpression) {
        XPath xpath = getDocumentFactory().createXPath( xpathExpression ); 
        xpath.setVariableContext( JspVariableContext.getInstance( pageContext ) );
        return xpath;
    }

    /** @return the factory used to create XPath instances */
    protected DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
