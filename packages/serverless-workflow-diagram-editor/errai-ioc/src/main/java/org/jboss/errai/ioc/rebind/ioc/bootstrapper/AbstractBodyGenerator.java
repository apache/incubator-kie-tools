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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Qualifier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import org.jboss.errai.codegen.BooleanOperator;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ConstructorBlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.MethodCommentBuilder;
import org.jboss.errai.codegen.builder.impl.AbstractStatementBuilder;
import org.jboss.errai.codegen.builder.impl.BooleanExpressionBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.BeanActivator;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ParamDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.Parameter.finalOf;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.PrivateAccessUtil.addPrivateAccessStubs;
import static org.jboss.errai.codegen.util.PrivateAccessUtil.getPrivateMethodName;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.if_;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.load;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newArray;
import static org.jboss.errai.codegen.util.Stmt.newObject;
import static org.jboss.errai.codegen.util.Stmt.throw_;
import static org.jboss.errai.codegen.util.Stmt.try_;

/**
 * Implements functionality common to most {@link FactoryBodyGenerator} such as
 * generating proxies and managing
 * {@link Factory#getReferenceAs(Object, String, Class) references}.
 *
 * @see FactoryBodyGenerator
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class AbstractBodyGenerator implements FactoryBodyGenerator {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBodyGenerator.class);

  protected static Multimap<DependencyType, Dependency> separateByType(final Collection<Dependency> dependencies) {
    final Multimap<DependencyType, Dependency> separated = HashMultimap.create();

    for (final Dependency dep : dependencies) {
      separated.put(dep.getDependencyType(), dep);
    }

    return separated;
  }

  /**
   * This is populated at the start of
   * {@link #generate(ClassStructureBuilder, Injectable, DependencyGraph, InjectionContext, TreeLogger, GeneratorContext)}
   * .
   *
   * Calls to {@link FactoryController#addInvokeAfter(MetaMethod, Statement)},
   * {@link FactoryController#addInvokeBefore(MetaMethod, Statement)}, or
   * {@link FactoryController#addProxyProperty(String, Class, Statement)} will
   * require the type produced by the generated factory to be proxiable.
   */
  protected FactoryController controller;

  protected void maybeImplementCreateProxy(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable) {
    final MetaClass proxyImpl = maybeCreateProxyImplementation(injectable, bodyBlockBuilder);

    if (proxyImpl != null) {
      final BlockBuilder<?> createProxyBody = bodyBlockBuilder
              .publicMethod(parameterizedAs(Proxy.class, typeParametersOf(injectable.getInjectedType())), "createProxy",
                      finalOf(Context.class, "context"))
              .body();
      final Object proxyInstanceStmt;
      if (injectable.getInjectedType().isInterface() || getAccessibleNoArgConstructor(injectable.getInjectedType()) != null) {
        proxyInstanceStmt = newObject(proxyImpl);
      } else {
        bodyBlockBuilder
                .privateMethod(parameterizedAs(Proxy.class, typeParametersOf(injectable.getInjectedType())),
                        "createProxyWithErrorMessage")
                .body()
                .append(try_().append(load(newObject(proxyImpl)).returnValue()).finish()
                        .catch_(Throwable.class, "t").append(throw_(RuntimeException.class,
                                loadLiteral(injectableConstructorErrorMessage(injectable)), loadVariable("t")))
                        .finish())
                .finish();
        proxyInstanceStmt = loadVariable("this").invoke("createProxyWithErrorMessage");
      }

      createProxyBody
              .append(declareFinalVariable("proxyImpl",
                      parameterizedAs(Proxy.class, typeParametersOf(injectable.getInjectedType())),
                      proxyInstanceStmt))
              .append(loadVariable("proxyImpl").invoke("setProxyContext", loadVariable("context")))
              .append(loadVariable("proxyImpl").returnValue()).finish();
    }
  }

  private String injectableConstructorErrorMessage(final Injectable injectable) {
    final MetaConstructor constr = getAccessibleConstructor(injectable);

    return "While creating a proxy for " + injectable.getInjectedType().getFullyQualifiedName()
            + " an exception was thrown from this constructor: " + constr
            + "\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.";
  }

  /**
   * @return Returns {@code null} if unproxiable and a proxy is not required.
   */
  private MetaClass maybeCreateProxyImplementation(final Injectable injectable, final ClassStructureBuilder<?> bodyBlockBuilder) {

    final ClassStructureBuilder<?> proxyImpl;
    final MetaClass injectedType = injectable.getInjectedType();
    final boolean requiresProxy = requiresProxy(injectable);
    if (requiresProxy && injectedType.isInterface()) {
      proxyImpl = ClassBuilder
              .define(injectable.getFactoryName() + "ProxyImpl")
              .privateScope()
              .implementsInterface(parameterizedAs(Proxy.class, typeParametersOf(injectedType)))
              .implementsInterface(injectedType).body();
      declareAndInitializeProxyHelper(injectable, proxyImpl);
    } else if (requiresProxy && isProxiableClass(injectable)) {
      proxyImpl = ClassBuilder
              .define(injectable.getFactoryName() + "ProxyImpl", injectedType)
              .privateScope()
              .implementsInterface(parameterizedAs(Proxy.class, typeParametersOf(injectedType))).body();
      declareAndInitializeProxyHelper(injectable, proxyImpl);
    } else if (!requiresProxy) {
      return null;
    } else {
      throw new RuntimeException(injectedType + " must be proxiable but is not.");
    }

    maybeImplementConstructor(proxyImpl, injectable);
    implementProxyMethods(proxyImpl, injectable);
    implementAccessibleMethods(proxyImpl, injectable, bodyBlockBuilder.getClassDefinition());

    bodyBlockBuilder.declaresInnerClass(new InnerClass(proxyImpl.getClassDefinition()));

    return proxyImpl.getClassDefinition();
  }

  private boolean requiresProxy(final Injectable injectable) {
    return injectable.requiresProxy() || controller.requiresProxy();
  }

  private boolean isProxiableClass(final Injectable injectable) {
    final MetaClass type = injectable.getInjectedType();

    return !type.isFinal() && hasAccessibleConstructor(injectable);
  }

  private boolean hasAccessibleConstructor(final Injectable injectable) {
    return getAccessibleConstructor(injectable) != null;
  }

  private MetaConstructor getAccessibleConstructor(final Injectable injectable) {
    final MetaClass type = injectable.getInjectedType();
    final MetaConstructor noArgConstr = getAccessibleNoArgConstructor(type);

    if (noArgConstr != null) {
      return noArgConstr;
    }

    for (final Dependency dep : injectable.getDependencies()) {
      if (dep.getDependencyType().equals(DependencyType.Constructor)) {
        final MetaConstructor injectableConstr = (MetaConstructor) ((ParamDependency) dep).getParameter().getDeclaringMember();

        return (injectableConstr.isPublic() || injectableConstr.isProtected()) ? injectableConstr : null;
      }
    }

    return null;
  }

  private MetaConstructor getAccessibleNoArgConstructor(final MetaClass type) {
    final MetaConstructor noArgConstr = type.getDeclaredConstructor(new MetaClass[0]);

    if (noArgConstr != null && (noArgConstr.isPublic() || noArgConstr.isProtected())) {
      return noArgConstr;
    } else {
      return null;
    }
  }

  private void declareAndInitializeProxyHelper(final Injectable injectable, final ClassStructureBuilder<?> bodyBlockBuilder) {
    bodyBlockBuilder
            .privateField("proxyHelper",
                    parameterizedAs(ProxyHelper.class, typeParametersOf(injectable.getInjectedType())))
            .modifiers(Modifier.Final)
            .initializesWith(initializeProxyHelper(injectable))
            .finish();
  }

  private Statement initializeProxyHelper(final Injectable injectable) {
    return newObject(
            parameterizedAs(ProxyHelperImpl.class, typeParametersOf(injectable.getInjectedType())),
            injectable.getFactoryName());
  }

  private void maybeImplementConstructor(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    if (injectable.getInjectedType().isInterface()) {
      return;
    }

    final MetaConstructor accessibleConstructor = getAccessibleConstructor(injectable);
    if (accessibleConstructor.getParameters().length > 0) {
      implementConstructor(proxyImpl, accessibleConstructor);
    }
  }

  private void implementConstructor(final ClassStructureBuilder<?> proxyImpl, final MetaConstructor accessibleConstructor) {
    final Object[] args = new Object[accessibleConstructor.getParameters().length];
    for (int i = 0; i < args.length; i++) {
      args[i] = loadLiteral(null);
    }

    proxyImpl.publicConstructor().callSuper(args).finish();
  }

  private void implementAccessibleMethods(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable, final BuildMetaClass factoryClass) {
    final Multimap<String, MetaMethod> proxiedMethodsByName = HashMultimap.create();
    final MetaClass injectedType = injectable.getInjectedType();
    for (final MetaMethod method : injectedType.getMethods()) {
      if (shouldProxyMethod(method, proxiedMethodsByName)) {
        proxiedMethodsByName.put(method.getName(), method);
        final BlockBuilder<?> body = createProxyMethodDeclaration(proxyImpl, method);
        final StatementBuilder proxiedInstanceDeclaration = declareFinalVariable("proxiedInstance",
                injectable.getInjectedType(), loadVariable("proxyHelper").invoke("getInstance", loadVariable("this")));
        final ContextualStatementBuilder proxyHelperInvocation = proxyHelperInvocation(method, factoryClass);
        final Statement nonInitializedCase;
        final boolean nonInitializedReturns;
        if (injectedType.isInterface() || method.isAbstract()) {
          nonInitializedCase = throw_(RuntimeException.class, "Cannot invoke public method on proxied interface before constructor completes.");
          nonInitializedReturns = false;
        } else if (!(method.isPublic() || method.isPrivate() || method.isProtected())) {
          nonInitializedCase = throw_(RuntimeException.class, "Cannot invoke proxied package private method before constructor completes.");
          nonInitializedReturns = false;
        } else {
          nonInitializedCase = loadVariable("super").invoke(method.getName(), getParametersForInvocation(method));
          nonInitializedReturns = true;
        }
        final BlockBuilder<ElseBlockBuilder> ifBlock = if_(
                BooleanExpressionBuilder.create(loadVariable("proxyHelper"), BooleanOperator.NotEquals, null))
                        .append(proxiedInstanceDeclaration)
                        .appendAll(controller.getInvokeBeforeStatements(method));
        if (method.getReturnType().isVoid()) {
          ifBlock.append(proxyHelperInvocation);
          ifBlock.appendAll(controller.getInvokeAfterStatements(method));
          body.append(ifBlock.finish().else_().append(nonInitializedCase).finish());
        } else {
          ifBlock.append(declareFinalVariable("retVal", method.getReturnType().getErased(), proxyHelperInvocation));
          ifBlock.appendAll(controller.getInvokeAfterStatements(method));
          ifBlock.append(loadVariable("retVal").returnValue());
          if (nonInitializedReturns) {
            body.append(ifBlock.finish().else_().append(nestedCall(nonInitializedCase).returnValue()).finish());
          } else {
            body.append(ifBlock.finish().else_().append(nonInitializedCase).finish());
          }
        }

        body.finish();
      }
      else if (!(method.isPublic() || method.isPrivate() || method.isProtected()) && injectable.requiresProxy()) {
        logger.warn("The normal scoped type, " + injectable.getInjectedType().getFullyQualifiedName()
                + ", has a package-private method, " + method.getName()
                + ", that cannot be proxied. Invoking this method on an injected instance may cause errors.");
      }
    }
  }

  private BlockBuilder<?> createProxyMethodDeclaration(final ClassStructureBuilder<?> proxyImpl, final MetaMethod method) {
    final MethodCommentBuilder<?> methodBuilder;
    if (method.isPublic()) {
      methodBuilder = proxyImpl.publicMethod(method.getReturnType().getErased(), method.getName(),
              getParametersForDeclaration(method));
    } else if (method.isProtected()) {
      methodBuilder = proxyImpl.protectedMethod(method.getReturnType().getErased(), method.getName(),
              getParametersForDeclaration(method));
    } else {
      final String methodType = (method.isProtected()) ? "private" : "package private";
      throw new RuntimeException(
              "Cannot proxy " + methodType + " method from " + method.getDeclaringClassName());
    }

    if (method.isPublic() || method.isProtected()) {
      return methodBuilder.annotatedWith(new Override() {
        @Override
        public Class<? extends Annotation> annotationType() {
          return Override.class;
        }
      }).throws_(method.getCheckedExceptions()).body();
    } else {
      return methodBuilder.throws_(method.getCheckedExceptions()).body();
    }
  }

  private ContextualStatementBuilder proxyHelperInvocation(final MetaMethod method, final BuildMetaClass factoryClass) {
    if (method.isPublic()) {
      return loadVariable("proxiedInstance").invoke(method.getName(), getParametersForInvocation(method));
    } else {
      controller.addExposedMethod(method);
      return invokeStatic(factoryClass, getPrivateMethodName(method), getParametersForInvocation(method, loadVariable("proxiedInstance")));
    }
  }

  private boolean shouldProxyMethod(final MetaMethod method, final Multimap<String, MetaMethod> proxiedMethodsByName) {
    return (method.getDeclaringClass() != null && method.getDeclaringClass().isInterface())
            || !method.isStatic() && (method.isPublic() || method.isProtected()) && !method.isFinal()
                    && methodIsNotFromObjectUnlessHashCode(method)
            && typesInSignatureAreVisible(method)
            && isNotAlreadyProxied(method, proxiedMethodsByName);
  }

  private boolean isNotAlreadyProxied(final MetaMethod method, final Multimap<String, MetaMethod> proxiedMethodsByName) {
    methodLoop:
    for (final MetaMethod proxiedMethod : proxiedMethodsByName.get(method.getName())) {
      final MetaParameter[] proxiedParams = proxiedMethod.getParameters();
      final MetaParameter[] methodParams = method.getParameters();
      if (proxiedParams.length == methodParams.length) {
        for (int i = 0; i < methodParams.length; i++) {
          if (!proxiedParams[i].getType().isAssignableTo(methodParams[i].getType())) {
            continue methodLoop;
          }
        }

        return false;
      }
    }

    return true;
  }

  private boolean methodIsNotFromObjectUnlessHashCode(final MetaMethod method) {
    return (method.asMethod() == null || method.asMethod().getDeclaringClass() == null
            || !method.asMethod().getDeclaringClass().equals(Object.class)
            || method.getName().equals("hashCode"))
            && isNotEqualsMethod(method);
  }

  private boolean isNotEqualsMethod(final MetaMethod method) {
    return !(method.getName().equals("equals") && method.getParameters().length == 1);
  }

  private boolean typesInSignatureAreVisible(final MetaMethod method) {
    if (!isVisibleType(method.getReturnType())) {
      return false;
    }

    for (final MetaParameter param : method.getParameters()) {
      if (!isVisibleType(param.getType())) {
        return false;
      }
    }

    return true;
  }

  private boolean isVisibleType(final MetaClass type) {
    if (type.isArray()) {
      return isVisibleType(type.getComponentType());
    } else {
      return type.isPublic() || type.isProtected() || type.isPrimitive();
    }
  }

  private Object[] getParametersForInvocation(final MetaMethod method, final Object... prependedParams) {
    final int paramLength = method.getParameters().length + prependedParams.length;
    final Object[] params = new Object[paramLength];
    for (int i = 0; i < prependedParams.length; i++) {
      params[i] = prependedParams[i];
    }
    final MetaParameter[] declaredParams = method.getParameters();
    for (int i = 0; i < declaredParams.length; i++) {
      params[prependedParams.length+i] = loadVariable(declaredParams[i].getName());
    }

    return params;
  }

  private Parameter[] getParametersForDeclaration(final MetaMethod method) {
    final MetaParameter[] metaParams = method.getParameters();
    final Parameter[] params = new Parameter[metaParams.length];

    for (int i = 0; i < params.length; i++) {
      params[i] = Parameter.of(metaParams[i].getType().getErased(), metaParams[i].getName());
    }

    return params;
  }

  private void implementProxyMethods(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    implementInitProxyProperties(proxyImpl, injectable);
    implementAsBeanType(proxyImpl, injectable);
    implementSetInstance(proxyImpl, injectable);
    implementClearInstance(proxyImpl, injectable);
    implementSetContext(proxyImpl, injectable);
    implementGetContext(proxyImpl, injectable);
    implementUnwrappedInstance(proxyImpl, injectable);
    implementEquals(proxyImpl);
  }

  private void implementEquals(final ClassStructureBuilder<?> proxyImpl) {
    proxyImpl.publicMethod(boolean.class, "equals", Parameter.of(Object.class, "obj")).body()
      .append(loadVariable("obj").assignValue(invokeStatic(Factory.class, "maybeUnwrapProxy", loadVariable("obj"))))
      .append(loadVariable("proxyHelper").invoke("getInstance", loadVariable("this")).invoke("equals", loadVariable("obj")).returnValue())
      .finish();
  }

  private void implementInitProxyProperties(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    final BlockBuilder<?> initBody = proxyImpl
            .publicMethod(void.class, "initProxyProperties", finalOf(injectable.getInjectedType(), "instance")).body();

    for (final Entry<String, Statement> prop : controller.getProxyProperties()) {
      proxyImpl.privateField(prop.getKey(), prop.getValue().getType()).finish();
      initBody.append(loadVariable(prop.getKey()).assignValue(prop.getValue()));
    }

    initBody.finish();
  }

  private void implementUnwrappedInstance(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    proxyImpl.publicMethod(Object.class, "unwrap")
             .body()
             .append(loadVariable("proxyHelper").invoke("getInstance", loadVariable("this")).returnValue())
             .finish();
  }

  private void implementSetContext(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    proxyImpl.publicMethod(void.class, "setProxyContext", finalOf(Context.class, "context"))
             .body()
             .append(loadVariable("proxyHelper").invoke("setProxyContext", loadVariable("context")))
             .finish();
  }

  private void implementGetContext(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    proxyImpl.publicMethod(Context.class, "getProxyContext")
             .body()
             .append(loadVariable("proxyHelper").invoke("getProxyContext").returnValue())
             .finish();
  }

  private void implementClearInstance(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    proxyImpl.publicMethod(void.class, "clearInstance")
             .body()
             .append(loadVariable("proxyHelper").invoke("clearInstance"))
             .finish();
  }

  private void implementSetInstance(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    proxyImpl.publicMethod(void.class, "setInstance", finalOf(injectable.getInjectedType(), "instance"))
             .body()
             .append(loadVariable("proxyHelper").invoke("setInstance", loadVariable("instance")))
             .finish();
  }

  private void implementAsBeanType(final ClassStructureBuilder<?> proxyImpl, final Injectable injectable) {
    proxyImpl.publicMethod(injectable.getInjectedType(), "asBeanType")
             .body()
             .append(loadVariable("this").returnValue())
             .finish();
  }

  protected void implementCreateInstance(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable, final List<Statement> createInstanceStatements) {
    String createInstanceMethodName;
    Parameter[] params;
    if (injectable.isContextual()) {
      createInstanceMethodName = "createContextualInstance";
      params = new Parameter[] {
          finalOf(ContextManager.class, "contextManager"),
          finalOf(Class[].class, "typeArgs"),
          finalOf(Annotation[].class, "qualifiers") };
    }
    else {
      createInstanceMethodName = "createInstance";
      params = new Parameter[] { finalOf(ContextManager.class, "contextManager") };
    }
    bodyBlockBuilder.publicMethod(injectable.getInjectedType(), createInstanceMethodName, params)
                    .appendAll(createInstanceStatements)
                    .finish();
  }

  protected void addReturnStatement(final List<Statement> createInstanceStatements) {
    createInstanceStatements.add(loadVariable("this").invoke("setIncompleteInstance", loadLiteral(null)));
    createInstanceStatements.add(loadVariable("instance").returnValue());
  }

  /**
   * @param bodyBlockBuilder
   *          The {@link ClassStructureBuilder} for the {@link Factory} being
   *          generated.
   * @param injectable
   *          Contains metadata (including dependencies) or the bean that the
   *          generated factory will produce.
   * @param graph
   *          The dependency graph that the {@link Injectable} parameter is
   *          from.
   * @param injectionContext
   *          The single injection context shared between all
   *          {@link FactoryBodyGenerator FactoryBodyGenerators}.
   * @return A list of statements that will generated in the
   *         {@link Factory#createInstance(ContextManager)} method.
   */
  protected abstract List<Statement> generateCreateInstanceStatements(ClassStructureBuilder<?> bodyBlockBuilder,
          Injectable injectable, DependencyGraph graph, InjectionContext injectionContext);

  @Override
  public void generate(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable,
          final DependencyGraph graph, final InjectionContext injectionContext, final TreeLogger logger,
          final GeneratorContext context) {
    controller = new FactoryController(injectable.getInjectedType(), injectable.getFactoryName(), bodyBlockBuilder.getClassDefinition());
    preGenerationHook(bodyBlockBuilder, injectable, graph, injectionContext);

    final List<Statement> factoryInitStatements = generateFactoryInitStatements(bodyBlockBuilder, injectable, graph, injectionContext);
    final List<Statement> createInstanceStatements = generateCreateInstanceStatements(bodyBlockBuilder, injectable, graph, injectionContext);
    final List<Statement> destroyInstanceStatements = generateDestroyInstanceStatements(bodyBlockBuilder, injectable, graph, injectionContext);
    final List<Statement> invokePostConstructStatements = generateInvokePostConstructsStatements(bodyBlockBuilder, injectable, graph, injectionContext);

    implementConstructor(bodyBlockBuilder, injectable);
    maybeImplementFactoryInit(bodyBlockBuilder, injectable, factoryInitStatements);
    implementCreateInstance(bodyBlockBuilder, injectable, createInstanceStatements);
    maybeImplementDestroyInstance(bodyBlockBuilder, injectable, destroyInstanceStatements);
    maybeImplementInvokePostConstructs(bodyBlockBuilder, injectable, invokePostConstructStatements);
    maybeImplementCreateProxy(bodyBlockBuilder, injectable);

    addPrivateAccessors(bodyBlockBuilder);
  }

  private void addPrivateAccessors(final ClassStructureBuilder<?> bodyBlockBuilder) {
    for (final MetaField field : controller.getExposedFields()) {
      addPrivateAccessStubs("jsni", bodyBlockBuilder, field);
    }
    for (final MetaMethod method : controller.getExposedMethods()) {
      addPrivateAccessStubs("jsni", bodyBlockBuilder, method);
    }
    for (final MetaConstructor constructor : controller.getExposedConstructors()) {
      addPrivateAccessStubs("jsni", bodyBlockBuilder, constructor);
    }
  }

  protected void preGenerationHook(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable,
          final DependencyGraph graph, final InjectionContext injectionContext) {
  }

  /**
   * @param bodyBlockBuilder
   *          The {@link ClassStructureBuilder} for the {@link Factory} being
   *          generated.
   * @param injectable
   *          Contains metadata (including dependencies) or the bean that the
   *          generated factory will produce.
   * @param graph
   *          The dependency graph that the {@link Injectable} parameter is
   *          from.
   * @param injectionContext
   *          The single injection context shared between all
   *          {@link FactoryBodyGenerator FactoryBodyGenerators}.
   * @return A list of statements that will generated in the
   *         {@link Factory#init(Context)} method.
   */
  protected List<Statement> generateFactoryInitStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    return Collections.emptyList();
  }

  /**
   * @param bodyBlockBuilder
   *          The {@link ClassStructureBuilder} for the {@link Factory} being
   *          generated.
   * @param injectable
   *          Contains metadata (including dependencies) or the bean that the
   *          generated factory will produce.
   * @param graph
   *          The dependency graph that the {@link Injectable} parameter is
   *          from.
   * @param injectionContext
   *          The single injection context shared between all
   *          {@link FactoryBodyGenerator FactoryBodyGenerators}.
   * @return A list of statements that will generated in the
   *         {@link Factory#invokePostConstructs(Object)} method.
   */
  protected List<Statement> generateInvokePostConstructsStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    return Collections.emptyList();
  }

  private void maybeImplementFactoryInit(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable,
          final List<Statement> factoryInitStatements) {
    if (!factoryInitStatements.isEmpty()) {
      bodyBlockBuilder.publicMethod(void.class, "init", finalOf(Context.class, "context")).appendAll(factoryInitStatements).finish();
    }
  }

  private void maybeImplementInvokePostConstructs(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final List<Statement> invokePostConstructStatements) {
    if (!invokePostConstructStatements.isEmpty()) {
      bodyBlockBuilder
      .publicMethod(MetaClassFactory.get(void.class), "invokePostConstructs", finalOf(injectable.getInjectedType(), "instance"))
      .appendAll(invokePostConstructStatements).finish();
    }
  }

  private void maybeImplementDestroyInstance(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable,
          final List<Statement> destroyInstanceStatements) {
    if (!destroyInstanceStatements.isEmpty()) {
      bodyBlockBuilder
      .publicMethod(void.class, "generatedDestroyInstance", finalOf(Object.class, "instance"),
              finalOf(ContextManager.class, "contextManager"))
      .append(loadVariable("this").invoke("destroyInstanceHelper",
              Stmt.castTo(injectable.getInjectedType(), loadVariable("instance")),
              loadVariable("contextManager")))
      .finish();
      bodyBlockBuilder.publicMethod(void.class, "destroyInstanceHelper",
              finalOf(injectable.getInjectedType(), "instance"), finalOf(ContextManager.class, "contextManager"))
      .appendAll(destroyInstanceStatements).finish();
    }
  }

  /**
   * @param bodyBlockBuilder
   *          The {@link ClassStructureBuilder} for the {@link Factory} being
   *          generated.
   * @param injectable
   *          Contains metadata (including dependencies) or the bean that the
   *          generated factory will produce.
   * @param graph
   *          The dependency graph that the {@link Injectable} parameter is
   *          from.
   * @param injectionContext
   *          The single injection context shared between all
   *          {@link FactoryBodyGenerator FactoryBodyGenerators}.
   * @return A list of statements that will generated in the
   *         {@link Factory#destroyInstance(Object, ContextManager)} method.
   */
  protected List<Statement> generateDestroyInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    return controller.getDestructionStatements();
  }

  protected void implementConstructor(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable) {
    final Statement newObject = generateFactoryHandleStatement(injectable);
    final ConstructorBlockBuilder<?> con = bodyBlockBuilder.publicConstructor();
    con.callSuper(newObject);
    con.append(loadVariable("handle").invoke("setAssignableTypes", getAssignableTypesArrayStmt(injectable)));

    final org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier qualifier = injectable.getQualifier();
    if (!qualifier.isDefaultQualifier()) {
      final AbstractStatementBuilder qualArray =
              getAnnotationArrayStmt(qualifier);
      con.append(loadVariable("handle").invoke("setQualifiers", qualArray));
    }
    con.finish();
  }

  public static AbstractStatementBuilder getAssignableTypesArrayStmt(final Injectable injectable) {
    final Object[] assignableTypes =
            injectable.getAnnotatedObject()
            .flatMap(annotated -> Optional.ofNullable(annotated.getAnnotation(Typed.class)))
            .map(typedAnno -> typedAnno.value())
            // Ensure that Object is an assignable type
            .map(beanTypes -> {
              if (Arrays.stream(beanTypes).anyMatch(type -> Object.class.equals(type))) {
                return (Object[]) beanTypes;
              }
              else {
                final Class<?>[] copyWithObject = Arrays.copyOf(beanTypes, beanTypes.length+1);
                copyWithObject[beanTypes.length] = Object.class;
                return (Object[]) copyWithObject;
              }
            })
            .orElseGet(() -> getAllAssignableTypes(injectable.getInjectedType()).stream().filter(MetaClass::isPublic).toArray());

    return newArray(Class.class).initialize(assignableTypes);
  }

  public static AbstractStatementBuilder getAnnotationArrayStmt(final org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier qualifier) {
    return newArray(Annotation.class).initialize(qualifier.stream().map(AbstractBodyGenerator::annotationLiteral).toArray());
  }

  protected Statement generateFactoryHandleStatement(final Injectable injectable) {
    final Statement newObject;
    if (injectable.getInjectedType().isAnnotationPresent(ActivatedBy.class)) {
      final Class<? extends BeanActivator> activatorType = injectable.getInjectedType().getAnnotation(ActivatedBy.class).value();
      newObject = newObject(FactoryHandleImpl.class, loadLiteral(injectable.getInjectedType()),
              injectable.getFactoryName(), injectable.getScope(), isEager(injectable.getInjectedType()),
              injectable.getBeanName(), !injectable.isContextual(), loadLiteral(activatorType));
    } else {
      newObject = newObject(FactoryHandleImpl.class, loadLiteral(injectable.getInjectedType()),
              injectable.getFactoryName(), injectable.getScope(), isEager(injectable.getInjectedType()),
              injectable.getBeanName(), !injectable.isContextual());
    }
    return newObject;
  }

  protected static Object isEager(final MetaClass injectedType) {
    return injectedType.isAnnotationPresent(EntryPoint.class) ||
            // TODO review this before adding any scopes other than app-scoped and depdendent
            (!injectedType.isAnnotationPresent(Dependent.class) && hasStartupAnnotation(injectedType));
  }

  protected static boolean hasStartupAnnotation(final MetaClass injectedType) {
    for (final Annotation anno : injectedType.getAnnotations()) {
      if (anno.annotationType().getName().equals("javax.ejb.Startup")) {
        return true;
      }
    }

    return false;
  }

  public static Statement annotationLiteral(final Annotation qual) {
    if (qual.annotationType().equals(Any.class)) {
      return loadStatic(QualifierUtil.class, "ANY_ANNOTATION");
    } else if (qual.annotationType().equals(Default.class)) {
      return loadStatic(QualifierUtil.class, "DEFAULT_ANNOTATION");
    } else if (qual.annotationType().equals(Named.class)) {
      return invokeStatic(QualifierUtil.class, "createNamed", ((Named) qual).value());
    } else {
      return LiteralFactory.getLiteral(qual);
    }
  }

  protected Collection<Annotation> getQualifiers(final HasAnnotations injectedType) {
    final Collection<Annotation> annos = new ArrayList<>();
    for (final Annotation anno : injectedType.getAnnotations()) {
      if (anno.annotationType().isAnnotationPresent(Qualifier.class)) {
        annos.add(anno);
      }
    }

    return annos;
  }

  protected MetaClass[] getTypeArguments(final MetaClass type) {
    final MetaParameterizedType pType = type.getParameterizedType();
    final MetaType[] typeArgs = (pType != null ? pType.getTypeParameters() : new MetaType[0]);
    final MetaClass[] typeArgsClasses = new MetaClass[typeArgs.length];

    for (int i = 0; i < typeArgs.length; i++) {
      final MetaType argType = typeArgs[i];

      if (argType instanceof MetaClass) {
        typeArgsClasses[i] = (MetaClass) argType;
      }
      else if (argType instanceof MetaParameterizedType) {
        typeArgsClasses[i] = (MetaClass) ((MetaParameterizedType) argType).getRawType();
      }
    }
    return typeArgsClasses;
  }

  public static Collection<MetaClass> getAllAssignableTypes(final MetaClass injectedType) {
    return injectedType.getAllSuperTypesAndInterfaces();
  }

}
