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


package org.jboss.errai.ioc.rebind.ioc.graph.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.Alternative;

import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

/**
 * An enum with priority categories for resolved dependencies. The enum values
 * are in descending order of priority.
 *
 * When the {@link DependencyGraphBuilder} resolves dependencies, injectables
 * will be filtered by priority and only injectables in the highest non-empty
 * priority will be considered.
 *
 * @see DependencyGraphBuilder#createGraph()
 * @author Max Barkley <mbarkley@redhat.com>
 */
public enum ResolutionPriority {
  /**
   * Category for {@link Alternative} beans that have been enabled.
   */
  EnabledAlternative {
    @Override
    public boolean matches(final Injectable injectable) {
      return injectable.getWiringElementTypes().contains(WiringElementType.AlternativeBean)
              && !InjectableType.Disabled.equals(injectable.getInjectableType());
    }
  },
  /**
   * Category for {@link WindowScoped} {@link jsinterop.annotations.JsType} beans so that the local instance does not
   * override the single global instance.
   */
  WindowScopedJsType {
    @Override
    public boolean matches(final Injectable injectable) {
      return injectable.getInjectableType().equals(InjectableType.JsType)
              && injectable.getWiringElementTypes().contains(WiringElementType.SharedSingleton);
    }
  },
  /**
   * Category for explicitly scoped concrete types, or producer methods.
   */
  NormalType {
    final Collection<InjectableType> matchingTypes = Arrays.<InjectableType> asList(InjectableType.Type,
            InjectableType.Producer);
    @Override
    public boolean matches(final Injectable injectable) {
      return matchingTypes.contains(injectable.getInjectableType())
              && !injectable.getWiringElementTypes().contains(WiringElementType.Simpleton);
    }
  },
  /**
   * Category for injectables that may or may not be satisfied by separately compiled GWT modules at runtime.
   */
  JsType {
    @Override
    public boolean matches(final Injectable injectable) {
      return InjectableType.JsType.equals(injectable.getInjectableType());
    }
  },
  /**
   * Category for types form {@link IOCProvider providers}.
   */
  Provided {
    private final Collection<InjectableType> providerTypes = Arrays.<InjectableType> asList(InjectableType.Provider,
            InjectableType.ContextualProvider);
    @Override
    public boolean matches(final Injectable injectable) {
      return providerTypes.contains(injectable.getInjectableType());
    }
  },
  /**
   * Category for injectables provided by
   * {@link DependencyGraphBuilder#addExtensionInjectable(org.jboss.errai.codegen.meta.MetaClass, org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier, Class, WiringElementType...)
   * extensions}.
   */
  Extension {
    private final Collection<InjectableType> extensionTypes = Arrays.asList(InjectableType.Extension, InjectableType.ExtensionProvided);
    @Override
    public boolean matches(final Injectable injectable) {
      return extensionTypes.contains(injectable.getInjectableType());
    }
  },
  /**
   * Category for concrete types with no explicit scopes or injection points, and that are default constructible.
   */
  Simpleton {
    @Override
    public boolean matches(final Injectable injectable) {
      return injectable.getWiringElementTypes().contains(WiringElementType.Simpleton);
    }
  },
  /**
   * Category for disabled beans. These cannot satisfy dependencies, but are used for giving hints when errors occur.
   */
  Disabled {
    @Override
    public boolean matches(final Injectable injectable) {
      return InjectableType.Disabled.equals(injectable.getInjectableType());
    }
  };

  public abstract boolean matches(final Injectable injectable);

  public static ResolutionPriority getMatchingPriority(final Injectable injectable) {
    for (final ResolutionPriority priority : values()) {
      if (priority.matches(injectable)) {
        return priority;
      }
    }

    throw new RuntimeException("The injectable " + injectable + " does not match any resolution priority.");
  }

  public static Iterable<ResolutionPriority> enabledValues() {
    return () -> Arrays.stream(values()).filter(p -> !Disabled.equals(p)).iterator();
  }
}
