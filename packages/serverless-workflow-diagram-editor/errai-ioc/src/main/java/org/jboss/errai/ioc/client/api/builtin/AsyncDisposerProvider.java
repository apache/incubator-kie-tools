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


package org.jboss.errai.ioc.client.api.builtin;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManager;

/**
 * @author Mike Brock
 */
@IOCProvider
@Singleton
@EnabledByProperty("errai.ioc.async_bean_manager")
public class AsyncDisposerProvider implements ContextualTypeProvider<Disposer> {
  @Inject
  AsyncBeanManager beanManager;

  @Override
  public Disposer provide(final Class<?>[] typeargs, final Annotation[] qualifiers) {
    return new Disposer() {
      @Override
      public void dispose(Object beanInstance) {
        beanManager.destroyBean(beanInstance);
      }
    };
  }
}
