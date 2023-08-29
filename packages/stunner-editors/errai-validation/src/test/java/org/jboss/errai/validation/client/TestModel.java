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


package org.jboss.errai.validation.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.junit.Ignore;

/**
 * Simple bindable model for testing purposes.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Johannes Barop <jb@barop.de>
 */
@Bindable
@Portable
@Ignore
public class TestModel {

  @Min(value = 100)
  private int numVal;

  @NotNull
  private String stringVal;

  @TestConstraint(groups = TestGroup.class)
  private String testConstraint;

  @Valid
  private TestModel child;

  @NotNull
  @Valid
  private List<TestModel> list = new ArrayList<TestModel>();

  @NotNull
  @Valid
  private HashSet<TestModel> set = new HashSet<TestModel>();
  
  // Serves as a regression test for ERRAI-763
  private HashSet<TestModel> nullList;

  public int getNumVal() {
    return numVal;
  }

  public void setNumVal(int numVal) {
    this.numVal = numVal;
  }

  public String getStringVal() {
    return stringVal;
  }

  public void setStringVal(String stringVal) {
    this.stringVal = stringVal;
  }

  public String getTestConstraint() {
    return testConstraint;
  }

  public void setTestConstraint(String lowerCase) {
  }

  public TestModel getChild() {
    return child;
  }

  public void setChild(TestModel child) {
    this.child = child;
  }

  public List<TestModel> getList() {
    return list;
  }

  public void setList(List<TestModel> list) {
    this.list = list;
  }

  public void addToList(TestModel l) {
    this.list.add(l);
  }
  
  public HashSet<TestModel> getSet() {
    return set;
  }

  public void setSet(HashSet<TestModel> set) {
    this.set = set;
  }
  
  public void addToSet(TestModel l) {
    this.set.add(l);
  }
  
  public HashSet<TestModel> getNullList() {
    return nullList;
  }

  public void setNullList(HashSet<TestModel> nullList) {
    this.nullList = nullList;
  }

}
