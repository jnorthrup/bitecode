/* AbstractSoftMember.java - Abstract SoftMember implementation
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/02 02:15:30 $
 * Originated: 03-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/** 
 * Base-class for {@link SoftMember} implementations. As well as providing
 * default implementations of the required methods, this class also provides
 * convenience methods for ensuring consistent modification. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/02 02:15:30 $ 
 */
public abstract class AbstractSoftMember implements Opcodes, SoftMember
{
  /* **********************************
   * INSTANCE INIT
   */
  private final SoftClass softClass;

  private int access;
  private String name;
  private String signature;
  
  protected AbstractSoftMember(SoftClass softClass) {
    this(softClass,0,null,null);
  }
  
  protected AbstractSoftMember(SoftClass softClass, int access, String name, String signature) {
    if (softClass == null) throw(new NullArgumentException(getClass(),"softClass"));
    
    this.access = access;
    this.name = name;    
    this.signature = signature;
    this.softClass = softClass;
  }
  
  /* **********************************
   * ACCESS METHODS
   */
  /* (non-Javadoc)
   * @see jen.SoftMember#getSoftClass()
   */
  public SoftClass getSoftClass() {
    return softClass;
  }
    
  /* (non-Javadoc)
   * @see jen.SoftMember#getAccess()
   */
  public int getModifiers() {
    return access;
  }

  /* (non-Javadoc)
   * @see jen.SoftMember#setAccess(int)
   */
  protected void setModifiers(int access) {
    checkModify();
    this.access = access;
  }
  
  /* (non-Javadoc)
   * @see jen.SoftMember#getName()
   */
  public String getName() {
    return name;
  }
  
  /* (non-Javadoc)
   * @see jen.SoftMember#setName(java.lang.String)
   */
  protected void setName(String name) {
    checkModify();
    this.name = name;
  }    
  
  /* (non-Javadoc)
   * @see jen.SoftMember#getName()
   */
  public String getSignature() {
    return signature;
  }
  
  /* (non-Javadoc)
   * @see jen.SoftMember#setName(java.lang.String)
   */
  protected void setSignature(String signature) {
    checkModify();
    this.signature = signature;
  }    
  
  /**
   * Checks whether this member should allow modification, throwing an 
   * {@link IllegalStateException} if not. Failure to perform this check may
   * invalidate the entire SoftClass, preventing generation by throwing an 
   * {@link InconsistentSoftClassException}.
   * <p/>
   * This calls through (via {@link #canModify()}) to the associated 
   * {@link SoftClass} to perform the check, since {@link SoftClass} is itself
   * a subclass of this class.  
   * 
   * @throws IllegalStateException if the associated {@link SoftClass} is frozen.
   */
  protected void checkModify() {
    if (!canModify())
      throw (new IllegalStateException("Cannot modify member '"+getName()+"' of frozen SoftClass '"
          + softClass.getName() + "'"));
  }
  
  /**
   * Provides a non-terminal way to check whether a member should allow modification to
   * it's properties. Failure to perform such a check may invalidate the entire 
   * SoftClass by allowing modification while frozen, and preventing generation from
   * taking place.
   * <p/>
   * This calls through (via {@link #canModify()}) to the associated 
   * {@link SoftClass} to perform the check, since {@link SoftClass} is itself
   * a subclass of this class.
   *   
   * @return <code>true</code> if the associated {@link SoftClass} is not frozen.
   */
  protected boolean canModify() {
    return !softClass.isFrozen();
  }
  
  public abstract void accept(ClassVisitor vis);
  
  /* **********************************
   * SPECIAL CASE API FOR SOFTCLASS
   */
  AbstractSoftMember(int access, String name, boolean dummy) {
    softClass = null;
    this.access = access;
    this.name = name; 
  }    
}
