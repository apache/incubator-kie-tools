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


package org.jboss.errai.ioc.rebind.ioc.injector.api;

import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.CustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.InjectionSite;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;

/**
 * This allows {@link IOCExtensionConfigurator IOC extensions} to generate
 * custom {@link Factory factories} per injection site using
 * {@link InjectionContext#registerInjectableProvider(org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle, InjectableProvider)}
 * and
 * {@link InjectionContext#registerSubTypeMatchingInjectableProvider(org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle, InjectableProvider)}
 * .
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface InjectableProvider {

  /**
   * @param injectionSite Metadata for an injection site.
   * @param nameGenerator TODO
   * @return A {@link Injectable} for the given injeciton site.
   */
  CustomFactoryInjectable getInjectable(InjectionSite injectionSite, FactoryNameGenerator nameGenerator);

}
