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


package org.jboss.errai.ioc.tests.extensions.client;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.extensions.client.res.ClassWithLoggerField;
import org.jboss.errai.ioc.tests.extensions.client.res.ClassWithNamedLoggerField;

public class IOCLoggingInjectorTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.extensions.IOCExtensionTests";
  }

  public void testSimpleLoggerFieldInjection() throws Exception {
    ClassWithLoggerField instance = IOC.getBeanManager().lookupBean(ClassWithLoggerField.class).getInstance();
    assertNotNull("Logger was not injected", instance.getLogger());
    assertEquals("Logger should have name of enclosing class", ClassWithLoggerField.class.getName(), instance
            .getLogger().getName());
  }

  public void testNamedLoggerFieldInjection() throws Exception {
    ClassWithNamedLoggerField instance = IOC.getBeanManager().lookupBean(ClassWithNamedLoggerField.class).getInstance();
    assertNotNull("Logger was not injected", instance.getLogger());
    assertEquals("Logger should have had the given name", ClassWithNamedLoggerField.LOGGER_NAME, instance.getLogger()
            .getName());
  }

}
