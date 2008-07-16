/* Property.java - Annotation that will be recognized by the PropertizeTask
 *
 * Copyright (c)2006 Ross Bamford & Contributors
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
 * File version: $Revision: 1.1 $ $Date: 2006/08/15 12:16:24 $
 * Originated: Aug 15, 2006
 * Author: Jeff Schnitzer (jeff<at>infohazard.org)
 */

package jen.tools.ant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation that will be recognized by the PropertizeTask.  When
 * placed on a field, a public getter and setter will be created by
 * bytecode manipulation.  If either method already exists, it is
 * left intact.
 * 
 * @author Jeff Schnitzer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property
{
    /** Create a get method if one does not exist */
    boolean get() default true;
    
    /** Create a set method if one doesnot exist */
    boolean set() default true;
}
