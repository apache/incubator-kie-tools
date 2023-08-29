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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessor;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.InjectionSite;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;
import org.jboss.errai.ioc.rebind.ioc.graph.api.QualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.ioc.rebind.ioc.graph.impl.ResolutionPriority.getMatchingPriority;

/**
 * @see DependencyGraphBuilder
 * @author Max Barkley <mbarkley@redhat.com>
 */
public final class DependencyGraphBuilderImpl implements DependencyGraphBuilder {

  private static final Logger logger = LoggerFactory.getLogger(DependencyGraphBuilder.class);

  private final QualifierFactory qualFactory;
  private final Map<InjectableHandle, InjectableReference> injectableReferences = new HashMap<>();
  private final Multimap<MetaClass, InjectableReference> directInjectableReferencesByAssignableTypes = HashMultimap.create();
  private final Map<String, Injectable> injectablesByName = new HashMap<>();
  private final List<InjectableImpl> specializations = new ArrayList<>();
  private final FactoryNameGenerator nameGenerator = new FactoryNameGenerator();
  private final boolean async;

  public DependencyGraphBuilderImpl(final QualifierFactory qualFactory, final boolean async) {
    this.qualFactory = qualFactory;
    this.async = async;
  }

  @Override
  public Injectable addInjectable(final MetaClass injectedType, final Qualifier qualifier,
          final Predicate<List<InjectableHandle>> pathPredicate, final Class<? extends Annotation> literalScope,
          final InjectableType injectableType, final WiringElementType... wiringTypes) {
    final InjectableImpl injectable = new InjectableImpl(injectedType, qualifier, pathPredicate,
            nameGenerator.generateFor(injectedType, qualifier, injectableType), literalScope, injectableType,
            Arrays.asList(wiringTypes));

    return registerNewInjectable(injectable);
  }

  private Injectable registerNewInjectable(final InjectableImpl injectable) {
    logAddedInjectable(injectable);
    final String factoryName = injectable.getFactoryName();
    if (injectablesByName.containsKey(factoryName)) {
      GraphUtil.throwDuplicateConcreteInjectableException(factoryName, injectablesByName.get(factoryName), injectable);
    }
    injectablesByName.put(factoryName, injectable);
    if (injectable.wiringTypes.contains(WiringElementType.Specialization)) {
      specializations.add(injectable);
    }
    linkDirectInjectableReference(injectable);

    return injectable;
  }

  private void logAddedInjectable(final Injectable injectable) {
    logger.debug("Adding new injectable: {}", injectable);
    if (logger.isTraceEnabled()) {
      logger.trace("Injectable type: {}", injectable.getInjectableType());
      logger.trace("Injectable wiring types: {}", injectable.getWiringElementTypes());
    }
  }

  @Override
  public Injectable addExtensionInjectable(final MetaClass injectedType, final Qualifier qualifier,
          final Predicate<List<InjectableHandle>> pathPredicate, final InjectableProvider provider,
          final WiringElementType... wiringTypes) {
    final InjectableImpl injectable = new ExtensionInjectable(injectedType, qualifier, pathPredicate,
            nameGenerator.generateFor(injectedType, qualifier, InjectableType.Extension), null,
            InjectableType.Extension, Arrays.asList(wiringTypes), provider);
    return registerNewInjectable(injectable);
  }

  private void linkDirectInjectableReference(final InjectableImpl injectable) {
    final InjectableReference injectableReference = lookupInjectableReference(injectable.type, injectable.qualifier);
    injectableReference.linked.add(injectable);
  }

  private void processAssignableTypes(final InjectableReference injectableReference) {
    for (final MetaClass assignable : injectableReference.type.getAllSuperTypesAndInterfaces()) {
      try {
        directInjectableReferencesByAssignableTypes.put(assignable.getErased(), injectableReference);
      } catch (final Throwable t) {
        throw new RuntimeException("Error occurred adding the assignable type " + assignable.getFullyQualifiedName(), t);
      }
    }
  }

  private InjectableReference lookupInjectableReference(final MetaClass type, final Qualifier qualifier) {
    final InjectableHandle handle = new InjectableHandle(type, qualifier);
    InjectableReference injectableReference = injectableReferences.get(handle);
    if (injectableReference == null) {
      injectableReference = new InjectableReference(type, qualifier);
      injectableReferences.put(handle, injectableReference);
      processAssignableTypes(injectableReference);
    }

    return injectableReference;
  }

  private void addDependency(final Injectable injectable, final Dependency dependency) {
    assert (injectable instanceof InjectableImpl);
    if (InjectableType.Disabled.equals(injectable.getInjectableType())
            && (!DependencyType.ProducerMember.equals(dependency.getDependencyType())
                    || !injectable.getDependencies().isEmpty())) {
      throw new RuntimeException("The injectable, " + injectable + ", is disabled."
              + " A disabled injectable may only have a single dependency if it is produced by a disabled bean.");
    }

    final InjectableImpl injectableAsImpl = (InjectableImpl) injectable;
    injectableAsImpl.dependencies.add(BaseDependency.class.cast(dependency));
  }

  @Override
  public DependencyGraph createGraph(final ReachabilityStrategy strategy) {
    logger.debug("Creating dependency graph...");
    resolveSpecializations();
    linkInjectableReferences();
    resolveDependencies();
    validateInjectables();
    removeUnreachableInjectables(strategy);
    logger.debug("Finished creating dependency graph.");

    return new DependencyGraphImpl(injectablesByName);
  }

  private Collection<Validator> createValidators() {
    final Collection<Validator> validators = new ArrayList<>();
    validators.add(new CycleValidator());
    if (async) {
      validators.add(new AsyncValidator());
    }

    return validators;
  }

  private void resolveSpecializations() {
    logger.debug("Processing {} specializations...", specializations.size());
    final Set<InjectableImpl> toBeRemoved = new HashSet<>();
    GraphUtil.sortSuperTypesBeforeSubtypes(specializations);
    for (final InjectableImpl specialization : specializations) {
      if (specialization.injectableType.equals(InjectableType.Producer)) {
        resolveProducerSpecialization(specialization, toBeRemoved);
      } else {
        resolveTypeSpecialization(specialization, toBeRemoved);
      }
    }
    logger.debug("Removed {} beans that were specialized.", toBeRemoved.size());
    logger.trace("Types removed by specialization: {}", toBeRemoved);
    injectablesByName.values().removeAll(toBeRemoved);
  }

  private void resolveProducerSpecialization(final InjectableImpl specialization, final Set<InjectableImpl> toBeRemoved) {
    final ProducerInstanceDependencyImpl producerMemberDep = GraphUtil.findProducerInstanceDep(specialization);
    if (producerMemberDep.producingMember instanceof MetaMethod) {
      final MetaMethod specializedMethod = GraphUtil.getOverridenMethod((MetaMethod) producerMemberDep.producingMember);
      final MetaClass specializingType = producerMemberDep.producingMember.getDeclaringClass();
      if (specializedMethod != null && specializedMethod.isAnnotationPresent(Produces.class)) {
        updateLinksToSpecialized(specialization, toBeRemoved, specializedMethod, specializingType);
      }
    } else {
      throw new RuntimeException("Specialized producers can only be methods. Found " + producerMemberDep.producingMember
              + " in " + producerMemberDep.producingMember.getDeclaringClassName());
    }
  }

  private void updateLinksToSpecialized(final InjectableImpl specialization, final Set<InjectableImpl> toBeRemoved,
          final MetaMethod specializedMethod, final MetaClass specializingType) {
    final MetaClass enclosingType = specializedMethod.getDeclaringClass();
    final MetaClass producedType = specializedMethod.getReturnType().getErased();

    /*
     * Need to copy this because the call to lookupInjectableReference may modify the collection.
     */
    final Collection<InjectableReference> directInjectableReferencesOfProducedType = new ArrayList<>(
            directInjectableReferencesByAssignableTypes.get(producedType));
    for (final InjectableReference injectable : directInjectableReferencesOfProducedType) {
      if (injectable.type.equals(producedType)) {
        final Iterator<InjectableBase> linkedIter = injectable.linked.iterator();
        while (linkedIter.hasNext()) {
          final InjectableBase link = linkedIter.next();
          if (link instanceof InjectableImpl) {
            final InjectableImpl concreteLink = (InjectableImpl) link;
            removeSpecializedAndSpecializingLinks(specialization, toBeRemoved, specializingType, enclosingType, linkedIter, concreteLink);
          }
        }
        injectable.linked.add(lookupInjectableReference(specialization.type, specialization.qualifier));
      }
    }
  }

  private void removeSpecializedAndSpecializingLinks(final InjectableImpl specialization, final Set<InjectableImpl> toBeRemoved,
          final MetaClass specializingType, final MetaClass enclosingType, final Iterator<InjectableBase> linkedIter,
          final InjectableImpl linkedInjectable) {
    if (linkedInjectable.injectableType.equals(InjectableType.Producer)) {
      final MetaClass foundProducerType = GraphUtil.findProducerInstanceDep(linkedInjectable).injectable.type.getErased();
      if (foundProducerType.equals(enclosingType.getErased())
              || foundProducerType.equals(specializingType.getErased())) {
        linkedIter.remove();
      }
      if (foundProducerType.equals(enclosingType.getErased())) {
        toBeRemoved.add(linkedInjectable);
        specialization.qualifier = qualFactory.combine(specialization.qualifier, linkedInjectable.qualifier);
      }
    }
  }

  private void resolveTypeSpecialization(final InjectableImpl specialization, final Set<InjectableImpl> toBeRemoved) {
    final MetaClass specializedType = specialization.type.getSuperClass().getErased();
    for (final InjectableReference injectable : directInjectableReferencesByAssignableTypes.get(specializedType)) {
      if (injectable.type.equals(specializedType)) {
        if (!injectable.linked.isEmpty()) {
          updateSpecializedReferenceLinks(specialization, toBeRemoved, injectable);
          break;
        }
      }
    }
  }

  private void updateSpecializedReferenceLinks(final InjectableImpl specialization,
          final Set<InjectableImpl> toBeRemoved, final InjectableReference injectableReference) {
    assert injectableReference.linked.size() == 1 : "The injectable " + injectableReference
            + " should have one link but instead has:\n" + injectableReference.linked;
    final InjectableImpl specialized = (InjectableImpl) injectableReference.linked.iterator().next();
    specialization.qualifier = qualFactory.combine(specialization.qualifier, specialized.qualifier);
    toBeRemoved.add(specialized);
    injectableReference.linked.clear();
    injectableReference.linked.add(lookupInjectableReference(specialization.type, specialization.qualifier));
    removeLinksToProducedTypes(specialized, toBeRemoved);
  }

  private void removeLinksToProducedTypes(final InjectableImpl specialized, final Set<InjectableImpl> toBeRemoved) {
    final Collection<InjectableReference> producedReferences = new ArrayList<>();
    for (final MetaMethod method : specialized.type.getDeclaredMethodsAnnotatedWith(Produces.class)) {
      producedReferences.add(lookupInjectableReference(method.getReturnType(), qualFactory.forSource(method)));
    }
    for (final MetaField field : specialized.type.getDeclaredFields()) {
      if (field.isAnnotationPresent(Produces.class)) {
        producedReferences.add(lookupInjectableReference(field.getType(), qualFactory.forSource(field)));
      }
    }

    for (final InjectableReference reference : producedReferences) {
      final Iterator<InjectableBase> linkIter = reference.linked.iterator();
      while (linkIter.hasNext()) {
        final InjectableBase link = linkIter.next();
        if (link instanceof InjectableImpl && ((InjectableImpl) link).injectableType.equals(InjectableType.Producer)) {
          final InjectableImpl concreteLink = (InjectableImpl) link;
          final ProducerInstanceDependencyImpl producerMemberDep = GraphUtil.findProducerInstanceDep(concreteLink);
          if (producerMemberDep.producingMember.getDeclaringClass().equals(specialized.type)) {
            linkIter.remove();
            toBeRemoved.add(concreteLink);
          }
        }
      }
    }
  }

  private void validateInjectables() {
    logger.debug("Validating dependency graph...");
    final Collection<String> problems = new ArrayList<>();
    final Collection<Validator> validators = createValidators();
    for (final Injectable injectable : injectablesByName.values()) {
      for (final Validator validator : validators) {
        if (validator.canValidate(injectable)) {
          validator.validate(injectable, problems);
        }
      }
    }
    if (!problems.isEmpty()) {
      throw new RuntimeException(GraphUtil.combineProblemMessages(problems));
    }
  }

  private void removeUnreachableInjectables(final ReachabilityStrategy strategy) {
    logger.debug("Removing unreachable injectables from dependency graph using {} strategy.", strategy);
    final Set<String> reachableNames = new HashSet<>();
    final Queue<Injectable> processingQueue = new LinkedList<>();
    final Predicate<Injectable> reachabilityRoot = reachabilityRootPredicate(strategy);
    for (final Injectable injectable : injectablesByName.values()) {
      if (reachabilityRoot.test(injectable)
              && !reachableNames.contains(injectable.getFactoryName())
              && !InjectableType.Disabled.equals(injectable.getInjectableType())) {
        processingQueue.add(injectable);
        do {
          final Injectable processedInjectable = processingQueue.poll();
          reachableNames.add(processedInjectable.getFactoryName());
          logger.trace("Marked as reachable: {}", processedInjectable);
          for (final Dependency dep : processedInjectable.getDependencies()) {
            final Injectable resolvedDep = GraphUtil.getResolvedDependency(dep, processedInjectable);
            if (!reachableNames.contains(resolvedDep.getFactoryName())) {
              processingQueue.add(resolvedDep);
            }
          }
        } while (processingQueue.size() > 0);
      }
    }

    final int initialSize = injectablesByName.size();
    injectablesByName.keySet().retainAll(reachableNames);
    logger.debug("Removed {} unreachable injectables.", initialSize - injectablesByName.size());
  }

  private Predicate<Injectable> reachabilityRootPredicate(final ReachabilityStrategy strategy) {
    switch (strategy) {
    case All:
      return inj -> true;
    case Annotated:
      return inj -> !inj.getWiringElementTypes().contains(WiringElementType.Simpleton);
    case Aggressive:
      return inj -> EntryPoint.class.equals(inj.getScope()) || inj.getWiringElementTypes().contains(WiringElementType.JsType);
    default:
      throw new RuntimeException("Unrecognized reachability strategy, " + strategy.toString());
    }
  }

  private void resolveDependencies() {
    logger.debug("Resolving dependencies for {} injectables...", injectablesByName.size());
    final Set<Injectable> visited = new HashSet<>();
    final Set<String> transientInjectableNames = new HashSet<>();
    final List<String> dependencyProblems = new ArrayList<>();
    final Map<String, Injectable> customProvidedInjectables = new IdentityHashMap<>();

    for (final Injectable injectable : injectablesByName.values()) {
      if (injectable.isExtension()) {
        transientInjectableNames.add(injectable.getFactoryName());
      }
      if (!visited.contains(injectable)) {
        logger.debug("Resolving {} dependencies for: {}", injectable.getDependencies().size(), injectable);
        for (final Dependency dep : injectable.getDependencies()) {
          resolveDependency(BaseDependency.as(dep), injectable, dependencyProblems, customProvidedInjectables);
        }
      }
    }

    injectablesByName.keySet().removeAll(transientInjectableNames);
    injectablesByName.putAll(customProvidedInjectables);

    if (!dependencyProblems.isEmpty()) {
      throw new RuntimeException(GraphUtil.buildMessageFromProblems(dependencyProblems));
    }
  }

  private Injectable resolveDependency(final BaseDependency dep, final Injectable depOwner,
          final Collection<String> problems,
          final Map<String, Injectable> customProvidedInjectables) {
    if (dep.injectable.resolution != null) {
      return dep.injectable.resolution;
    }

    logger.trace("Resolving dependency: {}", dep);
    final Multimap<ResolutionPriority, InjectableImpl> resolvedByPriority = traverseLinks(dep.injectable);

    final Iterable<ResolutionPriority> priorities;
    final boolean reportProblems;
    if (InjectableType.Disabled.equals(depOwner.getInjectableType())) {
      priorities = Collections.singleton(ResolutionPriority.Disabled);
      reportProblems = false;
    }
    else {
      priorities = ResolutionPriority.enabledValues();
      reportProblems = true;
    }

    // Iterates through priorities from highest to lowest.
    for (final ResolutionPriority priority : priorities) {
      if (resolvedByPriority.containsKey(priority)) {
        final Collection<InjectableImpl> resolved = resolvedByPriority.get(priority);
        if (resolved.size() > 1) {
          if (reportProblems) {
            problems.add(GraphUtil.ambiguousDependencyMessage(dep, depOwner, new ArrayList<>(resolved)));
          }

          return null;
        } else {
          final Injectable injectable = maybeProcessAsExtension(dep, depOwner, customProvidedInjectables, resolvedByPriority, resolved);
          logger.trace("Resolved dependency: {}", injectable);

          return (dep.injectable.resolution = injectable);
        }
      }
    }

    if (reportProblems) {
      final Collection<Injectable> resolvedDisabledInjectables =
              resolvedByPriority
                .get(ResolutionPriority.Disabled)
                .stream()
                .map(inj -> getRootDisabledInjectable(inj, problems, customProvidedInjectables))
                .collect(Collectors.toList());

      problems.add(GraphUtil.unsatisfiedDependencyMessage(dep, depOwner, resolvedDisabledInjectables));
    }
    return null;
  }

  private Injectable maybeProcessAsExtension(final BaseDependency dep, final Injectable depOwner,
          final Map<String, Injectable> customProvidedInjectables,
          final Multimap<ResolutionPriority, InjectableImpl> resolvedByPriority,
          final Collection<InjectableImpl> resolved) {
    Injectable injectable = resolved.iterator().next();
    if (injectable.isExtension()) {
      final ExtensionInjectable providedInjectable = (ExtensionInjectable) injectable;
      final Collection<Injectable> otherResolvedInjectables = new ArrayList<>(resolvedByPriority.values());
      otherResolvedInjectables.remove(injectable);

      final InjectionSite site = new InjectionSite(depOwner.getInjectedType(), dep, otherResolvedInjectables);
      injectable = providedInjectable.provider.getInjectable(site, nameGenerator);
      customProvidedInjectables.put(injectable.getFactoryName(), injectable);
      dep.injectable = GraphUtil.copyInjectableReference(dep.injectable);
    }

    return injectable;
  }

  private Multimap<ResolutionPriority, InjectableImpl> traverseLinks(final InjectableReference dep) {
    final Multimap<ResolutionPriority, InjectableImpl> resolvedByPriority = HashMultimap.create();
    final LinkedList<InjectableHandle> handleStack = new LinkedList<>();
    final LinkedList<Iterator<InjectableBase>> linkStack = new LinkedList<>();
    handleStack.addLast(dep.getHandle());
    linkStack.addLast(dep.linked.iterator());
    do {
      final Iterator<InjectableBase> iter = linkStack.getLast();
      if (iter.hasNext()) {
        final InjectableBase link = iter.next();
        if (link instanceof InjectableReference) {
          logger.trace("Adding linked reference to resolution stack: {}", link);
          handleStack.addLast(link.getHandle());
          if (isRawType(link.type)) {
            /*
             * SPECIAL CASE:
             * Java types with the relation "assignableTo" would form an ordering of types,
             * EXCEPT that raw types are assignable to any parameterization and vice-versa.
             *
             * This breaks anti-symmetry (A<B> assignableTo A and A assignableTo A<B> but A != A<B>)
             * and transitivity (for unrelated B and C, A<B> assignableTo A assignableTo A<C> but NOT
             * A<B> assignableTo A<C> since NOT B assignableTo C).
             *
             * As a consequence, following reference links from a raw type can lead to cycles (from lack of anti-symmetry)
             * or incorrect resolution of parameterized types (from lack of transitivity).
             *
             * The only time following reference links from a raw type is acceptable is when they
             * are at the beginning of a path being traversed (since then we really do want to find any possible
             * parameterization, and when we arrive back at the same raw-typed reference we will ignore the reference
             * links that we have already traversed, which avoids the possibility of an endless cycle).
             */
            linkStack.addLast(getIteratorOfRawTypedInjectableLinks(link));
          }
          else {
            linkStack.addLast(((InjectableReference) link).linked.iterator());
          }
        } else if (link instanceof InjectableImpl) {
          final InjectableImpl resolvedInjectable = (InjectableImpl) link;
          if (resolvedInjectable.pathPredicate.test(handleStack)) {
            logger.trace("Adding linked injectable to resolution results: {}", link);
            resolvedByPriority.put(getMatchingPriority(resolvedInjectable), resolvedInjectable);
          }
          else {
            logger.trace("Rejecting linked injectable from resolution results based on path predicate: {}", link);
          }
        }
      }
      else {
        handleStack.removeLast();
        linkStack.removeLast();
      }
    } while (!linkStack.isEmpty());
    logger.trace("Finished processing resolution stack. Resolved {} injectables.", resolvedByPriority.size());

    return resolvedByPriority;
  }

  private Iterator<InjectableBase> getIteratorOfRawTypedInjectableLinks(final InjectableBase link) {
    final List<InjectableBase> injectableLinks =
            ((InjectableReference) link)
            .linked
            .stream()
            .filter(l -> l instanceof InjectableImpl)
            .filter(l -> isRawType(l.type))
            .collect(Collectors.toList());
    final Iterator<InjectableBase> injectablesInterator = injectableLinks.iterator();
    return injectablesInterator;
  }

  private boolean isRawType(final MetaClass type) {
    // Guaranteed to be true for all raw types in all MetaClass impls
    return type == type.getErased();
  }

  private Injectable getRootDisabledInjectable(Injectable inj, final Collection<String> problems,
          final Map<String, Injectable> customProvidedInjectables) {
    while (inj.getDependencies().size() == 1) {
      final Dependency dep = inj.getDependencies().iterator().next();
      if (DependencyType.ProducerMember.equals(dep.getDependencyType())) {
        inj = resolveDependency((BaseDependency) dep, inj, problems, customProvidedInjectables);
      }
    }

    return inj;
  }

  private void linkInjectableReferences() {
    logger.debug("Linking {} references in dependencies...", injectableReferences.size());
    final Set<InjectableReference> linked = new HashSet<>(injectableReferences.size());
    for (final Injectable injectable : injectablesByName.values()) {
      for (final Dependency dep : injectable.getDependencies()) {
        final BaseDependency baseDep = BaseDependency.as(dep);
        if (!linked.contains(baseDep.injectable)) {
          logger.debug("Processing dependency: {}", baseDep);
          linkInjectableReference(baseDep.injectable);
          linked.add(baseDep.injectable);
        }
      }
    }
  }

  private void linkInjectableReference(final InjectableReference injectableReference) {
    final Collection<InjectableReference> candidates = directInjectableReferencesByAssignableTypes
            .get(injectableReference.type.getErased());
    logger.debug("Found {} candidate references.", candidates.size());
    for (final InjectableReference candidate : candidates) {
      if (GraphUtil.candidateSatisfiesInjectable(injectableReference, candidate)) {
        logger.trace("Candidate has been linked: {}", candidate);
        injectableReference.linked.add(candidate);
      }
    }
  }

  private InjectableReference createStaticMemberInjectable(final MetaClass producerType, final MetaClassMember member) {
    final InjectableReference retVal = new InjectableReference(producerType, qualFactory.forUniversallyQualified());
    retVal.resolution = new InjectableImpl(producerType, qualFactory.forUniversallyQualified(), IOCProcessor.ANY, "",
            ApplicationScoped.class, InjectableType.Static, Collections.<WiringElementType> emptyList());

    return retVal;
  }

  @Override
  public void addFieldDependency(final Injectable concreteInjectable, final MetaClass type, final Qualifier qualifier,
          final MetaField dependentField) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final FieldDependency dep = new FieldDependencyImpl(injectableReference, dependentField);
    addDependency(concreteInjectable, dep);
  }

  @Override
  public void addConstructorDependency(final Injectable concreteInjectable, final MetaClass type,
          final Qualifier qualifier, final int paramIndex, final MetaParameter param) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final ParamDependency dep = new ParamDependencyImpl(injectableReference, DependencyType.Constructor, paramIndex,
            param);
    addDependency(concreteInjectable, dep);
  }

  @Override
  public void addProducerParamDependency(final Injectable concreteInjectable, final MetaClass type,
          final Qualifier qualifier, final int paramIndex, final MetaParameter param) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final ParamDependency dep = new ParamDependencyImpl(injectableReference, DependencyType.ProducerParameter,
            paramIndex, param);
    addDependency(concreteInjectable, dep);
  }

  @Override
  public void addProducerMemberDependency(final Injectable concreteInjectable, final MetaClass type,
          final Qualifier qualifier, final MetaClassMember producingMember) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final ProducerInstanceDependency dep = new ProducerInstanceDependencyImpl(injectableReference,
            DependencyType.ProducerMember, producingMember);
    addDependency(concreteInjectable, dep);
  }

  @Override
  public void addProducerMemberDependency(final Injectable producedInjectable, final MetaClass producerType, final MetaClassMember member) {
    final InjectableReference abstractInjectable = createStaticMemberInjectable(producerType, member);
    final ProducerInstanceDependency dep = new ProducerInstanceDependencyImpl(
            abstractInjectable, DependencyType.ProducerMember, member);
    addDependency(producedInjectable, dep);
  }

  @Override
  public void addSetterMethodDependency(final Injectable concreteInjectable, final MetaClass type,
          final Qualifier qualifier, final MetaMethod setter) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final SetterParameterDependency dep = new SetterParameterDependencyImpl(injectableReference, setter);
    addDependency(concreteInjectable, dep);
  }

  @Override
  public void addDisposesMethodDependency(final Injectable concreteInjectable, final MetaClass type, final Qualifier qualifier, final MetaMethod disposer) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final DisposerMethodDependency dep = new DisposerMethodDependencyImpl(injectableReference, disposer);
    addDependency(concreteInjectable, dep);
  }

  @Override
  public void addDisposesParamDependency(final Injectable concreteInjectable, final MetaClass type, final Qualifier qualifier,
          final Integer index, final MetaParameter param) {
    final InjectableReference injectableReference = lookupInjectableReference(type, qualifier);
    final ParamDependency dep = new ParamDependencyImpl(injectableReference, DependencyType.DisposerParameter, index,
            param);
    addDependency(concreteInjectable, dep);
  }

}
