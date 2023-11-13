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


package org.jboss.errai.ui.test.common.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Simple bindable model for testing purposes.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Bindable
@Portable
public class TestModel {

  private Integer id;
  private String name;
  private String title;
  private Integer age;
  private Date lastChanged;
  private String phoneNumber;
  private List<TestModel> children = new ArrayList<TestModel>();

  private TestModel child;

  public TestModel() {}

  public TestModel(Integer id, String name) {
    this.id = id;
    this.name = name;
    this.lastChanged = new Date();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getLastChanged() {
    return lastChanged;
  }

  public void setLastChanged(Date lastChanged) {
    this.lastChanged = lastChanged;
  }

  public void setChild(TestModel child) {
    this.child = child;
  }

  public TestModel getChild() {
    return child;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public List<TestModel> getChildren() {
    return children;
  }

  public void setChildren(List<TestModel> children) {
    this.children = children;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return "TestModel [id=" + id + ", name=" + name + ", lastChanged=" + lastChanged + ", phoneNumber=" + phoneNumber
        + ", child=" + child + "]";
  }

}
