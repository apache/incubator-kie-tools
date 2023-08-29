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
import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
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
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultCustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.injector.api.ExtensionTypeCallback;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newObject;

/**
 * Creates injectables for all {@link Widget} subtypes. Without this extension, injecting some widget types (such as
 * {@link TextBox}) would result in an ambigous resolution because of subtypes (such as {@link PasswordTextBox}) that
 * are also type injectable.
 *
 * This extension creates a new instance of the exact {@link Widget} subtype for every injection point.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@IOCExtension
public class WidgetIOCExtension implements IOCExtensionConfigurator {

  @Override
  public void configure(final IOCProcessingContext context, final InjectionContext injectionContext) {
  }

  @Override
  public void afterInitialization(final IOCProcessingContext context, final InjectionContext injectionContext) {
    final MetaClass widgetClazz = MetaClassFactory.get(Widget.class);
    injectionContext.registerExtensionTypeCallback(new ExtensionTypeCallback() {

      @Override
      public void callback(final MetaClass type) {
        if (type.isDefaultInstantiable() && type.isAssignableTo(widgetClazz)) {
          addProviderForWidgetType(injectionContext, type);
        }
      }
    });
  }

  private void addProviderForWidgetType(final InjectionContext injectionContext, final MetaClass widgetType) {
    if (widgetType.isPublic() && widgetType.isDefaultInstantiable()) {
      final InjectableHandle handle = new InjectableHandle(widgetType,
              injectionContext.getQualifierFactory().forDefault());
      injectionContext.registerExactTypeInjectableProvider(handle, new InjectableProvider() {

        private CustomFactoryInjectable provided;

        @Override
        public CustomFactoryInjectable getInjectable(final InjectionSite injectionSite, final FactoryNameGenerator nameGenerator) {
          if (provided == null) {
            final FactoryBodyGenerator generator = new AbstractBodyGenerator() {
              @Override
              protected List<Statement> generateCreateInstanceStatements(ClassStructureBuilder<?> bodyBlockBuilder,
                      Injectable injectable, DependencyGraph graph, InjectionContext injectionContext) {
                return Collections
                        .<Statement> singletonList(nestedCall(newObject(widgetType, new Object[0])).returnValue());
              }
            };
            provided = new DefaultCustomFactoryInjectable(handle.getType(), handle.getQualifier(),
                    nameGenerator.generateFor(handle.getType(), handle.getQualifier(),
                            InjectableType.ExtensionProvided),
                    Dependent.class, Collections.singletonList(WiringElementType.DependentBean), generator);
          }

          return provided;
        }
      });
    }
  }
}
