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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.junit.Test;

public class QuickHandlerTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testInsertAndReplaceWithCompositeTemplate() {
    final QuickHandlerTemplateTestApp app = IOC.getBeanManager().lookupBean(CompositeQuickHandlerTemplateTestApp.class).getInstance();
    runAssertions(app);
    IOC.getBeanManager().destroyBean(app);
  }

  @Test
  public void testInsertAndReplaceWithNonCompositeTemplate() {
    final QuickHandlerTemplateTestApp app = IOC.getBeanManager().lookupBean(NonCompositeQuickHandlerTemplateTestApp.class).getInstance();
    runAssertions(app);
    IOC.getBeanManager().destroyBean(app);
  }

  private void runAssertions(QuickHandlerTemplateTestApp app) {
    assertNotNull("Component was not injected.", app.getComponent());

    assertFalse("This event fired prematurely.", app.getComponent().isThisEventFired());
    DomEvent.fireNativeEvent(generateClickEvent(), app.getComponent());
    assertTrue("This event handler was not called.", app.getComponent().isThisEventFired());

    DivElement c0 = DivElement.as(Document.get().getElementById("c0"));
    assertNotNull("Could not find c0 element in DOM.", c0);
    AnchorElement c1 = app.getComponent().getC1();
    ButtonElement c2 = ButtonElement.as(Document.get().getElementById("c2"));
    ButtonElement c3 = ButtonElement.as(TemplateUtil.asElement(app.getComponent().getC3()));
    ButtonElement c4 = ButtonElement.as(TemplateUtil.asElement(app.getComponent().getC4()));
    AnchorElement c5 = app.getComponent().getC5();
    assertNotNull("Could not find c2 element in DOM.", c2);

    assertFalse("c0 event fired prematurely.", app.getComponent().isC0EventFired());
    c0.dispatchEvent(generateClickEvent());
    assertTrue("c0 event handler was not called.", app.getComponent().isC0EventFired());
    assertFalse("c4 event fired after click event on c0.", app.getComponent().isC4EventFired());

    assertFalse("c1 dup event fired prematurely.", app.getComponent().isC1_dupEventFired());
    assertFalse("c1 event fired prematurely.", app.getComponent().isC1EventFired());
    c1.dispatchEvent(generateClickEvent());
    assertTrue("c1 event handler was not called.", app.getComponent().isC1EventFired());
    assertTrue("c1 dup event handler was not called.", app.getComponent().isC1_dupEventFired());

    assertFalse("c2 event fired prematurely.", app.getComponent().isC2EventFired());
    c2.click();
    assertTrue("c2 event handler was not called.", app.getComponent().isC2EventFired());

    assertFalse("c3 event fired prematurely.", app.getComponent().isC3EventFired());
    c3.dispatchEvent(generateClickEvent());
    assertTrue("c3 event handler was not called.", app.getComponent().isC3EventFired());

    assertFalse("c4 event fired prematurely.", app.getComponent().isC4EventFired());
    c4.dispatchEvent(generateClickEvent());
    assertTrue("c4 event handler was not called.", app.getComponent().isC4EventFired());

    assertFalse("c5 event fired prematurely.", app.getComponent().isC5EventFired());
    c5.dispatchEvent(generateClickEvent());
    assertTrue("c5 event handler was not called.", app.getComponent().isC5EventFired());
  }

  private NativeEvent generateClickEvent() {
    return Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
  }

}
