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


package org.jboss.errai.ui.test.basic.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidget;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.test.basic.client.res.BasicComponent;
import org.jboss.errai.ui.test.basic.client.res.BasicComponentUsingDataFields;
import org.jboss.errai.ui.test.basic.client.res.LessStyledComponent;
import org.jboss.errai.ui.test.basic.client.res.LessStyledComponentAbsolute;
import org.jboss.errai.ui.test.basic.client.res.LessStyledComponentRelative;
import org.jboss.errai.ui.test.basic.client.res.LessStyledComponentWithImport;
import org.jboss.errai.ui.test.basic.client.res.NonCompositeComponent;
import org.jboss.errai.ui.test.basic.client.res.StyledComponent;
import org.jboss.errai.ui.test.basic.client.res.StyledComponentWithAbsoluteSheetPath;
import org.jboss.errai.ui.test.basic.client.res.StyledComponentWithRelativeSheetPath;
import org.jboss.errai.ui.test.basic.client.res.StyledTemplatedBean;
import org.junit.Test;

import static org.jboss.errai.ui.shared.TemplateUtil.asElement;

public class BasicTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    /*
     * This acts as a reset for the LESS stylesheet tests. Without this, the tests are not independent.
     */
    StyleInjector.inject(".styled {\n color: black;\n background-color: black;\n}");
  }

  @Test
  public void testInsertAndReplace() {
    BasicTemplateTestApp app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingDataFields.class).getInstance();
    testInsertAndReplace(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingIds.class).getInstance();
    testInsertAndReplace(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingStyleClasses.class).getInstance();
    testInsertAndReplace(app);
  }

  private void testInsertAndReplace(final BasicTemplateTestApp app) {
    assertNotNull(app.getComponent());
    final String innerHtml = app.getComponent().getElement().getInnerHTML();
    assertTrue(RegExp.compile("<h1(.)*>This will be rendered</h1>").test(innerHtml));
    assertTrue(RegExp.compile("<div(.)*>This will be rendered</div>").test(innerHtml));

    final RegExp buttonInnerHtmlRegExp = RegExp.compile("This will be rendered inside button", "g");
    assertTrue("Did find single instance of button text.", buttonInnerHtmlRegExp.test(innerHtml));
    assertTrue("Did find second instance of button text.", buttonInnerHtmlRegExp.test(innerHtml));

    final Element c1 = Document.get().getElementById("c1");
    assertNotNull(c1);
    assertEquals("Added by component", c1.getInnerText());

    assertNotNull(Document.get().getElementById("c2"));
    assertNotNull(Document.get().getElementById("c3"));
    assertNull(Document.get().getElementById("content"));

    final Element nc1 = Document.get().getElementById("nc1");
    assertNotNull(nc1);
    assertEquals("Added by component", nc1.getInnerText());

    assertNotNull(Document.get().getElementById("nc2"));
    assertNotNull(Document.get().getElementById("nc3"));
    assertNull(Document.get().getElementById("nativeContent"));
  }

  @Test
  public void testAttributesFromTemplateOverrideComponentElement() {
    BasicTemplateTestApp app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingDataFields.class).getInstance();
    testAttributesFromTemplateOverrideComponentElement(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingIds.class).getInstance();
    testAttributesFromTemplateOverrideComponentElement(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingStyleClasses.class).getInstance();
    testAttributesFromTemplateOverrideComponentElement(app);
  }

  private void testAttributesFromTemplateOverrideComponentElement(final BasicTemplateTestApp app) {
    final Element c1 = app.getComponent().getLabel().getElement();
    assertTrue("Element did not contain the class name [c1]. Observed class value: " + c1.getAttribute("class"), c1.hasClassName("c1"));
    assertEquals("left", c1.getAttribute("align"));

    final Element c3 = app.getComponent().getTextBox().getElement();
    assertEquals("address", c3.getAttribute("name"));

    final Element nc1 = asElement(app.getComponent().getNativeLabel());
    assertEquals("nc1", nc1.getAttribute("class"));
    assertEquals("left", nc1.getAttribute("align"));

    final Element nc3 = TemplateUtil.asElement(app.getComponent().getNativeTextBox());
    assertEquals("address", nc3.getAttribute("name"));
  }

  @Test
  public void testHasHTMLPreservesInnerHTML() throws Exception {
    BasicTemplateTestApp app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingDataFields.class).getInstance();
    testHasHTMLPreservesInnerHTML(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingIds.class).getInstance();
    testHasHTMLPreservesInnerHTML(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingStyleClasses.class).getInstance();
    testHasHTMLPreservesInnerHTML(app);
  }

  private void testHasHTMLPreservesInnerHTML(final BasicTemplateTestApp app) throws Exception {
    final Anchor c4comp = app.getComponent().getC4();
    final String c4compHtml = c4comp.getHTML();
    assertTrue("Inner HTML should be preserved when component implements ",
        RegExp.compile("<span(.)*>LinkHTML</span>").test(c4compHtml));

    final Element c4 = c4comp.getElement();
    assertEquals("blah", c4.getAttribute("href"));
    assertEquals("SPAN", c4.getFirstChildElement().getTagName());
    assertEquals("LinkHTML", c4.getFirstChildElement().getInnerHTML());

    final Element nc4 = TemplateUtil.asElement(app.getComponent().getNc4());
    final String nc4Html = nc4.getInnerHTML();
    assertTrue("Inner HTML should be preserved when component implements ",
        RegExp.compile("<span(.)*>LinkHTML</span>").test(nc4Html));

    assertEquals("blah", nc4.getAttribute("href"));
    assertEquals("SPAN", nc4.getFirstChildElement().getTagName());
    assertEquals("LinkHTML", nc4.getFirstChildElement().getInnerHTML());
  }

  @Test
  public void testHasHTMLReparentsChildElements() throws Exception {
    BasicTemplateTestApp app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingDataFields.class).getInstance();
    testHasHTMLReparentsChildElements(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingIds.class).getInstance();
    testHasHTMLReparentsChildElements(app);
    app = IOC.getBeanManager().lookupBean(BasicTemplateTestAppUsingStyleClasses.class).getInstance();
    testHasHTMLReparentsChildElements(app);
  }

  private void testHasHTMLReparentsChildElements(final BasicTemplateTestApp app) throws Exception {
    final Anchor c5 = app.getComponent().getC5();
    final Image c6 = app.getComponent().getC6();

    System.out.println("DUMPING: " + Document.get().getElementById("root").getInnerHTML());
    assertEquals(c6.getElement(), c5.getElement().getFirstChildElement());

    final org.jboss.errai.ui.test.common.client.dom.Element nc5 = app.getComponent().getNc5();
    final org.jboss.errai.ui.test.common.client.dom.Element nc6 = app.getComponent().getNc6();

    System.out.println("DUMPING: " + Document.get().getElementById("root").getInnerHTML());
    assertEquals(TemplateUtil.asElement(nc6), TemplateUtil.asElement(nc5).getFirstChildElement());
  }

  @Test
  public void testPrecedenceRules() throws Exception {
    final PrecedenceTemplateTestApp app = IOC.getBeanManager().lookupBean(PrecedenceTemplateTestApp.class).getInstance();

    assertEquals(app.getComponent().getA().getText(), "This is a");
    assertEquals(app.getComponent().getB().getText(), "This is b");
    assertEquals(app.getComponent().getC().getText(), "This is c");
    assertEquals(app.getComponent().getE().getText(), "This is d, e, and f");
  }

  @Test
  public void testLoadingNonCompositeTemplatedBean() throws Exception {
    final NonCompositeComponent instance = IOC.getBeanManager().lookupBean(NonCompositeComponent.class).getInstance();
    final TemplateWidget rootWidget = TemplateWidgetMapper.get(instance);

    assertEquals("The root @DataField was not used.", rootWidget.getElement(), instance.getRoot());

    assertTrue("The text @DataField was not used.", instance.getTextBox().getElement().hasParentElement());
    assertTrue("The button @DataField was not used.", instance.getButton().getElement().hasParentElement());

    assertTrue("The text @DataField is not a child of the root element.",
            instance.getTextBox().getElement().getParentElement().equals(rootWidget.getElement()));
    assertTrue("The button @DataField is not a child of the root element.",
            instance.getButton().getElement().getParentElement().equals(rootWidget.getElement()));
  }

  @Test
  public void testNonCompositeTemplateCleanup() throws Exception {
    final NonCompositeComponent instance = IOC.getBeanManager().lookupBean(NonCompositeComponent.class).getInstance();

    assertTrue("Non-composite templated bean should have a TemplateWidget mapping after initialization.", TemplateWidgetMapper.containsKey(instance));
    final TemplateWidget templateWidget = TemplateWidgetMapper.get(instance);
    assertTrue("TemplateWidget should be attached after initialization.", templateWidget.isAttached());
    assertTrue("TemplateWidget should be in detach list after initialization.", RootPanel.isInDetachList(templateWidget));

    IOC.getBeanManager().destroyBean(instance);

    assertFalse("Non-composite templated bean should not have a TemplateWidget mapping after destruction.", TemplateWidgetMapper.containsKey(instance));
    assertFalse("TemplateWidget should not be attached after destruction.", templateWidget.isAttached());
    assertFalse("TemplateWidget should not be in detach list after destruction.", RootPanel.isInDetachList(templateWidget));
  }

  @Test
  public void testCompositeTemplateCleanup() throws Exception {
    final BasicComponent instance = IOC.getBeanManager().lookupBean(BasicComponentUsingDataFields.class).getInstance();

    assertFalse("Composite templated beans should not have TemplateWidget mappings after initialization.", TemplateWidgetMapper.containsKey(instance));
    assertTrue("Composite templated bean should be attached after initialization.", instance.isAttached());
    assertTrue("Composite templated bean should be in the detach list after initialization.", RootPanel.isInDetachList(instance));

    IOC.getBeanManager().destroyBean(instance);

    assertFalse("Composite templated beans should not have TemplateWidget mappings after destructions.", TemplateWidgetMapper.containsKey(instance));
    assertFalse("Composite templated bean should not be attached after destruction.", instance.isAttached());
    assertFalse("Composite templated bean should not be in the detach list after destruction.", RootPanel.isInDetachList(instance));
  }

  @Test
  public void testStyleSheetWithDefaultPath() throws Exception {
    final StyledTemplatedBean bean = IOC.getBeanManager().lookupBean(StyledComponent.class).getInstance();
    styledBeanAssertions(bean, "font-size", "100px");
  }

  @Test
  public void testStyleSheetWithRelativePath() throws Exception {
    final StyledTemplatedBean bean = IOC.getBeanManager().lookupBean(StyledComponentWithRelativeSheetPath.class).getInstance();
    styledBeanAssertions(bean, "font-color", "red");
  }

  @Test
  public void testStyleSheetWithAbsolutePath() throws Exception {
    final StyledTemplatedBean bean = IOC.getBeanManager().lookupBean(StyledComponentWithAbsoluteSheetPath.class).getInstance();
    styledBeanAssertions(bean, "margin", "10px");
  }

  @Test
  public void testLessStyleSheetWithDefaultPath() throws Exception {
    try {
      final LessStyledComponent bean = IOCUtil.getInstance(LessStyledComponent.class);
      StyleInjector.flush();
      assertTrue("Element does not have correct CSS class.", bean.styled.getClassList().contains("styled"));
      assertEquals("rgb(255,0,0)", getPropertyValue(bean.styled, "color").replaceAll("\\s+", ""));
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  @Test
  public void testLessStyleSheetWithRelativePath() throws Exception {
    try {
      final LessStyledComponentRelative bean = IOCUtil.getInstance(LessStyledComponentRelative.class);
      StyleInjector.flush();
      assertTrue("Element does not have correct CSS class.", bean.styled.getClassList().contains("styled"));
      assertEquals("rgb(255,0,0)", getPropertyValue(bean.styled, "color").replaceAll("\\s+", ""));
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  @Test
  public void testLessStyleSheetWithAbsolutePath() throws Exception {
    try {
      final LessStyledComponentAbsolute bean = IOCUtil.getInstance(LessStyledComponentAbsolute.class);
      StyleInjector.flush();
      assertTrue("Element does not have correct CSS class.", bean.styled.getClassList().contains("styled"));
      assertEquals("rgb(255,0,0)", getPropertyValue(bean.styled, "color").replaceAll("\\s+", ""));
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  @Test
  public void testLessStyleSheetWithImportedSheet() throws Exception {
    try {
      final LessStyledComponentWithImport bean = IOCUtil.getInstance(LessStyledComponentWithImport.class);
      StyleInjector.flush();
      assertTrue("Element does not have correct CSS class.", bean.styled.getClassList().contains("styled"));
      assertEquals("rgb(255,0,0)", getPropertyValue(bean.styled, "color").replaceAll("\\s+", ""));
      assertEquals("rgb(0,0,255)", getPropertyValue(bean.styled, "background-color").replaceAll("\\s+", ""));
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError(t);
    }
  }

  private void styledBeanAssertions(final StyledTemplatedBean bean, final String propertyName, final String propertyValue) {
    // Need to flush so that styles are computed immediately.
    StyleInjector.flush();
    assertTrue("The span element did not receive the class attribute from the template.", bean.getStyled().hasClassName("styled"));
    assertEquals("Style from StyledComponent.css was not applied.", propertyValue, getPropertyValue(bean.getStyled(), propertyName));
  }

  private static native String getPropertyValue(Object elem, String prop) /*-{
    return $wnd.getComputedStyle(elem,null).getPropertyValue(prop);
  }-*/;

}
