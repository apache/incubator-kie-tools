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


package org.jboss.errai.ioc.tests.extensions.rebind;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.InjectionSite;
import org.jboss.errai.ioc.rebind.ioc.graph.api.QualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultCustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.tests.extensions.client.res.AnnoWithNonBindingAttribute;

import static org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType.ExtensionProvided;
import static org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType.DependentBean;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@IOCExtension
public class NonBindingAttributeExtensionProvider implements IOCExtensionConfigurator {

  @Override
  public void configure(final IOCProcessingContext context, final InjectionContext injectionContext) {
  }

  @Override
  public void afterInitialization(final IOCProcessingContext context, final InjectionContext injectionContext) {
    final AnnoWithNonBindingAttribute representative = new AnnoWithNonBindingAttribute() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return AnnoWithNonBindingAttribute.class;
      }

      @Override
      public String value() {
        return "";
      }
    };
    final QualifierFactory qualFactory = injectionContext.getQualifierFactory();
    final InjectableHandle handle = new InjectableHandle(MetaClassFactory.get(String.class), qualFactory.forSource(() -> new Annotation[] { representative }));
    injectionContext.registerInjectableProvider(handle, (injectionSite, nameGenerator) ->
      new DefaultCustomFactoryInjectable(handle, nameGenerator.generateFor(handle, ExtensionProvided),
            Dependent.class, Arrays.asList(DependentBean), getGenerator(injectionSite)));
  }

  private FactoryBodyGenerator getGenerator(final InjectionSite injectionSite) {
    final AnnoWithNonBindingAttribute anno = injectionSite.getAnnotation(AnnoWithNonBindingAttribute.class);
    final String value = anno.value();

    return new AbstractBodyGenerator() {
      @Override
      protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
              final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
        return Arrays.asList(Stmt.loadLiteral(value).returnValue());
      }
    };
  }

}
