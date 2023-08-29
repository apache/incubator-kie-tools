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


package org.jboss.errai.enterprise.rebind;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.MethodCommentBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.enterprise.client.cdi.CDIEventTypeLookup;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

@IOCExtension
public class JSR299IOCExtensionConfigurator implements IOCExtensionConfigurator {
  @Override
  public void configure(final IOCProcessingContext context, final InjectionContext injectionContext) {
    injectionContext.mapElementType(WiringElementType.ProducerElement, Produces.class);
  }

  @SuppressWarnings("unchecked")
  private static void addTypeHierarchyFor(final IOCProcessingContext context, final Set<MetaClass> classes) {
    final BlockStatement instanceInitializer = context.getBootstrapClass().getInstanceInitializer();

    int i = 0, addLookupMethodCount = 0;
    MethodCommentBuilder<?> currentBlock = null;
    for (final MetaClass subClass : classes) {
      // We'll generate a separate lookup method for every 500 lines to make sure we're not
      // exceeding the method size byte limit. See ERRAI-346 and ERRAI-679
      if ((i++ % 500) == 0) {
        final Statement lookupMethod = Stmt.invokeStatic(context.getBootstrapClass(), "addLookups_" + addLookupMethodCount);
        if (currentBlock != null) {
          currentBlock
              .append(lookupMethod);
          currentBlock.modifiers(Modifier.Static).finish();
        }
        else {
          instanceInitializer.addStatement(lookupMethod);
        }
        currentBlock =
            context.getBootstrapBuilder().privateMethod(void.class, "addLookups_" + addLookupMethodCount++);
      }

      if (subClass.getSuperClass() != null) {
        currentBlock.append(Stmt.invokeStatic(CDIEventTypeLookup.class, "get")
                .invoke("addLookup", subClass.getFullyQualifiedName(), subClass.getSuperClass().getFullyQualifiedName()));
      }

      for (final MetaClass interfaceClass : subClass.getInterfaces()) {
        currentBlock.append(Stmt.invokeStatic(CDIEventTypeLookup.class, "get")
                .invoke("addLookup", subClass.getFullyQualifiedName(), interfaceClass.getFullyQualifiedName()));

      }
    }

    if (currentBlock != null) {
      currentBlock.modifiers(Modifier.Static).finish();
    }
  }

  @Override
  public void afterInitialization(final IOCProcessingContext context, final InjectionContext injectionContext) {

    final BlockStatement instanceInitializer = context.getBootstrapClass().getInstanceInitializer();
    final Set<MetaClass> knownObserverTypes = new HashSet<>();

    for (final MetaParameter parameter : ClassScanner.getParametersAnnotatedWith(Observes.class,
            context.getGeneratorContext())) {
      knownObserverTypes.add(parameter.getType());
    }

    final Set<MetaClass> knownTypesWithSuperTypes = new HashSet<>(knownObserverTypes);
    for (final MetaClass cls : knownObserverTypes) {
      for (final MetaClass subClass : ClassScanner.getSubTypesOf(cls, context.getGeneratorContext())) {
        knownTypesWithSuperTypes.add(subClass);
      }
    }

    addTypeHierarchyFor(context, knownTypesWithSuperTypes);

    instanceInitializer.addStatement(Stmt.nestedCall(Stmt.newObject(CDI.class))
            .invoke("initLookupTable", Stmt.invokeStatic(CDIEventTypeLookup.class, "get")));
  }
}
