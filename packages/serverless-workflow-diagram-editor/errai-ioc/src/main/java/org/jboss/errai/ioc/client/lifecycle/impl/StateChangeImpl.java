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

package org.jboss.errai.ioc.client.lifecycle.impl;

import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.lifecycle.api.StateChange;

@Dependent
public class StateChangeImpl<T> extends LifecycleEventImpl<T> implements StateChange<T> {
  
  private Set<String> changedFieldNames;
  
  public StateChangeImpl(final Set<String> changedFieldNames) {
    this.changedFieldNames = Collections.unmodifiableSet(changedFieldNames);
  }
  
  public StateChangeImpl() {}

  @Override
  public Set<String> getChangedFieldNames() {
    return changedFieldNames;
  }

  @Override
  public Class<?> getEventType() {
    return StateChange.class;
  }

  @Override
  public void setChangedFieldNames(final Set<String> changedFieldNames) {
    this.changedFieldNames = changedFieldNames;
  }

}
