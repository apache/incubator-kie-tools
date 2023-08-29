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


package org.jboss.errai.ioc.async.test.beanmanager.client;

import java.util.Collection;

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.ADependent;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.AirDependentBean;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.Bar;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.Cow;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.Foo;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.Pig;
import org.jboss.errai.ioc.async.test.beanmanager.client.res.TestInterface;
import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.api.LoadAsync;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.container.async.AsyncBeanDef;

/**
 * @author Mike Brock
 */
public class AsyncBeanManagerTests extends IOCClientTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.async.test.beanmanager.AsyncBeanManagerTests";
  }

  public void testTypeWithLoadAsyncIsNotAvailableThroughSyncLookup() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(Foo.class);
      fail("Should not have been able to lookup " + Foo.class.getSimpleName() + " with " + LoadAsync.class.getSimpleName() + " annotation via the SyncBeanManager.");
    } catch (IOCResolutionException e) {
    }
  }

  public void testTypeWithLoadAsyncIsAvailableThroughSyncLookupAfterAsyncLoading() throws Exception {
    try {
      testTypeWithLoadAsyncIsNotAvailableThroughSyncLookup();
    } catch (AssertionError e) {
      fail("Precondition failed: " + e.getMessage());
    }

    delayTestFinish(10000);
    IOC.getAsyncBeanManager().lookupBean(Foo.class).getInstance(new CreationalCallback<Foo>() {

      @Override
      public void callback(final Foo beanInstance) {
        try {
          final Foo instance = IOC.getBeanManager().lookupBean(Foo.class).getInstance();
          assertEquals(beanInstance, instance);
          finishTest();
        } catch (IOCResolutionException e) {
          fail("Should have been able to perform sync lookup for instance of " + Foo.class.getSimpleName()
                  + " after async loading.");
        }
      }
    });
  }

  public void testAsyncLookup() {
    delayTestFinish(10000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(Foo.class).getInstance(new CreationalCallback<Foo>() {
          @Override
          public void callback(final Foo bean) {
            assertNotNull(bean);
            assertNotNull(bean.getBar());
            assertNotNull(bean.getBar2());
            assertNotNull(bean.getBarDisposer());
            assertNotNull(bean.getBar2().getManager());
            assertNotNull(bean.getBazTheSingleton());
            assertNotNull(bean.getBar().getBazTheSingleton());
            assertNotNull(bean.getBar2().getBazTheSingleton());

            assertSame(bean.getBazTheSingleton(), bean.getBar().getBazTheSingleton());
            assertSame(bean.getBazTheSingleton(), bean.getBar2().getBazTheSingleton());

            final Object fooRef1 = IOC.getAsyncBeanManager().getActualBeanReference(bean.getBar().getFoo());
            final Object fooRef2 = IOC.getAsyncBeanManager().getActualBeanReference(bean);

            assertSame(fooRef1, fooRef2);

            // confirm post-construct fired
            assertTrue(bean.getBar().isPostContr());

            System.out.println("foo.bar=" + bean.getBar());
            finishTest();
          }
        });
      }
    });
  }

  public void testCreateAndDestroyBean() {
    delayTestFinish(10000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(Bar.class).getInstance(new CreationalCallback<Bar>() {
          @Override
          public void callback(final Bar bean) {
            assertTrue(IOC.getAsyncBeanManager().isManaged(bean));

            IOC.getAsyncBeanManager().destroyBean(bean);

            assertFalse(IOC.getAsyncBeanManager().isManaged(bean));

            finishTest();
          }
        });
      }
    });
  }

  public void testLookupDependentBean() {
    delayTestFinish(100000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(ADependent.class)
            .getInstance(new CreationalCallback<ADependent>() {
              @Override
              public void callback(final ADependent bean) {
                assertNotNull(bean);

                assertEquals("foo", bean.testString());

                finishTest();
              }
            });
      }
    });
  }

  public void testDependentBeanNotReturnedTwiceAfterLoading() {
    delayTestFinish(100000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(ADependent.class)
            .getInstance(new CreationalCallback<ADependent>() {
              @Override
              public void callback(final ADependent bean) {
                assertNotNull(bean);
                assertEquals("foo", bean.testString());
                assertEquals(1, IOC.getAsyncBeanManager().lookupBeans(ADependent.class).size());

                finishTest();
              }
            });
      }
    });
  }

  public void testLookupFromSuperTypes() {
    delayTestFinish(10000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        final Collection<AsyncBeanDef<TestInterface>> asyncBeanDefs = IOC.getAsyncBeanManager().lookupBeans(TestInterface.class);

        assertEquals(2, asyncBeanDefs.size());

        finishTest();
      }
    });
  }

  public void testNamedLookupsOfBean() {
    delayTestFinish(10000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        @SuppressWarnings("rawtypes")
        final Collection<AsyncBeanDef> beans = IOC.getAsyncBeanManager().lookupBeans("animal");

        assertEquals("wrong number of beans", 2, beans.size());
        assertTrue("should contain a pig", containsInstanceOf(beans, Pig.class));
        assertTrue("should contain a cow", containsInstanceOf(beans, Cow.class));

        finishTest();
      }
    });
  }

  public void testBeanInjectedByInterface() {
    delayTestFinish(100000);

    Container.$(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(AirDependentBean.class)
            .getInstance(new CreationalCallback<AirDependentBean>() {
              @Override
              public void callback(AirDependentBean beanInstance) {
                assertNotNull(beanInstance);
                assertNotNull(beanInstance.getAir());
                finishTest();
              }
            });
      }
    });
  }


  @SuppressWarnings("rawtypes")
  private static boolean containsInstanceOf(final Collection<AsyncBeanDef> defs, final Class<?> clazz) {
    for (final AsyncBeanDef def : defs) {
      if (def.getType().equals(clazz)) return true;
    }
    return false;
  }
}

