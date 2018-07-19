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



import java.lang.reflect.Method;

import org.dom4j.Node;
import org.dom4j.rule.Action;

/** Calls a void method on an instance when the action is fired.
  *
  * @author James Strachan
  */
public class ReflectionAction implements Action {

    protected static final Object[] NULL_ARGS = {};
    
    protected static final Class[] NULL_PARAMS = {};
    
    /** Holds the object on which to execute a method */
    private Object object; 
    
    /** The method to call */
    private Method method;
    
    

    public ReflectionAction( Object object, String methodName ) {
        this.object = object;
        Class theClass = object.getClass();
        try {
            this.method = theClass.getMethod( methodName, NULL_PARAMS );
        }
        catch (NoSuchMethodException e) {
            System.out.println( "WARNING: " + getClass().getName() +  
                ": Could not find method: " + methodName 
                + " in class: " + theClass.getName() 
            );
        }
    }

    public ReflectionAction( Object object, Method method ) {
        this.object = object;
        this.method = method;
    }

    
    // Action interface
    //-------------------------------------------------------------------------     
    public void run( Node node ) throws Exception {
        //TagHelper.setContext(pageContext, node);
        if ( method != null ) {
            method.invoke( object, NULL_ARGS );
        }
    }
}
