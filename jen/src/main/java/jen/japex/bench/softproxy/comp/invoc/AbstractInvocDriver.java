/* AbstractInvocDriver.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/21 23:11:41 $
 * Originated: Oct 21, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.japex.bench.softproxy.comp.invoc;

import com.sun.japex.*;
import jen.tools.*;
import org.roscopeco.juno.*;
import static org.roscopeco.juno.Constraints.*;

import java.lang.reflect.*;

/** 
 * Not yet documented.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/21 23:11:41 $ 
 */
public abstract class AbstractInvocDriver extends JapexDriverBase
{
  // shared across all platform-based proxies, and used in both tests
  private static final InvocationHandler plat_handler = new InvocationHandler() {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return "INVHANDLER";
    }
  };
  
  static final InvocationHandler mock_handler = new MockProxyHandler(
      new MockMethod[] {
          new StubMock("getResult",new Constraint[] { isNull() }, null)
      }
    );
  
  static final InvocationHandler cgl_handler = new  InvocationHandler() {
    public Object invoke(Object proxy, Method m, Object[] args) {
      return "CGLIB_INV_HANDLER";
    }
  };
  
  protected static InvocationHandler getPlatHandler() { 
    return plat_handler;
  }
  
  protected static InvocationHandler getMockHandler() {
    return mock_handler;    
  }
  
  protected static  InvocationHandler getCglHandler() {
    return cgl_handler;
  }  
}
