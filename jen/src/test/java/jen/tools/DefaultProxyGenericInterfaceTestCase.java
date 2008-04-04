/* DefaultProxyGenericInterfaceTestCase.java - Awaiting description
 *
 * Copyright (c)2005 Roscopeco Open Technologies & Contributors
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
 *
 * File version: $Revision: 1.1 $ $Date: 2005/10/31 14:12:28 $
 * Originated: Oct 31, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.converters.JUnitSupport.*;

public class DefaultProxyGenericInterfaceTestCase extends TestCase
{
  public static interface MyInter<T> {
    public T returnOneT();    
    public T[] returnSomeTCopies();    
    public void setTheT(T t);
  }
  
  static final InvocationHandler myInterHandler = new InvocationHandler() {
    private Object t;
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("returnOneT".equals(method.getName())) {
        
        // This should get checkCasted internally so we don't need to bother
        return t;
        
      }
      if ("returnSomeTCopies".equals(method.getName())) {
        
        // Array methods should be careful to return the correct component type.
        // Since arrays are implemented as separate classes, returning the wrong
        // type (it must be an _exact_ match) will cause CCE.
        return new String[] { t.toString(), t.toString(), t.toString() };
        
      }
      if ("setTheT".equals(method.getName())) {
        
        // No need for casting - the JVM does it for us.
        t = args[0];
        
      }
      
      return null;
    }      
  };
  
  /* more common pattern, a handler for a specific class */
  static final InvocationHandler listInterHandler = new InvocationHandler() {
    private List t;
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("returnOneT".equals(method.getName())) {
        return t;        
      }
      if ("returnSomeTCopies".equals(method.getName())) {
        return new List[] { t, t, t };        
      }
      if ("setTheT".equals(method.getName())) {        
        t = (List)args[0];        
      }
      
      return null;
    }      
  };
  
  public void testDefaultProxyWithGenericInterfaceDoesntCauseAbstractMethodErrorsOrThrowCheckCasts() {  
    @SuppressWarnings("unchecked")    
    MyInter<String> mi = SoftProxy.newProxyInstance((Class<MyInter<String>>)MyInter.class, myInterHandler);
    
    mi.setTheT("This is the t");
    assertThat(mi.returnOneT(),isEqual("This is the t"));    
    assertThat(mi.returnSomeTCopies(),exactSize(3));    
    assertThat(mi.returnSomeTCopies().getClass(),isEqual(String[].class));    
    assertThat(mi.returnSomeTCopies()[0],isEqual(mi.returnSomeTCopies()[1]));    
    assertThat(mi.returnSomeTCopies()[1],isEqual(mi.returnSomeTCopies()[2]));
    
    // TODO how to fix this warning? 
    @SuppressWarnings("unchecked")
    MyInter<List> ml = SoftProxy.newProxyInstance(MyInter.class, listInterHandler);
    List l = new ArrayList();
    
    ml.setTheT(l);    
    assertThat(ml.returnOneT(),isSame(l));    
    assertThat(ml.returnSomeTCopies().getClass(),isEqual(List[].class));    
    assertThat(ml.returnSomeTCopies(),exactSize(3));    
    assertThat(ml.returnSomeTCopies()[0],isEqual(ml.returnSomeTCopies()[1]));    
    assertThat(ml.returnSomeTCopies()[1],isEqual(ml.returnSomeTCopies()[2]));    
  }
  
  public void testDefaultProxyWithGenericInterfaceFailsCleanlyOnBadType() {  
    @SuppressWarnings("unchecked")    
    MyInter<String> mi = SoftProxy.newProxyInstance((Class<MyInter<String>>)MyInter.class, new InvocationHandler() {
      private Object t;
      
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("returnOneT".equals(method.getName())) {
          return new Thread();
        }
        if ("returnSomeTCopies".equals(method.getName())) {
          return new Thread[] { new Thread(), new Thread(), new Thread() }; 
        }
        if ("setTheT".equals(method.getName())) {
          t = args[0];
        }
        
        return null;
      }      
    });

    /* It doesn't fail here, since we're only assigning to an Object ref */
    try {
      mi.setTheT("This is the t");
    } catch (ClassCastException e) {
      fail("Unexpected ClassCastException");
    }

    try {
      String t = mi.returnOneT();
      fail("Expected ClassCastException, but method returned " + t);
    } catch (ClassCastException e) {
      /* PASS */
    }

    try {
      String t = mi.returnSomeTCopies()[2];
      fail("Expected ClassCastException, but method returned " + t);
    } catch (ClassCastException e) {
      /* PASS */
    }
  }
}
