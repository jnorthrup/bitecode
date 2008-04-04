/* AbstractSoftFieldTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/23 02:15:13 $
 * Originated: Oct 2, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import static jen.ASMSoftFieldTestCase.private_field;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import org.roscopeco.juno.pre50.JunoTestCase;

public class AbstractSoftFieldTestCase extends JunoTestCase
{
  public void testAbstractSoftFieldGetMethods() {
    ASMSoftField f = new ASMSoftField(new SoftClass(getClass()),private_field);
    assertThat(f.getDescriptor(),isEqual(private_field.desc));
    assertThat(f.getType(),isEqual(Type.getType(Object.class)));
  }
  
  private static class AbstractSoftFieldTestHarness extends AbstractSoftField {
    AbstractSoftFieldTestHarness() {
      super(new SoftClass(AbstractSoftFieldTestHarness.class));
    }

    public void accept(ClassVisitor vis) { }
    
    public void setType(Type type) {
      super.setType(type);
    }
  }

  public void testInstantiateWithJustSoftClass() {
    AbstractSoftField f = new AbstractSoftFieldTestHarness();
    assertThat(f.getName(),isNull());        
  }
  
  public void testGetSetType() {
    AbstractSoftField f = new AbstractSoftFieldTestHarness();
    assertThat(f.getType(),isNull());
    f.setType(Type.BOOLEAN_TYPE);
    assertThat(f.getType(),isEqual(Type.BOOLEAN_TYPE));    
  }  
}
