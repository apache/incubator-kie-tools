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

package org.jboss.errai.ioc.client.lifecycle.api;

/**
 * Generates {@link LifecycleListener LifecycleListeners} for IOC bean instances
 * of type {@code T}. Every instance of an IOC bean type will have the method
 * {@link #newInstance()} invoked exactly once.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface LifecycleListenerGenerator<T> {

  /**
   * @return An instance of {@link LifecycleListener}.
   */
  public LifecycleListener<T> newInstance();

}
