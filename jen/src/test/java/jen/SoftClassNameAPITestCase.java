/* SoftClassNameAPITestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:40 $
 * Originated: Oct 1, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.roscopeco.juno.pre50.JunoTestCase;

public class SoftClassNameAPITestCase extends JunoTestCase
{
  public void testGetNamesConsistent() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.SomeClazz");
    
    assertThat(sc.getName(),isEqual("com.mycompany.SomeClazz"));
    assertThat(sc.getInternalName(),isEqual("com/mycompany/SomeClazz"));    
  }
  
  public void testGetNamesConsistentAfterNameChange() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.SomeClazz");
    
    assertThat(sc.getName(),isEqual("com.mycompany.SomeClazz"));
    assertThat(sc.getInternalName(),isEqual("com/mycompany/SomeClazz"));    
    
    sc.setName("com.yourcompany.SomeOtherClazz");
    
    assertThat(sc.getName(),isEqual("com.yourcompany.SomeOtherClazz"));
    assertThat(sc.getInternalName(),isEqual("com/yourcompany/SomeOtherClazz"));        
  }
  
  public void testGetNamesConsistentAfterInternalNameChange() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.SomeClazz");
    
    assertThat(sc.getName(),isEqual("com.mycompany.SomeClazz"));
    assertThat(sc.getInternalName(),isEqual("com/mycompany/SomeClazz"));    
    
    sc.setInternalName("com/yourcompany/SomeOtherClazz");
    
    assertThat(sc.getName(),isEqual("com.yourcompany.SomeOtherClazz"));
    assertThat(sc.getInternalName(),isEqual("com/yourcompany/SomeOtherClazz"));        
  }
  
  public void testItDoesntChangeWithNestedClasses() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.SomeClazz$NestedClass");
    
    assertThat(sc.getName(),isEqual("com.mycompany.SomeClazz$NestedClass"));
    assertThat(sc.getInternalName(),isEqual("com/mycompany/SomeClazz$NestedClass"));    
    
    sc.setName("com.yourcompany.SomeOtherClazz$NestedClass");
    
    assertThat(sc.getName(),isEqual("com.yourcompany.SomeOtherClazz$NestedClass"));
    assertThat(sc.getInternalName(),isEqual("com/yourcompany/SomeOtherClazz$NestedClass"));        
    
    sc.setInternalName("com/yourcompany/SomeOtherClazz$0$InnerClass");
    
    assertThat(sc.getName(),isEqual("com.yourcompany.SomeOtherClazz$0$InnerClass"));
    assertThat(sc.getInternalName(),isEqual("com/yourcompany/SomeOtherClazz$0$InnerClass"));        
  }
  
  public void testGetSuperNamesConsistent() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.MyNewClass","com.mycompany.SomeClazz");
    
    assertThat(sc.getSuperClassName(),isEqual("com.mycompany.SomeClazz"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/mycompany/SomeClazz"));    
  }
  
  public void testGetSuperNamesConsistentAfterSuperNameChange() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.MyNewClass","com.mycompany.SomeClazz");
    
    assertThat(sc.getSuperClassName(),isEqual("com.mycompany.SomeClazz"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/mycompany/SomeClazz"));    
    
    sc.setSuperClassName("com.yourcompany.SomeOtherClazz");
    
    assertThat(sc.getSuperClassName(),isEqual("com.yourcompany.SomeOtherClazz"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/yourcompany/SomeOtherClazz"));        
  }
  
  public void testGetSuperNamesConsistentAfterSuperInternalNameChange() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.MyNewClass","com.mycompany.SomeClazz");
    
    assertThat(sc.getSuperClassName(),isEqual("com.mycompany.SomeClazz"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/mycompany/SomeClazz"));    
    
    sc.setSuperClassInternalName("com/yourcompany/SomeOtherClazz");
    
    assertThat(sc.getSuperClassName(),isEqual("com.yourcompany.SomeOtherClazz"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/yourcompany/SomeOtherClazz"));        
  }
  
  public void testSDoesntChangeWithNestedSuperClasses() {
    SoftClass sc = new SoftClass(SoftClass.ACC_PUBLIC,"com.mycompany.MyNewClass","com.mycompany.SomeClazz$NestedClass");
    
    assertThat(sc.getSuperClassName(),isEqual("com.mycompany.SomeClazz$NestedClass"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/mycompany/SomeClazz$NestedClass"));    
    
    sc.setSuperClassName("com.yourcompany.SomeOtherClazz$NestedClass");
    
    assertThat(sc.getSuperClassName(),isEqual("com.yourcompany.SomeOtherClazz$NestedClass"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/yourcompany/SomeOtherClazz$NestedClass"));        
    
    sc.setSuperClassInternalName("com/yourcompany/SomeOtherClazz$0$InnerClass");
    
    assertThat(sc.getSuperClassName(),isEqual("com.yourcompany.SomeOtherClazz$0$InnerClass"));
    assertThat(sc.getSuperClassInternalName(),isEqual("com/yourcompany/SomeOtherClazz$0$InnerClass"));        
  }
  
  public static interface NewRunnable extends Runnable { 
    public void run2();
  }
    
  public void testNamingOnInterfaceWithSuper() throws Exception {
    SoftClass nrsc = new SoftClass(NewRunnable.class);
    
    assertThat(nrsc.getSuperClassInternalName(),isNull());
    assertThat(nrsc.getSuperClassName(),isNull());
    assertThat(nrsc.getSuperClass(),isNull());
    
    assertThat(nrsc.getInterfaces(),and(exactSize(1),exactSize(nrsc.getInterfaceClasses().size())));    
  }  
}
