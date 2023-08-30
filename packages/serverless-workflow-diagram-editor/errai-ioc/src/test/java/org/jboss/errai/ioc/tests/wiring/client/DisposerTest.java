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
import org.jboss.errai.ioc.tests.wiring.client.res.BeanWithProvidedBeansInjected;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentBean;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentBeanWithDisposer;
import org.jboss.errai.ioc.tests.wiring.client.res.NestedDependentBean;
import org.jboss.errai.ioc.tests.wiring.client.res.NestedDependentBeanWIthDisposer;
import org.jboss.errai.ioc.tests.wiring.client.res.SingletonBeanWithDisposer;

/**
 * @author Mike Brock
 */
public class DisposerTest extends AbstractErraiIOCTest {
  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.wiring.IOCWiringTests";
  }

  public void testDisposerFailsToDestroyAppScope() {

    final SingletonBeanWithDisposer outerBean = IOC.getBeanManager().lookupBean(SingletonBeanWithDisposer.class).getInstance();
    assertNotNull(outerBean);
    assertNotNull(outerBean.getDependentBeanDisposer());
    final DependentBean innerBean = outerBean.getBean();
    assertNotNull(innerBean);

    outerBean.dispose();

    assertFalse("inner bean should have been disposed", IOC.getBeanManager().isManaged(innerBean));
    assertTrue("outer bean should not have been disposed", IOC.getBeanManager().isManaged(outerBean));
    assertTrue("bean's destructor should have been called", innerBean.isPreDestroyCalled());
  }

  public void testDisposerWorksWithDependentScope() {

    final DependentBeanWithDisposer outerBean = IOC.getBeanManager().lookupBean(DependentBeanWithDisposer.class).getInstance();
    assertNotNull(outerBean);
    assertNotNull(outerBean.getDependentBeanDisposer());
    final DependentBean innerBean = outerBean.getBean();
    assertNotNull(innerBean);

    outerBean.dispose();

    assertFalse("inner bean should have been disposed", IOC.getBeanManager().isManaged(innerBean));
    assertTrue("outer bean should not have been disposed", IOC.getBeanManager().isManaged(outerBean));
    assertTrue("inner bean's destructor should have been called", innerBean.isPreDestroyCalled());
  }

  public void testDisposerWorksWithNestedDependentScopedBeans() throws Exception {

    final NestedDependentBeanWIthDisposer outerBean = IOC.getBeanManager().lookupBean(NestedDependentBeanWIthDisposer.class).getInstance();
    assertNotNull(outerBean);
    final NestedDependentBean middleBean = outerBean.getNestedBean();
    assertNotNull(middleBean);
    final DependentBean innerBean = middleBean.getBean();
    assertNotNull(innerBean);

    outerBean.dispose();

    assertTrue("outer bean should not have been disposed", IOC.getBeanManager().isManaged(outerBean));
    assertFalse("middle bean should have been disposed", IOC.getBeanManager().isManaged(middleBean));
    assertTrue("middle bean's destructor should have been called", middleBean.isPreDestroyCalled());
    assertFalse("inner bean should have been disposed", IOC.getBeanManager().isManaged(innerBean));
    assertTrue("inner bean's destructor should have been called", innerBean.isPreDestroyCalled());
  }

  public void testDisposerMethodCalledOnProviderWhenProvidedBeanIsDestroyed() throws Exception {
    final BeanWithProvidedBeansInjected module = IOC.getBeanManager().lookupBean(BeanWithProvidedBeansInjected.class).getInstance();
    assertFalse("Bean was prematurely destroyed.", module.providedBean.isDestroyed());
    IOC.getBeanManager().destroyBean(module.providedBean);
    assertTrue("Disposer method not called after bean was destroyed.", module.providedBean.isDestroyed());
  }

  public void testDisposerMethodCalledOnContextualProviderWhenProvidedBeanIsDestroyed() throws Exception {
    final BeanWithProvidedBeansInjected module = IOC.getBeanManager().lookupBean(BeanWithProvidedBeansInjected.class).getInstance();
    assertFalse("Bean was prematurely destroyed.", module.contextuallyProvidedBean.isDestroyed());
    IOC.getBeanManager().destroyBean(module.contextuallyProvidedBean);
    assertTrue("Disposer method not called after bean was destroyed.", module.contextuallyProvidedBean.isDestroyed());
  }
}
