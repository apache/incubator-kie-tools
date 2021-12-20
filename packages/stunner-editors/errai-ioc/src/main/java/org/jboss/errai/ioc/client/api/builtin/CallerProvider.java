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

package org.jboss.errai.ioc.client.api.builtin;


import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.NoOpCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.framework.RemoteServiceProxyFactory;
import org.jboss.errai.common.client.framework.RpcStub;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.IOCProvider;

/**
 * @author Mike Brock
 * @author Max Barkley <mbarkley@redhat.com>
 */
@IOCProvider @Singleton
@SuppressWarnings("rawtypes")
public class CallerProvider implements ContextualTypeProvider<Caller>, Disposer<Caller> {

  private static final RemoteServiceProxyFactory factory = new RemoteServiceProxyFactory();

  @Override
  public Caller provide(final Class<?>[] typeargs, final Annotation[] qualifiers) {
    return new CallerImplementation(qualifiers, typeargs);
  }

  @Override
  public void dispose(final Caller beanInstance) {
    if (beanInstance instanceof CallerImplementation) {
      ((CallerImplementation) beanInstance).dispose();
    }
  }

  private final class CallerImplementation implements Caller<Object> {
    private final Annotation[] qualifiers;
    private final Class<?>[] typeargs;
    private boolean enabled = true;

    private CallerImplementation(final Annotation[] qualifiers, final Class<?>[] typeargs) {
      this.qualifiers = qualifiers;
      this.typeargs = typeargs;
    }

    @Override
    public Object call() {
      final Object proxy = factory.getRemoteProxy(typeargs[0]);
      ((RpcStub) proxy).setRemoteCallback(new NoOpCallback());
      ((RpcStub) proxy).setQualifiers(qualifiers);
      return proxy;
    }

    @Override
    public Object call(final RemoteCallback<?> callback) {
      final Object proxy = factory.getRemoteProxy(typeargs[0]);
      ((RpcStub) proxy).setRemoteCallback(wrap(callback));
      ((RpcStub) proxy).setQualifiers(qualifiers);
      return proxy;
    }

    @Override
    public Object call(final RemoteCallback<?> callback, final ErrorCallback<?> errorCallback) {
      final Object proxy = factory.getRemoteProxy(typeargs[0]);
      ((RpcStub) proxy).setRemoteCallback(wrap(callback));
      ((RpcStub) proxy).setErrorCallback(wrap(errorCallback));
      ((RpcStub) proxy).setQualifiers(qualifiers);
      return proxy;
    }

    private <T> RemoteCallback<T> wrap(final RemoteCallback<T> wrapped) {
      return retVal -> {
        if (enabled) {
          wrapped.callback(retVal);
        }
      };
    }

    private <T> ErrorCallback<T> wrap(final ErrorCallback<T> wrapped) {
      return (msg, error) -> {
        return !enabled || wrapped.error(msg, error);
      };
    }

    public void dispose() {
      enabled = false;
    }
  }
}
