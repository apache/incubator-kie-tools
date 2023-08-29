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


package org.jboss.errai.ioc.tests.wiring.client;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.wiring.client.res.AlternativeBeanA;
import org.jboss.errai.ioc.tests.wiring.client.res.AlternativeDependentBean;
import org.jboss.errai.ioc.tests.wiring.client.res.OverridingAltCommonInterfaceBImpl;
import org.jboss.errai.ioc.tests.wiring.client.res.SingleAlternativeDependentBean;

/**
 * @author Mike Brock
 */
public class AlternativeBeanIntegrationTest extends AbstractErraiIOCTest {
  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.wiring.IOCWiringTests";
  }


  public void testAlternativeBeanInjection() throws Exception {
    // AlternativeBeanA should be configured as an alternative in the ErraiApp.properties of this
    // test module.

    AlternativeDependentBean bean = IOC.getBeanManager().lookupBean(AlternativeDependentBean.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getCommonInterface());
    assertTrue("wrong instance of bean injected", bean.getCommonInterface() instanceof AlternativeBeanA);

  }

  /**
   * This test ensures that a single enabled alternative will take resolution precedence over all other matching
   * beans for an injection point.
   *
   * @throws Exception
   */
  public void testSingleAlternativeTakesPrecedence() throws Exception {

    SingleAlternativeDependentBean bean = IOC.getBeanManager()
        .lookupBean(SingleAlternativeDependentBean.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getAlternativeCommonInterfaceB());
    assertTrue("wrong instance of bean injected",
        bean.getAlternativeCommonInterfaceB() instanceof OverridingAltCommonInterfaceBImpl);

  }
}
