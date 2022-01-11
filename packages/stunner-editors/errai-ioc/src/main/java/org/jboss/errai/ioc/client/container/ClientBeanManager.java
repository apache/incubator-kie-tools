/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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
 * Contract for injectable client-side instances for run-time bean management.
 *
 * @author Mike Brock
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface ClientBeanManager {
  /**
   * Destroy a bean and all other dependent scoped dependencies of this bean in the bean manager.
   *
   * @param ref
   *     The instance reference of the bean.
   */
  void destroyBean(Object ref);

  /**
   * Indicates whether the referenced object is currently a managed bean.
   *
   * @param ref
   *     The instance reference of the bean.
   *
   * @return returns true if under management
   */
  boolean isManaged(Object ref);

  /**
   * Obtains an instance to the <em>actual</em> bean. If the specified reference is a proxy, this method will
   * return an un-proxied reference to the object.
   *
   * @param ref
   *     The proxied or unproxied reference.
   *
   * @return The actual reference to the bean if the specified reference is a proxy.
   *         Otherwise the same instance passed to the method is returned.
   *
   * @see #isProxyReference(Object)
   */
  Object getActualBeanReference(Object ref);

  /**
   * Determines whether the referenced object is itself a proxy to a managed bean.
   *
   * @param ref
   *     The reference to check.
   *
   * @return True iff the specified reference is a proxy.
   *
   * @see #getActualBeanReference(Object)
   */
  boolean isProxyReference(Object ref);

  /**
   * Associates a {@link DestructionCallback} with a bean instance. If the given reference is not a managed bean,
   * or the bean is no longer considered active, the method returns <tt>false</tt>. Otherwise, the method returns
   * <tt>true</tt>, indicating the callback is now registered and will be called when the bean is destroyed.
   *
   * @param beanInstance
   *     The bean instance to associate the callback to.
   * @param destructionCallback
   *     The instance of the {@link DestructionCallback}.
   *
   * @return <tt>true</tt> if the {@link DestructionCallback} is successfully registered for the given bean
   *         and <tt>false</tt> if not.
   */
  boolean addDestructionCallback(Object beanInstance, DestructionCallback<?> destructionCallback);
}
