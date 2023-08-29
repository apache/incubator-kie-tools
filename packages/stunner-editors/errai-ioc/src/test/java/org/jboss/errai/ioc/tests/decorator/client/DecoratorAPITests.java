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


package org.jboss.errai.ioc.tests.decorator.client;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.decorator.client.res.MyDecoratedBean;
import org.jboss.errai.ioc.tests.decorator.client.res.TestDataCollector;

/**
 * @author Mike Brock
 */
public class DecoratorAPITests extends AbstractErraiIOCTest {
  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.decorator.DecoratorAPITests";
  }

  public void testBeanDecoratedWithProxy() {
    final MyDecoratedBean instance = IOC.getBeanManager().lookupBean(MyDecoratedBean.class).getInstance();

    instance.someMethod("a", 1);
    instance.someMethod("b", 2);
    instance.someMethod("c", 3);

    assertEquals(instance.getTestMap(), TestDataCollector.getBeforeInvoke());
    assertEquals(instance.getTestMap(), TestDataCollector.getAfterInvoke());

    final Map<String, Object> expectedProperties = new HashMap<>();
    expectedProperties.put("foobar", "foobie!");

    assertEquals(expectedProperties, TestDataCollector.getProperties());
  }

  public void testInitializationStatementsInvoked() throws Exception {
    final MyDecoratedBean instance = IOC.getBeanManager().lookupBean(MyDecoratedBean.class).getInstance();

    // setFlag(true) should be called by init callback.
    assertTrue(instance.isFlag());
  }

  public void testDestructionStatementsInvoked() throws Exception {
    final MyDecoratedBean instance = IOC.getBeanManager().lookupBean(MyDecoratedBean.class).getInstance();
    final MyDecoratedBean unwrapped = Factory.maybeUnwrapProxy(instance);

    // precondition
    assertTrue(instance.isFlag());
    IOC.getBeanManager().destroyBean(instance);

    // Must use unwrapped here or else proxy loads a new instance
    assertFalse(unwrapped.isFlag());
  }
}
