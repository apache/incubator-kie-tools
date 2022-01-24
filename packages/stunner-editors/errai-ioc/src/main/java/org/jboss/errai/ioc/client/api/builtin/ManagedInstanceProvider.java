/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.api.builtin;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_ANNOTATION;
import static org.jboss.errai.ioc.client.container.IOC.getBeanManager;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@IOCProvider
public class ManagedInstanceProvider implements ContextualTypeProvider<ManagedInstance<?>>, Disposer<ManagedInstance<?>> {

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ManagedInstance<?> provide(final Class<?>[] typeargs, final Annotation[] qualifiers) {
    return new ManagedInstanceImpl(typeargs[0], qualifiers.length == 0 ? new Annotation[] { DEFAULT_ANNOTATION } : qualifiers);
  }

  @Override
  public void dispose(final ManagedInstance<?> managedInstance) {
    managedInstance.destroyAll();
  }

  private static final Map<Class<?>, SubTypeNode> subTypeNodes = new HashMap<>();

  private static void addSubTypeRelation(final Class<?> superType, final Class<?> subType) {
    if (!superType.equals(subType)) {
      SubTypeNode superNode = subTypeNodes.get(superType);
      if (superNode == null) {
        superNode = new SubTypeNode(superType, new LinkedHashSet<>(0));
        subTypeNodes.put(superType, superNode);
      }
      SubTypeNode subNode = subTypeNodes.get(subType);
      if (subNode == null) {
        subNode = new SubTypeNode(subType, new LinkedHashSet<>(1));
        subTypeNodes.put(subType, subNode);
      }
      subNode.superTypes.add(superNode);
    }
  }

  private static boolean isSubTypeRelation(final Class<?> superType, final Class<?> subType) {
    if (superType.equals(subType)) {
      return true;
    }
    else {
      final SubTypeNode superNode = subTypeNodes.get(superType);
      final SubTypeNode subNode = subTypeNodes.get(subType);

      return superNode != null && subNode != null && isSubTypeRelation(superNode, subNode);
    }
  }

  private static boolean isSubTypeRelation(final SubTypeNode superNode, final SubTypeNode subNode) {
    if (subNode.superTypes.contains(superNode)) {
      return true;
    }
    else {
      for (final SubTypeNode node : subNode.superTypes) {
        if (isSubTypeRelation(superNode, node)) {
          return true;
        }
      }

      return false;
    }
  }

  private static class SubTypeNode {
    private final Class<?> type;
    private final Set<SubTypeNode> superTypes;

    private SubTypeNode(final Class<?> type, final Set<SubTypeNode> superTypes) {
      this.type = type;
      this.superTypes = superTypes;
    }

    @Override
    public int hashCode() {
      return type.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      return obj instanceof SubTypeNode && ((SubTypeNode) obj).type.equals(type);
    }
  }

  private static class InstanceKey<T> {
    private final Class<T> type;
    private final Set<Annotation> qualifiers;

    private InstanceKey(final Class<T> type, final Set<Annotation> qualifiers) {
      this.type = type;
      this.qualifiers = qualifiers;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof InstanceKey) {
        final InstanceKey<?> other = (InstanceKey<?>) obj;
        return type.equals(other.type) && qualifiers.equals(other.qualifiers);
      }
      else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return type.hashCode() ^ qualifiers.hashCode();
    }
  }

  private static class ManagedInstanceImpl<S, T extends S> implements ManagedInstance<T> {

    private final InstanceKey<T> key;
    private final Multimap<InstanceKey<? extends S>, ? super T> dependentInstances;

    private ManagedInstanceImpl(final Class<T> type, final Annotation[] qualifiers) {
      this(type, qualifiers, ArrayListMultimap.create());
    }

    private ManagedInstanceImpl(final Class<T> type, final Annotation[] qualifiers, final Multimap<InstanceKey<? extends S>, ? super T> dependentInstances) {
      this(type, new HashSet<>(Arrays.asList(qualifiers)), dependentInstances);
    }

    private ManagedInstanceImpl(final Class<T> type, final Set<Annotation> qualifiers, final Multimap<InstanceKey<? extends S>, ? super T> dependentInstances) {
      this.key = new InstanceKey<>(type, qualifiers);
      this.dependentInstances = dependentInstances;
    }

    @Override
    public T get() {
      final SyncBeanDef<T> bean = IOCUtil.getSyncBean(key.type, qualifierArray());
      final T instance = bean.getInstance();
      if (Dependent.class.equals(bean.getScope())) {
        dependentInstances.put(key, instance);
      }

      return instance;
    }

    private Annotation[] qualifierArray() {
      return key.qualifiers.toArray(new Annotation[key.qualifiers.size()]);
    }

    @Override
    public Iterator<T> iterator() {
      return new ManagedInstanceImplIterator<>(getBeanManager().lookupBeans(key.type, qualifierArray()), key, dependentInstances);
    }

    @Override
    public ManagedInstance<T> select(final Annotation... qualifiers) {
      return select(key.type, qualifiers);
    }

    @Override
    public <U extends T> ManagedInstance<U> select(final Class<U> subtype, final Annotation... qualifiers) {
      final Set<Annotation> combined = new HashSet<>(key.qualifiers);
      combined.addAll(Arrays.asList(qualifiers));
      addSubTypeRelation(key.type, subtype);
      return new ManagedInstanceImpl<>(subtype, combined, dependentInstances);
    }

    @Override
    public boolean isUnsatisfied() {
      return IOCUtil.isUnsatisfied(key.type, qualifierArray());
    }

    @Override
    public boolean isAmbiguous() {
      return IOCUtil.isAmbiguous(key.type, qualifierArray());
    }

    @Override
    public void destroy(final T instance) {
      IOCUtil.destroy(instance);
      dependentInstances.remove(key, instance);
    }

    @Override
    public void destroyAll() {
      final Iterator<InstanceKey<? extends S>> keysIter = dependentInstances.keySet().iterator();
      while (keysIter.hasNext()) {
        final InstanceKey<? extends S> key = keysIter.next();
        final Set<Annotation> quals = key.qualifiers;
        if (quals.containsAll(this.key.qualifiers) && isSubTypeRelation(this.key.type, key.type)) {
          final Collection<? super T> instances = dependentInstances.get(key);
          for (final Object instance : instances) {
            IOCUtil.destroy(instance);
          }
          keysIter.remove();
        }
      }
    }

    private static class ManagedInstanceImplIterator<S, T extends S> implements Iterator<T> {

      private final Iterator<SyncBeanDef<T>> delegate;
      private final InstanceKey<T> key;
      private final Multimap<InstanceKey<? extends S>, ? super T> dependentInstances;
      private Object lastCreatedInstance;

      private ManagedInstanceImplIterator(final Collection<SyncBeanDef<T>> beanDefs, final InstanceKey<T> key,
              final Multimap<InstanceKey<? extends S>, ? super T> dependentInstances) {
        this.key = key;
        this.delegate = beanDefs.iterator();
        this.dependentInstances = dependentInstances;
      }

      @Override
      public boolean hasNext() {
        return delegate.hasNext();
      }

      @Override
      public T next() {
        final SyncBeanDef<T> bean = delegate.next();
        final T instance = bean.getInstance();
        if (Dependent.class.equals(bean.getScope())) {
          dependentInstances.put(key, instance);
        }
        lastCreatedInstance = instance;
        return instance;
      }

      @Override
      public void remove() {
        if (lastCreatedInstance == null) {
          throw new IllegalStateException();
        }
        else if (dependentInstances.remove(key, lastCreatedInstance)) {
          IOCUtil.destroy(lastCreatedInstance);
          lastCreatedInstance = null;
        }
      }
    }

  }
}