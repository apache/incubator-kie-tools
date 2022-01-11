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

package org.jboss.errai.ioc.client.api.builtin;

import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * @author Mike Brock
 */
@IOCProvider
@Singleton
@EnabledByProperty(value = "errai.ioc.async_bean_manager", negated = true)
public class DisposerProvider implements ContextualTypeProvider<Disposer> {
  @Inject
  SyncBeanManager beanManager;

  @Override
  public Disposer provide(final Class<?>[] typeArguments, final Annotation[] qualifiers) {
    return new Disposer() {
      @Override
      public void dispose(final Object beanInstance) {
        beanManager.destroyBean(beanInstance);
      }
    };
  }
}
