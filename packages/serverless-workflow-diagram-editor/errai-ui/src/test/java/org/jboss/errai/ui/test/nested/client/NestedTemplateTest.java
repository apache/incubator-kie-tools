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


package org.jboss.errai.ui.test.nested.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateWidget;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.test.nested.client.res.A;
import org.jboss.errai.ui.test.nested.client.res.NestedClassComponent;
import org.jboss.errai.ui.test.nested.client.res.ParentCompositeComponent;
import org.jboss.errai.ui.test.nested.client.res.ParentNonCompositeComponent;
import org.junit.Test;

public class NestedTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testInsertAndReplaceNested() {
    NestedTemplateTestApp app = IOC.getBeanManager().lookupBean(NestedTemplateTestApp.class).getInstance();
    assertNotNull(app.getComponent());
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
  }

  /**
   * Regression test for the failure case documented in ERRAI-464.
   */
  @Test
  public void testNestedComponentsWhichBothInjectSomethingCalledAddress() {
    A a = IOC.getBeanManager().lookupBean(A.class).getInstance();
    assertNotNull(a);

    assertEquals("This is the address field in A.html", a.getAddress().getValue());
    assertEquals("This is the address field in B.html", a.getB().getAddress().getText());
  }

  /**
   * Regression test for the failure case documented in ERRAI-790.
   */
  @Test
  public void testComponentAsStaticInnerClass() {
    NestedClassComponent.Content c = IOC.getBeanManager().lookupBean(NestedClassComponent.Content.class).getInstance();
    assertNotNull(c);
  }

  @Test
  public void testCompositeParentOfNonComposite() throws Exception {
    final ParentCompositeComponent instance = IOC.getBeanManager().lookupBean(ParentCompositeComponent.class).getInstance();
    final TemplateWidget childRootWidget = TemplateWidgetMapper.get(instance.getChild());

    assertTrue(childRootWidget.getElement().hasParentElement());
    assertTrue(childRootWidget.getElement().getParentElement().equals(instance.getElement()));
  }

  @Test
  public void testNonCompositeParentOfNonComposite() throws Exception {
    final ParentNonCompositeComponent instance = IOC.getBeanManager().lookupBean(ParentNonCompositeComponent.class).getInstance();
    final TemplateWidget parentRootWidget = TemplateWidgetMapper.get(instance);
    final TemplateWidget childRootWidget = TemplateWidgetMapper.get(instance.getChild());

    assertTrue(childRootWidget.getElement().hasParentElement());
    assertTrue(childRootWidget.getElement().getParentElement().equals(parentRootWidget.getElement()));
  }

}
