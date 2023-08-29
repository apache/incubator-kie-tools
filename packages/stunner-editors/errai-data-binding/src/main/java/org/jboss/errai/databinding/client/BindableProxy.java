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


package org.jboss.errai.databinding.client;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jboss.errai.databinding.client.api.Converter;

/**
 * This interface is implemented by the generated proxies for {@link Bindable} types.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface BindableProxy<T> extends HasProperties {

  /**
   * Returns the {@link BindableProxyAgent} of this proxy.
   * 
   * @return the proxy's agent, never null.
   */
  public BindableProxyAgent<T> getBindableProxyAgent();

  /**
   * Updates all widgets bound to the model instance associated with this proxy (see
   * {@link BindableProxyAgent#bind(Widget, String, Converter)}). This method is only useful if the model instance has
   * undergone changes that were not caused by calls to methods on this proxy and were therefore not visible to this
   * proxy (e.g direct field access by JPA).
   */
  public void updateWidgets();
  
  /**
   * Returns a new non-proxied instance with state copied recursively from this target.
   * 
   * @return A recursively unwrapped (i.e. non-proxied) instance with state copied from the proxy target.
   */
  public T deepUnwrap();

  /** Migrated from deleted WrappedPortal
   *
   * @return The wrapped object
   */
  Object unwrap();
}
