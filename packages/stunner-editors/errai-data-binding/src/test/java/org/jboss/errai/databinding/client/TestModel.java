/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.databinding.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.junit.Ignore;

/**
 * Simple bindable model for testing purposes.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Bindable
@Portable
@Ignore
public class TestModel {

  private int id;
  private boolean active;
  private Date lastChanged;

  // to test direct field access
  public String value;
  public TestModel child;

  // the _ is used to test proper JavaBean property discovery (based on getters/setters and not on
  // the field name)
  private String _name;
  private Integer _age;

  // test that this variable name does not cause a duplicate local variable in the generated proxy
  private String oldValue;

  // test for the case there's a field name collision in the generated proxy
  private String agent;

  private BigDecimal amountDec;
  private BigInteger amountInt;

  public TestModel() {}

  public TestModel(String value) {
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getName() {
    return _name;
  }

  public void setName(String _name) {
    this._name = _name;
  }

  public Integer getAge() {
    return _age;
  }

  public TestModel setAge(Integer _age) {
    this._age = _age;
    return this;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public TestModel getChild() {
    return child;
  }

  // this method is used to test changes to references of nested bindables using a non accessor
  // method
  public void resetChildren() {
    TestModel newChild = new TestModel();

    TestModel curChild = child;
    while ((curChild = curChild.child) != null) {
      newChild.child = new TestModel();
      newChild = newChild.child;
    }

    this.child = newChild;
  }

  public void setChild(TestModel child) {
    this.child = child;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public Date getLastChanged() {
    return lastChanged;
  }

  public void setLastChanged(Date lastChanged) {
    this.lastChanged = lastChanged;
  }

  // these methods are used to test property changes using non accessor methods
  public void activate() {
    this.active = true;
    if (child != null) {
      child.activate();
    }
  }
  
  public void setActivateStatus(boolean value) {
    this.active = value;
  }
  
  public boolean getActivateStatus(String ignore) {
    return this.active;
  }

  public TestModel activate(boolean activate) {
    this.active = activate;
    return this;
  }

  public BigDecimal getAmountDec() {
    return amountDec;
  }

  public void setAmountDec(BigDecimal amountDec) {
    this.amountDec = amountDec;
  }

  public BigInteger getAmountInt() {
    return amountInt;
  }

  public void setAmountInt(BigInteger amountInt) {
    this.amountInt = amountInt;
  }
  
  // This guards against regressions of https://issues.jboss.org/browse/ERRAI-840
  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  // This guards against regressions of https://issues.jboss.org/browse/ERRAI-479
  public static void staticMethod() {};

  // This guards against regressions of https://issues.jboss.org/browse/ERRAI-476
  @Bindable
  public static class DuplicateNamedBindableType {}

  // This guards against regressions of https://issues.jboss.org/browse/ERRAI-512
  public <M extends Serializable> M genericMethod(M m) {
    return null;
  }

  public <M> M simpleGenericMethod(M m) {
    return null;
  }

  // This guards against regressions of https://issues.jboss.org/browse/ERRAI-551
  public void methodWithParameterizedType(List<String> list) {

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_age == null) ? 0 : _age.hashCode());
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((child == null) ? 0 : child.hashCode());
    result = prime * result + id;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    TestModel other = (TestModel) obj;
    if (_age == null) {
      if (other.getAge() != null)
        return false;
    }
    else if (!_age.equals(other.getAge()))
      return false;
    if (_name == null) {
      if (other.getName() != null)
        return false;
    }
    else if (!_name.equals(other.getName()))
      return false;
    if (active != other.isActive())
      return false;
    if (child == null) {
      if (other.getChild() != null)
        return false;
    }
    else if (!child.equals(other.getChild()))
      return false;
    if (id != other.getId())
      return false;
    if (value == null) {
      if (other.getValue() != null)
        return false;
    }
    else if (!value.equals(other.getValue()))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Model [id=" + id + ", value=" + value + ", _name=" + _name + ", _age=" + _age + ", active=" + active
        + ", child=" + child + "]";
  }

}
