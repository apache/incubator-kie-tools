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
 * Portable type used to test marshalling of abstract classes (see {@link BImpl1}, {@link BImpl2}, {@link AbstractClassB}).
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
@Portable
public class EntityWithAbstractFieldType {

  private AbstractClassB b;

  public AbstractClassB getB() {
    return b;
  }

  public void setB(AbstractClassB a) {
    this.b = a;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((b == null) ? 0 : b.hashCode());
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
    EntityWithAbstractFieldType other = (EntityWithAbstractFieldType) obj;
    if (b == null) {
      if (other.b != null)
        return false;
    }
    else if (!b.equals(other.b))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EntityWithAbstractFieldType [b=" + b + "]";
  }

}
