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


package org.jboss.errai.common.it.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.Body;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;

/**
 * Tests utility methods in {@link DOMUtil} that deal with {@link Widget Widgets}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DOMUtilWidgetTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.common.it.CommonTests";
  }

  public void testAppendWidgetToElementPreservesWidgetEvents() throws Exception {
    final Button button = new Button();
    final Body body = Window.getDocument().getBody();
    final List<ClickEvent> clicks = new ArrayList<>();
    button.addClickHandler(evt -> clicks.add(evt));

    // Control for clicking working without any parent
    button.click();
    assertEquals(0, clicks.size());

    // Control for clicking working with an element parent and no special configuration
    body.appendChild((HTMLElement) button.getElement());
    button.click();
    assertEquals(0, clicks.size());

    DOMUtil.appendWidgetToElement(body, button);

    button.click();
    assertEquals(1, clicks.size());
  }

  public void testRemoveFromParentBreaksWidgetEventsAndDOMHierarchy() throws Exception {
    final Button button = new Button();
    final Body body = Window.getDocument().getBody();
    final List<ClickEvent> clicks = new ArrayList<>();
    button.addClickHandler(evt -> clicks.add(evt));

    // Precondition
    DOMUtil.appendWidgetToElement(body, button);
    button.click();
    assertEquals(1, clicks.size());

    DOMUtil.removeFromParent(button);
    assertFalse("Button should not still be attached.", button.isAttached());
    assertFalse("Button should not still have element parent.", button.getElement().hasParentElement());
    button.click();
    assertEquals("Button clicks should not work after being removed.", 1, clicks.size());
  }
}
