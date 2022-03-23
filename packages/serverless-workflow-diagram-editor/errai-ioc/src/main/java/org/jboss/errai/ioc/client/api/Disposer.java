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

package org.jboss.errai.ioc.client.api;

/**
 * A disposer exposes the bean managers explicit disposal functionality. An injected disposer can be used to
 * dispose of the parameterized bean type.
 *
 * @author Mike Brock
 */
public interface Disposer<T> {

  /**
   * Requests that the bean manager dispose of the specified bean instance.
   *
   * @param beanInstance the instance of the bean to be disposed.
   */
  public void dispose(T beanInstance);
}
