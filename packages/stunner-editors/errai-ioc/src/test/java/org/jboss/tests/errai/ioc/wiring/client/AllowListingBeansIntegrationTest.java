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


package org.jboss.tests.errai.ioc.wiring.client;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.tests.errai.ioc.wiring.client.res.AllowlistedAndDenylistedBean;
import org.jboss.tests.errai.ioc.wiring.client.res.AllowlistedBean;
import org.jboss.tests.errai.ioc.wiring.client.res.NotAllowlistedBean;
import org.jboss.tests.errai.ioc.wiring.client.res.sub.AllowlistedPackageBean;

/**
 * @author (htfv) Aliaksei Lahachou
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class AllowListingBeansIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.tests.errai.ioc.wiring.IOCWiringTests";
  }

  public void testNotAllowlistedPackage() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(NotAllowlistedBean.class).getInstance();
      fail("Should not be able to resolve a not allowlisted bean!");
    }
    catch (final IOCResolutionException e) {
      // expected
    }
  }

  public void testAllowlistedBean() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(AllowlistedBean.class).getInstance();
    }
    catch (final IOCResolutionException e) {
      fail("Should be able to resolve a allowlisted bean!");
    }
  }

  public void testAllowlistedAndDenylistedBean() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(AllowlistedAndDenylistedBean.class).getInstance();
      fail("Should not be able to resolve a allowlisted bean if it is denylisted!");
    }
    catch (final IOCResolutionException e) {
      // expected
    }
  }
  
  public void testAllowlistedPackage() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(AllowlistedPackageBean.class).getInstance();
    }
    catch (IOCResolutionException e) {
      fail("Should be able to resolve a bean in a allowlisted package!");
    }
  }
}
