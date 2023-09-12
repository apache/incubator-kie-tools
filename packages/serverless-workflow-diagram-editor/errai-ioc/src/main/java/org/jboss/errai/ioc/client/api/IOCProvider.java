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

/**
 * Defines a top-level provider for the IOC Container. Top-level providers are a special feature of the container
 * used for defining injectable beans which are available anywhere within the container, from any scope, at any
 * point of the runtime lifecycle.
 * <p>
 * Top-level providers are used for defining framework-level features such as support for injecting instances of
 * the <tt>MessageBus</tt> or the RPC <tt>Callable</tt> interface.
 * <p>
 * The production of top-level providers are <em>not</em> managed beans. But the instances of the top-level providers
 * are themselves, managed and are scopable. Thus, all top-level providers that do not declare an explicit scope are
 * of the <em>pseudo-dependant scope</em> and are instantiated prior to all invocations of the provider.
 * <p>
 * Classes which are annotated with <tt>@IOCProvider</tt> MUST implement either:
 * <ul>
 *   <li>{@link javax.inject.Provider}</li>
 *   <li>{@link ContextualTypeProvider}</li>
 * </ul>
 *
 * A provider can also implement {@link Disposer}, allowing it to clean up resources when a provided bean is destroyed.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IOCProvider {
}
