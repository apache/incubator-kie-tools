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


package org.jboss.errai.ui.test.elemental2.client;

import java.util.Collections;

import elemental2.dom.Element;
import elemental2.dom.MouseEvent;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ui.test.elemental2.client.res.ElementalComponent;

import static java.util.Collections.singletonList;
import static org.jboss.errai.common.client.util.EventTestingUtil.invokeEventListeners;
import static org.jboss.errai.common.client.util.EventTestingUtil.setupAddEventListenerInterceptor;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ElementalQuickEventHandlerTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    setupAddEventListenerInterceptor();
  }

  public void testClickMouseEventOnElementWithField() throws Exception {
    final ElementalComponent comp = IOCUtil.getInstance(ElementalComponent.class);
    assertEquals("Precondition failed: should be no previous observed events.", Collections.emptyList(), comp.observed);

    invokeEventListeners(comp.foo, "click", new MouseEvent("click"));
    assertEquals(singletonList(new ElementalComponent.Observed("click", "foo")), comp.observed);
  }

  public void testDbClickBaseEventOnElementWithField() throws Exception {
    final ElementalComponent comp = IOCUtil.getInstance(ElementalComponent.class);
    assertEquals("Precondition failed: should be no previous observed events.", Collections.emptyList(), comp.observed);

    invokeEventListeners(comp.foo, "dblclick", new MouseEvent("dblclick"));
    assertEquals(singletonList(new ElementalComponent.Observed("dblclick", "foo")), comp.observed);
  }

  public void testClickMouseEventOnElementWithoutField() throws Exception {
    final ElementalComponent comp = IOCUtil.getInstance(ElementalComponent.class);
    assertEquals("Precondition failed: should be no previous observed events.", Collections.emptyList(), comp.observed);

    final Element bar = comp.getElement().querySelector("#bar");
    invokeEventListeners(bar, "click", new MouseEvent("click"));
    assertEquals(singletonList(new ElementalComponent.Observed("click", "bar")), comp.observed);
  }
}
