/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.enterprise.context;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a bean is application scoped.
 *
 * <p>While <code>ApplicationScoped</code> must be associated with the built-in application context
 * required by the specification, third-party extensions are allowed to also associate it with their
 * own context. Behavior described below is only related to the built-in application context.
 *
 * <p>The application scope is active:
 *
 * <ul>
 *   <li>during the <code>service()</code> method of any servlet in the web application, during the
 *       <code>doFilter()</code> method of any servlet filter and when the container calls any
 *       <code>ServletContextListener</code>, <code>HttpSessionListener</code>, <code>AsyncListener
 *       </code> or <code>ServletRequestListener</code>,
 *   <li>during any Java EE web service invocation,
 *   <li>during any remote method invocation of any EJB, during any asynchronous method invocation
 *       of any EJB, during any call to an EJB timeout method and during message delivery to any EJB
 *       message-driven bean,
 *   <li>when the disposer method or <code>@PreDestroy</code> callback of any bean with any normal
 *       scope other than <code>@ApplicationScoped</code> is called, and
 *   <li>during <code>@PostConstruct</code> callback of any bean.
 * </ul>
 *
 * <p>The application context is shared between all servlet requests, web service invocations, EJB
 * remote method invocations, EJB asynchronous method invocations, EJB timeouts and message
 * deliveries to message-driven beans that execute within the same application.
 *
 * <p>The application context is destroyed when the application is shut down.
 *
 * <p>An event with qualifier <code>@Initialized(ApplicationScoped.class)</code> is fired when the
 * application context is initialized and an event with qualifier <code>
 * @Destroyed(ApplicationScoped.class)</code> when the application context is destroyed. The event
 * payload is:
 *
 * <ul>
 *   <li>the <code>ServletContext</code> if the application is a web application deployed to a
 *       Servlet container, or
 *   <li>any <code>java.lang.Object</code> for other types of application.
 * </ul>
 *
 * @author Gavin King
 * @author Pete Muir
 * @author Antoine Sabot-Durand
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Documented
@NormalScope
@Inherited
public @interface ApplicationScoped {}
