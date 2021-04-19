/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.tests.errai.ioc.wiring.client;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.tests.errai.ioc.wiring.client.res.NotWhitelistedBean;
import org.jboss.tests.errai.ioc.wiring.client.res.WhitelistedAndBlacklistedBean;
import org.jboss.tests.errai.ioc.wiring.client.res.WhitelistedBean;
import org.jboss.tests.errai.ioc.wiring.client.res.sub.WhitelistedPackageBean;

/**
 * @author (htfv) Aliaksei Lahachou
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class WhiteListingBeansIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.tests.errai.ioc.wiring.IOCWiringTests";
  }

  public void testNotWhitelistedPackage() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(NotWhitelistedBean.class).getInstance();
      fail("Should not be able to resolve a not whitelisted bean!");
    }
    catch (final IOCResolutionException e) {
      // expected
    }
  }

  public void testWhitelistedBean() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(WhitelistedBean.class).getInstance();
    }
    catch (final IOCResolutionException e) {
      fail("Should be able to resolve a whitelisted bean!");
    }
  }

  public void testWhitelistedAndBlacklistedBean() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(WhitelistedAndBlacklistedBean.class).getInstance();
      fail("Should not be able to resolve a whitelisted bean if it is blacklisted!");
    }
    catch (final IOCResolutionException e) {
      // expected
    }
  }
  
  public void testWhitelistedPackage() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(WhitelistedPackageBean.class).getInstance();
    }
    catch (IOCResolutionException e) {
      fail("Should be able to resolve a bean in a whitelisted package!");
    }
  }
}
