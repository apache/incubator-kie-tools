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

import java.util.ArrayList;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable type used to test marshalling of types having public fields.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Portable
public class EntityWithPublicFields {

  public Integer value;
  public ArrayList<String> values;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result = prime * result + ((values == null) ? 0 : values.hashCode());
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
    EntityWithPublicFields other = (EntityWithPublicFields) obj;
    if (value == null) {
      if (other.value != null)
        return false;
    }
    else if (!value.equals(other.value))
      return false;
    if (values == null) {
      if (other.values != null)
        return false;
    }
    else if (!values.equals(other.values))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EntityWithPublicFields [value=" + value + ", values=" + values + "]";
  }

}
