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

package org.jboss.errai.ioc.client.container;

/**
 * For any {@link Proxy}, all proxied methods should dispatch to the instance
 * returned by {@link #getInstance(Proxy)}. It is the helpers job to load
 * an instance for the proxy on demand.
 *
 * @param <T>
 *          The type wrapped by the proxy containing this helper.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface ProxyHelper<T> {

  /**
   * @param instance The instance to be returened by future calls to {@link #getInstance(Proxy)}.
   */
  void setInstance(T instance);

  /**
   * @param proxy The proxy containing this helper.
   * @return The instance that the containing proxy should dispatch to.
   */
  T getInstance(Proxy<T> proxy);

  /**
   * Removes the stored proxied reference so that future calls to
   * {@link #getInstance(Proxy)} will need to load a new instance.
   */
  void clearInstance();

  /**
   * Required for loading instances on demand.
   *
   * @param context The context associated with the containing {@link Proxy}.
   */
  void setProxyContext( Context context );

  /**
   * @return The {@link Context} previously set by {@link #setProxyContext(Context)}.
   */
  Context getProxyContext();
}
