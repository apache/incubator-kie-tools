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

package org.jboss.errai.marshalling.tests.res;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Part of the regression test for ERRAI-460.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Portable
public class EntityWithInheritedTypeVariable<T> implements InterfaceWithTypeVariable<T> {

  private List<T> list;

  // no getters/setters for this one. this forces private accessors to be generated.
  private List<T> fieldAccessedList = new ArrayList<T>();

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public void addToFieldAccessedList(T val) {
    fieldAccessedList.add(val);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fieldAccessedList == null) ? 0 : fieldAccessedList.hashCode());
    result = prime * result + ((list == null) ? 0 : list.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    EntityWithInheritedTypeVariable other = (EntityWithInheritedTypeVariable) obj;
    if (fieldAccessedList == null) {
      if (other.fieldAccessedList != null)
        return false;
    }
    else if (!fieldAccessedList.equals(other.fieldAccessedList))
      return false;
    if (list == null) {
      if (other.list != null)
        return false;
    }
    else if (!list.equals(other.list))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EntityWithInheritedTypeVariable [list=" + list + ", fieldAccessedList=" + fieldAccessedList + "]";
  }

}
