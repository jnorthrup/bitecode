/* MixinTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/20 22:34:45 $
 * Originated: Oct 17, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jen.SoftClass;
import junit.framework.TestCase;

import static org.roscopeco.juno.pre50.JunoTestCase.*;

public class MixinTestCase extends TestCase
{
  public static interface WithAnyMixin {
    public boolean basicMixin();    
  }
    
  public static interface WithStringMixin {
    public String addMore(String add);    
  }
    
  public static boolean anyMixin(Object o) {
    return true;    
  }
  
  static String addMore(List o, String more) {
    return o + more;
  }
  
  static <T> String addMoreGeneric(List o, T more) {
    return o.toString() + more.toString(); 
  }
  
  static final Method ANY_MIXIN; 
  static final Method STRING_MIXIN; 
  static final Method GENERIC_MIXIN; 
  
  static {
    try {
      ANY_MIXIN = MixinTestCase.class.getDeclaredMethod("anyMixin",Object.class);
      STRING_MIXIN = MixinTestCase.class.getDeclaredMethod("addMore",List.class,String.class);
      GENERIC_MIXIN = MixinTestCase.class.getDeclaredMethod("addMoreGeneric",List.class,Object.class);
    } catch (Exception e) {
      throw(new RuntimeException(e));
    }
  }
  
  public void testBasicMixin() throws Exception {
    SoftClass sc = new SoftClass(ArrayList.class);
    sc.setName("tmp.java.util.AnotherArrayList");
    sc.addInterface(WithAnyMixin.class);
    sc.putSoftMethod(new StaticMixinMethod(sc,ANY_MIXIN,"basicMixin"));

    Class<WithAnyMixin> c =(Class<WithAnyMixin> ) sc.defineClass();
        
    WithAnyMixin wm = c.newInstance();
    
    assertThat(wm.basicMixin(),isTrue());
  }
  
  // has to be same package with this one, because we made the mixin package private
  public void testStringMixin() throws Exception {
    SoftClass sc = new SoftClass(ArrayList.class);
    sc.setName("jen.methods.AddableArrayList");
    sc.addInterface(WithStringMixin.class);
    sc.putSoftMethod(new StaticMixinMethod(sc,STRING_MIXIN,SoftClass.ACC_PUBLIC,"addMore"));
    
    @SuppressWarnings("unchecked")
    Class<WithStringMixin> c = sc.defineClass();
        
    WithStringMixin wm = c.newInstance();
    List<String> l = (List<String>) wm;    
    l.add("add");
    
    assertThat(wm.addMore("More"),isEqual("[add]More"));    
  }

  public void testGenericMixin() throws Exception {
    SoftClass sc = new SoftClass(ArrayList.class);
    sc.setName("jen.methods.AddableArrayList02");
    sc.putSoftMethod(new StaticMixinMethod(sc,GENERIC_MIXIN,SoftClass.ACC_PUBLIC | SoftClass.ACC_STATIC, "addMore"));
    
    Class<? extends Object> c = sc.defineClass();
    assertThat(c.getMethod("addMore",Object.class).toGenericString(),
        isEqual("public <T> java.lang.String jen.methods.AddableArrayList02.addMore(java.util.List,T)"));
  }
}
