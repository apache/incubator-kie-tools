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
import org.jboss.errai.ioc.rebind.ioc.extension.builtin.WidgetIOCExtension;
import org.jboss.errai.ioc.tests.extensions.client.res.ClassWithInjectedTextBox;

/**
 * Tests that the {@link WidgetIOCExtension} allows injection of widget types that would otherwise be ambiguous.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class WidgetIOCExtensionTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.extensions.IOCExtensionTests";
  }

  public void testTextBoxInjection() throws Exception {
    try {
      IOC.getBeanManager().lookupBean(ClassWithInjectedTextBox.class).getInstance();
    } catch (Throwable t) {
      throw new AssertionError("Could not create instance of type with injected TextBox.", t);
    }
  }

}
