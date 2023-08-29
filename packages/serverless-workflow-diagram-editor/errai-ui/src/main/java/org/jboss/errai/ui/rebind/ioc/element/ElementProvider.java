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


package org.jboss.errai.ui.rebind.ioc.element;

import javax.enterprise.context.Dependent;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.CustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.InjectionSite;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultCustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;

import static java.util.Collections.singletonList;
import static org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType.ExtensionProvided;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.DependentBean;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
class ElementProvider implements InjectableProvider {

  private final InjectableHandle handle;
  private final AbstractBodyGenerator injectionBodyGenerator;

  private CustomFactoryInjectable injectable;

  ElementProvider(final InjectableHandle handle, final AbstractBodyGenerator injectionBodyGenerator) {
    this.handle = handle;
    this.injectionBodyGenerator = injectionBodyGenerator;
  }

  @Override
  public CustomFactoryInjectable getInjectable(final InjectionSite injectionSite,
          final FactoryNameGenerator nameGenerator) {

    if (injectable == null) {
      injectable = buildCustomFactoryInjectable(nameGenerator);
    }

    return injectable;
  }

  private CustomFactoryInjectable buildCustomFactoryInjectable(final FactoryNameGenerator nameGenerator) {

    final MetaClass type = handle.getType();
    final Qualifier qualifier = handle.getQualifier();
    final String factoryName = nameGenerator.generateFor(type, qualifier, ExtensionProvided);

    return new DefaultCustomFactoryInjectable(type, qualifier, factoryName, Dependent.class,
            singletonList(DependentBean), injectionBodyGenerator);
  }

}
