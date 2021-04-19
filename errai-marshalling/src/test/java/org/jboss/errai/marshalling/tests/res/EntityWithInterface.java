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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable type used to test marshalling of interface types (see {@link AImpl1}, {@link AImpl2}, {@link InterfaceA}).
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
@Portable
public class EntityWithInterface {

  private InterfaceA a;

  public InterfaceA getA() {
    return a;
  }

  public void setA(InterfaceA a) {
    this.a = a;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((a == null) ? 0 : a.hashCode());
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
    EntityWithInterface other = (EntityWithInterface) obj;
    if (a == null) {
      if (other.a != null)
        return false;
    }
    else if (!a.equals(other.a))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EntityWithInterface [a=" + a + "]";
  }

}
