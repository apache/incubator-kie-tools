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


package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class BaseProviderGenerator extends AbstractBodyGenerator {

  @Override
  protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final List<Statement> stmts = new ArrayList<>(2);
    final Injectable providerInjectable = getProviderInjectable(injectable);

    final MetaClass paramterizedProviderType = parameterizedAs(getProviderRawType(), typeParametersOf(injectable.getInjectedType()));
    stmts.add(declareFinalVariable("provider", paramterizedProviderType, lookupProviderStmt(providerInjectable, paramterizedProviderType)));
    stmts.add(declareFinalVariable("instance", injectable.getInjectedType(), invokeProviderStmt(loadVariable("provider"))));
    if (providerInjectable.getInjectedType().isAssignableTo(Disposer.class)) {
      stmts.add(loadVariable("this").invoke("setReference", loadVariable("instance"), "disposer", loadVariable("provider")));
    }
    stmts.add(loadVariable("instance").returnValue());

    return stmts;
  }

  @Override
  protected List<Statement> generateDestroyInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final Injectable provider = getProviderInjectable(injectable);
    if (provider.getInjectedType().isAssignableTo(Disposer.class)) {
      return singletonList(castTo(Disposer.class,
              loadVariable("this").invoke("getReferenceAs", loadVariable("instance"), "disposer", Disposer.class))
                      .invoke("dispose", loadVariable("instance")));
    }
    else {
      return emptyList();
    }
  }

  protected Injectable getProviderInjectable(final Injectable depInjectable) {
    for (final Dependency dep : depInjectable.getDependencies()) {
      if (dep.getDependencyType().equals(DependencyType.ProducerMember)) {
        final MetaClass providerType = dep.getInjectable().getInjectedType();
        if (providerType.isAssignableTo(getProviderRawType())) {
          return dep.getInjectable();
        }
        else {
          throw new RuntimeException("Unrecognized contextual provider type " + providerType.getFullyQualifiedName());
        }
      }
    }

    throw new RuntimeException();
  }

  protected ContextualStatementBuilder lookupProviderStmt(final Injectable providerInjectable, final MetaClass paramterizedProviderType) {
    final ContextualStatementBuilder provider = castTo(paramterizedProviderType,
            loadVariable("contextManager").invoke("getInstance", loadLiteral(providerInjectable.getFactoryName())));
    return provider;
  }

  protected abstract Class<?> getProviderRawType();

  protected abstract ContextualStatementBuilder invokeProviderStmt(ContextualStatementBuilder provider);

}
