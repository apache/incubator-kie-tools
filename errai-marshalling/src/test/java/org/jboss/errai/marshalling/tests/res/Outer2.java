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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Outer2 {
  private final Nested key;
  private final List<Nested> keys;

  public Outer2(@MapsTo("key") Nested key, @MapsTo("keys") List<Nested> keys) {
    this.key = key;
    this.keys = keys;
  }

  @Portable
  public static class Nested {
    private final String value;

    public Nested(@MapsTo("value") String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "Nested [value=" + value + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((value == null) ? 0 : value.hashCode());
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
      Nested other = (Nested) obj;
      if (value == null) {
        if (other.value != null)
          return false;
      }
      else if (!value.equals(other.value))
        return false;
      return true;
    }
  }

  @Override
  public String toString() {
    return "Outer [key=" + key + ", keys=" + keys + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((keys == null) ? 0 : keys.hashCode());
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
    Outer2 other = (Outer2) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    }
    else if (!key.equals(other.key))
      return false;
    if (keys == null) {
      if (other.keys != null)
        return false;
    }
    else if (!keys.equals(other.keys))
      return false;
    return true;
  }
  
  
}
