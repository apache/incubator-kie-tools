/*
 * Copyright (c) 2005, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The <code>PostConstruct</code> annotation is used on a method that needs to be executed after
 * dependency injection is done to perform any initialization. This method must be invoked before
 * the class is put into service. This annotation must be supported on all classes that support
 * dependency injection. The method annotated with <code>PostConstruct</code> must be invoked even
 * if the class does not request any resources to be injected. Only one method in a given class can
 * be annotated with this annotation. The method on which the <code>PostConstruct</code> annotation
 * is applied must fulfill all of the following criteria:
 *
 * <ul>
 *   <li>The method must not have any parameters except in the case of interceptors in which case it
 *       takes an <code>InvocationContext</code> object as defined by the Jakarta Interceptors
 *       specification.
 *   <li>The method defined on an interceptor class or superclass of an interceptor class must have
 *       one of the following signatures:
 *       <p>void &#060;METHOD&#062;(InvocationContext)
 *       <p>Object &#060;METHOD&#062;(InvocationContext) throws Exception
 *       <p><i>Note: A PostConstruct interceptor method must not throw application exceptions, but
 *       it may be declared to throw checked exceptions including the java.lang.Exception if the
 *       same interceptor method interposes on business or timeout methods in addition to lifecycle
 *       events. If a PostConstruct interceptor method returns a value, it is ignored by the
 *       container.</i>
 *   <li>The method defined on a non-interceptor class must have the following signature:
 *       <p>void &#060;METHOD&#062;()
 *   <li>The method on which the <code>PostConstruct</code> annotation is applied may be public,
 *       protected, package private or private.
 *   <li>The method must not be static except for the application client.
 *   <li>The method should not be final.
 *   <li>If the method throws an unchecked exception the class must not be put into service except
 *       in the case where the exception is handled by an interceptor.
 * </ul>
 *
 * @see jakarta.annotation.PreDestroy
 * @see jakarta.annotation.Resource
 * @since 1.6, Common Annotations 1.0
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface PostConstruct {}
