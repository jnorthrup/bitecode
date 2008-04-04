package org.roscopeco.juno.converters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

/** 
 * Allows Juno constraints to be used as <a href='http://www.picocontainer.org'>
 * PicoContainer</a>-compatible parameter constraints. These are <i>not</i> 
 * your garden variety constraints - for more information on using them within
 * the Pico environment, see their documentation.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:35 $
 */
public class JunoPicoConstraint implements org.picocontainer.gems.constraints.Constraint {
  final org.roscopeco.juno.Constraint delegate;
  final Parameter param;
  
  JunoPicoConstraint(org.roscopeco.juno.Constraint delegate, Parameter param) {
    this.delegate = delegate;
    this.param = param;
  }      
  
  public boolean evaluate(ComponentAdapter arg) {
    return delegate.eval(arg);
  }

  public void accept(PicoVisitor arg) {
    param.accept(arg);
  }
  
  public boolean isResolvable(PicoContainer arg0, ComponentAdapter arg1, Class arg2) {
    return param.isResolvable(arg0,arg1,arg2);
  }
  
  public Object resolveInstance(PicoContainer arg0, ComponentAdapter arg1, Class arg2) throws PicoInitializationException {
    return param.resolveInstance(arg0,arg1,arg2);
  }
  
  public void verify(PicoContainer arg0, ComponentAdapter arg1, Class arg2) throws PicoIntrospectionException {
    param.verify(arg0,arg1,arg2);
  }
}