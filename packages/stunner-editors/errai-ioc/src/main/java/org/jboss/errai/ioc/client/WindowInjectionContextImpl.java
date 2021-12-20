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

package org.jboss.errai.ioc.client;

import java.util.List;

import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.container.JsTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 * @see WindowInjectionContext
 */
public class WindowInjectionContextImpl implements WindowInjectionContext {
  private final ListMultimap<String, JsTypeProvider<?>> beanProviders = ArrayListMultimap.create();
  private final Logger logger = LoggerFactory.getLogger(WindowInjectionContextImpl.class);

  public WindowInjectionContextImpl() {
    logger.debug("WindowInjectionContext created.");
  }

  @Override
  public void addBeanProvider(final String name, final JsTypeProvider<?> provider) {
    logger.debug("Added provider {} for name {}", provider.getName(), name);
    beanProviders.put(name, provider);
    if (provider.getName() != null) {
      beanProviders.put(provider.getName(), provider);
    }
  }

  @Override
  public void addSuperTypeAlias(final String superTypeName, final String typeName) {
    logger.debug("Added super type alias {} for type {}", superTypeName, typeName);
    beanProviders.putAll(superTypeName, beanProviders.get(typeName));
  }

  @Override
  public Object getBean(final String name) {
    logger.debug("Looking up bean {}", name);
    final List<JsTypeProvider<?>> providers = beanProviders.get(name);

    if (providers.isEmpty()) {
      throw new IOCResolutionException("no matching bean instances for: " + name);
    }
    else if (providers.size() > 1) {
      throw new IOCResolutionException("multiple matching bean instances for: " + name);
    }
    else {
      return providers.get(0).getInstance();
    }
  }

  @Override
  public JsArray<JsTypeProvider<?>> getProviders(final String name) {
    logger.debug("Looking up providers for {}", name);
    final List<JsTypeProvider<?>> providers = beanProviders.get(name);

    logger.debug("Found {} providers: {}", providers.size(), providers);
    return new JsArray<>(providers.toArray(new JsTypeProvider<?>[providers.size()]));
  }

  @Override
  public boolean hasProvider(final String name) {
    logger.debug("Checking for providers for {}", name);
    return getProviders(name).length() > 0;
  }

}
