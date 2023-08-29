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
import java.util.Collections;
import java.util.List;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.WindowInjectionContextImpl;
import org.jboss.errai.ioc.client.WindowInjectionContextStorage;
import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.jboss.errai.ioc.client.api.builtin.DummyJsTypeProvider;
import org.jboss.errai.ioc.client.container.BeanActivator;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.jboss.errai.codegen.Parameter.of;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newObject;
import static org.jboss.errai.ioc.rebind.ioc.extension.builtin.JsTypeAntiInliningExtension.numberOfRequiredAntiInliningDummies;
import static org.jboss.errai.ioc.rebind.ioc.extension.builtin.JsTypeAntiInliningExtension.requiresAntiInliningDummy;

/**
 * Generates factories that lookup types from the {@link WindowInjectionContextImpl}
 * , allowing the injection of types between dynamic runtime modules.
 *
 * @see FactoryBodyGenerator
 * @see AbstractBodyGenerator
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class JsTypeFactoryBodyGenerator extends AbstractBodyGenerator {

  @Override
  protected List<Statement> generateFactoryInitStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final MetaClass type = injectable.getInjectedType();
    if (IOCProcessor.isJsInteropSupportEnabled() && requiresAntiInliningDummy(type)) {
      final int count = numberOfRequiredAntiInliningDummies(type);
      final List<Statement> stmts = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
        stmts.add(invokeStatic(WindowInjectionContextStorage.class, "createOrGet")
                    .invoke("addBeanProvider", "$$_anti_inlining_dummy_$$", createJsTypeProvider(type)));
      }

      return stmts;
    }
    else {
      return emptyList();
    }

  }

  private ObjectBuilder createJsTypeProvider(final MetaClass type) {
    return newObject(DummyJsTypeProvider.class)
      .extend()
      .publicOverridesMethod("getInstance")
      .append(nestedCall(createAnonymousImpl(type)).returnValue())
      .finish()
      .publicOverridesMethod("getName")
      .append(loadLiteral("Anti-inlining impl for: " + type.getFullyQualifiedName()).returnValue())
      .finish()
      .finish();
  }

  private ObjectBuilder createAnonymousImpl(final MetaClass type) {
    final AnonymousClassStructureBuilder builder = newObject(type).extend();
    stream(type.getMethods())
      .filter(m -> m.isPublic() && m.isAbstract())
      .forEach(m -> {
        builder
          .publicOverridesMethod(m.getName(), of(m.getParameters()))
          .append(Stmt.throw_(RuntimeException.class))
          .finish();
      });
    return builder.finish();
  }

  @Override
  protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    return Collections.<Statement> singletonList(
            Stmt.castTo(injectable.getInjectedType(), invokeStatic(WindowInjectionContextStorage.class, "createOrGet")
                    .invoke("getBean", injectable.getInjectedType().getFullyQualifiedName())).returnValue());
  }

  @Override
  protected Statement generateFactoryHandleStatement(final Injectable injectable) {
    final Object[] args;
    if (injectable.getInjectedType().isAnnotationPresent(ActivatedBy.class)) {
      final Class<? extends BeanActivator> activatorType = injectable.getInjectedType().getAnnotation(ActivatedBy.class).value();
      args =  new Object[] {
          loadLiteral(injectable.getInjectedType()),
          injectable.getFactoryName(),
          injectable.getScope(),
          isEager(injectable.getInjectedType()),
          injectable.getBeanName(),
          loadLiteral(false),
          loadLiteral(activatorType)
      };
    } else {
      args =  new Object[] {
          loadLiteral(injectable.getInjectedType()),
          injectable.getFactoryName(),
          injectable.getScope(),
          isEager(injectable.getInjectedType()),
          injectable.getBeanName(),
          loadLiteral(false)
      };
    }

    return newObject(FactoryHandleImpl.class, args);
  }

}
