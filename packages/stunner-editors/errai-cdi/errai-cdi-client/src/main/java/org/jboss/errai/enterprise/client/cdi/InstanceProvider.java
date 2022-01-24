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

package org.jboss.errai.enterprise.client.cdi;

import static org.jboss.errai.ioc.client.IOCUtil.joinQualifiers;
import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_ANNOTATION;
import static org.jboss.errai.ioc.client.container.IOC.getBeanManager;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

@SuppressWarnings("rawtypes")
@IOCProvider
@Singleton
public class InstanceProvider implements ContextualTypeProvider<Instance> {

  @SuppressWarnings("unchecked")
  @Override
  public Instance provide(final Class[] typeargs, final Annotation[] qualifiers) {

    /*
     * If you see a compile error here, ensure that you are using Errai's custom
     * version of javax.enterprise.event.Event, which comes from the
     * errai-javax-enterprise project. The errai-cdi-client POM is set up this
     * way.
     *
     * Eclipse users: seeing an error here probably indicates that M2E has
     * clobbered your errai-javax-enterprise source folder settings. To fix your
     * setup, see the README in the root of errai-javax-enterprise.
     */

    return new InstanceImpl(typeargs[0], qualifiers.length == 0 ? new Annotation[] { DEFAULT_ANNOTATION } : qualifiers);
  }

  static class InstanceImpl<T> implements Instance<T> {
    private final Class<T> type;
    private final Annotation[] qualifiers;

    InstanceImpl(final Class<T> type, final Annotation[] qualifiers) {
      this.type = type;
      this.qualifiers = qualifiers;
    }

    @Override
    public Instance<T> select(final Annotation... qualifiers) {
      return select(type, qualifiers);
    }

    @Override
    public <U extends T> Instance<U> select(final Class<U> subtype, final Annotation... qualifiers) {
      return new InstanceImpl<U>(subtype, joinQualifiers(this.qualifiers, qualifiers));
    }

    @Override
    public boolean isUnsatisfied() {
      return IOCUtil.isUnsatisfied(type, qualifiers);
    }

    @Override
    public boolean isAmbiguous() {
      return IOCUtil.isAmbiguous(type, qualifiers);
    }

    @Override
    public Iterator<T> iterator() {
      final Collection<SyncBeanDef<T>> beanDefs = IOC.getBeanManager().lookupBeans( type, qualifiers );
      if (beanDefs==null){
        return Collections.<T>emptyList().iterator();
      }
      return new InstanceImplIterator(beanDefs);
    }

    @Override
    public T get() {
      return IOCUtil.getInstance(type, qualifiers);
    }

    @Override
    public void destroy(final Object instance) {
      getBeanManager().destroyBean(instance);
    }

    private class InstanceImplIterator implements Iterator<T> {

      private final Iterator<SyncBeanDef<T>> delegate;

      public InstanceImplIterator( final Collection<SyncBeanDef<T>> beanDefs ) {
        this.delegate = beanDefs.iterator();
      }

      @Override
      public boolean hasNext() {
        return delegate.hasNext();
      }

      @Override
      public T next() {
        return delegate.next().getInstance();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
