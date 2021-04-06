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
 * A portable entity type that contains an EnumContainer and an EnumTestA, for testing enum backreferences between
 * different types of objects (because EnumContainer can be set up to contain a reference to the same EnumTestA instance
 * as this object does).
 * 
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
@Portable
public class EnumContainerContainer {

  private EnumContainer enumContainer;
  private EnumTestA enumA;
  private EnumWithAbstractMethod abstractEnum;

  public EnumContainer getEnumContainer() {
    return enumContainer;
  }

  public EnumTestA getEnumA() {
    return enumA;
  }

  public void setEnumContainer(EnumContainer enumContainer) {
    this.enumContainer = enumContainer;
  }

  public void setEnumA(EnumTestA enumA) {
    this.enumA = enumA;
  }

  public void setAbstractEnum(EnumWithAbstractMethod abstractEnum) {
    this.abstractEnum = abstractEnum;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((abstractEnum == null) ? 0 : abstractEnum.hashCode());
    result = prime * result + ((enumA == null) ? 0 : enumA.hashCode());
    result = prime * result + ((enumContainer == null) ? 0 : enumContainer.hashCode());
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
    EnumContainerContainer other = (EnumContainerContainer) obj;
    if (abstractEnum != other.abstractEnum)
      return false;
    if (enumA != other.enumA)
      return false;
    if (enumContainer == null) {
      if (other.enumContainer != null)
        return false;
    }
    else if (!enumContainer.equals(other.enumContainer))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EnumContainerContainer [enumContainer=" + enumContainer + ", enumA=" + enumA + ", abstractEnum="
        + abstractEnum + "]";
  }

}
