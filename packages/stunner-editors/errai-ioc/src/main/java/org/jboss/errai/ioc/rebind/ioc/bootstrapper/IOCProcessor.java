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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsType;
import org.jboss.errai.codegen.ArithmeticExpression;
import org.jboss.errai.codegen.ArithmeticOperator;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.impl.ArithmeticExpressionBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.AnnotationSerializer;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.config.rebind.EnvUtil;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.ioc.client.Bootstrapper;
import org.jboss.errai.ioc.client.JsArray;
import org.jboss.errai.ioc.client.WindowInjectionContext;
import org.jboss.errai.ioc.client.WindowInjectionContextStorage;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.api.LoadAsync;
import org.jboss.errai.ioc.client.api.ScopeContext;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.ContextManagerImpl;
import org.jboss.errai.ioc.client.container.DependentScopeContext;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.JsTypeProvider;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManagerSetup;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManagerSetup.FactoryLoader;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManagerSetup.FactoryLoaderCallback;
import org.jboss.errai.ioc.client.container.async.DefaultRunAsyncCallback;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ReachabilityStrategy;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;
import org.jboss.errai.ioc.rebind.ioc.graph.api.QualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DependencyGraphBuilderImpl;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.injector.api.ExtensionTypeCallback;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.Parameter.finalOf;
import static org.jboss.errai.codegen.builder.impl.ObjectBuilder.newInstanceOf;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.declareVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator.getAnnotationArrayStmt;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator.getAssignableTypesArrayStmt;

/**
 * Creates {@link DependencyGraph} by adding all types and dependencies to the
 * {@link DependencyGraphBuilder}. Generates {@link Factory} subclasses and
 * {@link GWT#create(Class)} calls for every {@link Injectable} in the
 * dependency graph.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class IOCProcessor {

  private static final Logger log = LoggerFactory.getLogger(IOCProcessor.class);

  public static final Predicate<List<InjectableHandle>> ANY = handle -> true;
  public static final Predicate<List<InjectableHandle>> EXACT_TYPE = IOCProcessor::exactTypePredicate;

  public static final String REACHABILITY_PROPERTY = "errai.ioc.reachability";
  public static final String PLUGIN_PROPERTY = "errai.ioc.jsinterop.support";

  public static boolean isJsInteropSupportEnabled() {
    return Boolean.getBoolean(PLUGIN_PROPERTY);
  }

  private final Set<Class<? extends Annotation>> nonSimpletonTypeAnnotations = new HashSet<>();

  private final InjectionContext injectionContext;
  private final QualifierFactory qualFactory;
  private Collection<String> alternatives;

  public IOCProcessor(final InjectionContext injectionContext) {
    this.injectionContext = injectionContext;
    this.qualFactory = injectionContext.getQualifierFactory();

    nonSimpletonTypeAnnotations.add(IOCProvider.class);
    nonSimpletonTypeAnnotations.add(Specializes.class);
    nonSimpletonTypeAnnotations.add(LoadAsync.class);
    nonSimpletonTypeAnnotations.add(EnabledByProperty.class);
    nonSimpletonTypeAnnotations.add(Typed.class);
    nonSimpletonTypeAnnotations.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.DependentBean));
    nonSimpletonTypeAnnotations.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.PseudoScopedBean));
    nonSimpletonTypeAnnotations.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.NormalScopedBean));
    nonSimpletonTypeAnnotations.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.AlternativeBean));
  }

  /**
   * Process the dependency graph and generate code in BoostrapperImpl to declare and create factories for all {@link Injectable injectables}.
   *
   * @param processingContext
   */
  public void process(final IOCProcessingContext processingContext) {
    long start = System.currentTimeMillis();

    final Collection<MetaClass> allMetaClasses = findRelevantClasses(processingContext);
    log.debug("Found {} classes", allMetaClasses.size());
    final DependencyGraphBuilder graphBuilder = new DependencyGraphBuilderImpl(qualFactory, injectionContext.isAsync());

    runExtensionCallbacks(allMetaClasses);
    log.debug("Ran {} extension callbacks on all types {} types.", injectionContext.getExtensionTypeCallbacks().size(), allMetaClasses.size());

    addAllInjectableProviders(graphBuilder);
    processDependencies(allMetaClasses, graphBuilder);
    log.debug("Added {} classes to dependency graph in {}ms", allMetaClasses.size(), System.currentTimeMillis() - start);

    start = System.currentTimeMillis();
    final DependencyGraph dependencyGraph = graphBuilder.createGraph(getReachabilityStrategy());
    log.debug("Resolved dependency graph with {} reachable injectables in {}ms", dependencyGraph.getNumberOfInjectables(), System.currentTimeMillis() - start);

    FactoryGenerator.resetTotalTime();
    FactoryGenerator.setDependencyGraph(dependencyGraph);
    FactoryGenerator.setInjectionContext(injectionContext);

    start = System.currentTimeMillis();

    final Map<Class<? extends Annotation>, MetaClass> scopeContexts = findScopeContexts(processingContext);
    final Set<MetaClass> scopeContextSet = new LinkedHashSet<>(scopeContexts.values());
    final Statement[] contextLocalVarInvocation = contextLocalVarInvocation(scopeContextSet);

    @SuppressWarnings("rawtypes")
    final BlockBuilder registerFactoriesBody = createRegisterFactoriesMethod(processingContext, scopeContextSet);

    declareAndRegisterFactories(processingContext, dependencyGraph, scopeContexts, scopeContextSet, registerFactoriesBody);
    final String contextManagerFieldName = declareContextManagerField(processingContext);
    if (isJsInteropSupportEnabled()) {
      declareWindowInjectionContextField(processingContext);
    }
    declareStaticLogger(processingContext);
    if (injectionContext.isAsync()) {
      declareAsyncBeanManagerSetupField(processingContext);
    }

    registerFactoriesBody.finish();
    bootstrapContainer(processingContext, dependencyGraph, scopeContextSet, contextLocalVarInvocation, contextManagerFieldName);
    log.debug("Processed factory GWT.create calls in {}ms", System.currentTimeMillis() - start);
  }

  private ReachabilityStrategy getReachabilityStrategy() {
    final String reachabilityStrategyName = System.getProperty(REACHABILITY_PROPERTY, ReachabilityStrategy.Annotated.name());
    log.info("Reachability strategy set to " + reachabilityStrategyName);
    try {
      return ReachabilityStrategy.valueOf(reachabilityStrategyName);
    } catch (final IllegalArgumentException iae) {
      throw new RuntimeException("Unrecognized reachability strategy, " + reachabilityStrategyName
              + ". Please use one of the following: " + Arrays.toString(ReachabilityStrategy.values()), iae);
    }
  }

  private void bootstrapContainer(final IOCProcessingContext processingContext, final DependencyGraph dependencyGraph,
          final Set<MetaClass> scopeContextSet, final Statement[] contextLocalVarInvocation,
          final String contextManagerFieldName) {
    processingContext.getBlockBuilder()
      .appendAll(contextLocalVarDeclarations(scopeContextSet))
      .append(loadVariable("logger").invoke("debug", "Registering factories with contexts."))
      .append(declareVariable("start", long.class, currentTime()))
      .append(loadVariable("this").invoke("registerFactories", (Object[]) contextLocalVarInvocation))
      .append(loadVariable("logger").invoke("debug",
              "Registered " + dependencyGraph.getNumberOfInjectables() + " factories in {}ms", subtractFromCurrentTime(loadVariable("start"))))
      .append(loadVariable("logger").invoke("debug", "Adding contexts to context manager..."))
      .append(loadVariable("start").assignValue(currentTime()));
    addContextsToContextManager(scopeContextSet, contextManagerFieldName, processingContext.getBlockBuilder());
    processingContext.getBlockBuilder()
      .append(loadVariable("logger").invoke("debug",
            "Added " + scopeContextSet.size() + " contexts in {}ms", subtractFromCurrentTime(loadVariable("start"))))
      .append(loadVariable("logger").invoke("debug", "Calling finishInit on " + ContextManager.class.getSimpleName()))
      .append(loadVariable("start").assignValue(currentTime()));
    callFinishInitOnContextManager(contextManagerFieldName, processingContext.getBlockBuilder());
    processingContext.getBlockBuilder()
      .append(loadVariable("logger").invoke("debug",
            "ContextManager#finishInit ran in {}ms", subtractFromCurrentTime(loadVariable("start"))));
  }

  private static ContextualStatementBuilder currentTime() {
    return invokeStatic(System.class, "currentTimeMillis");
  }

  private static ArithmeticExpression subtractFromCurrentTime(final Statement toSubtract) {
    return ArithmeticExpressionBuilder.create(currentTime(),
            ArithmeticOperator.Subtraction, toSubtract);
  }

  private void runExtensionCallbacks(final Collection<MetaClass> allMetaClasses) {
    final Collection<ExtensionTypeCallback> extensionCallbacks = injectionContext.getExtensionTypeCallbacks();
    extensionCallbacks.forEach(cb -> cb.init());
    allMetaClasses.forEach(mc -> extensionCallbacks.forEach(cb -> cb.callback(mc)));
    extensionCallbacks.forEach(cb -> cb.finish());
  }

  private void callFinishInitOnContextManager(final String contextManagerFieldName, final BlockBuilder<?> methodBody) {
    methodBody.append(loadVariable(contextManagerFieldName).invoke("finishInit"));
  }

  private void addAllInjectableProviders(final DependencyGraphBuilder graphBuilder) {
    for (final Entry<InjectableHandle, InjectableProvider> entry : injectionContext.getInjectableProviders().entries()) {
      graphBuilder.addExtensionInjectable(entry.getKey().getType(), entry.getKey().getQualifier(), ANY, entry.getValue());
    }
    for (final Entry<InjectableHandle, InjectableProvider> entry : injectionContext.getExactTypeInjectableProviders().entries()) {
      graphBuilder.addExtensionInjectable(entry.getKey().getType(), entry.getKey().getQualifier(), EXACT_TYPE, entry.getValue());
    }
  }

  private void addContextsToContextManager(final Collection<MetaClass> scopeContextImpls,
          final String contextManagerFieldName, @SuppressWarnings("rawtypes") final BlockBuilder methodBody) {
    for (final MetaClass scopeContextImpl : scopeContextImpls) {
      methodBody.append(loadVariable(contextManagerFieldName).invoke("addContext", loadVariable(getContextVarName(scopeContextImpl))));
    }
  }

  @SuppressWarnings("unchecked")
  private String declareContextManagerField(final IOCProcessingContext processingContext) {
    final String contextManagerFieldName = "contextManager";
    processingContext.getBootstrapBuilder()
      .privateField(contextManagerFieldName, ContextManager.class)
      .initializesWith(ObjectBuilder.newInstanceOf(ContextManagerImpl.class))
      .finish();

    return contextManagerFieldName;
  }

  @SuppressWarnings("unchecked")
  private void declareStaticLogger(final IOCProcessingContext processingContext) {
    processingContext.getBootstrapBuilder()
      .privateField("logger", Logger.class)
      .modifiers(Modifier.Static, Modifier.Final)
      .initializesWith(invokeStatic(LoggerFactory.class, "getLogger", Bootstrapper.class))
      .finish();
  }

  @SuppressWarnings("unchecked")
  private void declareAsyncBeanManagerSetupField(final IOCProcessingContext processingContext) {
    processingContext.getBootstrapBuilder()
      .privateField("asyncBeanManagerSetup", AsyncBeanManagerSetup.class)
      .initializesWith(castTo(AsyncBeanManagerSetup.class, invokeStatic(IOC.class, "getAsyncBeanManager")))
      .finish();
  }

  @SuppressWarnings("unchecked")
  private void declareWindowInjectionContextField(final IOCProcessingContext processingContext) {
    processingContext.getBootstrapBuilder().privateField("windowContext", WindowInjectionContext.class)
            .modifiers(Modifier.Final).initializesWith(Stmt.invokeStatic(WindowInjectionContextStorage.class, "createOrGet"))
            .finish();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void declareAndRegisterFactories(final IOCProcessingContext processingContext,
          final DependencyGraph dependencyGraph, final Map<Class<? extends Annotation>, MetaClass> scopeContexts,
          final Collection<MetaClass> orderedScopeContexts, final BlockBuilder registerFactoriesBody) {
    final Parameter[] contextParamsDeclaration = contextParamsDeclaration(orderedScopeContexts);
    final Statement[] contextLocalVarInvocation = contextLocalVarInvocation(orderedScopeContexts);
    int methodNumber = 0;
    int registeredInThisMethod = 0;
    BlockBuilder curMethod = null;
    for (final Injectable injectable : dependencyGraph) {
      if (registeredInThisMethod % 500 == 0) {
        if (curMethod != null) {
          curMethod.finish();
          registerFactoriesBody.append(loadVariable("this").invoke("registerFactories" + methodNumber, (Object[]) contextLocalVarInvocation));
          methodNumber++;
          registeredInThisMethod = 0;
        }
        curMethod = processingContext.getBootstrapBuilder().privateMethod(void.class, "registerFactories" + methodNumber, contextParamsDeclaration).body();
      }
      declareAndProcessInjectable(processingContext, scopeContexts, curMethod, injectable);
      registeredInThisMethod++;
    }
    if (curMethod != null) {
      curMethod.finish();
      registerFactoriesBody.append(loadVariable("this").invoke("registerFactories" + methodNumber, (Object[]) contextLocalVarInvocation));
    }
  }

  private void declareAndProcessInjectable(final IOCProcessingContext processingContext,
          final Map<Class<? extends Annotation>, MetaClass> scopeContexts,
          @SuppressWarnings("rawtypes") final BlockBuilder curMethod, final Injectable injectable) {
    if (injectionContext.isAsync() && injectable.loadAsync()) {
      final MetaClass factoryClass = addFactoryDeclaration(injectable, processingContext);
      registerAsyncFactory(injectable, processingContext, curMethod, factoryClass);
    } else {
      declareAndRegisterConcreteInjectable(injectable, processingContext, scopeContexts, curMethod);
    }
  }

  private void registerAsyncFactory(final Injectable injectable, final IOCProcessingContext processingContext,
          @SuppressWarnings("rawtypes") final BlockBuilder curMethod, final MetaClass factoryClass) {
    final Statement handle = generateFactoryHandle(injectable, curMethod);
    final Statement loader = generateFactoryLoader(injectable, factoryClass);
    curMethod.append(loadVariable("asyncBeanManagerSetup").invoke("registerAsyncBean", handle, loader));
    for (final Dependency dep : injectable.getDependencies()) {
      if (dep.getInjectable().loadAsync()) {
        curMethod.append(loadVariable("asyncBeanManagerSetup").invoke("registerAsyncDependency", injectable.getFactoryName(),
                dep.getInjectable().getFactoryName()));
      }
    }
  }

  private Statement generateFactoryLoader(final Injectable injectable, final MetaClass factoryClass) {
    final Statement runAsyncCallback = ObjectBuilder.newInstanceOf(DefaultRunAsyncCallback.class).extend()
            .publicOverridesMethod("onSuccess").append(loadVariable("callback").invoke("callback",
                    castTo(Factory.class, invokeStatic(GWT.class, "create", loadLiteral(factoryClass)))))
            .finish().finish();
    final Class<?> fragmentId = getAsyncFragmentId(injectable);
    final Object[] runAsyncParams = (fragmentId.equals(LoadAsync.NO_FRAGMENT.class) ? new Object[] { runAsyncCallback }
            : new Object[] { fragmentId, runAsyncCallback });

    return ObjectBuilder.newInstanceOf(FactoryLoader.class).extend()
            .publicOverridesMethod("call", finalOf(FactoryLoaderCallback.class, "callback"))
            .append(invokeStatic(GWT.class, "runAsync", runAsyncParams)).finish().finish();
  }

  private Class<?> getAsyncFragmentId(final Injectable injectable) {
    final LoadAsync loadAsync = injectable.getInjectedType().getAnnotation(LoadAsync.class);
    if (loadAsync == null) {
      return LoadAsync.NO_FRAGMENT.class;
    } else {
      return loadAsync.value();
    }
  }

  private Statement generateFactoryHandle(final Injectable injectable,
          @SuppressWarnings("rawtypes") final BlockBuilder curMethod) {
    final String handleVarName = "handleFor" + injectable.getFactoryName();
    curMethod.append(declareFinalVariable(handleVarName, FactoryHandleImpl.class, ObjectBuilder.newInstanceOf(FactoryHandleImpl.class)
                         .withParameters(loadLiteral(injectable.getInjectedType()),
                                         loadLiteral(injectable.getFactoryName()),
                                         loadLiteral(injectable.getScope()),
                                         loadLiteral(false),
                                         loadLiteral(injectable.getBeanName()),
                                         loadLiteral(!injectable.isContextual()))));
    curMethod.append(loadVariable(handleVarName).invoke("setAssignableTypes", getAssignableTypesArrayStmt(injectable)));

    if (!injectable.getQualifier().isDefaultQualifier()) {
      curMethod.append(loadVariable(handleVarName).invoke("setQualifiers", getAnnotationArrayStmt(injectable.getQualifier())));
    }

    return loadVariable(handleVarName);
  }

  @SuppressWarnings("unchecked")
  private void declareAndRegisterConcreteInjectable(final Injectable injectable,
          final IOCProcessingContext processingContext, final Map<Class<? extends Annotation>, MetaClass> scopeContexts,
          @SuppressWarnings("rawtypes") final BlockBuilder registerFactoriesBody) {
    final MetaClass factoryClass = addFactoryDeclaration(injectable, processingContext);
    registerFactoryWithContext(injectable, factoryClass, scopeContexts, registerFactoriesBody);
    final boolean windowScoped = injectable.getWiringElementTypes().contains(WiringElementType.SharedSingleton);
    final boolean jsType = injectable.getWiringElementTypes().contains(WiringElementType.JsType);
    final boolean jsinteropSupportEnabled = isJsInteropSupportEnabled();
    if (jsinteropSupportEnabled && (jsType || windowScoped)) {
      final List<Statement> stmts = new ArrayList<>();
      stmts.add(loadVariable("windowContext").invoke("addBeanProvider",
              injectable.getInjectedType().getFullyQualifiedName(), createJsTypeProviderFor(injectable)));
      for (final MetaClass mc : injectable.getInjectedType().getAllSuperTypesAndInterfaces()) {
        if (mc.isPublic() && !mc.equals(injectable.getInjectedType())
                && !mc.getFullyQualifiedName().equals("java.lang.Object") && mc.isAnnotationPresent(JsType.class)) {
          stmts.add(loadVariable("windowContext").invoke("addSuperTypeAlias",
                  mc.getFullyQualifiedName(), injectable.getInjectedType().getFullyQualifiedName()));
        }
      }

      if (windowScoped) {
        registerFactoriesBody
                .append(If
                        .cond(Bool.expr(loadVariable("windowContext").invoke("hasProvider",
                                injectable.getInjectedType().getFullyQualifiedName())).negate())
                        .appendAll(stmts).finish());
      }
      else {
        registerFactoriesBody.appendAll(stmts);
      }
    }
  }

  private Statement createJsTypeProviderFor(final Injectable injectable) {
    final MetaClass type = injectable.getInjectedType();
    final AnonymousClassStructureBuilder jsTypeProvider = newInstanceOf(parameterizedAs(JsTypeProvider.class, typeParametersOf(type))).extend();
    jsTypeProvider
      .publicOverridesMethod("getInstance")
            .append(Stmt.castTo(type, loadVariable("contextManager").invoke("getInstance", injectable.getFactoryName()))
                    .returnValue()).finish()
      .publicOverridesMethod("getName")
            .append(Stmt.loadLiteral(getBeanName(injectable)).returnValue())
            .finish()
      .publicOverridesMethod("getFactoryName")
            .append(Stmt.loadLiteral(injectable.getFactoryName()).returnValue())
            .finish()
      .publicOverridesMethod("getQualifiers")
            .append(Stmt.nestedCall(Stmt.newObject(parameterizedAs(JsArray.class,  typeParametersOf(String.class)),
                    Stmt.loadLiteral(AnnotationSerializer.serialize(injectable.getQualifier().iterator())))).returnValue())
            .finish();

    return jsTypeProvider.finish();
  }

  private String getBeanName(final Injectable injectable) {
    final Named named = injectable.getInjectedType().getAnnotation(Named.class);
    return (named != null) ? named.value() : null;
  }

  @SuppressWarnings("rawtypes")
  private BlockBuilder createRegisterFactoriesMethod(final IOCProcessingContext processingContext,
          final Collection<MetaClass> scopeContexts) {
    final Parameter[] contextParams = contextParamsDeclaration(scopeContexts);

    @SuppressWarnings({ "unchecked" })
    final BlockBuilder methodBody = processingContext.getBootstrapBuilder().privateMethod(void.class, "registerFactories", contextParams).body();

    return methodBody;
  }

  private Parameter[] contextParamsDeclaration(final Collection<MetaClass> scopeContexts) {
    final Parameter[] contextParams = new Parameter[scopeContexts.size()];
    final Iterator<MetaClass> iter = scopeContexts.iterator();
    int i = 0;
    while (iter.hasNext()) {
      final MetaClass scopeContextImpl = iter.next();
      contextParams[i++] = finalOf(Context.class, getContextVarName(scopeContextImpl));
    }
    return contextParams;
  }

  private Statement[] contextLocalVarInvocation(final Collection<MetaClass> scopeContexts) {
    final Statement[] vars = new Statement[scopeContexts.size()];
    final Iterator<MetaClass> iter = scopeContexts.iterator();
    int i = 0;
    while (iter.hasNext()) {
      vars[i++] = loadVariable(getContextVarName(iter.next()));
    }

    return vars;
  }

  private List<Statement> contextLocalVarDeclarations(final Collection<MetaClass> scopeContextTypes) {
    final List<Statement> declarations = new ArrayList<>();
    for (final MetaClass scopeContextImpl : scopeContextTypes) {
      if (!scopeContextImpl.isDefaultInstantiable()) {
        throw new RuntimeException(
                "The @ScopeContext " + scopeContextImpl.getName() + " must have a public, no-args constructor.");
      }

      declarations.add(declareFinalVariable(getContextVarName(scopeContextImpl), Context.class,
              newInstanceOf(scopeContextImpl)));
    }

    return declarations;
  }

  private void registerFactoryWithContext(final Injectable injectable, final MetaClass factoryClass,
          final Map<Class<? extends Annotation>, MetaClass> scopeContexts,
          @SuppressWarnings("rawtypes") final BlockBuilder registerFactoriesBody) {
    final Class<? extends Annotation> scope = injectable.getScope();
    final MetaClass injectedType = injectable.getInjectedType();
    final MetaClass scopeContextImpl = Assert.notNull("No scope context for " + scope.getSimpleName(), scopeContexts.get(scope));
    final String contextVarName = getContextVarName(scopeContextImpl);
    registerFactoriesBody.append(loadVariable(contextVarName).invoke("registerFactory",
            Stmt.castTo(parameterizedAs(Factory.class, typeParametersOf(injectedType)),
                    invokeStatic(GWT.class, "create", factoryClass))));
  }

  private String getContextVarName(final MetaClass scopeContextImpl) {
    return scopeContextImpl.getFullyQualifiedName().replace('.', '_') + "_context";
  }

  private Map<Class<? extends Annotation>, MetaClass> findScopeContexts(final IOCProcessingContext processingContext) {
    final Collection<MetaClass> scopeContexts = ClassScanner.getTypesAnnotatedWith(ScopeContext.class);
    final Map<Class<? extends Annotation>, MetaClass> annoToContextImpl = new HashMap<>();
    for (final MetaClass scopeContext : scopeContexts) {
      if (!scopeContext.isAssignableTo(Context.class)) {
        throw new RuntimeException("They type " + scopeContext.getFullyQualifiedName()
                + " was annotated with @ScopeContext but does not implement " + Context.class.getName());
      }
      final ScopeContext anno = scopeContext.getAnnotation(ScopeContext.class);
      for (final Class<? extends Annotation> scope : anno.value()) {
        annoToContextImpl.put(scope, scopeContext);
      }
    }
    final MetaClass depContextImpl = MetaClassFactory.get(DependentScopeContext.class);
    for (final Class<? extends Annotation> customAnno : injectionContext.getAnnotationsForElementType(WiringElementType.DependentBean)) {
      annoToContextImpl.put(customAnno, depContextImpl);
    }

    return annoToContextImpl;
  }

  private Collection<MetaClass> findRelevantClasses(final IOCProcessingContext processingContext) {
    final Collection<MetaClass> allMetaClasses = new HashSet<>();
    allMetaClasses.addAll(MetaClassFactory.getAllCachedClasses());
    allMetaClasses.remove(MetaClassFactory.get(Object.class));

    return allMetaClasses;
  }

  private MetaClass addFactoryDeclaration(final Injectable injectable, final IOCProcessingContext processingContext) {
    final String factoryName = injectable.getFactoryName();
    final MetaClass typeCreatedByFactory = injectable.getInjectedType();
    return addFactoryDeclaration(factoryName, typeCreatedByFactory, processingContext);
  }

  private MetaClass addFactoryDeclaration(final String factoryName, final MetaClass typeCreatedByFactory,
          final IOCProcessingContext processingContext) {
    final ClassStructureBuilder<?> builder = processingContext.getBootstrapBuilder();
    final BuildMetaClass factory = ClassBuilder
            .define(factoryName,
                    parameterizedAs(Factory.class, typeParametersOf(typeCreatedByFactory)))
            .publicScope().abstractClass().body().getClassDefinition();
    builder.declaresInnerClass(new InnerClass(factory));

    return factory;
  }

  private void processDependencies(final Collection<MetaClass> types, final DependencyGraphBuilder builder) {
    final List<String> problems = new ArrayList<>();
    for (final MetaClass type : types) {
      processType(type, builder, problems);
    }

    if (!problems.isEmpty()) {
      throw new RuntimeException(buildProblemsMessage(problems));
    }
  }

  private String buildProblemsMessage(final List<String> problems) {
    final StringBuilder builder = new StringBuilder();
    builder.append("The following problems were found:\n");
    for (final String problem : problems) {
      builder.append('\t')
             .append(problem)
             .append('\n');
    }

    return builder.toString();
  }

  private void processType(final MetaClass type, final DependencyGraphBuilder builder, final List<String> problems) {
    try {
      if (isTypeAccessible(type)) {
        if (type.isConcrete()) {
          final boolean enabled;
          if (isSimpleton(type)) {
            builder.addInjectable(type, qualFactory.forSource(type), ANY, Dependent.class, InjectableType.Type,
                    WiringElementType.DependentBean, WiringElementType.Simpleton);
            maybeProcessAsStaticOnlyProducer(builder, type, problems);
          }
          else if ((enabled = isEnabled(type)) && isConstructable(type, problems)) {
            final Class<? extends Annotation> directScope = getScope(type);
            final Injectable typeInjectable = builder.addInjectable(type, qualFactory.forSource(type), getPathPredicate(type, problems),
                    directScope, InjectableType.Type, getWiringTypes(type, directScope));
            processInjectionPoints(typeInjectable, builder, problems);
            maybeProcessAsProducer(builder, type, typeInjectable, true, problems);
            maybeProcessAsProvider(typeInjectable, builder, true);
          }
          else if (!enabled) {
            final Class<? extends Annotation> directScope = getScope(type);
            final Injectable typeInjectable = builder.addInjectable(type, qualFactory.forSource(type), getPathPredicate(type, problems),
                    directScope, InjectableType.Disabled, getWiringTypes(type, directScope));
            maybeProcessAsProducer(builder, type, typeInjectable, false, problems);
            maybeProcessAsProvider(typeInjectable, builder, false);
          }
          else {
            maybeProcessAsStaticOnlyProducer(builder, type, problems);
          }
        }
        else {
          maybeProcessAsStaticOnlyProducer(builder, type, problems);
        }
        if (isPublishableJsType(type)) {
          final WiringElementType scopeWiringType = (type.isAnnotationPresent(SharedSingleton.class)
                  ? WiringElementType.SharedSingleton : WiringElementType.DependentBean);
          builder.addInjectable(type, qualFactory.forUniversallyQualified(), ANY, Dependent.class, InjectableType.JsType, scopeWiringType);
        }
      }
    } catch (final Throwable t) {
      throw new RuntimeException("A fatal error occurred while processing " + type.getFullyQualifiedName(), t);
    }
  }

  private Predicate<List<InjectableHandle>> getPathPredicate(final HasAnnotations annotated, final List<String> problems) {
    if (annotated.isAnnotationPresent(Typed.class)) {
      final Class<?>[] beanTypes = annotated.getAnnotation(Typed.class).value();
      validateAssignableTypes(annotated, beanTypes, problems);
      return path -> Object.class.getName().equals(path.get(0)) || Arrays.stream(beanTypes)
              .anyMatch(beanType -> path.get(0).getType().getFullyQualifiedName().equals(beanType.getName()));
    }
    else {
      return ANY;
    }
  }

  private void validateAssignableTypes(final HasAnnotations annotated, final Class<?>[] beanTypes, final List<String> problems) {
    MetaClass actualRawType;
    if (annotated instanceof MetaClass) {
      actualRawType = ((MetaClass) annotated).getErased();
    }
    else if (annotated instanceof MetaField) {
      actualRawType = ((MetaField) annotated).getType().getErased();
    }
    else if (annotated instanceof MetaMethod) {
      actualRawType = ((MetaMethod) annotated).getReturnType().getErased();
    }
    else {
      throw new IllegalArgumentException("Unrecognized element kind annotated with @Typed: " + annotated);
    }

    final Set<String> assignableTypeNames =
            actualRawType
            .getAllSuperTypesAndInterfaces()
            .stream()
            .map(type -> type.getFullyQualifiedName())
            .collect(Collectors.toSet());

    final Optional<String> unassignableTypes =
            Arrays
            .stream(beanTypes)
            .map(Class::getName)
            .filter(name -> !assignableTypeNames.contains(name))
            .reduce((s1, s2) -> s1 + "\n" + s2);

    unassignableTypes.ifPresent(typeNameString -> problems.add(
            String.format("The @Typed declaration on [%s] contained the following types not assignable to [%s]:\n%s",
                    annotated, actualRawType, typeNameString)));
  }

  private void maybeProcessAsStaticOnlyProducer(final DependencyGraphBuilder builder, final MetaClass type, final List<String> problems) {
    maybeProcessAsProducer(builder, type, null, true, problems);
  }

  private boolean isPublishableJsType(final MetaClass type) {
    final JsType jsType = type.getAnnotation(JsType.class);

    return jsType != null && !jsType.isNative();
  }

  private boolean isTypeAccessible(final MetaClass type) {
    return type.isPublic() && (isTopLevel(type) || (type.isStatic() && isEnclosingTypeAccessible(type)));
  }

  /**
   * @param typeInjectable If null, only static members will be processed.
   */
  private void maybeProcessAsProducer(final DependencyGraphBuilder builder, final MetaClass producerType,
          final Injectable typeInjectable, final boolean enabled, final List<String> problems) {
    // TODO log error/warning for unused @Disposes methods?
    final Collection<MetaMethod> disposesMethods = getAllDisposesMethods(producerType, (typeInjectable == null));
    processProducerMethods(typeInjectable, producerType, builder, disposesMethods, enabled, problems);
    processProducerFields(typeInjectable, producerType, builder, disposesMethods, enabled, problems);
  }

  private boolean isTopLevel(final MetaClass type) {
    boolean isTopLevel;
    // Workaround for http://bugs.java.com/view_bug.do?bug_id=2210448
    try {
      isTopLevel = (type.asClass() == null || type.asClass().getDeclaringClass() == null);
    } catch (final IncompatibleClassChangeError ex) {
      isTopLevel = false;
    }
    return isTopLevel;
  }

  private boolean isEnclosingTypeAccessible(final MetaClass type) {
    boolean hasEnclosingClass = false;
    Class<?> enclosing;
    // Workaround for http://bugs.java.com/view_bug.do?bug_id=2210448
    try {
      enclosing = (type.asClass() == null ? null : type.asClass().getDeclaringClass());
      hasEnclosingClass = true;
    } catch (final IncompatibleClassChangeError ex) {
      enclosing = null;
      hasEnclosingClass = true;
    }

    // Assume that the enclosing class is inaccessible if we can't access it because of an IncomaptibleClassChangeError
    return !hasEnclosingClass || (enclosing != null && isTypeAccessible(MetaClassFactory.get(enclosing)));
  }

  private boolean isSimpleton(final MetaClass type) {
    for (final Annotation anno : type.getAnnotations()) {
      if (nonSimpletonTypeAnnotations.contains(anno.annotationType())
              || isStereotype(anno)
              || isNonNativeJsTypeAnnotation(anno)) {
        return false;
      }
    }

    if (!getInjectableConstructors(type).isEmpty()) {
      return false;
    }

    final Collection<Class<? extends Annotation>> producerAnnos = injectionContext.getAnnotationsForElementType(WiringElementType.ProducerElement);
    for (final Class<? extends Annotation> producerAnnoType : producerAnnos) {
      final List<MetaMethod> producerMethods = type.getMethodsAnnotatedWith(producerAnnoType);
      if (!producerMethods.isEmpty() && producerMethods.stream().anyMatch(method -> !method.isStatic())) {
        return false;
      }
      final List<MetaField> producerFields = type.getFieldsAnnotatedWith(producerAnnoType);
      if (!producerFields.isEmpty() && producerFields.stream().anyMatch(field -> !field.isStatic())) {
        return false;
      }
    }

    if (!type.getMethodsAnnotatedWith(PostConstruct.class).isEmpty()) {
      return false;
    }

    final Collection<Class<? extends Annotation>> injectAnnos = injectionContext.getAnnotationsForElementType(WiringElementType.InjectionPoint);
    for (final Class<? extends Annotation> anno : injectAnnos) {
      if (!type.getFieldsAnnotatedWith(anno).isEmpty()) {
        return false;
      }
    }

    final MetaConstructor noArgConstructor = type.getDeclaredConstructor(new MetaClass[0]);
    return noArgConstructor != null && (noArgConstructor.isPublic() || !isJavaScriptObject(type));
  }

  private boolean isNonNativeJsTypeAnnotation(final Annotation anno) {
    return JsType.class.equals(anno.annotationType()) && !((JsType) anno).isNative();
  }

  private boolean isStereotype(final Annotation anno) {
    return anno.annotationType().isAnnotationPresent(Stereotype.class);
  }

  private WiringElementType[] getWiringTypes(final MetaClass type, final Class<? extends Annotation> directScope) {
    final List<WiringElementType> wiringTypes = new ArrayList<>();
    wiringTypes.addAll(getWiringTypesForScopeAnnotation(directScope));

    if (type.isAnnotationPresent(Alternative.class)) {
      wiringTypes.add(WiringElementType.AlternativeBean);
    }

    if (isPublishableJsType(type)) {
      wiringTypes.add(WiringElementType.JsType);
    }
    if (type.isAnnotationPresent(SharedSingleton.class)) {
      wiringTypes.add(WiringElementType.SharedSingleton);
    }

    if (type.isAnnotationPresent(Specializes.class)) {
      wiringTypes.add(WiringElementType.Specialization);
    }

    if (type.isAnnotationPresent(LoadAsync.class)) {
      wiringTypes.add(WiringElementType.LoadAsync);
    }

    return wiringTypes.toArray(new WiringElementType[wiringTypes.size()]);
  }

  private void maybeProcessAsProvider(final Injectable typeInjectable, final DependencyGraphBuilder builder,
          final boolean enabled) {
    final MetaClass type = typeInjectable.getInjectedType();
    final Collection<Class<? extends Annotation>> providerAnnotations = injectionContext
            .getAnnotationsForElementType(WiringElementType.Provider);
    for (final Class<? extends Annotation> anno : providerAnnotations) {
      if (type.isAnnotationPresent(anno)) {
        if (type.isAssignableTo(Provider.class)) {
          addProviderInjectable(typeInjectable, builder, enabled);
        }
        else if (type.isAssignableTo(ContextualTypeProvider.class)) {
          addContextualProviderInjectable(typeInjectable, builder, enabled);
        }

        break;
      }
    }
  }

  private void addContextualProviderInjectable(final Injectable providerInjectable,
          final DependencyGraphBuilder builder, final boolean enabled) {
    final MetaClass providerImpl = providerInjectable.getInjectedType();
    final MetaMethod providerMethod = providerImpl.getMethod("provide", Class[].class, Annotation[].class);
    // Do not get generic return type for contextual providers
    final MetaClass providedType = providerMethod.getReturnType();
    final InjectableType injectableType = (enabled ? InjectableType.ContextualProvider : InjectableType.Disabled);
    final Injectable providedInjectable = builder.addInjectable(providedType,
            qualFactory.forUniversallyQualified(), EXACT_TYPE, Dependent.class, injectableType,
            WiringElementType.Provider, WiringElementType.DependentBean);
    builder.addProducerMemberDependency(providedInjectable, providerImpl, providerInjectable.getQualifier(), providerMethod);
  }

  private void addProviderInjectable(final Injectable providerImplInjectable, final DependencyGraphBuilder builder,
          final boolean enabled) {
    final MetaClass providerImpl = providerImplInjectable.getInjectedType();
    final MetaMethod providerMethod = providerImpl.getMethod("get", new Class[0]);
    final MetaClass providedType = getMethodReturnType(providerMethod);
    final InjectableType injectableType = (enabled ? InjectableType.Provider : InjectableType.Disabled);
    final Injectable providedInjectable = builder.addInjectable(providedType, qualFactory.forSource(providerMethod),
            EXACT_TYPE, Dependent.class, injectableType, WiringElementType.Provider, WiringElementType.DependentBean);
    builder.addProducerMemberDependency(providedInjectable, providerImplInjectable.getInjectedType(), providerImplInjectable.getQualifier(), providerMethod);
  }

  private Class<? extends Annotation> getScope(final HasAnnotations annotated) {
    final Class<? extends Annotation> foundScope = getDirectScope(annotated);
    return (foundScope != null && !injectionContext.isElementType(WiringElementType.DependentBean, foundScope))
            ? foundScope : Dependent.class;
  }

  private Class<? extends Annotation> getDirectScope(final HasAnnotations annotated) {
    // TODO validate that there's only one scope?
    final Set<Class<? extends Annotation>> scopeAnnoTypes = new HashSet<>();
    scopeAnnoTypes.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.DependentBean));
    scopeAnnoTypes.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.NormalScopedBean));
    scopeAnnoTypes.addAll(injectionContext.getAnnotationsForElementType(WiringElementType.PseudoScopedBean));

    final Predicate<Class<? extends Annotation>> isExplicitScope =
            type -> Arrays.stream(type.getAnnotations())
                          .map(a -> a.annotationType())
                          .filter(aType -> NormalScope.class.equals(aType) || Scope.class.equals(aType))
                          .findAny()
                          .isPresent();

    final PriorityQueue<Class<? extends Annotation>> pq = new PriorityQueue<>((o1, o2) -> {
      final int score1, score2;
      score1 = (isExplicitScope.test(o1) ? 1 : -1);
      score2 = (isExplicitScope.test(o2) ? 1 : -1);

      return score2 - score1;
    });

    for (final Annotation anno : annotated.getAnnotations()) {
      final Class<? extends Annotation> annoType = anno.annotationType();
      if (scopeAnnoTypes.contains(annoType)) {
        pq.add(annoType);
      } else if (annoType.isAnnotationPresent(Stereotype.class)) {
        final Class<? extends Annotation> stereotypeScope = getDirectScope(MetaClassFactory.get(annoType));
        if (stereotypeScope != null) {
          pq.add(stereotypeScope);
        }
      }
    }

    if (pq.isEmpty()) {
      return null;
    }
    else {
      return pq.poll();
    }
  }

  /**
   * @param producerTypeInjectable If this parameter is null, only static methods will be processed.
   */
  private void processProducerMethods(final Injectable producerTypeInjectable, final MetaClass producerType,
          final DependencyGraphBuilder builder, final Collection<MetaMethod> disposesMethods, final boolean enabled, final List<String> problems) {
    final boolean staticOnly = (producerTypeInjectable == null);
    final Collection<Class<? extends Annotation>> producerAnnos = injectionContext.getAnnotationsForElementType(WiringElementType.ProducerElement);
    for (final Class<? extends Annotation> anno : producerAnnos) {
      final List<MetaMethod> methods = producerType.getDeclaredMethodsAnnotatedWith(anno);
      for (final MetaMethod method : methods) {
        if (!staticOnly || method.isStatic()) {
          processProducerMethod(producerTypeInjectable, producerType, builder, disposesMethods, method, enabled, problems);
        }
      }
    }
  }

  private void processProducerMethod(final Injectable producerTypeInjectable, final MetaClass producerType,
          final DependencyGraphBuilder builder, final Collection<MetaMethod> disposesMethods, final MetaMethod method,
          final boolean enabled, final List<String> problems) {
    final Class<? extends Annotation> directScope = getScope(method);
    final WiringElementType[] wiringTypes = getWiringTypeForProducer(producerType, method, directScope);
    final InjectableType injectableType = (enabled ? InjectableType.Producer : InjectableType.Disabled);
    final Injectable producedInjectable = builder.addInjectable(getMethodReturnType(method),
            qualFactory.forSource(method), getPathPredicate(method, problems), directScope, injectableType, wiringTypes);
    if (method.isStatic()) {
      builder.addProducerMemberDependency(producedInjectable, producerType, method);
    }
    else {
      builder.addProducerMemberDependency(producedInjectable, producerType, producerTypeInjectable.getQualifier(), method);
    }

    if (enabled) {
      processProducerAndDisposerMethodsDependencies(builder, disposesMethods, method, producedInjectable);
    }
  }

  private MetaClass getMethodReturnType(final MetaMethod method) {
    final MetaType genericType = method.getGenericReturnType();
    return getMetaClassFromGeneric(genericType).orElseGet(method::getReturnType);
  }

  private static Optional<MetaClass> getMetaClassFromGeneric(final MetaType genericType) {
    if (genericType instanceof MetaClass) {
      return Optional.of((MetaClass) genericType);
    }
    else if (genericType instanceof MetaParameterizedType && ((MetaParameterizedType) genericType).getRawType() instanceof MetaClass) {
      final MetaParameterizedType mpt = (MetaParameterizedType) genericType;
      @SuppressWarnings({ "unchecked", "rawtypes" })
      final MetaType[] typeArgs = Arrays
        .stream(mpt.getTypeParameters())
        .map(arg -> ((Optional<MetaType>) (Optional) getMetaClassFromGeneric(arg)).orElse(arg))
        .toArray(MetaType[]::new);

      return Optional.of(parameterizedAs((MetaClass) mpt.getRawType(), typeParametersOf(typeArgs)));
    }
    else {
      return Optional.empty();
    }
  }

  private void processProducerAndDisposerMethodsDependencies(final DependencyGraphBuilder builder, final Collection<MetaMethod> disposesMethods,
          final MetaMethod method, final Injectable producedInjectable) {
    final MetaParameter[] params = method.getParameters();
    for (int i = 0; i < params.length; i++) {
      final MetaParameter param = params[i];
      builder.addProducerParamDependency(producedInjectable, param.getType(), qualFactory.forSink(param), i, param);
    }

    final Collection<MetaMethod> matchingDisposes = getMatchingMethods(method, disposesMethods);
    if (matchingDisposes.size() > 1) {
      // TODO descriptive message with names of disposers found.
      throw new RuntimeException();
    } else if (!matchingDisposes.isEmpty()) {
      addDisposerDependencies(producedInjectable, matchingDisposes.iterator().next(), builder);
    }
  }

  private WiringElementType[] getWiringTypeForProducer(final MetaClass enclosingClass, final HasAnnotations annotated,
          final Class<? extends Annotation> directScope) {
    final List<WiringElementType> wiringTypes = new ArrayList<>();

    wiringTypes.addAll(getWiringTypesForScopeAnnotation(directScope));
    if (annotated.isAnnotationPresent(Specializes.class)) {
      wiringTypes.add(WiringElementType.Specialization);
    }
    if (enclosingClass.isAnnotationPresent(LoadAsync.class)) {
      wiringTypes.add(WiringElementType.LoadAsync);
    }

    return wiringTypes.toArray(new WiringElementType[wiringTypes.size()]);
  }

  private Collection<MetaMethod> getAllDisposesMethods(final MetaClass type, final boolean staticOnly) {
    final Collection<MetaMethod> disposers = new ArrayList<>();
    for (final MetaMethod method : type.getMethods()) {
      if (staticOnly && !method.isStatic()) {
        continue;
      }

      final List<MetaParameter> disposerParams = method.getParametersAnnotatedWith(Disposes.class);
      if (disposerParams.size() > 1) {
        throw new RuntimeException("Found method " + method + " in " + method.getDeclaringClassName()
                + " with multiple @Disposes parameters.");
      } else if (disposerParams.size() == 1) {
        disposers.add(method);
      }
    }

    return disposers;
  }

  private Collection<MetaMethod> getMatchingMethods(final MetaClassMember member, final Collection<MetaMethod> disposesMethods) {
    final Collection<MetaMethod> matching = new ArrayList<>();
    final Qualifier memberQual = qualFactory.forSource(member);
    final MetaClass producedType = getProducedType(member);

    for (final MetaMethod candidate : disposesMethods) {
      final MetaParameter disposesParam = candidate.getParametersAnnotatedWith(Disposes.class).iterator().next();
      if (producedType.isAssignableTo(disposesParam.getType())) {
        final Qualifier paramQual = qualFactory.forSink(disposesParam);
        if (paramQual.isSatisfiedBy(memberQual)) {
          matching.add(candidate);
        }
      }
    }

    return matching;
  }

  private MetaClass getProducedType(final MetaClassMember member) {
    if (member instanceof MetaField) {
      return ((MetaField) member).getType();
    } else if (member instanceof MetaMethod) {
      return ((MetaMethod) member).getReturnType();
    } else {
      throw new RuntimeException("Producer members must be fields or methods, but found " + member);
    }
  }

  private void addDisposerDependencies(final Injectable producedInjectable, final MetaMethod disposer, final DependencyGraphBuilder builder) {
    for (final MetaParameter param : disposer.getParameters()) {
      if (param.isAnnotationPresent(Disposes.class)) {
        builder.addDisposesMethodDependency(producedInjectable, disposer.getDeclaringClass(), qualFactory.forSink(disposer.getDeclaringClass()), disposer);
      } else {
        builder.addDisposesParamDependency(producedInjectable, param.getType(), qualFactory.forSink(param), param.getIndex(), param);
      }
    }
  }

  private Collection<WiringElementType> getWiringTypesForScopeAnnotation(final Class<? extends Annotation> directScope) {
    if (injectionContext.isElementType(WiringElementType.NormalScopedBean, directScope)) {
      return Collections.singleton(WiringElementType.NormalScopedBean);
    } else if (injectionContext.isElementType(WiringElementType.DependentBean, directScope)) {
      return Arrays.asList(WiringElementType.DependentBean, WiringElementType.PseudoScopedBean);
    } else {
      return Collections.singleton(WiringElementType.PseudoScopedBean);
    }
  }

  /**
   * @param producerInjectable If null then only static fields will be processed.
   */
  private void processProducerFields(final Injectable producerInjectable, final MetaClass producerType,
          final DependencyGraphBuilder builder, final Collection<MetaMethod> disposesMethods, final boolean enabled,
          final List<String> problems) {
    final boolean staticOnly = (producerInjectable == null);
    final Collection<Class<? extends Annotation>> producerAnnos = injectionContext.getAnnotationsForElementType(WiringElementType.ProducerElement);
    for (final Class<? extends Annotation> producerAnno : producerAnnos) {
      final List<MetaField> fields = producerType.getFieldsAnnotatedWith(producerAnno);
      for (final MetaField field : fields) {
        if (!staticOnly || field.isStatic()) {
          processProducerField(producerInjectable, producerType, builder, disposesMethods, field, enabled, problems);
        }
      }
    }
  }

  private void processProducerField(final Injectable producerInjectable, final MetaClass producerType,
          final DependencyGraphBuilder builder, final Collection<MetaMethod> disposesMethods, final MetaField field,
          final boolean enabled, final List<String> problems) {
    final Class<? extends Annotation> scopeAnno = getScope(field);
    final InjectableType injectableType = (enabled ? InjectableType.Producer : InjectableType.Disabled);
    final Injectable producedInjectable = builder.addInjectable(field.getType(), qualFactory.forSource(field), getPathPredicate(field, problems),
            scopeAnno, injectableType, getWiringTypeForProducer(producerType, field, scopeAnno));

    if (field.isStatic()) {
      builder.addProducerMemberDependency(producedInjectable, producerType, field);
    }
    else {
      builder.addProducerMemberDependency(producedInjectable, producerInjectable.getInjectedType(), producerInjectable.getQualifier(), field);
    }

    if (enabled) {
      processDisposerDependencies(builder, disposesMethods, field, producedInjectable);
    }
  }

  private void processDisposerDependencies(final DependencyGraphBuilder builder, final Collection<MetaMethod> disposesMethods,
          final MetaField field, final Injectable producedInjectable) {
    final Collection<MetaMethod> matchingDisposers = getMatchingMethods(field, disposesMethods);
    if (matchingDisposers.size() > 1) {
      // TODO add descriptive error message.
      throw new RuntimeException();
    } else if (!matchingDisposers.isEmpty()) {
      addDisposerDependencies(producedInjectable, matchingDisposers.iterator().next(), builder);
    }
  }

  private void processInjectionPoints(final Injectable typeInjectable, final DependencyGraphBuilder builder, final List<String> problems) {
    final MetaClass type = typeInjectable.getInjectedType();
    final MetaConstructor injectableConstructor = getInjectableConstructor(type);
    if (injectableConstructor != null) {
      if (!injectableConstructor.isPublic()) {
        problems.add("The constructor of " + typeInjectable.getInjectedType().getFullyQualifiedName() + " annotated with @Inject must be public.");
      }
      addConstructorInjectionPoints(typeInjectable, injectableConstructor, builder);
    }
    addFieldInjectionPoints(typeInjectable, builder, problems);
    addMethodInjectionPoints(typeInjectable, builder, problems);
  }

  private void addMethodInjectionPoints(final Injectable typeInjectable, final DependencyGraphBuilder builder, final List<String> problems) {
    final MetaClass type = typeInjectable.getInjectedType();
    final Collection<Class<? extends Annotation>> injectAnnotations = injectionContext.getAnnotationsForElementType(WiringElementType.InjectionPoint);
    for (final Class<? extends Annotation> inject : injectAnnotations) {
      for (final MetaMethod setter : type.getMethodsAnnotatedWith(inject)) {
        if (setter.getParameters().length != 1) {
          problems.add("The method injection point " + setter.getName() + " in "
                  + setter.getDeclaringClass().getFullyQualifiedName() + " should have exactly one parameter, not "
                  + setter.getParameters().length + ".");
        } else {
          final MetaParameter metaParam = setter.getParameters()[0];
          builder.addSetterMethodDependency(typeInjectable, metaParam.getType(), qualFactory.forSink(setter.getParameters()[0]), setter);
        }
      }
    }
  }

  private void addFieldInjectionPoints(final Injectable typeInjectable, final DependencyGraphBuilder builder, final List<String> problems) {
    final boolean noPublicFieldsAllowed = typeInjectable.getWiringElementTypes().contains(WiringElementType.NormalScopedBean);
    final MetaClass type = typeInjectable.getInjectedType();
    final Collection<Class<? extends Annotation>> injectAnnotations = injectionContext.getAnnotationsForElementType(WiringElementType.InjectionPoint);
    for (final Class<? extends Annotation> inject : injectAnnotations) {
      for (final MetaField field : type.getFieldsAnnotatedWith(inject)) {
        if (noPublicFieldsAllowed && field.isPublic()) {
          problems.add("The normal scoped bean " + type.getFullyQualifiedName() + " has a public field " + field.getName());
        }
        builder.addFieldDependency(typeInjectable, field.getType(), qualFactory.forSink(field), field);
      }
    }
  }

  private void addConstructorInjectionPoints(final Injectable concreteInjectable, final MetaConstructor injectableConstructor, final DependencyGraphBuilder builder) {
    final MetaParameter[] params = injectableConstructor.getParameters();
    for (int i = 0; i < params.length; i++) {
      final MetaParameter param = params[i];
      builder.addConstructorDependency(concreteInjectable, param.getType(), qualFactory.forSink(param), i, param);
    }
  }

  private MetaConstructor getInjectableConstructor(final MetaClass type) {
    final Collection<Class<? extends Annotation>> injectAnnotations = injectionContext.getAnnotationsForElementType(WiringElementType.InjectionPoint);
    for (final MetaConstructor con : type.getConstructors()) {
      for (final Class<? extends Annotation> anno : injectAnnotations) {
        if (con.isAnnotationPresent(anno)) {
          return con;
        }
      }
    }

    return null;
  }

  private boolean isConstructable(final MetaClass type, final List<String> problems) {
    final boolean explicitlyScoped = getDirectScope(type) != null;
    final List<MetaConstructor> injectableConstructors = getInjectableConstructors(type);
    final MetaConstructor noArgConstructor = type.getDeclaredConstructor(new MetaClass[0]);

    if (injectableConstructors.size() > 1) {
      problems.add(type.getFullyQualifiedName() + " has " + injectableConstructors.size() + " constructors annotated with @Inject.");
      return false;
    }
    else {
      if (injectableConstructors.size() == 1) {
        final MetaConstructor injectConstructor = injectableConstructors.get(0);
        final boolean instantiable = injectConstructor.isPublic() || !isJavaScriptObject(type);
        if (!instantiable) {
          problems.add(String.format("Cannot access constructor for %s.", type.getFullyQualifiedName()));
        }

        if (scopeDoesNotRequireProxy(type)) {
          return instantiable;
        } else if (noArgConstructor == null || !(noArgConstructor.isPublic() || noArgConstructor.isProtected())) {
          log.debug("The class {} must be proxiable but does not have an accessible no-argument constructor", type.getFullyQualifiedName());
          final boolean injectConstructorProxiable = injectConstructor.isPublic() || injectConstructor.isProtected();
          if (!injectConstructorProxiable) {
            problems.add(String.format(
                    "The class %s must be proxiable but has no injectable constructor or no-argument constructor accessible to subclasses.",
                    type.getFullyQualifiedName()));
          }

          return instantiable && injectConstructorProxiable;
        } else {
          return instantiable;
        }
      } else {
        final boolean instantiable = noArgConstructor != null && (noArgConstructor.isPublic() || !isJavaScriptObject(type));
        final boolean proxiable = noArgConstructor != null && (noArgConstructor.isPublic() || noArgConstructor.isProtected());
        final boolean passesProxiability = scopeDoesNotRequireProxy(type) || proxiable;

        if (explicitlyScoped) {
          if (!instantiable) {
            problems.add(String.format("Cannot access constructor for %s.", type.getFullyQualifiedName()));
          }
          if (!passesProxiability) {
            problems.add(String.format(
                    "%s must be proxiable but does not have a no-argument constructor accessible to subclasses.",
                    type.getFullyQualifiedName()));
          }
        }

        return instantiable && passesProxiability;
      }
    }
  }

  private boolean isJavaScriptObject(final MetaClass type) {
    return type.isAssignableTo(JavaScriptObject.class) || isNativeJSType(type);
  }

  private boolean isNativeJSType(final MetaClass type) {
    final JsType anno = type.getAnnotation(JsType.class);
    return type.getAnnotation(JsType.class) != null && anno.isNative();
  }

  private boolean scopeDoesNotRequireProxy(final MetaClass type) {
    final Class<? extends Annotation> scope = getScope(type);

    return scope.equals(EntryPoint.class) || injectionContext.getAnnotationsForElementType(WiringElementType.DependentBean).contains(scope);
  }

  private List<MetaConstructor> getInjectableConstructors(final MetaClass type) {
    final Collection<Class<? extends Annotation>> injectAnnotations = injectionContext.getAnnotationsForElementType(WiringElementType.InjectionPoint);
    final List<MetaConstructor> cons = new ArrayList<>();
    for (final MetaConstructor con : type.getConstructors()) {
      for (final Class<? extends Annotation> anno : injectAnnotations) {
        if (con.isAnnotationPresent(anno)) {
          cons.add(con);
        }
      }
    }

    return cons;
  }

  private boolean isEnabled(final MetaClass type) {
    final boolean hasEnablingProperty = hasEnablingProperty(type);

    return (injectionContext.isAllowlisted(type) && !injectionContext.isDenylisted(type))
            && ((hasEnablingProperty && isEnabledByProperty(type)) || (!hasEnablingProperty && isActive(type)));
  }

  private boolean isActive(final MetaClass type) {
    if (type.isAnnotationPresent(Alternative.class)) {
      return isAlternativeEnabled(type);
    } else {
      return true;
    }
  }

  private boolean isAlternativeEnabled(final MetaClass type) {
    if (alternatives == null) {
      final String userDefinedAlternatives = EnvUtil.getEnvironmentConfig().getFrameworkOrSystemProperty("errai.ioc.enabled.alternatives");
      if (userDefinedAlternatives != null) {
        alternatives = new HashSet<>(Arrays.asList(userDefinedAlternatives.split("\\s+")));
      } else {
        alternatives = Collections.emptyList();
      }
    }

    return alternatives.contains(type.getFullyQualifiedName());
  }

  private boolean isEnabledByProperty(final MetaClass type) {
    final EnabledByProperty anno = type.getAnnotation(EnabledByProperty.class);
    final boolean propValue = getPropertyValue(anno.value(),
                                               anno.matchValue(),
                                               anno.matchByDefault(),
                                               anno.caseSensitive());
    final boolean negated = anno.negated();

    return propValue ^ negated;
  }

  protected boolean getPropertyValue(final String propName,
                                     final String matchValue,
                                     final boolean matchByDefault,
                                     final boolean caseSensitive) {
    final String propertyValue = EnvUtil.getEnvironmentConfig().getFrameworkOrSystemProperty(propName);
    if (propertyValue == null) {
      return matchByDefault;
    } else {
      return caseSensitive ? propertyValue.equals(matchValue) : propertyValue.equalsIgnoreCase(matchValue);
    }
  }

  private boolean hasEnablingProperty(final MetaClass type) {
    return type.isAnnotationPresent(EnabledByProperty.class);
  }

  private static boolean exactTypePredicate(final List<InjectableHandle> path) {
    final int pathLength = path.size() - 1;
    return pathLength == 0 || path.get(0).getType().getFullyQualifiedName().equals(path.get(pathLength).getType().getFullyQualifiedName());
  }

}
