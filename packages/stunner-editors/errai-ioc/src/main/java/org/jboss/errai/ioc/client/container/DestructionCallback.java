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

package org.jboss.errai.ioc.client.container;

/**
 * A destruction callback is used to implement a set of tasks to be performed on a bean prior to it being destroyed
 * (or taken out of service) by the bean manager. This interface is not designed to be directly used. Its
 * implementations are usually provided by Errai's code generators and are used to orchestrate tasks such as
 * {@link javax.annotation.PreDestroy} for beans. It is also used to implement implicit garbage collection tasks,
 * such as the de-registering of listeners and resources associated with the bean.
 *
 * @author Mike Brock
 */
public interface DestructionCallback<T> {
  /**
   * Called to perform the destruction task against the specified bean.
   * @param t the bean instance.
   */
  public void destroy(T bean);
}
