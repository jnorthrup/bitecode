/* Main.java - Exercise the agent
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
 * Originated: 23-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.demo.instrument;

import java.lang.reflect.Method;
import java.util.Random;

import junit.framework.TestSuite;

/** 
 * Simple app that exercises the instrumentation agent.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/01 10:48:20 $ 
 */
public class Main
{
  static final Random rand = new Random();
  
  static void checkBackDoor(Object o) {
    System.out.println("Has the BackDoor?:");
    if (o instanceof BackDoor) {
      System.out.println("Yep!");
      BackDoor b = (BackDoor)o;
      System.out.println("ObjectId is    : "+b.getObjectId());
      int i = rand.nextInt();
      b.setObjectId(i);
      System.out.println("ObjectId is now: "+b.getObjectId()+" (expected "+i+")");
    }        
  }
  
  /* **********************************
   * ENTRY POINT
   */
  public static void main(String[] args) {
    
    // This class?
    Object o = new Main();    
    System.out.println("Class name      : "+o.getClass().getName()+" has the following methods");
    
    try {
      for (Method m : o.getClass().getMethods()) System.out.println("Method: "+m.getName());
    } catch (Exception e) {
      System.out.println("Exception on reflection: "+e);
    }
    
    checkBackDoor(o);    

    // Other peoples classes? 
    o = new TestSuite();
    System.out.println("\nThird-party Class:");
    System.out.println("Class name      : "+o.getClass().getName());    
    checkBackDoor(o);
    
    // A platform class. There are a few limitations here - see text
    o = new InterruptedException();
    System.out.println("\nPlatform Class:");    
    System.out.println("Class name      : "+o.getClass().getName());
    
    checkBackDoor(o);    
  } 
}
