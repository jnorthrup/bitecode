/* ASMSoftNestedClass.java - ASM InnerClassNode based NestedSoftClass
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/02 02:15:30 $
 * Originated: 07-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.InnerClassNode;

/**
 * Basic {@link NestedSoftClass} implementation based on an existing ASM 
 * {@link InnerClassNode}.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/02 02:15:30 $ 
 */
public class ASMSoftNestedClass extends AbstractSoftNestedClass
{
  private final InnerClassNode node;
  
  public ASMSoftNestedClass(SoftClass outer, InnerClassNode node) {
    super(outer,node.access,node.name,node.outerName,node.innerName);
    this.node = node;
  }
    
  public void accept(ClassVisitor vis) {
    getNode().accept(vis);
  }  
  
  protected InnerClassNode getNode() {
    return node;
  }  
}
