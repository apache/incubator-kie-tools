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

import elemental2.dom.EventTarget;
import elemental2.dom.MouseEvent;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.junit.Test;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
public class ElementTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testUseElementInCompositeComponent() {
    final ElementTemplateTestApp app = IOC.getBeanManager()
            .lookupBean(CompositeElementTemplateTestApp.class)
            .getInstance();
    assertContentIsCorrect(app);
  }

  @Test
  public void testUseElementInNonCompositeComponent() {
    final ElementTemplateTestApp app = IOC.getBeanManager()
            .lookupBean(NonCompositeElementTemplateTestApp.class)
            .getInstance();
    assertContentIsCorrect(app);
  }

  private void assertContentIsCorrect(final ElementTemplateTestApp app) {
    assertTrue("[form] element in component is missing text.",
            app.getForm().getForm().innerHTML.contains("Keep me logged in on this computer"));
    assertEquals("Cancel button is missing text.", "Cancel", app.getForm().getCancel().textContent);

    assertEquals("Username field is missing placeholder attribute.", "Username",
            TemplateUtil.asElement(app.getForm().getUsername()).getAttribute("placeholder"));
    // This assertion on it's own could fail if there is a problem with how we use JsInterop
    assertEquals("Username field is missing placeholder attribute.", "Username",
            app.getForm().getUsername().getAttribute("placeholder"));

    assertEquals("Button pressed incorrect number of times.", 0, app.getForm().getNumberOfTimesPressed());
    click(app.getForm().getCancel());
    assertEquals("Button pressed incorrect number of times.", 1, app.getForm().getNumberOfTimesPressed());

    assertEquals("Did not copy inner text from template for element presenter div", "Inner presenter text",
            app.getForm().getElementPresenter().getElement().innerHTML);
  }

  // Fires a left-click event on the given target (typically a DOM node).
  public static void click(final EventTarget target) {
    final MouseEvent mouseEvent = new MouseEvent("click");
    mouseEvent.initEvent("click", true, true);
    target.dispatchEvent(mouseEvent);
  }

}
