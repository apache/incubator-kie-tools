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


package org.jboss.errai.ioc.rebind.ioc.graph.api;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Scope;

import jsinterop.annotations.JsType;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.extension.builtin.LoggerFactoryIOCExtension;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.ResolutionPriority;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

/**
 * Builds and resolves a dependency graph.
 *
 * {@link Injectable Injectables} represent a source for a bean (for example an
 * explicitly scoped concrete type, or a producer method). {@link Dependency
 * Dependencies} are beans that are required for injection points or producer
 * parameters in a bean represented by an {@link Injectable}.
 *
 * The {@link DependencyGraphBuilder} API allows for adding injectables and
 * declaring dependencies for those injectables by passing in the types and
 * {@link Qualifier qualifiers} of the dependencies.
 *
 * When all injectables and dependencies are added, calling
 * {@link #createGraph()} will return a fully-resolved dependency graph.
 *
 * @see DependencyGraph
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface DependencyGraphBuilder {

  /**
   * Add an {@link Injectable} to the graph.
   *
   * @param injectedType
   *          The class of the injectable.
   * @param qualifier
   *          The {@link Qualifier} of the injectable.
   * @param pathPredicate
   *          Used to add restrictions on what dependencies are satisfied by the added injectable. This predicate is
   *          called anytime the created injectable is a candidate for a dependency. The predicate argument is the
   *          {@link InjectableHandle InjectableHandles} traversed in order to arrive at this injectable. If the
   *          predicate returns false, the injectable does not satisfy a dependency.
   * @param literalScope
   *          The {@link Scope} of the injectable.
   * @param injectableType
   *          The kind of injectable (i.e. producer, provider, type, etc.).
   * @param wiringTypes
   *          A collection of {@link WiringElementType wiring types} that this injectable has.
   *
   * @return The newly added {@link Injectable}.
   */
  Injectable addInjectable(MetaClass injectedType, Qualifier qualifier, Predicate<List<InjectableHandle>> pathPredicate, Class<? extends Annotation> literalScope,
          InjectableType injectableType, WiringElementType... wiringTypes);

  /**
   * Some {@link IOCExtensionConfigurator IOC extensions} need to generate
   * special code per injection point (such as the
   * {@link LoggerFactoryIOCExtension}). Adding an extension injectable allows
   * for this by adding an object that satisfies a given type and qualifier, but
   * is not itself an injectable. For every injection point satisfied by a an
   * extension injectable, a {@link ProvidedInjectable} will be created
   * containing metadata for the specific injection point.
   *
   * @param injectedType
   *          The class of the injectable.
   * @param qualifier
   *          The {@link Qualifier} of the injectable.
   * @param pathPredicate
   *          Used to add restrictions on what dependencies are satisfied by the added injectable. This predicate is
   *          called anytime the created injectable is a candidate for a dependency. The predicate argument is the
   *          {@link InjectableHandle InjectableHandles} traversed in order to arrive at this injectable. If the
   *          predicate returns false, the injectable does not satisfy a dependency.
   * @param literalScope
   *          The {@link Scope} of the injectable.
   * @param injectableType
   *          The kind of injectable (i.e. producer, provider, type, etc.).
   * @param wiringTypes
   *          A collection of {@link WiringElementType wiring types} that this
   *          injectable has.
   *
   * @return The newly added extension {@link Injectable}.
   */
  Injectable addExtensionInjectable(MetaClass injectedType, Qualifier qualifier,
          Predicate<List<InjectableHandle>> pathPredicate, InjectableProvider provider,
          WiringElementType... wiringTypes);

  /**
   * Create a dependency for a field injection point in a bean class.
   *
   * @param injectable The {@link Injectable} that has the dependency.
   * @param type The class of the dependency.
   * @param qualifier The qualifier of the dependency.
   * @param dependentField The field that the dependency should be injected into.
   */
  void addFieldDependency(Injectable injectable, MetaClass type, Qualifier qualifier, MetaField dependentField);

  /**
   * Create a dependency for a constructor injection point in a bean class.
   *
   * @param injectable The {@link Injectable} that has the dependency.
   * @param type The class of the dependency.
   * @param qualifier The qualifier of the dependency.
   * @param dependentField The parameter of the constructor that the dependency should be injected into.
   */
  void addConstructorDependency(Injectable injectable, MetaClass type, Qualifier qualifier, int paramIndex, MetaParameter param);

  /**
   * Create a dependency for a producer parameter injection point in a bean class.
   *
   * @param injectable The {@link Injectable} that has the dependency.
   * @param type The class of the dependency.
   * @param qualifier The qualifier of the dependency.
   * @param dependentField The parameter of the producer method that the dependency should be injected into.
   */
  void addProducerParamDependency(Injectable injectable, MetaClass type, Qualifier qualifier, int paramIndex, MetaParameter param);

  /**
   * Create a dependency for a producer member (field or method) injection point in a bean class.
   *
   * @param injectable The {@link Injectable} that has the dependency.
   * @param type The class of the dependency.
   * @param qualifier The qualifier of the dependency.
   * @param dependentField The producer member (field or method) that must be invoked to satisfy the dependency.
   */
  void addProducerMemberDependency(Injectable injectable, MetaClass type, Qualifier qualifier, MetaClassMember producingMember);

  /**
   * Create a dependency for a static producer member (field or method) injection point in a bean class.
   *
   * @param injectable The {@link Injectable} that has the dependency.
   * @param type The class of the dependency.
   * @param dependentField The producer member (field or method) that must be invoked to satisfy the dependency.
   */
  void addProducerMemberDependency(Injectable producedInjectable, MetaClass producerType, MetaClassMember method);

  /**
   * Create a dependency for a setter method injection point in a bean class.
   *
   * @param injectable The {@link Injectable} that has the dependency.
   * @param type The class of the dependency.
   * @param qualifier The qualifier of the dependency.
   * @param dependentField The setter method that the dependency should be injected into.
   */
  void addSetterMethodDependency(Injectable injectable, MetaClass type, Qualifier qualifier, MetaMethod setter);

  /**
   * Create a dependency for a disposer method from a producer bean class. This
   * kind of dependency must always accomany a producer member dependency to be
   * of any use.
   *
   * @param injectable
   *          The {@link Injectable} that has the dependency.
   * @param type
   *          The class of the dependency.
   * @param qualifier
   *          The qualifier of the dependency.
   * @param dependentField
   *          The disposer method that must be invoked.
   */
  void addDisposesMethodDependency(Injectable injectable, MetaClass type, Qualifier qualifier, MetaMethod disposer);

  /**
   * Create a dependency for a disposer method parameter. This
   * kind of dependency must always accomany a disposer method dependency to be
   * of any use.
   *
   * @param injectable
   *          The {@link Injectable} that has the dependency.
   * @param type
   *          The class of the dependency.
   * @param qualifier
   *          The qualifier of the dependency.
   * @param dependentField
   *          The parameter that must have an injected value in the disposer method that must be invoked.
   */
  void addDisposesParamDependency(Injectable injectable, MetaClass type, Qualifier qualifier, Integer index, MetaParameter param);

  /**
   * Resolve all dependencies of added injectables. This method throws exceptions if any dependencies are unsastisfied
   * or are ambiguously satisfied.
   *
   * @param strategy
   *          Defines what strategy is used to determine reachability of beans. Beans considered unreachable will not be
   *          available at runtime (reducing the size of the generated code).
   *
   * @return A {@link DependencyGraph} where all contained {@link Injectable injectables} have fully resolved
   *         dependencies.
   *
   * @see ResolutionPriority
   */
  DependencyGraph createGraph(ReachabilityStrategy strategy);

  public static enum ReachabilityStrategy {
    /**
     * With this strategy all types are considered reachable.
     */
    All,
    /**
     * With this strategy only beans reachable from beans that have explicit annotations relevant to the IoC are
     * considered availablbe such as a scope, {@link Inject}, {@link Produces}, {@link JsType}, etc.
     */
    Annotated,
    /**
     * With this strategy only types reachable from a type that is an {@link EntryPoint} or a
     * {@link JsType} are kept.
     */
    Aggressive
  }

  /**
   * The kinds of {@link Injectable injectables}.
   *
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static enum InjectableType {
    Type, JsType, Producer, Provider, ContextualProvider, Extension, ExtensionProvided, Static, Disabled
  }

  /**
   * The kinds of {@link Dependency dependencies}.
   *
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static enum DependencyType {
    Constructor, Field, ProducerMember, ProducerParameter, SetterParameter, DisposerMethod, DisposerParameter
  }

  /**
   * When a dependency is added via a graph builder method such as
   * {@link DependencyGraphBuilder#addFieldDependency(Injectable, MetaClass, Qualifier, MetaField)}
   * , a subtype of {@link Dependency} is added to the depending
   * {@link Injectable} internally.
   *
   * Once the graph is resolved, an injectables dependencies can be found using {@link Injectable#getDependencies()}.
   *
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static interface Dependency extends HasAnnotations {

    /**
     * This will only return a meaningful value after {@link DependencyGraphBuilder#createGraph()} is called.
     *
     * @return The injectable that satisfied this dependency.
     */
    Injectable getInjectable();

    /**
     * @return The kind of this dependency.
     */
    DependencyType getDependencyType();

  }

  /**
   * A dependency for a parameter in a method or constructor.
   *
   * @see Dependency
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static interface ParamDependency extends Dependency {

    /**
     * @return The index of this parameter in its enclosing method or constructor.
     */
    int getParamIndex();

    /**
     * @return The parameter of this dependency.
     */
    MetaParameter getParameter();

  }

  /**
   * A dependency for a field in a type.
   *
   * @see Dependency
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static interface FieldDependency extends Dependency {

    /**
     * @return The field of this dependency.
     */
    MetaField getField();

  }

  /**
   * A dependency for a setter method parameter.
   *
   * @see Dependency
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static interface SetterParameterDependency extends Dependency {

    /**
     * @return The setter method of this dependency.
     */
    MetaMethod getMethod();

  }

  /**
   * A dependency on a producer instance. If a type has a producer method, two
   * injectables should be made for the type. One of the type with the method
   * (since it is itself type injectable) and one of the produced type.
   *
   * The injectable for the produced type will then depend on the producer type
   * via a {@link ProducerInstanceDependency}.
   *
   * @see Dependency
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static interface ProducerInstanceDependency extends Dependency {

    /**
     * @return The producer member of this dependency.
     */
    MetaClassMember getProducingMember();

  }

  /**
   * A dependency on a dispoer method. If a producer has a disposer method
   * satisfying the qualifiers of one of its producer, then the injectable for
   * the produced type should depend on the producer member (via a
   * {@link ProducerInstanceDependency}) and the disposer method via this
   * dependency.
   *
   * @see Dependency
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static interface DisposerMethodDependency extends Dependency {

    /**
     * @return The disposer method of this dependency.
     */
    MetaMethod getDisposerMethod();

  }

}
