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

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface AsyncBeanDef<T> extends IOCBeanDef<T> {

  /**
   * Returns an instance of the bean within the active scope.
   *
   * @return The bean instance.
   */
  public void getInstance(CreationalCallback<T> callback);

  /**
   * Returns a new instance of the bean. Calling this method overrides the underlying scope and instantiates a new
   * instance of the bean.
   *
   * @return a new instance of the bean.
   */
  public void newInstance(CreationalCallback<T> callback);
}
