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


package org.jboss.errai.ioc.async.test.constructor.client;

import org.jboss.errai.ioc.async.test.constructor.client.res.ConstrInjBean;
import org.jboss.errai.ioc.client.Container;
import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.container.IOC;

/**
 * @author Mike Brock
 */
public class AsyncConstructorInjectionTests extends IOCClientTestCase {

  @Override
  public void gwtSetUp() throws Exception {
    super.gwtSetUp();
  }

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.async.test.constructor.AsyncConstrInjectTests";
  }

  public void testBeanConstructedViaConstructor() {
    delayTestFinish(10000);
    Container.runAfterInit(new Runnable() {
      @Override
      public void run() {
        IOC.getAsyncBeanManager().lookupBean(ConstrInjBean.class)
            .getInstance(bean -> {
              assertNotNull(bean.getMyself());
              assertNotNull(bean.getApple());
              assertNotNull(bean.getPear());
              assertNotNull(bean.getOrange());

              assertNotNull(bean.getPeanut());
              assertNotNull(bean.getCashew());

              assertTrue(bean.isPostConstructFired());

              assertSame(IOC.getAsyncBeanManager().getActualBeanReference(bean),
                  IOC.getAsyncBeanManager().getActualBeanReference(bean.getMyself()));

              finishTest();
            });
      }
    });
  }
}
