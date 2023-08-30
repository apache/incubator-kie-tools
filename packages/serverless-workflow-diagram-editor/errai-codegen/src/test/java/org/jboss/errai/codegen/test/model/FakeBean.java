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


package org.jboss.errai.codegen.test.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Mike Brock
 */
public class FakeBean {
  @Inject Bwah bwahBean;

  @Inject Instance<Bar> instanceBar;

  public void testMethod() {
    addSomething("foobie", BigInteger.class, BigDecimal.class);
  }

  private void addSomething(final String name, final Class a, final Class b) {
    // yay
  }
}

