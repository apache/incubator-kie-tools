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


package org.jboss.errai.ioc.rebind.ioc.graph.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.ProducerInstanceDependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

/**
 * Concrete here does not mean "for a concrete class". Rather, it means that
 * this injectable was for bean that we know how to produce (either because it
 * is a scoped type or from producer member). In contrast,
 * {@link InjectableReference abstract injectables} are used in unresolved
 * dependencies to represent a injectable that we do not yet know how to
 * construct.
 *
 * When the {@link DependencyGraphImpl} is constructed, resolution has occurred
 * and it will contain only concrete injecables.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
class InjectableImpl extends InjectableBase implements Injectable {
  final InjectableType injectableType;
  final Collection<WiringElementType> wiringTypes;
  final List<BaseDependency> dependencies = new ArrayList<>();
  final Class<? extends Annotation> literalScope;
  Boolean proxiable = null;
  boolean requiresProxy = false;
  Integer hashContent = null;
  final String factoryName;
  final Predicate<List<InjectableHandle>> pathPredicate;

  InjectableImpl(final MetaClass type,
                     final Qualifier qualifier,
                     final Predicate<List<InjectableHandle>> pathPredicate,
                     final String factoryName,
                     final Class<? extends Annotation> literalScope,
                     final InjectableType injectorType,
                     final Collection<WiringElementType> wiringTypes) {
    super(type, qualifier);
    this.pathPredicate = pathPredicate;
    this.factoryName = factoryName;
    this.literalScope = literalScope;
    this.wiringTypes = wiringTypes;
    this.injectableType = injectorType;
  }

  @Override
  public String getFactoryName() {
    return factoryName;
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return literalScope;
  }

  @Override
  public InjectableType getInjectableType() {
    return injectableType;
  }

  @Override
  public Collection<Dependency> getDependencies() {
    return Collections.<Dependency>unmodifiableCollection(dependencies);
  }

  @Override
  public boolean loadAsync() {
    return wiringTypes.contains(WiringElementType.LoadAsync);
  }

  @Override
  public boolean requiresProxy() {
    switch (injectableType) {
    case ContextualProvider:
    case Provider:
      return false;
    case Producer:
    case Type:
    case JsType:
    case ExtensionProvided:
      return requiresProxy || wiringTypes.contains(WiringElementType.NormalScopedBean);
    case Extension:
    default:
      throw new RuntimeException("Not yet implemented!");
    }
  }

  @Override
  public Optional<HasAnnotations> getAnnotatedObject() {
    switch (injectableType) {
    case Type:
      return Optional.of(type);
    case Producer:
    case Static:
    case Provider:
    case ContextualProvider:
      return dependencies
              .stream()
              .filter(dep -> DependencyType.ProducerMember.equals(dep.dependencyType))
              .map(dep -> (HasAnnotations) ((ProducerInstanceDependency) dep).getProducingMember())
              .findFirst();
    case ExtensionProvided:
    case Extension:
    case JsType:
    case Disabled:
    default:
      return Optional.empty();
    }
  }

  @Override
  public Collection<WiringElementType> getWiringElementTypes() {
    return Collections.unmodifiableCollection(wiringTypes);
  }

  @Override
  public boolean isContextual() {
    return InjectableType.ContextualProvider.equals(injectableType);
  }

  @Override
  public void setRequiresProxyTrue() {
    requiresProxy = true;
  }

  @Override
  public boolean isExtension() {
    return false;
  }

  @Override
  public int hashContent() {
    if (hashContent == null) {
      hashContent = computeHashContent();
    }

    return hashContent;
  }

  private int computeHashContent() {
    int hashContent = type.hashContent();
    for (final BaseDependency dep: dependencies) {
      hashContent ^= dep.injectable.resolution.getInjectedType().hashContent();
    }

    return hashContent;
  }

  @Override
  public String toString() {
    final String injectableDescriptor;
    switch (injectableType) {
    case ContextualProvider:
      injectableDescriptor = "ContextualProvider";
      break;
    case Disabled:
      injectableDescriptor = "Disabled";
      break;
    case Extension:
      injectableDescriptor = "Extension";
      break;
    case ExtensionProvided:
      injectableDescriptor = "ExtensionProvided";
      break;
    case JsType:
      injectableDescriptor = "@JsType";
      break;
    case Producer:
      injectableDescriptor = "@Produces";
      break;
    case Provider:
      injectableDescriptor = "Provider";
      break;
    case Static:
      injectableDescriptor = "@Produces";
      break;
    case Type:
      injectableDescriptor = "Class";
      break;
    default:
      injectableDescriptor = "";
      break;
    }

    return String.format("%s %s %s", injectableDescriptor, getQualifier(), getInjectedType().getFullyQualifiedNameWithTypeParms());
  }
}
