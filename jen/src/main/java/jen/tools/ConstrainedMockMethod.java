/* ConstrainedMockMethod.java - Constraints-based Mock method
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:44 $
 * Originated: Oct 6, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.roscopeco.juno.Constraint;

import static java.util.Collections.unmodifiableList;

/** 
 * Abstract {@link MockMethod} implementation that determines whether an
 * invocation matches by testing the various invocation parameters against supplied
 * constraints. This is intended to provide a foundation for specific {@code MockMethod}s
 * with constraints-based matching.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/20 22:34:44 $ 
 */
public abstract class ConstrainedMockMethod implements MockMethod
{
  private final Constraint proxyConstraint;
  private final Constraint returnTypeConstraint;
  private final List<Constraint> argConstraints;
  private final boolean enforceCount;
  
  /**
   * Create a new {@code ConstrainedMockMethod} that will match a given invocation
   * if the constraints present in the supplied {@link List} match the corresponding
   * argument array elements.
   * <p/>
   * This constructor does not enforce the size of the argument array. When checking
   * the constraints, as many of the arguments as possible will be checked against
   * a constraint from the same array index. <code>null</code> constraints in the
   * supplied list are treated as a special case, equivalent to the {@link StubMock#ANYTHING}
   * constraint, so they will always match. Any left-over constraints or arguments 
   * are ignored (and matched).
   * <p/>
   * You can enfore the exact size of the argument array with the 
   * {@link #ConstrainedMockMethod(List, boolean)} constructor.  
   *  
   * @param argConstraints A {@code List} of {@link Constraint}s that will be
   *        tested against invocation arguments.
   */
  protected ConstrainedMockMethod(List<Constraint> argConstraints) {
    this(argConstraints,false);            
  }

  /**
   * Create a new {@code ConstrainedMockMethod} that will match a given invocation
   * if the constraints present in the supplied {@link List} match the corresponding
   * argument array elements.
   * <p/>
   * This constructor will optionally enforce the size of the argument array. If  
   * {@code enforceCount} is true and before checking the constraints, the size of the
   * argument array is compared to the size of the constraint list, with any mismatch 
   * causing immediate return {@code false}. If {@code enforceCount} is false then
   * this constructors behaviour is identical to that of {@link #ConstrainedMockMethod(List)}.
   * <p/>
   * {@code null} constraints in the supplied list are treated as a special case, 
   * equivalent to the {@link StubMock#ANYTHING} constraint, so they will always match. 
   *  
   * @param argConstraints A {@code List} of {@link Constraint}s that will be
   *        tested against invocation arguments.
   * @param enforceCount if {@code true} then the argument constraint check extends
   *        to the number of actual method arguments. 
   */
  protected ConstrainedMockMethod(List<Constraint> argConstraints, boolean enforceCount) {
    this(null,null,argConstraints,enforceCount);    
  }
  
  /**
   * Create a new {@code ConstrainedMockMethod} that will match a given invocation
   * if the supplied constraints match the corresponding invocation parameters.
   * This constructor allows separate constraints to be supplied for checking against
   * the proxy instance itself, the method's return type, and the actual arguments.
   * <p/>
   * Passing <code>null</code> for any of the constraints is equivalent to passing
   * {@link StubMock#ANYTHING}. See {@link #ConstrainedMockMethod(List, boolean)}
   * for the semantics of the {@code enforceCount} flag.
   *  
   * @param proxyConstraint A constraint that must match against the proxy instance
   *        itself if invocation is to proceed with this mock.
   * @param returnTypeConstraint A constraint that must match against the method's
   *        return type (a {@link Class}) if invocation is to proceed with this mock.
   * @param argConstraints A {@code List} of {@link Constraint}s that will be
   *        tested against invocation arguments.
   * @param enforceCount if {@code true} then the argument constraint check extends
   *        to the number of actual method arguments. 
   */
  /* WORKER */
  protected ConstrainedMockMethod(Constraint proxyConstraint, 
                                  Constraint returnTypeConstraint, 
                                  List<Constraint> argConstraints, 
                                  boolean enforceCount) {
    ArrayList<Constraint> t = new ArrayList<Constraint>(argConstraints != null ? argConstraints.size() : 0);
    if (argConstraints != null) t.addAll(argConstraints);    
    this.argConstraints = unmodifiableList(t);
    this.enforceCount = enforceCount;
    this.proxyConstraint = proxyConstraint;
    this.returnTypeConstraint = returnTypeConstraint;
  }

  /**
   * {@inheritDoc}
   * This implementation checks the various invocation parameters against the
   * supplied constraints. 
   */
  public boolean invocationMatches(Object proxy, Method method, Object[] args) {
    if (proxyConstraint != null && !proxyConstraint.eval(proxy)) return false;
    if (returnTypeConstraint != null && !returnTypeConstraint.eval(method.getReturnType())) return false;
    
    int argsLen = (args != null ? args.length : 0);
    if (enforceCount && (argConstraints.size() != argsLen)) return false;
    
    for (int i = 0; i < argsLen; i++) {
      
      // no more constraints? if so, just accept the rest of the args
      if (i >= argConstraints.size()) break;
      
      // first constraint that fails returns false
      Constraint argC = argConstraints.get(i);
      if (argC != null && !argC.eval(args[ i ])) return false;
    }
    
    // if we're here, it means we matched all the constraints we had, and 
    // all the other stuff was satisfied. So ... 
    return true;
  }

  /**
   * {@inheritDoc}
   * This method is left {@code abstract}, and must be implemented by subclasses as 
   * appropriate.
   */
  public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
