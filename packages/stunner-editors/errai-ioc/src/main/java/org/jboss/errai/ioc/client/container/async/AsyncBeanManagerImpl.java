/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.container.async;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.inject.Provider;

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.BeanManagerSetup;
import org.jboss.errai.ioc.client.container.BeanManagerUtil;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.DestructionCallback;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandle;
import org.jboss.errai.ioc.client.container.RefHolder;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Alternative
public class AsyncBeanManagerImpl implements AsyncBeanManager, BeanManagerSetup, AsyncBeanManagerSetup {

  private final SyncBeanManagerImpl innerBeanManager = new SyncBeanManagerImpl();

  private final Multimap<String, String> typeNamesByName = HashMultimap.create();
  private final Multimap<String, UnloadedFactory<?>> unloadedByTypeName = HashMultimap.create();
  private final Map<String, UnloadedFactory<?>> unloadedByFactoryName = new HashMap<>();

  @Override
  public void destroyBean(final Object ref) {
    innerBeanManager.destroyBean(ref);
  }

  @Override
  public boolean isManaged(final Object ref) {
    return innerBeanManager.isManaged(ref);
  }

  @Override
  public Object getActualBeanReference(final Object ref) {
    return innerBeanManager.getActualBeanReference(ref);
  }

  @Override
  public boolean isProxyReference(final Object ref) {
    return innerBeanManager.isProxyReference(ref);
  }

  @Override
  public boolean addDestructionCallback(final Object beanInstance, final DestructionCallback<?> destructionCallback) {
    return innerBeanManager.addDestructionCallback(beanInstance, destructionCallback);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Collection<AsyncBeanDef> lookupBeans(final String name) {
    final Collection syncBeans = innerBeanManager.lookupBeans(name);
    final Collection beans = wrapSyncBeans(syncBeans);
    addUnloadedBeans(beans, name);

    return beans;
  }

  private <T> Collection<AsyncBeanDef<T>> wrapSyncBeans(final Collection<SyncBeanDef<T>> syncBeans) {
    final Collection<AsyncBeanDef<T>> asyncBeans = new ArrayList<>(syncBeans.size());

    for (final SyncBeanDef<T> syncBean : syncBeans) {
      asyncBeans.add(new SyncToAsyncBeanDef<>(syncBean));
    }

    return asyncBeans;
  }

  @Override
  public <T> Collection<AsyncBeanDef<T>> lookupBeans(final Class<T> type) {
    return lookupBeans(type, QualifierUtil.DEFAULT_ANNOTATION);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T> Collection<AsyncBeanDef<T>> lookupBeans(final Class<T> type, Annotation... qualifiers) {
    if (qualifiers.length == 0) {
      qualifiers = new Annotation[] { QualifierUtil.DEFAULT_ANNOTATION };
    }

    final Collection beans = wrapSyncBeans(innerBeanManager.lookupBeans(type, qualifiers));
    addUnloadedBeans(beans, type, type.getName(), qualifiers);

    return beans;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private <T> void addUnloadedBeans(final Collection<AsyncBeanDef> beans, final Class<T> type, final String typeName,
          final Annotation... qualifiers) {
    final Collection<UnloadedFactory<?>> unloadedCandidates = unloadedByTypeName.get(typeName);
    final Collection<Annotation> allOf = Arrays.asList(qualifiers);
    for (final UnloadedFactory unloaded : unloadedCandidates) {
      if (QualifierUtil.matches(allOf, unloaded.getHandle().getQualifiers())) {
        final Class<T> beanType = (Class<T>) (type != null ? type : unloaded.getHandle().getActualType());
        beans.add(unloaded.createBeanDef(beanType));
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private void addUnloadedBeans(final Collection<AsyncBeanDef> beans, final String name) {
    for (final String typeName : typeNamesByName.get(name)) {
      addUnloadedBeans(beans, null, typeName, QualifierUtil.DEFAULT_ANNOTATION);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T> AsyncBeanDef<T> lookupBean(final Class<T> type, final Annotation... qualifiers) {
    final Collection beans = lookupBeans(type, qualifiers);

    if (beans.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, beans, qualifiers);
    } else if (beans.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    } else {
      return (AsyncBeanDef<T>) beans.iterator().next();
    }
  }

  @Override
  public void setContextManager(final ContextManager contextManager) {
    innerBeanManager.setContextManager(contextManager);
  }

  public void reset() {
    typeNamesByName.clear();
    unloadedByTypeName.clear();
    innerBeanManager.reset();
  }

  public SyncBeanManager getInnerBeanManager() {
    return innerBeanManager;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void registerAsyncBean(final FactoryHandle handle, final FactoryLoader<?> future) {
    final String beanName;
    if (handle.getBeanName() != null) {
      beanName = handle.getBeanName();
    } else {
      beanName = handle.getActualType().getName();
    }

    typeNamesByName.put(beanName, handle.getActualType().getName());

    final UnloadedFactory unloaded = new UnloadedFactory(handle, future);
    for (final Class<?> assignable : handle.getAssignableTypes()) {
      unloadedByTypeName.put(assignable.getName(), unloaded);
    }
    unloadedByFactoryName.put(handle.getFactoryName(), unloaded);
  }

  private void unregisterAsyncBean(final FactoryHandle handle) {
    final String name = (handle.getBeanName() != null ? handle.getBeanName() : handle.getActualType().getName());
    typeNamesByName.remove(name, handle.getActualType().getName());
    unloadedByFactoryName.remove(handle.getFactoryName());
    for (final Class<?> assignable : handle.getAssignableTypes()) {
      final Iterator<UnloadedFactory<?>> unloadedIter = unloadedByTypeName.get(assignable.getName()).iterator();
      while (unloadedIter.hasNext()) {
        final UnloadedFactory<?> unloaded = unloadedIter.next();
        if (unloaded.getHandle().getFactoryName().equals(handle.getFactoryName())) {
          unloadedIter.remove();
          break;
        }
      }
    }
  }

  @Override
  public void registerAsyncDependency(final String dependentFactoryName, final String dependencyFactoryName) {
    final UnloadedFactory<?> unloaded = getUnloadedFactory(dependentFactoryName);
    unloaded.addAsyncDependency(dependencyFactoryName);
  }

  private UnloadedFactory<?> getUnloadedFactory(final String factoryName) {
    final UnloadedFactory<?> unloadedFactory = unloadedByFactoryName.get(factoryName);
    if (unloadedFactory == null) {
      throw new RuntimeException("No unloaded factory found for " + factoryName);
    } else {
      return unloadedFactory;
    }
  }

  private class UnloadedFactory<T> {
    private final FactoryHandle handle;
    private final FactoryLoader<T> loader;
    private final Set<String> asyncDependencies = new HashSet<>();

    private boolean loaded = false;
    private boolean loading = false;
    private final Queue<Runnable> onLoad = new LinkedList<>();

    public UnloadedFactory(final FactoryHandle handle, final FactoryLoader<T> loader) {
      this.handle = handle;
      this.loader = loader;
    }

    private void loadSelf(final Runnable whenLoaded) {
      if (loaded) {
        whenLoaded.run();
        return;
      } else if (loading) {
        onLoad.add(whenLoaded);
        return;
      } else {
        loading = true;
        loader.call(new FactoryLoaderCallback<T>() {
          @Override
          public void callback(final Factory<T> factory) {
            innerBeanManager.addFactory(factory);
            unregisterAsyncBean(handle);
            whenLoaded.run();
          }
        });
      }
    }

    public void load(final Runnable onFinish) {
      if (loaded) {
        onFinish.run();
        return;
      } else if (loading) {
        onLoad.add(onFinish);
        return;
      }

      loading = true;
      onLoad.add(onFinish);
      final RefHolder<Integer> numLoaded = new RefHolder<>();
      numLoaded.set(0);
      final Collection<UnloadedFactory<?>> unloadedDeps = getUnloadedAsyncDependencies();

      for (final UnloadedFactory<?> unloadedDep : unloadedDeps) {
        unloadedDep.loadSelf(new Runnable() {
          @Override
          public void run() {
            numLoaded.set(numLoaded.get()+1);
            if (numLoaded.get().equals(unloadedDeps.size()+1)) {
              finishLoading(unloadedDeps);
            }
          }
        });
      }

      loader.call(new FactoryLoaderCallback<T>() {
        @Override
        public void callback(final Factory<T> factory) {
          numLoaded.set(numLoaded.get()+1);
          innerBeanManager.addFactory(factory);
          unregisterAsyncBean(handle);
          if (numLoaded.get().equals(unloadedDeps.size()+1)) {
            finishLoading(unloadedDeps);
          }
        }
      });
    }

    protected void finishLoading(final Collection<UnloadedFactory<?>> unloadedDeps) {
      finishDependencies(unloadedDeps);
      finishSelf();
    }

    private void finishDependencies(final Collection<UnloadedFactory<?>> unloadedDeps) {
      for (final UnloadedFactory<?> unloaded : unloadedDeps) {
        unloaded.finishSelf();
      }
    }

    private void finishSelf() {
      loading = false;
      loaded = true;
      while (!onLoad.isEmpty()) {
        onLoad.poll().run();
      }
    }

    private Collection<UnloadedFactory<?>> getUnloadedAsyncDependencies() {
      final Deque<UnloadedFactory<?>> unloadedDeps = new LinkedList<>();

      final Queue<String> bfsQueue = new LinkedList<>(asyncDependencies);
      final Set<String> visited = new HashSet<>();
      visited.add(handle.getFactoryName());

      while (bfsQueue.size() > 0) {
        final String factoryName = bfsQueue.poll();
        if (visited.contains(factoryName)) {
          continue;
        }

        final UnloadedFactory<?> unloadedDep = unloadedByFactoryName.get(factoryName);
        if (unloadedDep != null && !unloadedDep.loaded) {
          unloadedDeps.addFirst(unloadedDep);
          visited.add(factoryName);
          for (final String dependentFactoryName : unloadedDep.asyncDependencies) {
            bfsQueue.add(dependentFactoryName);
          }
        }
      }

      return unloadedDeps;
    }

    public FactoryHandle getHandle() {
      return handle;
    }

    public void addAsyncDependency(final String factoryName) {
      asyncDependencies.add(factoryName);
    }

    public FactoryLoaderBeanDef createBeanDef(final Class<T> type) {
      return new FactoryLoaderBeanDef(type);
    }

    private class FactoryLoaderBeanDef implements AsyncBeanDef<T> {

      private final Class<T> type;
      private Set<Annotation> qualifiers;

      public FactoryLoaderBeanDef(final Class<T> type) {
        this.type = type;
      }

      @Override
      public Class<T> getType() {
        return type;
      }

      @Override
      public Class<?> getBeanClass() {
        return handle.getActualType();
      }

      @Override
      public Class<? extends Annotation> getScope() {
        return handle.getScope();
      }

      @Override
      public void getInstance(final CreationalCallback<T> callback) {
        getInstanceHelper(callback, new Provider<T>() {
          @Override
          public T get() {
            return performSyncLookup().getInstance();
          }
        });
      }

      @Override
      public void newInstance(final CreationalCallback<T> callback) {
        getInstanceHelper(callback, new Provider<T>() {
          @Override
          public T get() {
            return performSyncLookup().newInstance();
          }
        });
      }

      private void getInstanceHelper(final CreationalCallback<T> callback, final Provider<T> instanceProvider) {
        if (!loaded) {
          load(new Runnable() {
            @Override
            public void run() {
              callback.callback(instanceProvider.get());
            }
          });
        } else {
          callback.callback(instanceProvider.get());
        }
      }

      private SyncBeanDef<T> performSyncLookup() {
        return innerBeanManager.lookupBean(type, handle.getQualifiers().toArray(new Annotation[0]));
      }

      @Override
      public Set<Annotation> getQualifiers() {
        if (qualifiers == null) {
          qualifiers = Collections.unmodifiableSet(new HashSet<>(handle.getQualifiers()));
        }
        return qualifiers;
      }

      @Override
      public boolean matches(final Set<Annotation> annotations) {
        return QualifierUtil.matches(handle.getQualifiers(), annotations);
      }

      @Override
      public String getName() {
        return handle.getBeanName();
      }

      @Override
      public boolean isActivated() {
        return true;
      }

      @Override
      public String toString() {
        return BeanManagerUtil.beanDeftoString(handle);
      }

      @Override
      public boolean isAssignableTo(final Class<?> type) {
        return handle.getAssignableTypes().contains(type);
      }

    }
  }
}
