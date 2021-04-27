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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable type used to test marshalling of arrays whos component type is a portable super type of another portable
 * type (see {@link AImpl1}, {@link ASubImpl1}).
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
@Portable
public class EntityWithPortableSubtypesInArray {

  private AImpl1 a[];

  public AImpl1[] getA() {
    return a;
  }

  public void setA(AImpl1[] a) {
    this.a = a;
  }

  @Override
  public String toString() {
    return "EntityWithInterfaceArray [a=" + Arrays.toString(a) + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(a);
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
    EntityWithPortableSubtypesInArray other = (EntityWithPortableSubtypesInArray) obj;
    if (!Arrays.equals(a, other.a))
      return false;
    return true;
  }

}
