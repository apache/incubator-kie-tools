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


package org.jboss.errai.ioc.rebind.ioc.extension.builtin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.annotations.NamedLogger;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.CustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.InjectionSite;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultCustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.util.Stmt.invokeStatic;

@IOCExtension
public class LoggerFactoryIOCExtension implements IOCExtensionConfigurator {

  @Override
  public void configure(IOCProcessingContext context, InjectionContext injectionContext) {
  }

  @Override
  public void afterInitialization(final IOCProcessingContext context, final InjectionContext injectionContext) {
    final InjectableHandle handle = new InjectableHandle(MetaClassFactory.get(Logger.class),
            injectionContext.getQualifierFactory().forUniversallyQualified());
    final Map<String, CustomFactoryInjectable> injectablesByLoggerName = new HashMap<String, CustomFactoryInjectable>();

    injectionContext.registerInjectableProvider(handle, new InjectableProvider() {
      @Override
      public CustomFactoryInjectable getInjectable(final InjectionSite injectionSite, final FactoryNameGenerator nameGenerator) {
        final String loggerName;
        if (injectionSite.isAnnotationPresent(NamedLogger.class)) {
          loggerName = injectionSite.getAnnotation(NamedLogger.class).value();
        }
        else {
          loggerName = injectionSite.getEnclosingType().getFullyQualifiedName();
        }

        if (!injectablesByLoggerName.containsKey(loggerName)) {
          final Statement loggerValue = invokeStatic(LoggerFactory.class, "getLogger", loggerName);
          final FactoryBodyGenerator generator = new AbstractBodyGenerator() {
            @Override
            protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
                    final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
              return Collections.singletonList(Stmt.nestedCall(loggerValue).returnValue());
            }
          };

          final MetaClass type = MetaClassFactory.get(Logger.class);
          final Qualifier qualifier = injectionContext.getQualifierFactory().forUniversallyQualified();

          injectablesByLoggerName.put(loggerName, new DefaultCustomFactoryInjectable(type, qualifier,
                  nameGenerator.generateFor(type, qualifier, InjectableType.ExtensionProvided), Dependent.class,
                  Collections.singletonList(WiringElementType.DependentBean), generator));
        }

        return injectablesByLoggerName.get(loggerName);
      }
    });
  }
}
