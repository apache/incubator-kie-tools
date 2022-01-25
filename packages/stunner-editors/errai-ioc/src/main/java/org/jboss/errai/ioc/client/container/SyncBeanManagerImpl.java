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

package org.jboss.errai.ioc.client.container;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.ioc.client.JsArray;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.WindowInjectionContext;
import org.jboss.errai.ioc.client.WindowInjectionContextStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A simple bean manager provided by the Errai IOC framework. The manager provides access to all of the wired beans
 * and their instances. Since the actual wiring code is generated, the bean manager is populated by the generated
 * code at bootstrap time.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Alternative
public class SyncBeanManagerImpl implements SyncBeanManager, BeanManagerSetup {

  private static final Logger logger = LoggerFactory.getLogger(SyncBeanManager.class);

  private ContextManager contextManager;
  private final Multimap<String, FactoryHandle> handlesByName = ArrayListMultimap.create();
  private final Multimap<String, SyncBeanDef<?>> runtimeBeanDefsByName = ArrayListMultimap.create();

  @Override
  public void destroyBean(final Object ref) {
    contextManager.destroy(ref);
  }

  @Override
  public boolean isManaged(final Object ref) {
    return contextManager.isManaged(ref);
  }

  @Override
  public Object getActualBeanReference(final Object ref) {
    return Factory.maybeUnwrapProxy(ref);
  }

  @Override
  public boolean isProxyReference(final Object ref) {
    return ref instanceof Proxy;
  }

  @Override
  public boolean addDestructionCallback(final Object beanInstance, final DestructionCallback<?> destructionCallback) {
    return contextManager.addDestructionCallback(beanInstance, destructionCallback);
  }

  @Override
  public void setContextManager(final ContextManager contextManager) {
    if (this.contextManager != null) {
      throw new RuntimeException("The ContextManager must only be set once.");
    }
    this.contextManager = contextManager;
    init();
  }

  private void init() {
    final Collection<FactoryHandle> eager = addFactories();
    initializeEagerBeans(eager);
  }

  private void initializeEagerBeans(final Collection<FactoryHandle> eager) {
    logger.debug("Initializing eager beans...");
    final long start = System.currentTimeMillis();
    if (logger.isTraceEnabled()) {
      for (final FactoryHandle handle : eager) {
        logger.trace("Initializing {}...", handle);
        final long beanStart = System.currentTimeMillis();
        contextManager.getEagerInstance(handle.getFactoryName());
        final long duration = System.currentTimeMillis() - beanStart;
        logger.trace("Finished initializing in {}ms.", duration);
      }
    }
    else {
      for (final FactoryHandle handle : eager) {
        contextManager.getEagerInstance(handle.getFactoryName());
      }
    }
    final long duration = System.currentTimeMillis() - start;
    logger.debug("Initialized {} eager beans in {}ms.", eager.size(), duration);
  }

  private Collection<FactoryHandle> addFactories() {
    final Collection<FactoryHandle> eager = new ArrayList<>();
    logger.debug("Adding factories...");
    final long start = System.currentTimeMillis();
    final Collection<FactoryHandle> allFactoryHandles = contextManager.getAllFactoryHandles();

    for (final FactoryHandle handle : allFactoryHandles) {
      if (handle.isEager()) {
        eager.add(handle);
      }
      addFactory(handle);
    }

    final long duration = System.currentTimeMillis() - start;
    logger.debug("Added {} factories in {}ms.", allFactoryHandles.size(), duration);

    return eager;
  }

  private void addFactory(final FactoryHandle handle) {
    for (final Class<?> assignableType : handle.getAssignableTypes()) {
      handlesByName.put(assignableType.getName(), handle);
    }
    if (handle.getBeanName() != null) {
      handlesByName.put(handle.getBeanName(), handle);
    }
  }

  @SuppressWarnings({ "rawtypes" })
  @Override
  public Collection<SyncBeanDef> lookupBeans(final String name) {
    return lookupBeans(name, false);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Collection<SyncBeanDef> lookupBeans(final String name, final boolean keepJsDups) {
    Assert.notNull(name);
    logger.debug("Looking up beans for {}", name);

    final Collection<FactoryHandle> handles = handlesByName.get(name);
    final Collection<SyncBeanDef<?>> runtimeBeanDefs = runtimeBeanDefsByName.get(name);
    final JsArray<JsTypeProvider<?>> jsProviders = getJsProviders(name);

    final Set<String> beanDefFactoryNames = new HashSet<>();
    final Collection beanDefs = new ArrayList<SyncBeanDef<Object>>(handles.size()+runtimeBeanDefs.size()+jsProviders.length());
    beanDefs.addAll(runtimeBeanDefs);
    for (final FactoryHandle handle : handles) {
      if (handle.isAvailableByLookup()) {
        beanDefs.add(new IOCBeanDefImplementation<>(handle, this.<Object>getType(name, handle, handle.getActualType())));
        beanDefFactoryNames.add(handle.getFactoryName());
      }
    }
    for (final JsTypeProvider<?> provider : JsArray.iterable(jsProviders)) {
      logger.debug("Found JS provider for name {} from factory {}", provider.getName(), provider.getFactoryName());
      if (keepJsDups || provider.getFactoryName() == null || !beanDefFactoryNames.contains(provider.getFactoryName())) {
        logger.debug("Keeping JS provider for name {} from factory {}", provider.getName(), provider.getFactoryName());
        beanDefs.add(new JsTypeBeanDefImplementation(provider, name));
      }
      else {
        logger.debug("Rejecting duplicate JS provider for name {} from factory {}", provider.getName(), provider.getFactoryName());
      }
    }

    logger.debug("Looked up {} beans: {}", beanDefs.size(), beanDefs);

    return beanDefs;
  }

  private JsArray<JsTypeProvider<?>> getJsProviders(final String name) {
    final WindowInjectionContext windowInjectionContext = WindowInjectionContextStorage.createOrGet();

    // This check may be false if -generateJsInteropExports is not set
    if (hasGetProvidersMethod(windowInjectionContext)) {
      return windowInjectionContext.getProviders(name);
    }
    else {
      logger.debug("Did not look up external JsTypeProviders for {}. "
              + "Hint: It looks like the WindowInjectionContext was supplied by an app "
              + "that was compiled without the flag \"-generateJsInteropExports\".", name);
      return new JsArray<>(new JsTypeProvider[0]);
    }
  }

  private static native boolean hasGetProvidersMethod(WindowInjectionContext obj)/*-{
    return obj.getProviders != undefined;
  }-*/;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type) {
    return (Collection) lookupBeans(type.getName());
  }

  @SuppressWarnings("unchecked")
  private <T> Class<T> getType(final String typeName, final FactoryHandle handle, final Class<?> defaultType) {
    for (final Class<?> type : handle.getAssignableTypes()) {
      if (type.getName().equals(typeName)) {
        return (Class<T>) type;
      }
    }

    return (Class<T>) defaultType;
  }

  @Override
  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type, final Annotation... qualifiers) {
    final Set<Annotation> qualifierSet = new HashSet<>(Arrays.asList(qualifiers));
    final Collection<SyncBeanDef<T>> candidates = lookupBeans(type);
    final Iterator<SyncBeanDef<T>> iter = candidates.iterator();
    while (iter.hasNext()) {
      final SyncBeanDef<T> beanDef = iter.next();
      if (!beanDef.matches(qualifierSet)) {
        iter.remove();
      }
    }

    return candidates;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T> SyncBeanDef<T> lookupBean(final Class<T> type, Annotation... qualifiers) {
    if (qualifiers.length == 0) {
      qualifiers = new Annotation[] { QualifierUtil.DEFAULT_ANNOTATION };
    }
    final Collection resolved = lookupBeans(type, qualifiers);
    if (resolved.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    } else if (resolved.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, resolved, qualifiers);
    } else {
      return (SyncBeanDef<T>) resolved.iterator().next();
    }
  }

  @Override
  public <T> void registerBean(final SyncBeanDef<T> beanDef) {
    runtimeBeanDefsByName.put(beanDef.getType().getName(), beanDef);
    if (!beanDef.getType().getName().equals(beanDef.getBeanClass().getName())) {
      runtimeBeanDefsByName.put(beanDef.getBeanClass().getName(), beanDef);
    }
    if (beanDef.getName() != null) {
      runtimeBeanDefsByName.put(beanDef.getName(), beanDef);
    }
  }

  @Override
  public <T> void registerBeanTypeAlias(final SyncBeanDef<T> beanDef, final Class<?> type) {
    runtimeBeanDefsByName.put(type.getName(), beanDef);
  }

  /**
   * For testing only.
   */
  public void reset() {
    contextManager = null;
    handlesByName.clear();
    runtimeBeanDefsByName.clear();
  }

  // TODO Find way to properly get scope, qualifiers, and assignable types.
  @SuppressWarnings("rawtypes")
  private final class JsTypeBeanDefImplementation implements SyncBeanDef {
    private final JsTypeProvider<?> provider;
    private final String name;

    private JsTypeBeanDefImplementation(final JsTypeProvider provider, final String name) {
      this.provider = provider;
      this.name = name;
    }

    @Override
    public boolean isAssignableTo(final Class type) {
      return Object.class.equals(type) || (type != null && type.getName().equals(name));
    }

    @Override
    public Class getType() {
      return null;
    }

    @Override
    public Class getBeanClass() {
      return null;
    }

    @Override
    public Class getScope() {
      return Dependent.class;
    }

    @Override
    public Set getQualifiers() {
      final Set<DynamicAnnotation> qualifiers = new HashSet<>();
      for (final String serializedQualifier : JsArray.iterable(provider.getQualifiers())) {
        qualifiers.add(DynamicAnnotation.create(serializedQualifier));
      }

      return qualifiers;
    }

    @Override
    public boolean matches(final Set annotations) {
      return true;
    }

    @Override
    public String getName() {
      return provider.getName();
    }

    @Override
    public boolean isActivated() {
      return true;
    }

    @Override
    public Object getInstance() {
      return provider.getInstance();
    }

    @Override
    public Object newInstance() {
      throw new UnsupportedOperationException("Cannot create new instance of JsType bean.");
    }

    @Override
    public boolean isDynamic() {
      return true;
    }

    @Override
    public String toString() {
      return "[JsTypeBeanDef: factoryName=" + provider.getFactoryName() + ", name=" + provider.getName() + "]";
    }
  }

  private final class IOCBeanDefImplementation<T> implements SyncBeanDef<T> {
    private final FactoryHandle handle;
    private final Class<T> type;
    private Set<Annotation> qualifiers;

    private IOCBeanDefImplementation(final FactoryHandle handle, final Class<T> type) {
      this.handle = handle;
      this.type = type;
    }

    @Override
    public String toString() {
      return BeanManagerUtil.beanDeftoString(handle);
    }

    @Override
    public Class<T> getType() {
      return type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getBeanClass() {
      return (Class<T>) handle.getActualType();
    }

    @Override
    public Class<? extends Annotation> getScope() {
      return handle.getScope();
    }

    @Override
    public T getInstance() {
      final T instance = contextManager.getInstance(handle.getFactoryName());
      if (instance instanceof Proxy) {
        @SuppressWarnings("unchecked")
        final Proxy<T> proxy = (Proxy<T>) instance;
        // Forces bean to be loaded.
        proxy.unwrap();
      }

      return instance;
    }

    @Override
    public T newInstance() {
      return contextManager.getNewInstance(handle.getFactoryName());
    }

    @Override
    public Set<Annotation> getQualifiers() {
      if (qualifiers == null) {
        qualifiers = new HashSet<>(handle.getQualifiers());
      }

      return qualifiers;
    }

    @Override
    public boolean matches(final Set<Annotation> annotations) {
      return QualifierUtil.matches(annotations, handle.getQualifiers());
    }

    @Override
    public String getName() {
      return handle.getBeanName();
    }

    @Override
    public boolean isActivated() {
      final Class<? extends BeanActivator> activatorType = handle.getBeanActivatorType();
      if (activatorType == null) {
        return true;
      } else {
        final BeanActivator activator = lookupBean(activatorType).getInstance();
        return activator.isActivated();
      }
    }

    @Override
    public boolean isAssignableTo(final Class<?> type) {
      return handle.getAssignableTypes().contains(type);
    }
  }

  public void addFactory(final Factory<?> factory) {
    contextManager.addFactory(factory);
    addFactory(factory.getHandle());
  }
}
