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

import java.util.Set;

/**
 * An event representing a change in a bean instances internal state.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface StateChange<T> extends LifecycleEvent<T> {
  
  /**
   * @return The set of names of fields whose values changed.
   */
  public Set<String> getChangedFieldNames();
  
  /**
   * This must be set before the event is fired.
   * 
   * @param changedFieldNames The set of names of fields whose values changed.
   */
  public void setChangedFieldNames(Set<String> changedFieldNames);
  
}
