/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package javax.enterprise.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>The built-in qualifier type.</p>
 *
 * <p>The <tt>&#064;New</tt> qualifier allows the application
 * to obtain a new instance of a bean which is not bound to
 * the declared scope, but has had dependency injection
 * performed.</p>
 *
 * <pre>
 * &#064;Produces &#064;ConversationScoped
 * &#064;Special Order getSpecialOrder(&#064;New(Order.class) Order order) {
 *    ...
 *    return order;
 * }
 * </pre>
 *
 * <p>When the <tt>&#064;New</tt> qualifier is specified
 * at an injection point and no
 * {@link javax.enterprise.inject.New#value() value}
 * member is explicitly specified, the container defaults
 * the {@link javax.enterprise.inject.New#value() value}
 * to the declared type of the injection point. So the
 * following injection point has qualifier
 * <tt>&#064;New(Order.class)</tt>:</p>
 *
 * <pre>
 * &#064;Produces &#064;ConversationScoped
 * &#064;Special Order getSpecialOrder(&#064;New Order order) { ... }
 * </pre>
 *
 * @author Gavin King
 * @author Pete Muir
 */

@Target( { FIELD, PARAMETER, METHOD, TYPE })
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface New
{
   /**
    * <p>Specifies the bean class of the new instance. The class
    * must be the bean class of an enabled or disabled bean. The
    * bean class need not be deployed in a bean archive.</p>
    *
    * <p>Defaults to the declared type of the injection point if
    * not specified.</p>
    *
    * @return the bean class of the new instance
    */
   Class<?> value() default New.class;

}
