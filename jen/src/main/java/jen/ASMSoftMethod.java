/* ASMSoftMethod.java - ASM MethodNode-based SoftMethod.
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/03 17:59:41 $
 * Originated: 07-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.util.Arrays;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

/** 
 * Basic {@link SoftMethod} implementation based on an existing ASM 
 * {@link MethodNode}. This is used to wrap existing Java methods
 * (and constructors) and can also be used to add any other existing 
 * method node to a class.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/03 17:59:41 $ 
 */
public class ASMSoftMethod extends AbstractReferenceSwitchingSoftMethod
{
  private final MethodNode node;
  
  /**
   * Create a new <code>ASMSoftMethod</code> that will generate the specified
   * {@link MethodNode}.
   * 
   * @param softClass The {@link SoftClass} upon which the node will be generated.
   * @param node The {@link MethodNode} representing this method.
   */
  @SuppressWarnings("unchecked")
  public ASMSoftMethod(SoftClass softClass, MethodNode node) {
    super(softClass,(SoftUtils.nullSafeArg(ASMSoftMethod.class,"node",node)).access,node.name,
        node.signature,Arrays.asList(Type.getArgumentTypes(node.desc)),Type.getReturnType(node.desc),
        (node.exceptions != null) ? 
            SoftUtils.typeForStringList(node.exceptions) : 
            null);
    this.node = node;
  }
    
  public void accept(ClassVisitor vis) {
    getNode().accept(switchingAdapter(vis));    
  }  
  
  protected MethodNode getNode() {
    return node;
  }
}
