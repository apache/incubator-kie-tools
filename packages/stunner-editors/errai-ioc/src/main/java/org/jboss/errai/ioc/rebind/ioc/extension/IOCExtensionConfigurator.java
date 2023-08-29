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


package org.jboss.errai.ioc.rebind.ioc.extension;

import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;

/**
 * This class is implemented by modules which need to extend functionality of the IOC container, such as by adding
 * additional annotation processing rules, problematically providing dependencies, etc.
 * <p>
 * All classes implementing <tt>IOCExtensionConfigurator</tt> and annotated with {@link org.jboss.errai.ioc.client.api.IOCExtension}
 * will be instantiated and called by the IOC container at generation time.
 * <p>
 * All classes implementing this interface MUST:
 * <ol>
 *   <li>Have a default public, no-argument constructor.</li>
 *   <li>Be in a Errai discoverable classpath location (with an <tt>ErraiApp.properties</tt> at the classpath root)</li>
 * </ol>
 *
 *
 */
public interface IOCExtensionConfigurator {
  /**
   * This method is called by the container <em>before</em> any processing or class discovery begins. This gives an
   * opportunity to configure the scanning rules, such as configuring annotation rules.
   *
   * @param context
   * @param injectionContext
   * @param procFactory
   */
  public void configure(IOCProcessingContext context, InjectionContext injectionContext);

  /**
   * THis method is called <em>after</em> the container has initialized all configuration, and <em>before</em> any
   * class generation for the container begins.
   *
   * @param context
   * @param injectionContext
   * @param procFactory
   */
  public void afterInitialization(IOCProcessingContext context, InjectionContext injectionContext);
}
