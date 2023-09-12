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


package org.jboss.errai.ioc.client.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.ioc.client.container.ErraiUncaughtExceptionHandler;

/**
 * <p>
 * Indicates that the annotated method should be invoked when an uncaught exception occurs in the client-side message
 * bus or an uncaught exception occurs in client code.
 *
 * <p>
 * Before the IOC container starts, an {@link ErraiUncaughtExceptionHandler} is set via
 * {@link GWT#setUncaughtExceptionHandler(com.google.gwt.core.client.GWT.UncaughtExceptionHandler)}, which dispatches to
 * all {@link UncaughtExceptionHandler} methods.
 *
 * <p>
 * The annotated method needs to have exactly one parameter of type {@link Throwable}.
 *
 * <p>
 * For {@link ApplicationScoped}, {@link EntryPoint}, and {@link Singleton} beans, the handler method is always called
 * on the same instance. An uncaught exception will force the creation of the bean instance if it has not already been
 * instantiated.
 *
 * <p>
 * For {@link Dependent} beans, only already existing instances will have their handler methods invoked. If multiple
 * instances exist for a type with a handler method, all instances will have their handler methods called. If none
 * exist, then no handler methods for that type will be called.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UncaughtExceptionHandler {
}
