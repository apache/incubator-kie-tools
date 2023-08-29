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


package org.jboss.errai.ui.test.extended.client;

import com.google.gwt.dom.client.Document;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.test.extended.client.res.ChildWithoutTemplatedFragmentValue;
import org.jboss.errai.ui.test.extended.client.res.CompositeChildWithoutTemplatedFragmentValue;

public class ExtendedTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  public void testInsertAndReplaceNestedWithCompositeTemplate() {
    final ElementTemplateTestApp app = IOC.getBeanManager().lookupBean(CompositeExtendedTemplateTestApp.class).getInstance();
    runAssertions(app);
  }

  public void testInsertAndReplaceNestedNonCompositeTemplate() {
    final ElementTemplateTestApp app = IOC.getBeanManager().lookupBean(NonCompositeExtendedTemplateTestApp.class).getInstance();
    runAssertions(app);
  }

  public void testParentWithFragmentValueChildWithout() throws Exception {
    try {
      final ChildWithoutTemplatedFragmentValue bean = IOCUtil.getInstance(ChildWithoutTemplatedFragmentValue.class);
      assertEquals("Child has the wrong root element.", "root", bean.getElement().getId());
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  public void testCompositeParentWithFragmentValueChildWithout() throws Exception {
    try {
      final CompositeChildWithoutTemplatedFragmentValue bean = IOCUtil.getInstance(CompositeChildWithoutTemplatedFragmentValue.class);
      assertEquals("Child has the wrong root element.", "root", bean.getElement().getId());
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  private void runAssertions(final ElementTemplateTestApp app) {
    assertNotNull(app.getExtComponent());

    System.out.println("DUMPING: " + Document.get().getBody().getInnerHTML());

    assertNotNull(Document.get().getElementById("root"));
    assertNotNull(Document.get().getElementById("c1"));
    assertEquals("This will be rendered inside anchor", Document.get().getElementById("c1").getInnerText());
    assertNotNull(Document.get().getElementById("c2"));
    assertEquals("This will be rendered inside label c2", Document.get().getElementById("c2").getInnerText());
    assertEquals("DIV", Document.get().getElementById("c2").getTagName());
    assertNotNull(Document.get().getElementById("c3"));
    assertEquals("This will be rendered inside label c3", Document.get().getElementById("c3").getInnerText());

    assertNotNull("Field in base template should be initialized", app.getExtComponent().getC2Base());
    assertFalse("Field in base template should not be attached", app.getExtComponent().getC2Base().isAttached());

    assertNotNull("Field in extension template should be initialized", app.getExtComponent().getC2());
    assertTrue("Field in extension template should be attached", app.getExtComponent().getC2().isAttached());

    assertFalse(app.getExtComponent().getElement().getInnerHTML().contains("This will not be rendered"));

    assertNotNull(app.getSecondExtComponent());
    assertNotNull(app.getSecondExtComponent().getC2());
    assertNotNull(app.getSecondExtComponent().getContent3());
  }

}
