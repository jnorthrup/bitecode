/* BasicGenTestCase.java - Awaiting description
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
 * File version: $Revision: 1.4 $ $Date: 2005/10/23 02:15:15 $
 * Originated: 04-Sep-04
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.roscopeco.juno.pre50.JunoTestCase;

/** 
 * Very basic generation
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.4 $ $Date: 2005/10/23 02:15:15 $ 
 */
public class BasicGenTestCase extends JunoTestCase
{  
  public void testCanPassThroughDummyClass() throws Exception {
    SoftClass c = new SoftClass(SimpleDummy.class);
    c.setName("jen.DummyClassTwo04");
    Class<?> dc2 = c.defineClass();
    
    assertThat("Defined class",dc2,not(isNull()));
    assertThat("Defined class name",dc2.getName(),isEqual("jen.DummyClassTwo04"));
    
    Object o = null;
    try {      
      o = dc2.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Instantiation failed with "+e);
    }
    
    assertThat(SoftUtils.reflectInvoke(o,"argsMethod",new Object[] { "one", "two" }),eq("onetwo"));    

    // Same number of methods and fields
    assertThat(o.getClass().getDeclaredFields(),exactSize(SimpleDummy.class.getDeclaredFields().length));    
    assertThat(o.getClass().getDeclaredMethods(),exactSize(SimpleDummy.class.getDeclaredMethods().length));    
    assertThat(o.getClass().getDeclaredConstructors(),exactSize(SimpleDummy.class.getDeclaredConstructors().length));    
    assertThat(o.getClass().getDeclaredClasses(),exactSize(SimpleDummy.class.getDeclaredClasses().length));    
  }

  public void testCanRemoveFieldsFromDummyClass() throws Exception {
    SoftClass c = new SoftClass(SimpleDummy.class);
    c.setName("jen.DummyClassThree04");
    c.removeSoftField(c.getSoftField("refField"));
    
    Class<?> dc2 = c.defineClass();
    
    assertThat("Defined class",dc2,not(isNull()));
    assertThat("Defined class name",dc2.getName(),isEqual("jen.DummyClassThree04"));
    
    Object o = null;
    try {      
      o = dc2.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Instantiation failed with "+e);
    }
    
    assertThat(SoftUtils.reflectInvoke(o,"argsMethod",new Object[] { "one", "two" }),eq("onetwo"));    

    // Same number of methods and fields
    assertThat(o.getClass().getDeclaredFields(),exactSize(SimpleDummy.class.getDeclaredFields().length - 1));    
    assertThat(o.getClass().getDeclaredMethods(),exactSize(SimpleDummy.class.getDeclaredMethods().length));    
    assertThat(o.getClass().getDeclaredConstructors(),exactSize(SimpleDummy.class.getDeclaredConstructors().length));    
    assertThat(o.getClass().getDeclaredClasses(),exactSize(SimpleDummy.class.getDeclaredClasses().length));    
  }
  
  public void testCanRemoveMethodsFromDummyClass() throws Exception {
    SoftClass c = new SoftClass(SimpleDummy.class);
    c.setName("jen.DummyClassFour04");
    c.removeSoftMethod(c.getSoftMethod("void <init>(boolean)"));
    c.removeSoftMethod(c.getSoftMethod("privateMethod","()Ljava/lang/String;"));
    Class<?> dc2 = c.defineClass();
    
    assertThat("Defined class",dc2,not(isNull()));
    assertThat("Defined class name",dc2.getName(),isEqual("jen.DummyClassFour04"));
    
    Object o = null;
    try {      
      o = dc2.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Instantiation failed with "+e);
    }
    
    assertThat(SoftUtils.reflectInvoke(o,"argsMethod",new Object[] { "one", "two" }),eq("onetwo"));    

    // Same number of methods and fields
    assertThat(o.getClass().getDeclaredFields(),exactSize(SimpleDummy.class.getDeclaredFields().length));    
    assertThat(o.getClass().getDeclaredMethods(),exactSize(SimpleDummy.class.getDeclaredMethods().length - 1));    
    assertThat(o.getClass().getDeclaredConstructors(),exactSize(SimpleDummy.class.getDeclaredConstructors().length - 1));    
    assertThat(o.getClass().getDeclaredClasses(),exactSize(SimpleDummy.class.getDeclaredClasses().length));    
  }  
}
