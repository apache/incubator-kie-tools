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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Disposes;

import com.google.common.collect.Multimap;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DisposerMethodDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ParamDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ProducerInstanceDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.declareVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryGenerator.getLocalVariableName;

/**
 * Generates factories for beans of producer methods or fields.
 *
 * @see FactoryBodyGenerator
 * @see AbstractBodyGenerator
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ProducerFactoryBodyGenerator extends AbstractBodyGenerator {

  private static final String PRODUCER_INSTANCE = "producerInstance";

  @Override
  protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final Multimap<DependencyType, Dependency> depsByType = separateByType(injectable.getDependencies());
    if (depsByType.get(DependencyType.ProducerMember).size() != 1) {
      throw new RuntimeException("A produced type must have exactly 1 producing instance but "
              + depsByType.get(DependencyType.ProducerMember).size() + " were found.");
    }
    final ProducerInstanceDependency producerInstanceDep = (ProducerInstanceDependency) depsByType.get(DependencyType.ProducerMember).iterator().next();
    final Injectable producerInjectable = producerInstanceDep.getInjectable();
    final MetaClassMember producingMember = producerInstanceDep.getProducingMember();

    if (producingMember instanceof MetaField) {
      return fieldCreateInstanceStatements((MetaField) producingMember, producerInjectable, injectable, bodyBlockBuilder);
    } else if (producingMember instanceof MetaMethod) {
      return methodCreateInstanceStatements((MetaMethod) producingMember, producerInjectable, injectable,
              depsByType.get(DependencyType.ProducerParameter), bodyBlockBuilder);
    } else {
      throw new RuntimeException("Unrecognized producing member: " + producingMember);
    }
  }

  private List<Statement> methodCreateInstanceStatements(final MetaMethod producingMember, final Injectable producerInjectable,
          final Injectable producedInjectable, final Collection<Dependency> paramDeps, final ClassStructureBuilder<?> bodyBlockBuilder) {
    final List<Statement> stmts = new ArrayList<>();
    controller.ensureMemberExposed(producingMember);

    if (!producingMember.isStatic()) {
      final Statement producerInstanceValue = loadVariable("contextManager").invoke("getInstance", producerInjectable.getFactoryName());
      stmts.add(declareVariable(PRODUCER_INSTANCE, producerInjectable.getInjectedType(), producerInstanceValue));
      stmts.add(loadVariable(PRODUCER_INSTANCE).assignValue(castTo(producerInjectable.getInjectedType(),
              invokeStatic(Factory.class, "maybeUnwrapProxy", loadVariable(PRODUCER_INSTANCE)))));
    }

    final List<Statement> depScopeRegistrationStmts = new ArrayList<>();
    final Statement[] producerParams = generateProducerParams(producingMember, paramDeps, stmts, depScopeRegistrationStmts);
    final Statement invocation = controller.exposedMethodStmt(loadVariable(PRODUCER_INSTANCE), producingMember,
            producerParams);
    stmts.add(declareFinalVariable("instance", producedInjectable.getInjectedType(), invocation));
    if (!producingMember.isStatic()) {
      stmts.add(setProducerInstanceReference());
      if (producerInjectable.getWiringElementTypes().contains(WiringElementType.DependentBean)) {
        stmts.add(loadVariable("this").invoke("registerDependentScopedReference", loadVariable("instance"), loadVariable(PRODUCER_INSTANCE)));
      }
    }
    stmts.addAll(depScopeRegistrationStmts);

    stmts.add(loadVariable("instance").returnValue());

    return stmts;
  }

  private Statement[] generateProducerParams(final MetaMethod producingMember, final Collection<Dependency> paramDeps,
          final List<Statement> varDeclarationStmts, final List<Statement> depScopeRegistrationStmts) {
    // TODO validate params
    final Statement[] params = new Statement[producingMember.getParameters().length];

    for (final Dependency dep : paramDeps) {
      final ParamDependency paramDep = (ParamDependency) dep;
      final ContextualStatementBuilder producerParamCreationStmt = generateProducerParamCreationStmt(paramDep);
      final String paramVarName = getLocalVariableName(paramDep.getParameter());
      varDeclarationStmts.add(declareFinalVariable(paramVarName, paramDep.getParameter().getType(), producerParamCreationStmt));
      if (paramDep.getInjectable().getWiringElementTypes().contains(WiringElementType.DependentBean)) {
        depScopeRegistrationStmts.add(loadVariable("this").invoke("registerDependentScopedReference",
              loadVariable("instance"), loadVariable(paramVarName)));
      }
      params[paramDep.getParamIndex()] = loadVariable(paramVarName);
    }

    return params;
  }

  private ContextualStatementBuilder generateProducerParamCreationStmt(final ParamDependency paramDep) {
    ContextualStatementBuilder producerParamCreationStmt;
    if (paramDep.getInjectable().isContextual()) {
      final MetaParameter param = paramDep.getParameter();
      final MetaClass[] typeArgs = getTypeArguments(param.getType());
      final Annotation[] annotations = param.getAnnotations();
      producerParamCreationStmt = castTo(paramDep.getInjectable().getInjectedType(),
              loadVariable("contextManager").invoke("getContextualInstance", paramDep.getInjectable().getFactoryName(), typeArgs, annotations));
    }
    else {
      producerParamCreationStmt = castTo(paramDep.getInjectable().getInjectedType(),
              loadVariable("contextManager").invoke("getInstance", paramDep.getInjectable().getFactoryName()));
    }
    return producerParamCreationStmt;
  }

  private List<Statement> fieldCreateInstanceStatements(final MetaField producingMember, final Injectable producerInjectable,
          final Injectable producedInjectable, final ClassStructureBuilder<?> bodyBlockBuilder) {
    final List<Statement> stmts = new ArrayList<>();
    controller.ensureMemberExposed(producingMember);

    if (!producingMember.isStatic()) {
      final Statement producerInstanceValue = loadVariable("contextManager").invoke("getInstance", producerInjectable.getFactoryName());
      stmts.add(declareVariable(PRODUCER_INSTANCE, producerInjectable.getInjectedType(), producerInstanceValue));

      stmts.add(loadVariable(PRODUCER_INSTANCE).assignValue(Stmt.castTo(producerInjectable.getInjectedType(),
              invokeStatic(Factory.class, "maybeUnwrapProxy", loadVariable(PRODUCER_INSTANCE)))));
    }

    final Statement invocation = controller.exposedFieldStmt(loadVariable(PRODUCER_INSTANCE), producingMember);
    stmts.add(declareFinalVariable("instance", producedInjectable.getInjectedType(), invocation));

    if (!producingMember.isStatic()) {
      stmts.add(setProducerInstanceReference());
      if (producerInjectable.getWiringElementTypes().contains(WiringElementType.DependentBean)) {
        stmts.add(loadVariable("this").invoke("registerDependentScopedReference", loadVariable("instance"), loadVariable(PRODUCER_INSTANCE)));
      }
    }

    stmts.add(loadVariable("instance").returnValue());

    return stmts;
  }

  private Statement setProducerInstanceReference() {
    return controller.setReferenceStmt(PRODUCER_INSTANCE, loadVariable(PRODUCER_INSTANCE));
  }

  @Override
  protected List<Statement> generateDestroyInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final List<Statement> destroyInstanceStmts = new ArrayList<>();
    final Multimap<DependencyType, Dependency> depsByType = separateByType(injectable.getDependencies());
    final Collection<Dependency> producerMemberDeps = depsByType.get(DependencyType.ProducerMember);

    if (producerMemberDeps.size() != 1) {
      throw new RuntimeException("A produced type must have exactly 1 producing instance but "
              + producerMemberDeps.size() + " were found.");
    }

    final Collection<Dependency> disposerMethodDeps = depsByType.get(DependencyType.DisposerMethod);
    if (disposerMethodDeps.size() > 1) {
      // TODO error message with matching disposer names.
      throw new RuntimeException();
    } else if (disposerMethodDeps.size() == 1) {
      final DisposerMethodDependency disposerDep = (DisposerMethodDependency) disposerMethodDeps.iterator().next();
      final MetaMethod disposer = disposerDep.getDisposerMethod();

      controller.ensureMemberExposed(disposer);
      final Statement invocation = controller.exposedMethodStmt(
              controller.getReferenceStmt(PRODUCER_INSTANCE, disposer.getDeclaringClass()), disposer,
              getDisposerParams(disposer, depsByType.get(DependencyType.DisposerParameter),
                      bodyBlockBuilder.getClassDefinition()));
      destroyInstanceStmts.add(invocation);
    }

    return destroyInstanceStmts;
  }

  private Statement[] getDisposerParams(final MetaMethod disposer, final Collection<Dependency> disposerParams, final BuildMetaClass factory) {
    final Statement[] params = new Statement[disposer.getParameters().length];

    for (final Dependency dep : disposerParams) {
      final ParamDependency paramDep = (ParamDependency) dep;
      final ContextualStatementBuilder paramInstance = castTo(paramDep.getInjectable().getInjectedType(),
              loadVariable("contextManager").invoke("getInstance", paramDep.getInjectable().getFactoryName()));
      final ContextualStatementBuilder paramExpression;
      if (paramDep.getInjectable().getWiringElementTypes().contains(WiringElementType.DependentBean)) {
        paramExpression = loadVariable("this").invoke("registerDependentScopedReference", loadVariable("instance"), paramInstance);
      } else {
        paramExpression = paramInstance;
      }

      params[paramDep.getParamIndex()] = paramExpression;
    }

    final MetaParameter disposesParam = disposer.getParametersAnnotatedWith(Disposes.class).get(0);
    params[disposesParam.getIndex()] = loadVariable("instance");

    return params;
  }

}
