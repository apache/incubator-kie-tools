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


package org.jboss.errai.ui.test.path.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.junit.Test;

public class PathTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testRelativePathTemplate() {
    PathTemplateTestApp app = IOC.getBeanManager().lookupBean(PathTemplateTestApp.class).getInstance();
    assertNotNull(app.getRelativeComponent());
    String innerHtml = app.getRelativeComponent().getElement().getInnerHTML();
    assertTrue(RegExp.compile("<h1(.)*>This will be rendered</h1>").test(innerHtml));
    assertTrue(RegExp.compile("<div(.)*>This will be rendered</div>").test(innerHtml));
    assertTrue(innerHtml.contains("This will be rendered inside button"));

    Element c1 = Document.get().getElementById("c1");
    assertNotNull(c1);
    assertEquals("Added by component", c1.getInnerText());

    assertNotNull(Document.get().getElementById("c2"));
    assertNotNull(Document.get().getElementById("c3"));
    assertNull(Document.get().getElementById("content"));
  }

  @Test
  public void testRelativeParentPathTemplate() {
    PathTemplateTestApp app = IOC.getBeanManager().lookupBean(PathTemplateTestApp.class).getInstance();
    assertNotNull(app.getRelativeParentComponent());
    String innerHtml = app.getRelativeParentComponent().getElement().getInnerHTML();
    assertTrue(RegExp.compile("<h1(.)*>This will be rendered</h1>").test(innerHtml));
    assertTrue(RegExp.compile("<div(.)*>This will be rendered</div>").test(innerHtml));
    assertTrue(innerHtml.contains("This will be rendered inside button"));
    
    Element c1 = Document.get().getElementById("c1");
    assertNotNull(c1);
    assertEquals("Added by component", c1.getInnerText());

    assertNotNull(Document.get().getElementById("c2"));
    assertNotNull(Document.get().getElementById("c3"));
    assertNull(Document.get().getElementById("content"));
  }

  @Test
  public void testAbsolutePathTemplate() {
    PathTemplateTestApp app = IOC.getBeanManager().lookupBean(PathTemplateTestApp.class).getInstance();
    assertNotNull(app.getAbsoluteComponent());
    String innerHtml = app.getAbsoluteComponent().getElement().getInnerHTML();
    assertTrue(RegExp.compile("<h1(.)*>This will be rendered</h1>").test(innerHtml));
    assertTrue(RegExp.compile("<div(.)*>This will be rendered</div>").test(innerHtml));
    assertTrue(innerHtml.contains("This will be rendered inside button"));

    Element c1 = Document.get().getElementById("c1");
    assertNotNull(c1);
    assertEquals("Added by component", c1.getInnerText());

    assertNotNull(Document.get().getElementById("c2"));
    assertNotNull(Document.get().getElementById("c3"));
    assertNull(Document.get().getElementById("content"));
  }

}
