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


package org.jboss.errai.ui.test.element.client;

import com.google.gwt.dom.client.Element;
import elemental.dom.Document;
import elemental.events.EventTarget;
import elemental.events.MouseEvent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.junit.Test;

import static elemental.client.Browser.getDocument;

public class ElementTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testUseElementInCompositeComponent() {
    final ElementTemplateTestApp app = IOC.getBeanManager().lookupBean(CompositeElementTemplateTestApp.class).getInstance();
    assertContentIsCorrect(app);
  }

  @Test
  public void testUseElementInNonCompositeComponent() {
    final ElementTemplateTestApp app = IOC.getBeanManager().lookupBean(NonCompositeElementTemplateTestApp.class).getInstance();
    assertContentIsCorrect(app);
  }

  @Test
  public void testElementWrapperWidgetIsRemovedOnDestructionInNonCompositeTemplate() throws Exception {
    final ElementTemplateTestApp app = IOC.getBeanManager().lookupBean(NonCompositeElementTemplateTestApp.class).getInstance();
    assertWrapperWidgetIsRemovedOnDestruction(app);
  }

  @Test
  public void testElementWrapperWidgetIsRemovedOnDestructionInCompositeTemplate() throws Exception {
    final ElementTemplateTestApp app = IOC.getBeanManager().lookupBean(CompositeElementTemplateTestApp.class).getInstance();
    assertWrapperWidgetIsRemovedOnDestruction(app);
  }

  private void assertWrapperWidgetIsRemovedOnDestruction(final ElementTemplateTestApp app) {
    final Element formElement = app.getForm().getForm();
    final ElementWrapperWidget<?> wrapper = ElementWrapperWidget.getWidget(formElement);
    assertSame("Control failed: This method should return the same instance until it is removed.", wrapper, ElementWrapperWidget.getWidget(formElement));
    IOC.getBeanManager().destroyBean(app);
    assertNotSame("Wrapper widget was not removed.", wrapper, ElementWrapperWidget.getWidget(formElement));
  }

  private void assertContentIsCorrect(final ElementTemplateTestApp app) {
    final Element form = app.getForm().getElement();
    assertTrue("Form component element is missing text.",
            form.getInnerHTML().contains("Keep me logged in on this computer"));
    assertTrue("[form] element in component is missing text.",
            app.getForm().getForm().getInnerHTML().contains("Keep me logged in on this computer"));
    assertEquals("Cancel button is missing text.", "Cancel", app.getForm().getCancel().getTextContent());

    assertEquals("Username field is missing placeholder attribute.", "Username",
            TemplateUtil.asElement(app.getForm().getUsername()).getAttribute("placeholder"));
    // This assertion on it's own could fail if there is a problem with how we use JsInterop
    assertEquals("Username field is missing placeholder attribute.", "Username",
            app.getForm().getUsername().getAttribute("placeholder"));

    assertEquals("Button pressed incorrect number of times.", 0, app.getForm().getNumberOfTimesPressed());
    click(app.getForm().getCancel());
    assertEquals("Button pressed incorrect number of times.", 1, app.getForm().getNumberOfTimesPressed());

    assertEquals("Did not copy inner text from template for element presenter div", "Inner presenter text",
            app.getForm().getElementPresenter().getElement().getInnerHTML());
  }

  /**
   * Fires a left-click event on the given target (typically a DOM node).
   */
  public static void click(final EventTarget target) {
    final MouseEvent evt = (MouseEvent) getDocument().createEvent(
        Document.Events.MOUSE);
    evt.initMouseEvent("click", true, true, null, 0, 0, 0, 0, 0, false, false,
        false, false, MouseEvent.Button.PRIMARY, null);
    target.dispatchEvent(evt);
  }

}
