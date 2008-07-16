/* StubMock.java - Stub-based MockMethod
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
 * File version: $Revision: 1.5 $ $Date: 2005/10/20 22:34:44 $
 * Originated: Oct 6, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.roscopeco.juno.Constraint;

import static java.util.Arrays.asList;

import static jen.SoftUtils.nullSafeArg;

/** 
 * {@link MockMethod} implementation that applies constraint-based invocation
 * selection, and uses a 'stub' (another {@link InvocationHandler} in this case)
 * to satisfy the invocation if it matches the constraints. This is the most
 * common {@code MockMethod} type.
 * <p/>
 * As well as the {@code MockMethod} implementation, this class provides a number
 * of static utility methods providing useful default stub implementations, 
 * with functionality such as returning a value and throwing an exception. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.5 $ $Date: 2005/10/20 22:34:44 $ 
 */
public class StubMock extends ConstrainedMockMethod
{
  /**
   * A {@link Constraint} that matches anything. This is provided as a 
   * convenience for use with the constraints-based constructors. 
   */
  public static final Constraint ANYTHING = new Constraint() {
    public boolean eval(Object o) {
      return true;
    }    
  };
  
  /* **********************************
   * INSTANCE STUFF
   */
  private final String name;
  private final InvocationHandler stub;
    
  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} and any combination of arguments (including none).
   * Calls to matching methods will be dispatched to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  public StubMock(String name, InvocationHandler stub) {
    this(name,(List<Constraint>)null,stub);
  }  

  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} only if the supplied {@link Constraint}s match the
   * actual method arguments at the equivalent index in the argument array.
   * The constraints match is performed in a relaxed way, with no requirement that
   * the constraint array be the same size as the actual method array at invocation.
   * If there are more arguments than constraints, then any remaining arguments 
   * are implicitly matched. Left-over constraints are simply ignored. Of the 
   * supplied constraints, <strong>all</strong> must match against their equivalent
   * argument in order for invocation to proceed with this mock.
   * <p/> 
   * Any {@code null} elements in the supplied array are considered equivalent
   * to the {@link #ANYTHING} constraint, though it is recommended this be 
   * explicitly specified when used (for the sake of readability).
   * <p/>  
   * Calls to matching methods will be dispatched to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param paramConstraints An array of {@code Constraint}s that will be matched
   *        against the supplied arguments.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  public StubMock(String name, Constraint[] paramConstraints, InvocationHandler stub) {
    this(name,asList(paramConstraints),stub);
  }

  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} only if the supplied {@link Constraint}s match the
   * actual method arguments at the equivalent index in the argument array.
   * See {@link #StubMock(String, Constraint[], InvocationHandler)} for details
   * on parameter matching.
   * Calls to matching methods will be dispatched to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param paramConstraints A {@code List} of {@code Constraint}s that will be matched
   *        against the supplied arguments.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  public StubMock(String name, List<Constraint> paramConstraints, InvocationHandler stub) {
    this(name,paramConstraints,false,stub);
  }

  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} only if the supplied {@link Constraint}s match the
   * actual method arguments at the equivalent index in the argument array.
   * This constructor optionally performs a more strict match on the arguments, 
   * requiring there be the same number of elements in the constraints array as 
   * there are arguments. In other respects, mocks instantiated with this 
   * constructor behave as described in 
   * {@link #StubMock(String, Constraint[], InvocationHandler)}.
   * Calls to matching methods will be dispatched to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param paramConstraints An array of {@code Constraint}s that will be matched
   *        against the supplied arguments.
   * @param enforceArgCount If {@code true}, invocation will match only if the
   *        {@code paramConstraints} array is the same size as the argument array.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  public StubMock(String name, Constraint[] paramConstraints, boolean enforceArgCount, InvocationHandler stub) {
    this(name,asList(paramConstraints),enforceArgCount,stub);
  }

  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} only if the supplied {@link Constraint}s match the
   * actual method arguments at the equivalent index in the argument array.
   * This constructor performs a more strict match on the arguments, as described
   * in {@link #StubMock(String, Constraint[], boolean, InvocationHandler)}.
   * Calls to matching methods will be dispatched to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param paramConstraints A {@code List} of {@code Constraint}s that will be matched
   *        against the supplied arguments.
   * @param enforceArgCount If {@code true}, invocation will match only if the
   *        {@code paramConstraints} array is the same size as the argument array.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  public StubMock(String name, List<Constraint> paramConstraints, boolean enforceArgCount, InvocationHandler stub) {
    this(name,null,null,paramConstraints,enforceArgCount,stub);
  }

  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} only if the supplied {@link Constraint}s match the
   * proxy instance, method return <strong>type</strong>, and actual method arguments 
   * resectively. See 
   * {@link #StubMock(String, Constraint, Constraint, List, boolean, InvocationHandler)}.
   * for details of the constraint match. Calls to matching methods will be dispatched 
   * to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param proxyConstraint A {@code Constraint} that will be matched against the proxy
   *        instance itself.
   * @param returnTypeConstraint A {@code Constraint} that will be matched against the
   *        proxied method's return type.
   * @param paramConstraints An array of {@code Constraint}s that will be matched
   *        against the supplied arguments.
   * @param enforceArgCount If {@code true}, invocation will match only if the
   *        {@code paramConstraints} array is the same size as the argument array.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  public StubMock(String name, 
      Constraint proxyConstraint,
      Constraint returnTypeConstraint,
      Constraint[] paramConstraints, 
      boolean enforceArgCount, 
      InvocationHandler stub) {
    this(name,asList(paramConstraints),stub);
  }
  
  /**
   * Create a new {@code StubMock} that will match invocation of methods with the 
   * specified {@code name} only if the supplied {@link Constraint}s match the
   * proxy instance, method return <strong>type</strong>, and actual method arguments 
   * resectively. See {@link #StubMock(String, Constraint[], boolean, InvocationHandler)} 
   * and {@link #StubMock(String, Constraint[], boolean, InvocationHandler)} for details
   * on parameter constraints. Either the proxy and return-type constraint (or both)
   * may be {@code null}, which is considered equivalent to the {@link #ANYTHING} 
   * constraint. Calls to matching methods will be dispatched to the supplied 'stub'.
   * 
   * @param name The method name this mock will match.
   * @param proxyConstraint A {@code Constraint} that will be matched against the proxy
   *        instance itself.
   * @param returnTypeConstraint A {@code Constraint} that will be matched against the
   *        proxied method's return type.
   * @param paramConstraints A {@code List} of {@code Constraint}s that will be matched
   *        against the supplied arguments.
   * @param enforceArgCount If {@code true}, invocation will match only if the
   *        {@code paramConstraints} array is the same size as the argument array.
   * @param stub An {@link InvocationHandler} that will service calls matched by 
   *        this mock.
   */
  /* WORKER */
  public StubMock(String name, 
                  Constraint proxyConstraint,
                  Constraint returnTypeConstraint,
                  List<Constraint> paramConstraints, 
                  boolean enforceArgCount, 
                  InvocationHandler stub) {
    super(proxyConstraint,returnTypeConstraint,paramConstraints,enforceArgCount);
    this.name = nullSafeArg(StubMock.class,"name",name);
    this.stub = nullSafeArg(StubMock.class,"stub",stub);
  }
  
  /**
   * Returns {@code true} if the specified invocation matches all constraints placed
   * on this {@code StubMock}.
   */
  public boolean invocationMatches(Object proxy, Method method, Object[] args) {
    return (method.getName().equals(name) ? super.invocationMatches(proxy,method,args) : false); 
  }

  /**
   * Dispatches the specified invocation to the stub {@link InvocationHandler} supplied 
   * to this {@code StubMock} at instantiation.
   */
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    return stub.invoke(proxy,method,args);
  }
}
