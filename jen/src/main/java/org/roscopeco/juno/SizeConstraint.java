/* SizeConstraint.java - True, type-independent size constraint
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:32 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

import java.lang.reflect.Array;
import java.util.Collection;

/** 
 * An abstract base-class that provides a unified {@link #sizeOf}
 * method that subclasses can use to obtain the size of the object
 * being evaluated independently of it's actual type (i.e. to 
 * implement a true size constraint).
 * <p/>
 * In other words, a size constraint initialized with a size of 
 * 7 would evaluate true against an array with 7 elements, a 
 * {@link java.lang.String} (or other {@link java.lang.CharSequence})
 * with 7 characters, and so on. An object's size 
 * <p/>
 * This class considers <code>null</code> to have a size of zero.
 * Unsized objects (i.e. those of no well known type, and for which
 * no <code>size()</code>, <code>length()</code> or <code>length</code>
 * member could be reflected are treated as a special case, denoted
 * by {@link java.lang.Long#MIN_VALUE} - sized constraint implementations 
 * should <strong>always</strong> return <code>false</code> if 
 * the {@link #sizeOf(Object)} method returns this value.  
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:32 $ 
 */
public abstract class SizeConstraint extends Constraint
{
  private long wellKnownSize(Object o) {
    if (o instanceof CharSequence) return ((CharSequence)o).length();
    if (o instanceof Collection) return ((Collection)o).size();
    if (o.getClass().isArray()) return arraySize(o); /* should never except */
    return -1;
  }
  
  private long reflectSize(Object o) {
    try {
      return sizeSize(o); // length() is most common, but caught with Well Known
    } catch (RuntimeException e) {
      try {
        return lengthSize(o);
      } catch (RuntimeException e2) {
        try {
          return arraySize(o);
        } catch (RuntimeException e3) {
          return Long.MIN_VALUE;
        }
      }
    }
  }
  
  /** Obtains the size of the object. This method will, if the object is of
   * a predetermined 'well-known' sized class (strings, collections, and 
   * arrays) return the size of the object as reported by it's appropriate
   * size / length member. If the object is not of one of these types, then
   * this method attempts to invoke (via reflection) the <code>size()</code>
   * and <code>length()</code> methods. Finally it will attempt to obtain the
   * value of an integer (up to Long) field named <code>length</code>. This
   * search is of course short circuited as soon as a matching method or field
   * is found and invoked.
   * 
   * @param o The object whose size is to be returned.
   * 
   * @return The size of the object, or <code>Long.MIN_VALUE</code> if the 
   * size could not be determined.
   */
  protected final long sizeOf(Object o) {
    if (o == null) return Long.MIN_VALUE;
    long l = wellKnownSize(o);
    if (l != Long.MIN_VALUE) return l; 
    return reflectSize(o);  // last ditch
  }
  
  /* ACCESSORS */
  /* Get a String style (length() method) size by reflection. */
  static long lengthSize(Object o) {
    Class c = o.getClass();
    try {
      return ((Long)c.getMethod("length",new Class[0]).invoke(o,new Object[0])).longValue();
    } catch (Exception e) {
      throw(new RuntimeException(e));
    }
  }
  
  /* Get a collection style (size() method) size by reflection. */
  static long sizeSize(Object o) {
    Class c = o.getClass();
    try {
      return ((Long)c.getMethod("size",new Class[0]).invoke(o,new Object[0])).longValue();
    } catch (Exception e) {
      throw(new RuntimeException(e));
    }
  }
  
  /* Get an array size size by reflection. */
  static long arraySize(Object o) {
    try {
      return Array.getLength(o);
    } catch (Exception e) {
      throw(new RuntimeException(e));
    }
  }

  public Object valueOf(Object o) {
    return new Long(sizeOf(o));
  }
}
