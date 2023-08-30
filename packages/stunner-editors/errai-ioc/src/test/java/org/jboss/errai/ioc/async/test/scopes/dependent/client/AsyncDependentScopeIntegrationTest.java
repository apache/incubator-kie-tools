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


package org.jboss.errai.ioc.async.test.scopes.dependent.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.Timer;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.ApplicationScopedBean;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.ApplicationScopedBeanB;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.Bean;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.BeanInjectsNonModuleDependentBean;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.BeanInjectsNonModuleDependentBeanB;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.DepScopedBeanWithASBeanDep;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.DependentScopedBean;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.DependentScopedBeanWithDependencies;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.DestroyA;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.OuterBean;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.ServiceA;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.ServiceB;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.ServiceC;
import org.jboss.errai.ioc.async.test.scopes.dependent.client.res.UnreferencedDependentRootBean;
import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.container.IOC;

/**
 * @author Mike Brock
 */
public class AsyncDependentScopeIntegrationTest extends IOCClientTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.async.test.scopes.dependent.AsyncDepScopeTests";
  }

  @Override
  public void gwtSetUp() throws Exception {
    super.gwtSetUp();
  }

  public void testDependentBeanScope() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        new Timer() {
          @Override
          public void run() {
            IOC.getAsyncBeanManager().lookupBean(ApplicationScopedBean.class)
                .getInstance(new CreationalCallback<ApplicationScopedBean>() {
                  @Override
                  public void callback(final ApplicationScopedBean beanA) {

                    final DependentScopedBean b1 = beanA.getBean1();
                    final DependentScopedBean b2 = beanA.getBean2();
                    final DependentScopedBean b3 = beanA.getBean3();
                    final DependentScopedBeanWithDependencies b4 = beanA.getBeanWithDependencies();

                    assertTrue("dependent scoped semantics broken", b2.getInstance() != b1.getInstance());
                    assertTrue("dependent scoped semantics broken", b3.getInstance() != b2.getInstance());

                    assertNotNull("dependent scoped bean with injections was not injected", b4);
                    assertNotNull("dependent scoped beans own injections not injected", b4.getBean());
                    assertTrue("dependent scoped semantics broken", b4.getBean().getInstance() != b3.getInstance());
                    finishTest();
                  }
                });
          }
        }.schedule(1500);
      }
    });
  }

  public void testDependentScopesWithTransverseDependentBeans() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(OuterBean.class)
            .getInstance(new CreationalCallback<OuterBean>() {
              @Override
              public void callback(final OuterBean outBean) {

                assertNotNull("outer bean was null", outBean);

                final Bean testBean = outBean.getTestBean();
                assertNotNull("outBean.getTestBean() returned null", testBean);

                final ServiceA serviceA = testBean.getServiceA();
                final ServiceB serviceB = testBean.getServiceB();
                final ServiceC serviceC = testBean.getServiceC();

                assertNotNull("serviceA is null", serviceA);
                assertNotNull("serviceB is null", serviceB);
                assertNotNull("serviceC is null", serviceC);

                final ServiceC serviceC1 = serviceA.getServiceC();
                final ServiceC serviceC2 = serviceB.getServiceC();

                assertNotNull("serviceC in serviceA is null", serviceC1);
                assertNotNull("serviceC in serviceB is null", serviceC2);

                final Set<String> testDependentScope = new HashSet<String>();
                testDependentScope.add(serviceC.getName());
                testDependentScope.add(serviceC1.getName());
                testDependentScope.add(serviceC2.getName());

                assertEquals("ServiceC should have been instantiated 3 times", 3, testDependentScope.size());
                finishTest();
              }
            });
      }
    });
  }

  public void testDependentScopeDoesNotViolateBroaderApplicationScope() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(ApplicationScopedBean.class)
            .getInstance(new CreationalCallback<ApplicationScopedBean>() {
              @Override
              public void callback(final ApplicationScopedBean applicationScopedBean) {

                assertNotNull("ApplicationScopedBean was null", applicationScopedBean);

                IOC.getAsyncBeanManager()
                    .lookupBean(ServiceC.class).getInstance(new CreationalCallback<ServiceC>() {
                  @Override
                  public void callback(final ServiceC serviceC) {
                    assertEquals("ApplicationScopedBean should be same instance even in dependent scoped",
                        applicationScopedBean.getBeanId(), serviceC.getBean().getBeanId());

                    finishTest();
                  }
                });
              }
            });
      }
    });
  }

  /**
   * Tests that a dependent scoped bean, which is not referenced by other beans, is accessible from the BeanManager.
   * Additionally, this test also ensures that a dependent scoped bean is resolveable behind an interface that
   * it implements and that the interface is an innerclass of another bean.
   */
  public void testUnreferencedDependentRootBeanAccessible() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(UnreferencedDependentRootBean.class)
            .getInstance(new CreationalCallback<UnreferencedDependentRootBean>() {
              @Override
              public void callback(final UnreferencedDependentRootBean applicationScopedBean) {
                assertNotNull("UnreferencedDependentRootBean was null", applicationScopedBean);
                assertNotNull("Dependent injection was null", applicationScopedBean.getBeanB());
                finishTest();
              }
            });
      }
    });
  }

  public void testDependentBeanCycleFromApplicationScopedRoot() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {

        IOC.getAsyncBeanManager().lookupBean(ApplicationScopedBeanB.class)
            .getInstance(new CreationalCallback<ApplicationScopedBeanB>() {
              @Override
              public void callback(final ApplicationScopedBeanB bean) {
                assertNotNull("DependentBeanCycleA was null", bean);
                assertNotNull("dependentScopedBean.dependentBeanCycleA injection was null",
                    bean.getDependentBeanCycleA());
                assertNotNull("dependentScopedBean.dependentBeanCycleA.dependentBeanCycleB was null",
                    bean.getDependentBeanCycleA().getApplicationScopedBeanCycleA());

                finishTest();
              }
            });
      }
    });
  }

  public void testDependentBeanCycleWithPreDestroy() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(DestroyA.class)
            .getInstance(new CreationalCallback<DestroyA>() {
              @Override
              public void callback(final DestroyA bean) {
                IOC.getAsyncBeanManager().destroyBean(bean);

                assertTrue("pre-destroy method not called!", bean.isDestroyed());
                assertTrue("pre-destroy method not called", bean.getTestDestroyB().isDestroyed());

                finishTest();
              }
            });
      }
    });
  }

  public void testDependentScopedBeanWithAppScopedDependencyDestroy() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(DepScopedBeanWithASBeanDep.class)
            .getInstance(new CreationalCallback<DepScopedBeanWithASBeanDep>() {
              @Override
              public void callback(final DepScopedBeanWithASBeanDep bean) {

                assertNotNull("no instance returned for bean", bean);
                assertNotNull("ApplicationScopedBean not injected", bean.getApplicationScopedBean());

                IOC.getAsyncBeanManager().destroyBean(bean);

                assertTrue("pre-destroy method not called", bean.isPreDestroyCalled());
                assertFalse("ApplicationScopedBean's pre-destruct method must NOT be called",
                    bean.getApplicationScopedBean().isPreDestroyCalled());

                assertFalse("bean should no longer be managed", IOC.getAsyncBeanManager().isManaged(bean));

                finishTest();
              }
            });
      }
    });

  }

  public void testNonModuleTranslatableClassInjectableAsDependent() {
    delayTestFinish(10000);

    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(BeanInjectsNonModuleDependentBean.class)
            .getInstance(new CreationalCallback<BeanInjectsNonModuleDependentBean>() {
              @Override
              public void callback(final BeanInjectsNonModuleDependentBean bean) {
                assertNotNull("no instance returned for bean", bean);
                assertNotNull("non-module dependent bean not injected", bean.getList());
                assertEquals("wrong number of elements in list", 2, bean.getList().size());
                assertEquals("wrong element", "foo", bean.getList().get(0));
                assertEquals("wrong element", "bar", bean.getList().get(1));

                finishTest();
              }
            });
      }
    });

  }

  public void testNonModuleTranslatableClassInjectableAsDependentWithAliasedInjectionPoint() {
    delayTestFinish(10000);
    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(BeanInjectsNonModuleDependentBeanB.class)
            .getInstance(new CreationalCallback<BeanInjectsNonModuleDependentBeanB>() {
              @Override
              public void callback(final BeanInjectsNonModuleDependentBeanB bean) {

                assertNotNull("no instance returned for bean", bean);
                assertNotNull("non-module dependent bean not injected", bean.getFunArrayListOfString());
                assertEquals("wrong number of elements in list", 2, bean.getFunArrayListOfString().size());
                assertEquals("wrong element", "foo", bean.getFunArrayListOfString().get(0));
                assertEquals("wrong element", "bar", bean.getFunArrayListOfString().get(1));

                finishTest();
              }
            });
      }
    });
  }
}
