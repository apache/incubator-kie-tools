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


package org.jboss.errai.ioc.async.test.constructor.client.res;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Mike Brock
 */
@ApplicationScoped
public class ConstrInjBean {
  private ConstrInjBean myself;
  private Apple apple;
  private Pear pear;

  private Peanut peanut;
  private Cashew cashew;

  @Inject
  private Orange orange;

  private boolean postConstructFired = false;

  public ConstrInjBean() {
  }

  @Inject
  public ConstrInjBean(final ConstrInjBean constrInjBean, final Apple apple, final Pear pear) {
    this.myself = constrInjBean;
    this.apple = apple;
    this.pear = pear;
  }

  @Inject
  public void setPeanut(final Peanut peanut) {
    this.peanut = peanut;
  }

  @Inject
  public void setCashew(final Cashew cashew) {
    this.cashew = cashew;
  }

  public ConstrInjBean getMyself() {
    return myself;
  }

  public Apple getApple() {
    return apple;
  }

  public Pear getPear() {
    return pear;
  }

  public Orange getOrange() {
    return orange;
  }

  @PostConstruct
  private void aPostConstructMethod() {
    postConstructFired = true;
  }

  public boolean isPostConstructFired() {
    return postConstructFired;
  }

  public Peanut getPeanut() {
    return peanut;
  }

  public Cashew getCashew() {
    return cashew;
  }
}
