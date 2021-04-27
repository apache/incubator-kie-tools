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

@Portable
public class ASubImpl1 extends AImpl1 {

  private float subValue;

  public ASubImpl1() {
  }

  public ASubImpl1(float subValue) {
    super();
    this.subValue = subValue;
  }

  public void setSubValue(float subValue) {
    this.subValue = subValue;
  }

  public float getSubValue() {
    return subValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Float.floatToIntBits(subValue);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ASubImpl1 other = (ASubImpl1) obj;
    if (Float.floatToIntBits(subValue) != Float.floatToIntBits(other.subValue))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ASubImpl1 [subValue=" + subValue + "]";
  }

}
