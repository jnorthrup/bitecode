/* SoftClassAgent.java - Instrumentation agent using Jen SoftClass
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/01 10:48:20 $
 * Originated: 22-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.demo.instrument;

import java.io.File;
import java.io.PrintWriter;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import jen.SoftClass;
import jen.fields.GeneratedSoftField;
import jen.methods.BeanPropertyGetter;
import jen.methods.BeanPropertySetter;

import org.objectweb.asm.ClassReader;

/** 
 * Java 5 instrumentation agent that uses the Jen SoftClass library to add a 
 * bean property and back-door interface to classes.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/01 10:48:20 $ 
 */
public class SoftClassAgent
{  
  static final PrintWriter log;
  static {
    PrintWriter t = null;
    try {
      t = new PrintWriter(new File("target/agent.log"));
    } catch (Exception e) {
      t = new PrintWriter(System.out);
    }
    
    log = t;      
  }
  
  static void log(String... ss) {
    if (ss != null) for (String s : ss) log.print(s);
    log.flush();
  }
  
  static void logln(String... ss) {
    if (ss != null) for (String s : ss) log.print(s);
    log.println("");
    log.flush();
  }  
  
  /** 
   * Simple transformer that uses <a href='http://jen.dev.java.net/'>Jen</a> {@link SoftClass}
   * to add a bean property and interface to classes.
   */
  public static final class SoftClassTransformer implements ClassFileTransformer 
  {
    public byte[] transform(ClassLoader loader, String className, 
        Class<?> classBeingRedefined, ProtectionDomain protectionDomain, 
        byte[] classfileBuffer) throws IllegalClassFormatException {      

      logln("Transforming class: "+className);
            
      SoftClass sc = new SoftClass(new ClassReader(classfileBuffer));
      
      sc.addInterface(BackDoor.class.getName());
      sc.putSoftMethod(new BeanPropertyGetter(sc,"objectId",int.class));
      sc.putSoftMethod(new BeanPropertySetter(sc,"objectId",int.class));
      sc.putSoftField(new GeneratedSoftField(sc,"objectId",int.class));
      
      return sc.generateBytecode();
    }
  }
  
  /**
   * Equivalent of static void <code>main</code> for an agent. This is run 
   * before the main method, and is the only time we get given the 
   * <code>Instrumentation</code> instance to use.
   * 
   * @param agentArgs Arguments passed (via the -javaagent:&lt;jar&gt;=args 
   *        parameter). 
   * @param inst The {@link java.lang.instrumentation.Instrumentation} instance
   *        that allows us to do the good stuff.
   */
  public static void premain(String agentArgs, Instrumentation inst) {    
    logln("Instrumentation Agent Premain...");
    
    if (inst.isRedefineClassesSupported()) {
      logln("Redefinition is supported");
    } else {
      logln("Redefinition not supported");
    }
    
    ClassFileTransformer f = new SoftClassTransformer();  
    inst.addTransformer(f);
    
    logln("Class transformer is registered...");
    logln();
  }
}
