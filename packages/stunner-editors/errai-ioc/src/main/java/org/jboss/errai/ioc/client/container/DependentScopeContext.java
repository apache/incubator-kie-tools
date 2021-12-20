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

package org.jboss.errai.ioc.client.container;

import static org.jboss.errai.ioc.client.container.Factory.maybeUnwrapProxy;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.api.ScopeContext;

/**
 * The {@link Context} implementation for all {@link Dependent} scoped beans.
 * Unlike other scopes, beans which have no explicit scope will be considered
 * dependent. Therefore some beans will be registered with this scope that do
 * not actually have the {@link Dependent} annotation.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@ScopeContext({Dependent.class})
public class DependentScopeContext extends AbstractContext implements HasContextualInstanceSupport {

  @Override
  public Class<? extends Annotation> getScope() {
    return Dependent.class;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public boolean handlesScope(final Class<? extends Annotation> scope) {
    return Dependent.class.equals(scope);
  }

  @Override
  protected <T> T getActiveInstance(final String factoryName) {
    return null;
  }

  @Override
  protected boolean hasActiveInstance(final String factoryName) {
    return false;
  }

  @Override
  protected boolean isCurrentlyCreatingActiveInstance(final String factoryName) {
    /**
     * Even if a factory of a dependent-scoped bean is creating an instance, that instance is never re-used so we return
     * false here.
     */
    return false;
  }

  @Override
  public Optional<HasContextualInstanceSupport> withContextualInstanceSupport() {
    return Optional.ofNullable(this);
  }

  @Override
  public <T> T getContextualInstance(final String factoryName, final Class<?>[] typeArgs, final Annotation[] qualifiers) {
    final Factory<T> factory = this.<T>getFactory(factoryName);
    final T instance = factory.createContextualInstance(getContextManager(), typeArgs, qualifiers);
    registerInstance(maybeUnwrapProxy(instance), factory);
    return instance;
  }

  @Override
  protected void afterDestroyInstance(final Object instance) {
    if (instance instanceof Proxy) removeProxy(instance);
  }

}
