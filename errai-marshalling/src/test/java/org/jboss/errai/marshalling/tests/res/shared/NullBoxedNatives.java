/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.tests.res.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * The marshallers once had trouble with null-valued boxed native class members.
 * This entity type exists as a regression test for that case.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Portable
public class NullBoxedNatives {

  private Byte byteMember;
  private Character charMember;
  private Short shortMember;
  private Integer intMember;
  private Long longMember;
  private Float floatMember;
  private Double doubleMember;
  private Object objectMember;
  private Boolean booleanMember;
  
  public Boolean getBooleanMember() {
    return booleanMember;
  }
  public void setBooleanMember(Boolean booleanMember) {
    this.booleanMember = booleanMember;
  }
  public Byte getByteMember() {
    return byteMember;
  }
  public Character getCharMember() {
    return charMember;
  }
  public Short getShortMember() {
    return shortMember;
  }
  public Integer getIntMember() {
    return intMember;
  }
  public Long getLongMember() {
    return longMember;
  }
  public Float getFloatMember() {
    return floatMember;
  }
  public Double getDoubleMember() {
    return doubleMember;
  }
  public Object getObjectMember() {
    return objectMember;
  }
  public void setByteMember(Byte byteMember) {
    this.byteMember = byteMember;
  }
  public void setCharMember(Character charMember) {
    this.charMember = charMember;
  }
  public void setShortMember(Short shortMember) {
    this.shortMember = shortMember;
  }
  public void setIntMember(Integer intMember) {
    this.intMember = intMember;
  }
  public void setLongMember(Long longMember) {
    this.longMember = longMember;
  }
  public void setFloatMember(Float floatMember) {
    this.floatMember = floatMember;
  }
  public void setDoubleMember(Double doubleMember) {
    this.doubleMember = doubleMember;
  }
  public void setObjectMember(Object objectMember) {
    this.objectMember = objectMember;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((booleanMember == null) ? 0 : booleanMember.hashCode());
    result = prime * result + ((byteMember == null) ? 0 : byteMember.hashCode());
    result = prime * result + ((charMember == null) ? 0 : charMember.hashCode());
    result = prime * result + ((doubleMember == null) ? 0 : doubleMember.hashCode());
    result = prime * result + ((floatMember == null) ? 0 : floatMember.hashCode());
    result = prime * result + ((intMember == null) ? 0 : intMember.hashCode());
    result = prime * result + ((longMember == null) ? 0 : longMember.hashCode());
    result = prime * result + ((objectMember == null) ? 0 : objectMember.hashCode());
    result = prime * result + ((shortMember == null) ? 0 : shortMember.hashCode());
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
    NullBoxedNatives other = (NullBoxedNatives) obj;
    if (booleanMember == null) {
      if (other.booleanMember != null)
        return false;
    }
    else if (!booleanMember.equals(other.booleanMember))
      return false;
    if (byteMember == null) {
      if (other.byteMember != null)
        return false;
    }
    else if (!byteMember.equals(other.byteMember))
      return false;
    if (charMember == null) {
      if (other.charMember != null)
        return false;
    }
    else if (!charMember.equals(other.charMember))
      return false;
    if (doubleMember == null) {
      if (other.doubleMember != null)
        return false;
    }
    else if (!doubleMember.equals(other.doubleMember))
      return false;
    if (floatMember == null) {
      if (other.floatMember != null)
        return false;
    }
    else if (!floatMember.equals(other.floatMember))
      return false;
    if (intMember == null) {
      if (other.intMember != null)
        return false;
    }
    else if (!intMember.equals(other.intMember))
      return false;
    if (longMember == null) {
      if (other.longMember != null)
        return false;
    }
    else if (!longMember.equals(other.longMember))
      return false;
    if (objectMember == null) {
      if (other.objectMember != null)
        return false;
    }
    else if (!objectMember.equals(other.objectMember))
      return false;
    if (shortMember == null) {
      if (other.shortMember != null)
        return false;
    }
    else if (!shortMember.equals(other.shortMember))
      return false;
    return true;
  }
}
