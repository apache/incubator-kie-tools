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


package org.jboss.errai.ui.test.quickhandler.client;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ui.test.quickhandler.client.res.InteropEventQuickHandlerTemplate;
import org.jboss.errai.ui.test.quickhandler.client.res.InteropEventQuickHandlerTemplate.ObservedEvent;

import static org.jboss.errai.common.client.util.EventTestingUtil.invokeEventListeners;
import static org.jboss.errai.common.client.util.EventTestingUtil.setupAddEventListenerInterceptor;

public class InteropEventQuickHandlerTemplateTest extends AbstractErraiCDITest {

  private InteropEventQuickHandlerTemplate bean;

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    setupAddEventListenerInterceptor();
    bean = IOCUtil.getInstance(InteropEventQuickHandlerTemplate.class);
  }

  public void testButtonSingleClickHandler() throws Exception {
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(bean.button, "click");
    assertEquals(Arrays.asList(new ObservedEvent("button", "click")), bean.observed);
    invokeEventListeners(bean.button, "dblclick");
    assertEquals(Arrays.asList(new ObservedEvent("button", "click")), bean.observed);
  }

  public void testAnchorSingleAndDoubleClick() throws Exception {
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(bean.anchor, "click");
    assertEquals(Arrays.asList(new ObservedEvent("anchor", "click")), bean.observed);
    invokeEventListeners(bean.anchor, "dblclick");
    assertEquals(Arrays.asList(new ObservedEvent("anchor", "click"), new ObservedEvent("anchor", "dblclick")), bean.observed);
  }

  public void testInputChange() throws Exception {
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(bean.input, "change");
    assertEquals(Arrays.asList(new ObservedEvent("input", "change")), bean.observed);
    invokeEventListeners(bean.input, "click");
    assertEquals(Arrays.asList(new ObservedEvent("input", "change")), bean.observed);
  }

  public void testListenersRemovedAfterBeanDestroyed() throws Exception {
    try {
      assertTrue(bean.observed.isEmpty());
      invokeEventListeners(bean.button, "click");
      assertEquals(Arrays.asList(new ObservedEvent("button", "click")), bean.observed);
    } catch (final AssertionError ae) {
      throw new AssertionError("Precondition failed.", ae);
    }

    bean.observed.clear();
    assertTrue(bean.observed.isEmpty());
    IOCUtil.destroy(bean);
    invokeEventListeners(bean.button, "click");
    assertEquals(Collections.emptyList(), bean.observed);
  }

  public void testWithWidget() throws Exception {
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(bean.buttonWidget.getElement(), "click");
    assertEquals(Arrays.asList(new ObservedEvent("buttonWidget", "click")), bean.observed);
  }

  public void testWithGwtUserElement() throws Exception {
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(bean.buttonGwtElement, "click");
    assertEquals(Arrays.asList(new ObservedEvent("buttonGwtElement", "click")), bean.observed);
  }

  public void testDataFieldOnlyInTemplate() throws Exception {
    final Button button = (Button) bean.root.querySelector("button[data-field=\"noFieldButton\"]");
    assertNotNull(button);
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(button, "click");
    assertEquals(Arrays.asList(new ObservedEvent("noFieldButton", "click")), bean.observed);
  }

  public void testPrivateMethodHandler() throws Exception {
    assertTrue(bean.observed.isEmpty());
    invokeEventListeners(bean.privateHandler, "dblclick");
    assertEquals(Arrays.asList(new ObservedEvent("privateHandler", "dblclick")), bean.observed);
  }
}
