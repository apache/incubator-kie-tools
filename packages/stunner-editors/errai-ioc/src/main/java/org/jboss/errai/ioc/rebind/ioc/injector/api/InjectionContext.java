/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ioc.rebind.ioc.injector.api;

import static java.util.Collections.unmodifiableCollection;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.NormalScope;
import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;
import javax.inject.Scope;

import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessor;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.QualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultQualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.reflections.util.SimplePackageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * At every rebind phase, a single {@link InjectionContext} is used. It contains
 * information on enabled alternatives, allowlisted and denylisted beans, and
 * annotations associated with various {@link WiringElementType wiring types}.
 *
 * The injection context also stores string-named attributes for sharing data
 * between the {@link IOCProcessor} and separate usages of the
 * {@link FactoryGenerator}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class InjectionContext {
  private static final Logger log = LoggerFactory.getLogger(InjectionContext.class);

  private final IOCProcessingContext processingContext;

  private final Multimap<WiringElementType, Class<? extends Annotation>> elementBindings = HashMultimap.create();
  private final Multimap<InjectableHandle, InjectableProvider> injectableProviders = HashMultimap.create();
  private final Multimap<InjectableHandle, InjectableProvider> exactTypeInjectableProviders = HashMultimap.create();

  private final Collection<ExtensionTypeCallback> extensionTypeCallbacks = new ArrayList<ExtensionTypeCallback>();

  private final boolean async;

  private final QualifierFactory qualifierFactory;

  private final Set<String> allowlist;
  private final Set<String> denylist;

  private static final String[] implicitAllowlist = { "org.jboss.errai.*", "com.google.gwt.*" };

  private final Multimap<Class<? extends Annotation>, IOCDecoratorExtension<? extends Annotation>> decorators = HashMultimap.create();
  private final Multimap<ElementType, Class<? extends Annotation>> decoratorsByElementType = HashMultimap.create();
  private final Multimap<Class<? extends Annotation>, Class<? extends Annotation>> metaAnnotationAliases
      = HashMultimap.create();

  private final Map<String, Object> attributeMap = new HashMap<String, Object>();


  private InjectionContext(final Builder builder) {
    this.processingContext = Assert.notNull(builder.processingContext);
    if (builder.qualifierFactory == null) {
      this.qualifierFactory = new DefaultQualifierFactory();
    } else {
      this.qualifierFactory = builder.qualifierFactory;
    }
    this.allowlist = Assert.notNull(builder.allowlist);
    this.denylist = Assert.notNull(builder.denylist);
    this.async = builder.async;
  }

  public static class Builder {
    private IOCProcessingContext processingContext;
    private boolean async;
    private QualifierFactory qualifierFactory;
    private final HashSet<String> enabledAlternatives = new HashSet<String>();
    private final HashSet<String> allowlist = new HashSet<String>();
    private final HashSet<String> denylist = new HashSet<String>();

    public static Builder create() {
      return new Builder();
    }

    public Builder qualifierFactory(final QualifierFactory qualifierFactory) {
      this.qualifierFactory = qualifierFactory;
      return this;
    }

    public Builder processingContext(final IOCProcessingContext processingContext) {
      this.processingContext = processingContext;
      return this;
    }

    public Builder enabledAlternative(final String fqcn) {
      enabledAlternatives.add(fqcn);
      return this;
    }

    public Builder addToAllowlist(final String item) {
      allowlist.add(item);
      return this;
    }

    public Builder addToDenylist(final String item) {
      denylist.add(item);
      return this;
    }

    public Builder asyncBootstrap(final boolean async) {
      this.async = async;
      return this;
    }

    public InjectionContext build() {
      Assert.notNull("the processingContext cannot be null", processingContext);

      return new InjectionContext(this);
    }
  }

  /**
   * Register an {@link InjectableProvider} for injection sites that are
   * sastisfied by the given {@link InjectableHandle}.
   *
   * @param handle
   *          Contains the type and qualifier that the given provider satisfies.
   * @param provider
   *          The
   *          {@link InjectableProvider#getInjectable(org.jboss.errai.ioc.rebind.ioc.graph.api.ProvidedInjectable.InjectionSite, FactoryNameGenerator)}
   *          will be called for every injection site satisified by the given
   *          handle. The returned {@link FactoryBodyGenerator} will be used to
   *          generate factories specific to the given injection sites.
   */
  public void registerInjectableProvider(final InjectableHandle handle, final InjectableProvider provider) {
    injectableProviders.put(handle, provider);
  }

  /**
   * Like
   * {@link #registerInjectableProvider(InjectableHandle, InjectableProvider)},
   * but only injection sites with the exact type of the given
   * {@link InjectableHandle} are satisfied.
   *
   * @param handle
   *          Contains the exact type and qualifier that the given provider
   *          satisfies.
   * @param provider
   *          The
   *          {@link InjectableProvider#getInjectable(org.jboss.errai.ioc.rebind.ioc.graph.api.ProvidedInjectable.InjectionSite, FactoryNameGenerator)}
   *          will be called for every injection site satisified by the given
   *          handle. The returned {@link FactoryBodyGenerator} will be used to
   *          generate factories specific to the given injection sites.
   */
  public void registerExactTypeInjectableProvider(final InjectableHandle handle, final InjectableProvider provider) {
    exactTypeInjectableProviders.put(handle, provider);
  }

  /**
   * An {@link ExtensionTypeCallback} registered with this method will be called for every type processed the Errai IoC
   * before the dependency graph is built. This gives {@link IOCExtensionConfigurator IOCExtensionConfigurators} a way
   * of registering {@link #registerInjectableProvider(InjectableHandle, InjectableProvider) injectable providers}
   * dynamically.
   *
   * @param callback Never null.
   */
  public void registerExtensionTypeCallback(final ExtensionTypeCallback callback) {
    extensionTypeCallbacks.add(callback);
  }

  public Collection<ExtensionTypeCallback> getExtensionTypeCallbacks() {
    return Collections.unmodifiableCollection(extensionTypeCallbacks);
  }

  public Multimap<InjectableHandle, InjectableProvider> getInjectableProviders() {
    return Multimaps.unmodifiableMultimap(injectableProviders);
  }

  public Multimap<InjectableHandle, InjectableProvider> getExactTypeInjectableProviders() {
    return Multimaps.unmodifiableMultimap(exactTypeInjectableProviders);
  }

  public QualifierFactory getQualifierFactory() {
    return qualifierFactory;
  }

  public boolean isIncluded(final MetaClass type) {
    return isAllowlisted(type) && !isDenylisted(type);
  }

  public boolean isAllowlisted(final MetaClass type) {
    if (allowlist.isEmpty()) {
      return true;
    }

    final SimplePackageFilter implicitFilter = new SimplePackageFilter(Arrays.asList(implicitAllowlist));
    final SimplePackageFilter allowlistFilter = new SimplePackageFilter(allowlist);
    final String fullName = type.getFullyQualifiedName();

    return implicitFilter.apply(fullName) || allowlistFilter.apply(fullName);
  }

  public boolean isDenylisted(final MetaClass type) {
    final SimplePackageFilter denylistFilter = new SimplePackageFilter(denylist);
    final String fullName = type.getFullyQualifiedName();

    return denylistFilter.apply(fullName);
  }

  public void registerDecorator(final IOCDecoratorExtension<?> iocExtension) {
    final Class<? extends Annotation> annotation = iocExtension.decoratesWith();

    final Target target = annotation.getAnnotation(Target.class);
    if (target != null) {
      final boolean oneTarget = target.value().length == 1;

      for (final ElementType type : target.value()) {
        if (type == ElementType.ANNOTATION_TYPE) {
          // type is a meta-annotation. so we need to map all annotations with this
          // meta-annotation to the decorator extension.

          for (final MetaClass annotationClazz : ClassScanner.getTypesAnnotatedWith(annotation,
                  processingContext.getGeneratorContext())) {
            if (Annotation.class.isAssignableFrom(annotationClazz.asClass())) {
              final Class<? extends Annotation> javaAnnoCls = annotationClazz.asClass().asSubclass(Annotation.class);
              decorators.get(javaAnnoCls).add(iocExtension);

              if (oneTarget) {
                metaAnnotationAliases.put(javaAnnoCls, annotation);
              }
            }
          }
          if (oneTarget) {
            return;
          }
        }
      }
    }
    decorators.get(annotation).add(iocExtension);
  }

  public Set<Class<? extends Annotation>> getDecoratorAnnotations() {
    return Collections.unmodifiableSet(decorators.keySet());
  }

  public <A extends Annotation> IOCDecoratorExtension<A>[] getDecorators(final Class<A> annotation) {
    final Collection<IOCDecoratorExtension<?>> decs = decorators.get(annotation);
    @SuppressWarnings("unchecked")
    final IOCDecoratorExtension<A>[] da = new IOCDecoratorExtension[decs.size()];
    decs.toArray(da);

    return da;
  }

  public Collection<Class<? extends Annotation>> getDecoratorAnnotationsBy(final ElementType type) {
    if (decoratorsByElementType.size() == 0) {
      sortDecorators();
    }
    if (decoratorsByElementType.containsKey(type)) {
      return unmodifiableCollection(decoratorsByElementType.get(type));
    }
    else {
      return Collections.emptySet();
    }
  }

  public boolean isMetaAnnotationFor(final Class<? extends Annotation> alias, final Class<? extends Annotation> forAnno) {
    return metaAnnotationAliases.containsEntry(alias, forAnno);
  }

  private void sortDecorators() {
    for (final Class<? extends Annotation> a : getDecoratorAnnotations()) {
      if (a.isAnnotationPresent(Target.class)) {
        for (final ElementType type : a.getAnnotation(Target.class).value()) {
          decoratorsByElementType.get(type).add(a);
        }
      }
      else {
        for (final ElementType type : ElementType.values()) {
          decoratorsByElementType.get(type).add(a);
        }
      }
    }
  }

  public IOCProcessingContext getProcessingContext() {
    return processingContext;
  }

  public void mapElementType(final WiringElementType type, final Class<? extends Annotation> annotationType) {
    elementBindings.put(type, annotationType);
  }

  public Collection<Class<? extends Annotation>> getAnnotationsForElementType(final WiringElementType type) {
    return unmodifiableCollection(elementBindings.get(type));
  }

  public boolean isAnyKnownElementType(final HasAnnotations hasAnnotations) {
    return isAnyOfElementTypes(hasAnnotations, WiringElementType.values());
  }

  public boolean isAnyOfElementTypes(final HasAnnotations hasAnnotations, final WiringElementType... types) {
    for (final WiringElementType t : types) {
      if (isElementType(t, hasAnnotations))
        return true;
    }
    return false;
  }

  public boolean isElementType(final WiringElementType type, final HasAnnotations hasAnnotations) {
    final Annotation matchingAnnotation = getMatchingAnnotationForElementType(type, hasAnnotations);
    if (matchingAnnotation != null && type == WiringElementType.NotSupported) {
      log.error(hasAnnotations + " was annotated with " + matchingAnnotation.annotationType().getName()
          + " which is not supported in client-side Errai code!");
    }

    return matchingAnnotation != null;
  }

  public boolean isElementType(final WiringElementType type, final Class<? extends Annotation> annotation) {
    return getAnnotationsForElementType(type).contains(annotation);
  }

  /**
   * Overloaded version to check GWT's JClassType classes.
   *
   * @param type
   * @param hasAnnotations
   *
   * @return
   */
  public boolean isElementType(final WiringElementType type,
                               final com.google.gwt.core.ext.typeinfo.HasAnnotations hasAnnotations) {
    final Collection<Class<? extends Annotation>> annotationsForElementType = getAnnotationsForElementType(type);
    for (final Annotation a : hasAnnotations.getAnnotations()) {
      if (annotationsForElementType.contains(a.annotationType())) {
        return true;
      }
    }
    return false;
  }

  public Annotation getMatchingAnnotationForElementType(final WiringElementType type,
                                                        final HasAnnotations hasAnnotations) {

    final Collection<Class<? extends Annotation>> annotationsForElementType = getAnnotationsForElementType(type);

    for (final Annotation a : hasAnnotations.getAnnotations()) {
      if (annotationsForElementType.contains(a.annotationType())) {
        return a;
      }
    }

    final Set<Annotation> annotationSet = new HashSet<Annotation>();

    fillInStereotypes(annotationSet, hasAnnotations.getAnnotations(), false);

    for (final Annotation a : annotationSet) {
      if (annotationsForElementType.contains(a.annotationType())) {
        return a;
      }
    }
    return null;
  }

  private static void fillInStereotypes(final Set<Annotation> annotationSet,
                                        final Annotation[] from,
                                        boolean filterScopes) {

    final List<Class<? extends Annotation>> stereotypes
        = new ArrayList<Class<? extends Annotation>>();

    for (final Annotation a : from) {
      final Class<? extends Annotation> aClass = a.annotationType();
      if (aClass.isAnnotationPresent(Stereotype.class)) {
        stereotypes.add(aClass);
      }
      else if (!filterScopes &&
          aClass.isAnnotationPresent(NormalScope.class) ||
          aClass.isAnnotationPresent(Scope.class)) {
        filterScopes = true;

        annotationSet.add(a);
      }
      else if (aClass.isAnnotationPresent(Qualifier.class)) {
        annotationSet.add(a);
      }
    }

    for (final Class<? extends Annotation> stereotype : stereotypes) {
      fillInStereotypes(annotationSet, stereotype.getAnnotations(), filterScopes);
    }
  }

  public Collection<Map.Entry<WiringElementType, Class<? extends Annotation>>> getAllElementMappings() {
    return unmodifiableCollection(elementBindings.entries());
  }

  public void setAttribute(final String name, final Object value) {
    attributeMap.put(name, value);
  }

  public Object getAttribute(final String name) {
    return attributeMap.get(name);
  }

  public boolean hasAttribute(final String name) {
    return attributeMap.containsKey(name);
  }

  public boolean isAsync() {
    return async;
  }
}
