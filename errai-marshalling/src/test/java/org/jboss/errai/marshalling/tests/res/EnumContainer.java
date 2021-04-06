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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A portable entity class that contains multiple references to the same two
 * enum types.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
@Portable
public class EnumContainer {

  private EnumTestA enumA1;
  private EnumTestA enumA2;
  private EnumTestA enumA3;

  private EnumWithState statefulEnum1;
  private EnumWithState statefulEnum2;
  private EnumWithState statefulEnum3;

  public EnumTestA getEnumA1() {
    return enumA1;
  }
  public EnumTestA getEnumA2() {
    return enumA2;
  }
  public EnumTestA getEnumA3() {
    return enumA3;
  }
  public EnumWithState getStatefulEnum1() {
    return statefulEnum1;
  }
  public EnumWithState getStatefulEnum2() {
    return statefulEnum2;
  }
  public EnumWithState getStatefulEnum3() {
    return statefulEnum3;
  }
  public void setEnumA1(EnumTestA enumA1) {
    this.enumA1 = enumA1;
  }
  public void setEnumA2(EnumTestA enumA2) {
    this.enumA2 = enumA2;
  }
  public void setEnumA3(EnumTestA enumA3) {
    this.enumA3 = enumA3;
  }
  public void setStatefulEnum1(EnumWithState statefulEnum1) {
    this.statefulEnum1 = statefulEnum1;
  }
  public void setStatefulEnum2(EnumWithState statefulEnum2) {
    this.statefulEnum2 = statefulEnum2;
  }
  public void setStatefulEnum3(EnumWithState statefulEnum3) {
    this.statefulEnum3 = statefulEnum3;
  }

  @Override
  public String toString() {
    return "EnumContainer [enumA1=" + enumA1 + ", enumA2=" + enumA2
            + ", enumA3=" + enumA3 + ", statefulEnum1=" + statefulEnum1
            + ", statefulEnum2=" + statefulEnum2 + ", statefulEnum3="
            + statefulEnum3 + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((enumA1 == null) ? 0 : enumA1.hashCode());
    result = prime * result + ((enumA2 == null) ? 0 : enumA2.hashCode());
    result = prime * result + ((enumA3 == null) ? 0 : enumA3.hashCode());
    result = prime * result
            + ((statefulEnum1 == null) ? 0 : statefulEnum1.hashCode());
    result = prime * result
            + ((statefulEnum2 == null) ? 0 : statefulEnum2.hashCode());
    result = prime * result
            + ((statefulEnum3 == null) ? 0 : statefulEnum3.hashCode());
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
    EnumContainer other = (EnumContainer) obj;
    if (enumA1 != other.enumA1)
      return false;
    if (enumA2 != other.enumA2)
      return false;
    if (enumA3 != other.enumA3)
      return false;
    if (statefulEnum1 != other.statefulEnum1)
      return false;
    if (statefulEnum2 != other.statefulEnum2)
      return false;
    if (statefulEnum3 != other.statefulEnum3)
      return false;
    return true;
  }

}
