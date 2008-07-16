/* SoftMock.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/31 01:50:46 $
 * Originated: Oct 31, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static jen.SoftUtils.defaultClassLoader;
import static jen.tools.DefaultInvokerStrategy.invokeSuper;

/** 
 * Convenient mock implementation proxy generation methods. This class
 * provides a number of static methods that provide for common mock 
 * usage patterns. As well as methods that return a new mock instance
 * based on the {@link MockProxyHandler} with a {@link SoftProxy} 
 * for the specified classes, it provides various useful stub 
 * {@link InvocationHandler}s for use with the {@link StubMock}
 * {@link MockMethod} implementation.
 * <p/>
 * This is a purely static class with no instances or shared state.
 * 
 * @see MockProxyHandler
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/31 01:50:46 $ 
 */
public class SoftMock
{
  /**
   * Create a new mock implementation proxy for the specified single class.
   * See {@link #newMockInstance(Class[], MockMethod[])
   * for detailed information.
   *   
   * @param clazz The class for which a mock implementation is required.
   * @param mocks An array of {@link MockMethod}s that will handle invocations
   *        on this mock. 
   * 
   * @return The new mock instance.
   */
  public static <T> T newMockInstance(Class<T> clazz, MockMethod[] mocks) {
    return newMockInstance(clazz, new Object[0], mocks);
  }

  /**
   * Create a new mock implementation proxy, passing the supplied arguments
   * to the proxied constructor. See 
   * {@link #newMockInstance(Class[], Object[], MockMethod[])
   * for detailed information.
   *  
   * @param clazz The class for which a mock implementation is required.
   * @param args An array of arguments to pass to the proxied constructor.
   * @param mocks An array of {@link MockMethod}s that will handle invocations
   *        on this mock. 
   * 
   * @return The new mock instance.
   */
  public static <T> T newMockInstance(Class<T> clazz, Object[] args, MockMethod[] mocks) {
    return newMockInstance(defaultClassLoader(), clazz, args, mocks);
  }

  /* CLASS WORKER */
  /**
   * Create a new mock implementation proxy in the supplied {@link ClassLoader}, passing
   * the supplied arguments to the proxied constructor. See 
   * {@link #newMockInstance(ClassLoader, Class[], Object[], MockMethod[])
   * for detailed information.
   *  
   * @param loader The {@code ClassLoader} from which to load, and in which to
   *        define, classes.
   * @param clazz The class for which a mock implementation is required.
   * @param args An array of arguments to pass to the proxied constructor.
   * @param mocks An array of {@link MockMethod}s that will handle invocations
   *        on this mock. 
   * 
   * @return The new mock instance.
   */
  public static <T> T newMockInstance(ClassLoader loader, Class<T> clazz, Object[] args, MockMethod[] mocks) {
    Object[] argsAndHandler;
    
    if (args != null && args.length > 0) {
      argsAndHandler = new Object[args.length + 1];
      argsAndHandler[0] = new MockProxyHandler(mocks);
      System.arraycopy(args,0,argsAndHandler,1,args.length);
    } else {
      argsAndHandler = new Object[] { new MockProxyHandler(mocks) };
    }
    
    return SoftProxy.newProxyInstance(loader,clazz,argsAndHandler);
  }
  
  /**
   * Create a new mock implementation proxy for the specified multiple classes.
   * If the supplied class is a non-interface, it must define a
   * null-constructor with non-{@code private} access - if it does not one
   * of the overloads that accept an {@code Object[]} argument array should be
   * used instead.
   *  
   * @param clazz The class for which a mock implementation is required.
   * @param mocks An array of {@link MockMethod}s that will handle invocations
   *        on this mock. 
   * 
   * @return The new mock instance.
   */
  public static Object newMockInstance(Class<?>[] classes, MockMethod[] mocks) {
    return newMockInstance(classes, new Object[0], mocks);
  }

  /**
   * Create a new mock implementation proxy for the specified multiple classes,
   * passing the supplied arguments to the proxied constructor. The constructor 
   * that most closely matches the runtime types of the {@code arg} array's 
   * elements (ignoring the initial {@link InvocationHandler} parameter) will
   * be used to initialize the new instance. If the specified class is an 
   * interface, the proxy will declare only the null constructor.
   *  
   * @param clazz The class for which a mock implementation is required.
   * @param args An array of arguments to pass to the proxied constructor.
   * @param mocks An array of {@link MockMethod}s that will handle invocations
   *        on this mock. 
   * 
   * @return The new mock instance.
   */
  public static Object newMockInstance(Class<?>[] classes, Object[] args, MockMethod[] mocks) {
    return newMockInstance(defaultClassLoader(), classes, args, mocks);
  }

  /* CLASSES WORKER */
  /**
   * Create a new mock implementation proxy for the specified multiple classes in
   * the supplied {@link ClassLoader}, passing the supplied arguments to the 
   * proxied constructor. The constructor that most closely matchesthe runtime types 
   * of the {@code arg} array's elements (ignoring the initial 
   * {@link InvocationHandler} parameter) will be used to initialize the
   * new instance. If the specified class is an interface, the proxy will 
   * declare only the null constructor.
   *  
   * @param loader The {@code ClassLoader} from which to load, and in which to
   *        define, classes.
   * @param classes The classes for which a mock implementation is required.
   * @param args An array of arguments to pass to the proxied constructor.
   * @param mocks An array of {@link MockMethod}s that will handle invocations
   *        on this mock. 
   * 
   * @return The new mock instance.
   */
  public static Object newMockInstance(ClassLoader loader, Class<?>[] classes, Object[] args, MockMethod[] mocks) {
    Object[] argsAndHandler;
    
    if (args != null && args.length > 0) {
      argsAndHandler = new Object[args.length + 1];
      argsAndHandler[0] = new MockProxyHandler(mocks);
      System.arraycopy(args,0,argsAndHandler,1,args.length);
    } else {
      argsAndHandler = new Object[] { new MockProxyHandler(mocks) };
    }
    
    return SoftProxy.newProxyInstance(loader,classes,argsAndHandler);
  }  
  
  /* **********************************
   * STANDARD STUB METHODS
   */
  /**
   * Returns a standard method stub {@link InvocationHandler} that 
   * returns the supplied object. It is your responsibility to ensure that
   * the type returned can be cast (or unboxed) to the return type of the
   * method.
   * 
   * @param o The {@code Object} to be returned from the mock method. Primitives
   *        should be wrapped with the appropriate wrapper from {@code java.lang}.
   *        
   * @return A new stub {@code InvocationHandler} that can be passed to the 
   *         appropriate {@code StubMock} constructor. 
   */
  public static final InvocationHandler returnValue(final Object o) {
    return new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
        return o;
      }      
    };    
  }

  /**
   * Returns a standard method stub {@link InvocationHandler} that will
   * throw the specified {@link Throwable}. If the supplied throwable is 
   * not an unchecked {@link RuntimeException} then you should ensure it 
   * is declared in the proxied method's signature to avoid unexpected
   * behaviour.
   * 
   * @param o The {@code Throwable} to be thrown by the mock method.
   *        
   * @return A new stub {@code InvocationHandler} that can be passed to the 
   *         appropriate {@code StubMock} constructor. 
   */
  public static final InvocationHandler throwException(final Throwable e) {
    return new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
        throw(e);
      }      
    };    
  }

  /**
   * Returns a standard method stub {@link InvocationHandler} that will
   * pass the invocation to the overriden (proxied) superclass method, 
   * if any. If the invoked method proxies an abstract (possibly interface)
   * method {@code null} is returned immediately. Otherwise, the superclass
   * method is invoked using {@link invokeSuper}
   * and it's return value (or {@code null} if the method is {@code void}) 
   * passed to the caller.
   * 
   * @return A new stub {@code InvocationHandler} that can be passed to the 
   *         appropriate {@code StubMock} constructor.
   */
  public static final InvocationHandler callSuper() {
    return new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
        return invokeSuper(proxy,method,args);
      }      
    };    
  }  
}
