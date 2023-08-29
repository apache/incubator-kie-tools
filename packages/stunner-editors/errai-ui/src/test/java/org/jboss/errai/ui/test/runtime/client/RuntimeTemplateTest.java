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


package org.jboss.errai.ui.test.runtime.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateInitializedEvent;
import org.jboss.errai.ui.test.runtime.client.res.RuntimeCustomProviderComponent;
import org.junit.Test;

public class RuntimeTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testServerTemplateProvider() {
    delayTestFinish(90000);

    final RuntimeTemplateTestApp app = IOC.getBeanManager().lookupBean(RuntimeTemplateTestApp.class).getInstance();
    assertNotNull(app.getComponent());

    TemplateInitializedEvent.Handler handler = new TemplateInitializedEvent.Handler() {
      @Override
      public void onInitialized() {
        String innerHtml = app.getComponent().getElement().getInnerHTML();
        assertTrue(RegExp.compile("<h1(.)*>This will be rendered</h1>").test(innerHtml));
        assertTrue(RegExp.compile("<div(.)*>This will be rendered</div>").test(innerHtml));
        assertTrue(innerHtml.contains("This will be rendered inside button"));

        Element lbl = Document.get().getElementById("c1a");
        assertNotNull(lbl);
        assertEquals("Added by component", lbl.getInnerText());

        assertNull(Document.get().getElementById("content"));
        assertNotNull(Document.get().getElementById("c1"));
        assertNotNull(Document.get().getElementById("c1a"));
        assertNotNull(Document.get().getElementById("c1b"));
        assertNotNull(Document.get().getElementById("c2"));
        finishTest();
      }
    };

    app.getComponent().addHandler(handler, TemplateInitializedEvent.TYPE);
  }

  @Test
  public void testCustomTemplateProvider() {
    final RuntimeCustomProviderComponent component = IOC.getBeanManager()
            .lookupBean(RuntimeCustomProviderComponent.class).getInstance();

    TemplateInitializedEvent.Handler handler = new TemplateInitializedEvent.Handler() {
      @Override
      public void onInitialized() {
        String innerHtml = component.getElement().getInnerHTML();
        assertTrue(RegExp.compile("<h1(.)*>This will be rendered</h1>").test(innerHtml));
        assertTrue(RegExp.compile("<div(.)*>This will be rendered</div>").test(innerHtml));
        assertTrue(innerHtml.contains("This will be rendered inside button"));

        Element c1 = Document.get().getElementById("c1");
        assertNotNull(c1);
        assertEquals("Added by component", c1.getInnerText());

        assertNotNull(Document.get().getElementById("c2"));
        assertNotNull(Document.get().getElementById("c3"));
        assertNull(Document.get().getElementById("content"));
        finishTest();
      }
    };

    component.addHandler(handler, TemplateInitializedEvent.TYPE);
  }
}
