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

package org.jboss.errai.marshalling.tests.res;

import java.util.Arrays;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable type used to test marshalling of types using maps with array values.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Portable
public class EntityWithMapUsingArrayValues {

  private Map<String, String[]> data = null;

  public Map<String, String[]> getData() {
    return data;
  }

  public void setData(Map<String, String[]> data) {
    this.data = data;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
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
    EntityWithMapUsingArrayValues other = (EntityWithMapUsingArrayValues) obj;
    if (data == null) {
      if (other.data != null)
        return false;
    }
    else if (!data.keySet().equals(other.data.keySet())) {
      return false;
    }
    else {
      for (String key : data.keySet()) {
        if (!Arrays.equals(data.get(key), other.data.get(key))) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "EntityWithMapUsingArrayValues [data=" + data + "]";
  }

}
