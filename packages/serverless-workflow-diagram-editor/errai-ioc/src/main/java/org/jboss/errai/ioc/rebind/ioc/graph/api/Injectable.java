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


package org.jboss.errai.ioc.rebind.ioc.graph.api;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.LoadAsync;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

/**
 * Represents a class, producer member, or provider that can supply a bean for
 * satisfying dependencies.
 *
 * @see DependencyGraphBuilder
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface Injectable extends HasInjectableHandle {

  /**
   * @return The scope of the injectable. For pseudo-dependent injectables, this
   *         should return {@link Dependent}.
   */
  Class<? extends Annotation> getScope();

  /**
   * @return If available, the annotated object that is the source of this injectable.
   */
  Optional<HasAnnotations> getAnnotatedObject();

  /**
   * @return The name of this injectable if {@link Named} annotation was
   *         present.
   */
  String getBeanName();

  /**
   * @return The unique name of the factory that will produce this injectable at
   *         runtime.
   */
  String getFactoryName();

  /**
   * @return The dependencies of this injectable. These depencies will not be
   *         resolved until after
   *         {@link DependencyGraphBuilder#createGraph()} is called.
   */
  Collection<Dependency> getDependencies();

  /**
   * @return The kind of this injectable.
   */
  InjectableType getInjectableType();

  /**
   * @return The wiring element types of this injectable.
   */
  Collection<WiringElementType> getWiringElementTypes();

  /**
   * @return True iff this injectable should be loaded asynchronously (via code splitting).
   * @see LoadAsync
   */
  boolean loadAsync();

  /**
   * @return True if this injectable requires a proxy because of its scope or
   *         because it is injected into a constructor.
   */
  boolean requiresProxy();

  /**
   * Once invoked, all subsequent calls to {@link #requiresProxy()} will return
   * true. Usually this is called by the {@link DependencyGraphBuilder} if this
   * injectable satisfies a constructor injection point.
   */
  void setRequiresProxyTrue();

  /**
   * Convenience method that checks if this injectable has the ContextualProvider {@link WiringElementType}.
   *
   * @return True if this injectable is for a {@link ContextualTypeProvider}.
   */
  boolean isContextual();

  /**
   * @return True if this injectable was created via {@link DependencyGraphBuilder#addExtensionInjectable(MetaClass, Qualifier, Class, WiringElementType...)}.
   */
  boolean isExtension();

  /**
   * @return A hashcode based on the following:
   *    <ul>
   *      <li>The {@link MetaClass#hashContent() hashContent} of the {@link MetaClass} produced by this injectable.
   *      <il>The hashContent of the MetaClasses of all dependencies.
   *    </ul>
   */
  int hashContent();

}
