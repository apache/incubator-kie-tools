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


package org.jboss.errai.ioc.tests.wiring.client.res;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;


@EntryPoint
public class SimpleBean extends SimpleSuperBean {

  @Inject
  public SimpleSingleton singletonA;

  private SimpleSingleton singletonB;
  private SimpleSingleton2 singleton2;

  private SimpleSingleton singletonC;
  
  @Inject ServiceA svcA;
  @Inject ServiceB svcB;

  private boolean postConstructCalled = false;
  private boolean preDestroyCalled = false;

  @PostConstruct
  private void init() {
    postConstructCalled = true;
  }

  @PreDestroy
  public void destroy() {
    preDestroyCalled = true;
  }

  @Inject
  public SimpleBean(SimpleSingleton2 singleton2, SimpleSingleton singletonB) {
    this.singleton2 = singleton2;
    this.singletonB = singletonB;
  }


  public SimpleSingleton getSingletonA() {
    return singletonA;
  }

  public SimpleSingleton getSingletonB() {
    return singletonB;
  }

  public SimpleSingleton2 getSingleton2() {
    return singleton2;
  }

  public void setSingletonB(SimpleSingleton singletonB) {
    this.singletonB = singletonB;
  }


  public SimpleSingleton2 getDispatcher4() {
    return singleton2;
  }

  @Inject
  public void setDispatcher4(SimpleSingleton2 singleton2) {
    this.singleton2 = singleton2;
  }

  public SimpleSingleton getSingletonC() {
    return singletonC;
  }

  @Inject
  public void setSingletonC(SimpleSingleton singletonC) {
    this.singletonC = singletonC;
  }

  public boolean isPostConstructCalled() {
    return postConstructCalled;
  }

  public ServiceA getSvcA() {
    return svcA;
  }

  public ServiceB getSvcB() {
    return svcB;
  }

  public SimpleSingleton getSuperSimpleSingleton() {
    return superSimpleSingleton;
  }
}
