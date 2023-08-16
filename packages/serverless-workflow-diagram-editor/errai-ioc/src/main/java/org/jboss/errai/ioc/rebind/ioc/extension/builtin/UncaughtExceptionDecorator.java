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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.api.UncaughtExceptionHandler;
import org.jboss.errai.ioc.client.container.ErraiUncaughtExceptionHandler;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newObject;

/**
 * Generates factory/instance initialization statements to register {@link UncaughtExceptionHandler} methods with the
 * {@link ErraiUncaughtExceptionHandler}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
@CodeDecorator
public class UncaughtExceptionDecorator extends IOCDecoratorExtension<UncaughtExceptionHandler> {

  public UncaughtExceptionDecorator(final Class<UncaughtExceptionHandler> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(final Decorable decorable, final FactoryController controller) {
    final MetaMethod method = validateExceptionHandlingMethod(decorable);

    final boolean enclosingTypeDependent = decorable.isEnclosingTypeDependent();
    final String handlerVar = method.getName() + "Handler";
    final String cleanupRunnableVar = method.getName() + "HandlerCleanup";
    final Statement setCleanupRefStmt = controller.setReferenceStmt(cleanupRunnableVar, Refs.get(cleanupRunnableVar));
    final Statement getCleanupRefStmt = controller.getReferenceStmt(cleanupRunnableVar, Runnable.class);

    final List<Statement> initStmts = new ArrayList<>(Arrays.asList(
            declareAndInitHandlerVar(decorable, controller, handlerVar, enclosingTypeDependent),
            declareFinalVariable(cleanupRunnableVar, Runnable.class, addHandler(handlerVar))));

    if (enclosingTypeDependent) {
      initStmts.add(setCleanupRefStmt);
    }

    final List<Statement> destructionStmts = Collections.singletonList(nestedCall(getCleanupRefStmt).invoke("run"));

    if (enclosingTypeDependent) {
      controller.addInitializationStatements(initStmts);
      controller.addDestructionStatements(destructionStmts);
    } else {
      controller.addFactoryInitializationStatements(initStmts);
    }
  }

  private ContextualStatementBuilder addHandler(final String handlerVar) {
    return castTo(ErraiUncaughtExceptionHandler.class, invokeStatic(GWT.class, "getUncaughtExceptionHandler"))
            .invoke("addHandler", Refs.get(handlerVar));
  }

  private MetaMethod validateExceptionHandlingMethod(final Decorable decorable) {
    // Ensure that method has exactly one parameter of type Throwable
    final MetaMethod method = decorable.getAsMethod();
    final MetaParameter[] parms = method.getParameters();
    if (!(parms.length == 1 && parms[0].getType().equals(MetaClassFactory.get(Throwable.class)))) {
      throw new GenerationException("Methods annotated with " + UncaughtExceptionHandler.class.getName()
          + " must have exactly one parameter of type " + Throwable.class.getName()
          + ". Invalid parameters in method: "
          + GenUtil.getMethodString(method) + " of type " + method.getDeclaringClass() + ".");
    }
    return method;
  }

  private Statement declareAndInitHandlerVar(final Decorable decorable, final FactoryController controller,
          final String name, final boolean enclosingTypeDependent) {
    final MetaClass throwableConsumerClass = parameterizedAs(Consumer.class, typeParametersOf(Throwable.class));
    final BlockBuilder<AnonymousClassStructureBuilder> initBuilder = newObject(throwableConsumerClass)
            .extend().publicOverridesMethod("accept", Parameter.of(Throwable.class, "t"));
    if (!enclosingTypeDependent) {
      final MetaClass enclosingType = decorable.getEnclosingInjectable().getInjectedType();
      initBuilder.append(declareFinalVariable("instance", enclosingType,
              castTo(enclosingType, invokeStatic(Factory.class, "maybeUnwrapProxy", controller.contextGetInstanceStmt()))));
    }

    final ObjectBuilder initStmt = initBuilder.append(decorable.call(Refs.get("t"))).finish().finish();
    final Statement handlerStatement = declareFinalVariable(name, throwableConsumerClass, initStmt);

    return handlerStatement;
  }
}
