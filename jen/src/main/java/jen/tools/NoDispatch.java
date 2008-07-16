/* NoDispatch.java - Indicates a 'placeholder' method to the multi dispatcher.
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/30 23:47:47 $
 * Originated: Oct 27, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Indicates to the {@link MultiDispatchStrategy} that the annotated
 * method should be <strong>excluded</strong> from multiple dispatch,
 * resulting in them being ignored when searching for a method to
 * invoke for a given set of arguments.
 * <p/>
 * The primary intended purpose of this annotation is to allow an
 * all-{@link Object}-argument overload to be declared, allowing the
 * method to be called with varying arguments at compile time. At
 * run-time, calls to this method will enter the dispatcher as normal,
 * and be passed to another overload on the superclass if possible.
 * When searching for an overload, however, the annotated method will
 * be ignored.
 * <p/>
 * Without this annotation the common requirement for an all-Object
 * overload would limit the dispatcher's usefullness, since there 
 * would be no way to prevent method calls with absolutely any
 * arguments from being dispatched to that overload on the superclass.
 * With it, these methods become essentially 'placeholders' that
 * exist purely for use at compile time - any arguments passed in
 * through that overload that cannot be matched to a non-{@code NoDispatch}
 * method will cause a {@link NoSuchMethodError} to be thrown.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/30 23:47:47 $ 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoDispatch {

}
