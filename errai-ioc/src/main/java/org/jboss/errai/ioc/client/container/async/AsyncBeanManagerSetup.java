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

package org.jboss.errai.ioc.client.container.async;

import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandle;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface AsyncBeanManagerSetup {

  void registerAsyncBean(FactoryHandle handle, FactoryLoader<?> future);

  void registerAsyncDependency(String dependentFactoryName, String dependencyFactoryName);

  public static interface FactoryLoaderCallback<T> {
    void callback(Factory<T> factory);
  }

  public static interface FactoryLoader<T> {
    void call(FactoryLoaderCallback<T> callback);
  }

}
