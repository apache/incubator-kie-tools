/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.enterprise.context;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * <p>Specifies that a bean is application scoped.</p>
 *
 * <p>The application scope is active:</p>
 *
 * <ul>
 * <li>during the <tt>service()</tt> method of any servlet in
 * the web application, during the <tt>doFilter()</tt> method of
 * any servlet filter and when the container calls any
 * <tt>ServletContextListener</tt>, <tt>HttpSessionListener</tt>,
 * <tt>AsyncListener</tt> or <tt>ServletRequestListener</tt>,</li>
 * <li>during any Java EE web service invocation,</li>
 * <li>during any remote method invocation of any EJB, during
 * any asynchronous method invocation of any EJB, during any
 * call to an EJB timeout method and during message delivery
 * to any EJB message-driven bean,</li>
 * <li>during any message delivery to a <tt>MessageListener</tt>
 * for a JMS topic or queue obtained from the Java EE component
 * environment, and</li>
 * <li>when the disposer method or <tt>@PreDestroy</tt> callback of
 * any bean with any normal scope other than <tt>@ApplicationScoped</tt>
 * is called.</li>
 * </ul>
 *
 * <p>The application context is shared between all servlet requests,
 * web service invocations, EJB remote method invocations, EJB
 * asynchronous method invocations, EJB timeouts and message
 * deliveries to message-driven beans that execute within the same
 * application. The application context is destroyed when the
 * application is shut down.</p>
 *
 * @author Gavin King
 * @author Pete Muir
 */

@Target( { TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
@NormalScope
@Inherited
public @interface ApplicationScoped
{

}
