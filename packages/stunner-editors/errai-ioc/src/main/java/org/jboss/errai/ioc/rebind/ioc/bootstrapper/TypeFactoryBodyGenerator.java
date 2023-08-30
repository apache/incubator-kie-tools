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
import java.lang.annotation.ElementType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.collect.Multimap;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.FieldDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ParamDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.SetterParameterDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

import static org.jboss.errai.codegen.util.PrivateAccessUtil.getPrivateFieldAccessorName;
import static org.jboss.errai.codegen.util.PrivateAccessUtil.getPrivateMethodName;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.newObject;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryGenerator.getLocalVariableName;

/**
 * Generate factories for type injectable beans. Generates code to support
 * constructor, field, and method injection.
 *
 * @see FactoryBodyGenerator
 * @see AbstractBodyGenerator
 * @author Max Barkley <mbarkley@redhat.com>
 */
class TypeFactoryBodyGenerator extends AbstractBodyGenerator {

  @Override
  protected void preGenerationHook(final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable,
          final DependencyGraph graph, final InjectionContext injectionContext) {
    runDecorators(injectable, injectionContext, bodyBlockBuilder);
  }

  @Override
  protected List<Statement> generateFactoryInitStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    return controller.getFactoryInitializaionStatements();
  }

  @Override
  protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final Multimap<DependencyType, Dependency> dependenciesByType = separateByType(injectable.getDependencies());

    final Collection<Dependency> constructorDependencies = dependenciesByType.get(DependencyType.Constructor);
    final Collection<Dependency> fieldDependencies = dependenciesByType.get(DependencyType.Field);
    final Collection<Dependency> setterDependencies = dependenciesByType.get(DependencyType.SetterParameter);

    final List<Statement> createInstanceStatements = new ArrayList<>();

    constructInstance(injectable, constructorDependencies, createInstanceStatements);
    injectFieldDependencies(injectable, fieldDependencies, createInstanceStatements, bodyBlockBuilder);
    injectSetterMethodDependencies(injectable, setterDependencies, createInstanceStatements, bodyBlockBuilder);
    addInitializationStatements(createInstanceStatements);
    addReturnStatement(createInstanceStatements);

    return createInstanceStatements;
  }

  @Override
  protected List<Statement> generateDestroyInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final List<Statement> destructionStmts = new ArrayList<>();

    maybeInvokePreDestroys(injectable, destructionStmts, bodyBlockBuilder);
    destructionStmts
            .addAll(super.generateDestroyInstanceStatements(bodyBlockBuilder, injectable, graph, injectionContext));

    return destructionStmts;
  }

  @Override
  protected List<Statement> generateInvokePostConstructsStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final List<Statement> stmts = new ArrayList<>();
    final Queue<MetaMethod> postConstructMethods = gatherPostConstructs(injectable);
    for (final MetaMethod postConstruct : postConstructMethods) {
      if (postConstruct.isPublic()) {
        stmts.add(loadVariable("instance").invoke(postConstruct));
      } else {
        controller.ensureMemberExposed(postConstruct);
        final String accessorName = getPrivateMethodName(postConstruct);
        stmts.add(invokePrivateAccessorWithNoParams(accessorName));
      }
    }

    return stmts;
  }

  private void addInitializationStatements(final List<Statement> createInstanceStatements) {
    createInstanceStatements.addAll(controller.getInitializationStatements());
  }

  private void runDecorators(final Injectable injectable, final InjectionContext injectionContext,
          final ClassStructureBuilder<?> bodyBlockBuilder) {
    final MetaClass type = injectable.getInjectedType();
    final Set<HasAnnotations> privateAccessors = new HashSet<>();
    final List<DecoratorRunnable> decoratorRunnables = new ArrayList<>();
    decoratorRunnables.addAll(generateDecoratorRunnablesForType(injectionContext, type, ElementType.FIELD,
            bodyBlockBuilder, privateAccessors, injectable));
    decoratorRunnables.addAll(generateDecoratorRunnablesForType(injectionContext, type, ElementType.PARAMETER,
            bodyBlockBuilder, privateAccessors, injectable));
    decoratorRunnables.addAll(generateDecoratorRunnablesForType(injectionContext, type, ElementType.METHOD,
            bodyBlockBuilder, privateAccessors, injectable));
    decoratorRunnables.addAll(generateDecoratorRunnablesForType(injectionContext, type, ElementType.TYPE,
            bodyBlockBuilder, privateAccessors, injectable));

    decoratorRunnables.sort(null);
    for (final DecoratorRunnable runnable : decoratorRunnables) {
      runnable.run();
    }

    for (final HasAnnotations annotated : privateAccessors) {
      if (annotated instanceof MetaField) {
        controller.addExposedField((MetaField) annotated);
      } else if (annotated instanceof MetaMethod) {
        controller.addExposedMethod((MetaMethod) annotated);
      }
    }
  }

  @SuppressWarnings({ "rawtypes" })
  private List<DecoratorRunnable> generateDecoratorRunnablesForType(final InjectionContext injectionContext,
          final MetaClass type, final ElementType elemType,
          final ClassStructureBuilder<?> builder, final Set<HasAnnotations> createdAccessors, final Injectable injectable) {
    final List<DecoratorRunnable> decoratorRunnables = new ArrayList<>();

    final Collection<Class<? extends Annotation>> decoratorAnnos = injectionContext
            .getDecoratorAnnotationsBy(elemType);
    for (final Class<? extends Annotation> annoType : decoratorAnnos) {
      final List<HasAnnotations> annotatedItems = getAnnotatedWithForElementType(type, elemType, annoType);

      final IOCDecoratorExtension[] decorators = injectionContext.getDecorators(annoType);
      for (final IOCDecoratorExtension decorator : decorators) {
        for (final HasAnnotations annotated : annotatedItems) {
          final DecoratorRunnable decoratorRunnable = new DecoratorRunnable(
                  decorator.getClass().getAnnotation(CodeDecorator.class).order(), elemType,
                  () -> {
                    final Decorable decorable = new Decorable(annotated, annotated.getAnnotation(annoType),
                            Decorable.DecorableType.fromElementType(elemType), injectionContext,
                            builder.getClassDefinition().getContext(), builder.getClassDefinition(), injectable);
                    if (isNonPublicField(annotated) && !createdAccessors.contains(annotated)) {
                      createdAccessors.add(type);
                    }
                    else if (isNonPublicMethod(annotated) && !createdAccessors.contains(annotated)) {
                      createdAccessors.add(annotated);
                    }
                    else if (isParamOfNonPublicMethod(annotated)
                            && !createdAccessors.contains(((MetaParameter) annotated).getDeclaringMember())) {
                      final MetaMethod declaringMethod = (MetaMethod) ((MetaParameter) annotated).getDeclaringMember();
                      createdAccessors.add(declaringMethod);
                    }
                    decorator.generateDecorator(decorable, controller);
                  });
          decoratorRunnables.add(decoratorRunnable);
        }
      }
    }

    return decoratorRunnables;
  }

  private boolean isParamOfNonPublicMethod(final HasAnnotations annotated) {
    if (!(annotated instanceof MetaParameter)) {
      return false;
    }
    final MetaClassMember member = ((MetaParameter) annotated).getDeclaringMember();

    return member instanceof MetaMethod && !member.isPublic();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private List<HasAnnotations> getAnnotatedWithForElementType(final MetaClass type, final ElementType elemType, final Class<? extends Annotation> annoType) {
    final List annotatedItems;
    switch (elemType) {
    case FIELD:
      annotatedItems = type.getFieldsAnnotatedWith(annoType);
      break;
    case METHOD:
      annotatedItems = type.getMethodsAnnotatedWith(annoType);
      break;
    case PARAMETER:
      annotatedItems = type.getParametersAnnotatedWith(annoType);
      break;
    case TYPE:
      annotatedItems = (type.isAnnotationPresent(annoType)) ? Collections.singletonList(type) : Collections.emptyList();
      break;
    default:
      throw new RuntimeException("Not yet implemented.");
    }
    return annotatedItems;
  }

  private boolean isNonPublicMethod(final HasAnnotations annotated) {
    return annotated instanceof MetaMethod && !((MetaMethod) annotated).isPublic();
  }

  private boolean isNonPublicField(final HasAnnotations annotated) {
    return annotated instanceof MetaField && !((MetaField) annotated).isPublic();
  }

  private void maybeInvokePreDestroys(final Injectable injectable, final List<Statement> destructionStmts,
          final ClassStructureBuilder<?> bodyBlockBuilder) {
    final Queue<MetaMethod> preDestroyMethods = gatherPreDestroys(injectable);
    for (final MetaMethod preDestroy : preDestroyMethods) {
      if (preDestroy.isPublic()) {
        destructionStmts.add(loadVariable("instance").invoke(preDestroy));
      } else {
        controller.ensureMemberExposed(preDestroy);
        final String accessorName = getPrivateMethodName(preDestroy);
        destructionStmts.add(invokePrivateAccessorWithNoParams(accessorName));
      }
    }
  }

  private Statement invokePrivateAccessorWithNoParams(final String accessorName) {
    return loadVariable("this").invoke(accessorName, loadVariable("instance"));
  }

  private Queue<MetaMethod> gatherPreDestroys(final Injectable injectable) {
    final Queue<MetaMethod> preDestroyQueue = new LinkedList<>();
    MetaClass type = injectable.getInjectedType();
    do {
      final List<MetaMethod> curPreDestroys = type.getDeclaredMethodsAnnotatedWith(PreDestroy.class);
      if (curPreDestroys.size() > 1) {
        throw new RuntimeException(type.getFullyQualifiedName() + " has multiple @PreDestroy methods.");
      } else if (curPreDestroys.size() == 1) {
        final MetaMethod preDestroy = curPreDestroys.get(0);
        if (preDestroy.getParameters().length > 0) {
          throw new RuntimeException(type.getFullyQualifiedName() + " has a @PreDestroy method with parameters.");
        }

        preDestroyQueue.add(preDestroy);
      }

      type = type.getSuperClass();
    } while (!type.getFullyQualifiedName().equals("java.lang.Object"));

    return preDestroyQueue;
  }

  private Queue<MetaMethod> gatherPostConstructs(final Injectable injectable) {
    MetaClass type = injectable.getInjectedType();
    final Deque<MetaMethod> postConstructs = new ArrayDeque<>();

    do {
      final List<MetaMethod> currentPostConstructs = type.getDeclaredMethodsAnnotatedWith(PostConstruct.class);
      if (currentPostConstructs.size() > 0) {
        if (currentPostConstructs.size() > 1) {
          throw new RuntimeException(type.getFullyQualifiedName() + " has multiple @PostConstruct methods.");
        }

        final MetaMethod postConstruct = currentPostConstructs.get(0);
        if (postConstruct.getParameters().length > 0) {
          throw new RuntimeException(type.getFullyQualifiedName() + " has a @PostConstruct method with parameters.");
        }

        postConstructs.push(postConstruct);
      }
      type = type.getSuperClass();
    } while (!type.getFullyQualifiedName().equals("java.lang.Object"));

    return postConstructs;
  }

  private void injectFieldDependencies(final Injectable injectable, final Collection<Dependency> fieldDependencies,
          final List<Statement> createInstanceStatements, final ClassStructureBuilder<?> bodyBlockBuilder) {
    for (final Dependency dep : fieldDependencies) {
      final FieldDependency fieldDep = FieldDependency.class.cast(dep);
      final MetaField field = fieldDep.getField();
      final Injectable depInjectable = fieldDep.getInjectable();

      final ContextualStatementBuilder injectedValue;
      if (depInjectable.isContextual()) {
        final MetaClass[] typeArgsClasses = getTypeArguments(field.getType());
        final Annotation[] qualifiers = getQualifiers(field).toArray(new Annotation[0]);
        injectedValue = castTo(depInjectable.getInjectedType(),
                loadVariable("contextManager").invoke("getContextualInstance",
                        loadLiteral(depInjectable.getFactoryName()), typeArgsClasses, qualifiers));
      } else {
        injectedValue = castTo(depInjectable.getInjectedType(),
                loadVariable("contextManager").invoke("getInstance", loadLiteral(depInjectable.getFactoryName())));
      }

      final String fieldDepVarName = field.getDeclaringClassName().replace('.', '_').replace('$', '_') + "_" + field.getName();

      createInstanceStatements.add(declareFinalVariable(fieldDepVarName, depInjectable.getInjectedType(), injectedValue));
      if (depInjectable.getWiringElementTypes().contains(WiringElementType.DependentBean)) {
        createInstanceStatements
                .add(loadVariable("this").invoke("registerDependentScopedReference", loadVariable("instance"), loadVariable(fieldDepVarName)));
      }

      if (!field.isPublic()) {
        controller.addExposedField(field);
        final String privateFieldInjectorName = getPrivateFieldAccessorName(field);
        createInstanceStatements.add(loadVariable("this").invoke(privateFieldInjectorName, loadVariable("instance"), loadVariable(fieldDepVarName)));
      } else {
        createInstanceStatements.add(loadVariable("instance").loadField(field).assignValue(loadVariable(fieldDepVarName)));
      }
    }
  }

  private void injectSetterMethodDependencies(final Injectable injectable, final Collection<Dependency> setterDependencies,
          final List<Statement> createInstanceStatements, final ClassStructureBuilder<?> bodyBlockBuilder) {
    for (final Dependency dep : setterDependencies) {
      final SetterParameterDependency setterDep = SetterParameterDependency.class.cast(dep);
      final MetaMethod setter = setterDep.getMethod();
      final Injectable depInjectable = setterDep.getInjectable();

      final ContextualStatementBuilder injectedValue;
      if (depInjectable.isContextual()) {
        final MetaClass[] typeArgsClasses = getTypeArguments(setter.getParameters()[0].getType());
        final Annotation[] qualifiers = getQualifiers(setter).toArray(new Annotation[0]);
        injectedValue = castTo(depInjectable.getInjectedType(),
                loadVariable("contextManager").invoke("getContextualInstance",
                        loadLiteral(depInjectable.getFactoryName()), typeArgsClasses, qualifiers));
      } else {
        injectedValue = castTo(depInjectable.getInjectedType(),
                loadVariable("contextManager").invoke("getInstance", loadLiteral(depInjectable.getFactoryName())));
      }
      final MetaParameter param = setter.getParameters()[0];
      final String paramLocalVarName = getLocalVariableName(param);

      createInstanceStatements.add(declareFinalVariable(paramLocalVarName, param.getType(), injectedValue));
      if (depInjectable.getWiringElementTypes().contains(WiringElementType.DependentBean)) {
        createInstanceStatements
                .add(loadVariable("this").invoke("registerDependentScopedReference", loadVariable("instance"), loadVariable(paramLocalVarName)));
      }

      if (!setter.isPublic()) {
        controller.addExposedMethod(setter);
        final String privateFieldInjectorName = getPrivateMethodName(setter);
        createInstanceStatements.add(loadVariable("this").invoke(privateFieldInjectorName, loadVariable("instance"), loadVariable(paramLocalVarName)));
      } else {
        createInstanceStatements.add(loadVariable("instance").invoke(setter, loadVariable(paramLocalVarName)));
      }
    }
  }

  private void constructInstance(final Injectable injectable, final Collection<Dependency> constructorDependencies,
          final List<Statement> createInstanceStatements) {
    if (constructorDependencies.size() > 0) {
      addConstructorInjectionStatements(injectable, constructorDependencies, createInstanceStatements);
    } else {
      assignNewObjectWithZeroArgConstructor(injectable, createInstanceStatements);
    }

    createInstanceStatements.add(loadVariable("this").invoke("setIncompleteInstance", loadVariable("instance")));
  }

  private void assignNewObjectWithZeroArgConstructor(final Injectable injectable, final List<Statement> createInstanceStatements) {
    final MetaConstructor noArgConstr = injectable.getInjectedType().getDeclaredConstructor(new MetaClass[0]);
    final Object newObjectStmt;

    if (noArgConstr.isPublic()) {
      newObjectStmt = newObject(injectable.getInjectedType());
    } else {
      newObjectStmt = controller.exposedConstructorStmt(noArgConstr);
    }

    createInstanceStatements.add(declareFinalVariable("instance", injectable.getInjectedType(), newObjectStmt));
  }

  private void addConstructorInjectionStatements(final Injectable injectable, final Collection<Dependency> constructorDependencies,
          final List<Statement> createInstanceStatements) {
    final Object[] constructorParameterStatements = new Object[constructorDependencies.size()];
    final List<Statement> dependentScopedRegistrationStatements = new ArrayList<>(constructorDependencies.size());
    for (final Dependency dep : constructorDependencies) {
      processConstructorDependencyStatement(createInstanceStatements, constructorParameterStatements, dependentScopedRegistrationStatements, dep);
    }

    createInstanceStatements.add(declareFinalVariable("instance", injectable.getInjectedType(),
            newObject(injectable.getInjectedType(), constructorParameterStatements)));
    createInstanceStatements.addAll(dependentScopedRegistrationStatements);
  }

  private void processConstructorDependencyStatement(final List<Statement> createInstanceStatements, final Object[] constructorParameterStatements,
          final List<Statement> dependentScopedRegistrationStatements, final Dependency dep) {
    final Injectable depInjectable = dep.getInjectable();
    final ParamDependency paramDep = ParamDependency.class.cast(dep);

    final ContextualStatementBuilder injectedValue = getInjectedValue(depInjectable, paramDep);
    final String paramLocalVarName = getLocalVariableName(paramDep.getParameter());

    createInstanceStatements.add(declareFinalVariable(paramLocalVarName, paramDep.getParameter().getType(), injectedValue));
    if (dep.getInjectable().getWiringElementTypes().contains(WiringElementType.DependentBean)) {
      dependentScopedRegistrationStatements.add(loadVariable("this").invoke("registerDependentScopedReference",
              loadVariable("instance"), loadVariable(paramLocalVarName)));
    }
    constructorParameterStatements[paramDep.getParamIndex()] = loadVariable(paramLocalVarName);
  }

  private ContextualStatementBuilder getInjectedValue(final Injectable depInjectable, final ParamDependency paramDep) {
    final ContextualStatementBuilder injectedValue;

    if (depInjectable.isContextual()) {
      final MetaClass[] typeArgsClasses = getTypeArguments(paramDep.getParameter().getType());
      final Annotation[] qualifiers = getQualifiers(paramDep.getParameter()).toArray(new Annotation[0]);

      injectedValue = castTo(depInjectable.getInjectedType(),
              loadVariable("contextManager").invoke("getContextualInstance",
                      loadLiteral(depInjectable.getFactoryName()), typeArgsClasses, qualifiers));
    } else {
      injectedValue = castTo(depInjectable.getInjectedType(),
              loadVariable("contextManager").invoke("getInstance", loadLiteral(depInjectable.getFactoryName())));
    }

    return injectedValue;
  }
}
