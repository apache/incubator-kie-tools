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


package org.jboss.errai.ui.test.i18n.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.test.i18n.client.res.AppScopedWidget;
import org.jboss.errai.ui.test.i18n.client.res.ComplexTemplatedChild;
import org.jboss.errai.ui.test.i18n.client.res.ComplexTemplatedParent;
import org.jboss.errai.ui.test.i18n.client.res.I18nAppScopeTestApp;
import org.jboss.errai.ui.test.i18n.client.res.I18nDepInDepScopeTestApp;
import org.jboss.errai.ui.test.i18n.client.res.I18nDepScopeTestApp;
import org.jboss.errai.ui.test.i18n.client.res.TemplatedParent;
import org.junit.Test;

/**
 * Test that templated beans of different scopes are re-translated when the locale is manually
 * changed (ERRAI-610). Templated widgets attached to the DOM and detached application scoped Templated widgets
 * should be translated.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class I18nRetranslationTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ui.test.i18n.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    TranslationService.setCurrentLocaleWithoutUpdate("en_us");
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    TranslationService.setCurrentLocaleWithoutUpdate("en_us");
  }

  /**
   * Test locale translation with a dependent scoped UI element in an AppScoped container.
   */
  @Test
  public void testDepScopeInAppScope() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    I18nAppScopeTestApp app1 = IOC.getBeanManager().lookupBean(I18nAppScopeTestApp.class).getInstance();

    assertEquals("Failed to load default text", "hello", app1.getWidget().getInlineLabelText());
    assertTrue("Widget must be attached to DOM", app1.getWidget().isAttached());

    TranslationService.setCurrentLocale("fr_fr");

    assertEquals("Failed to translate application scoped widget", "bonjour", app1.getWidget().getInlineLabelText());
  }

  /**
   * Test locale translation with application scoped UI element within Dependent container.
   */
  @Test
  public void testAppScopeInDepScope() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    I18nDepScopeTestApp app1 = IOC.getBeanManager().lookupBean(I18nDepScopeTestApp.class).getInstance();

    assertEquals("Failed to load default text", "hello", app1.getWidget().getInlineLabelText());
    assertTrue("Widget must be attached to DOM", app1.getWidget().isAttached());

    TranslationService.setCurrentLocale("fr_fr");

    assertEquals("Failed to translate application scoped widget", "bonjour", app1.getWidget().getInlineLabelText());
  }

  /**
   * Test that dependent scoped beans will be translated after manual locale change.
   */
  @Test
  public void testDepScopeTest() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    I18nDepInDepScopeTestApp app1 = IOC.getBeanManager().lookupBean(I18nDepInDepScopeTestApp.class).getInstance();

    assertEquals("Failed to load default text", "hello", app1.getWidget().getInlineLabelText());
    assertTrue("Widget must be attached to DOM", app1.getWidget().isAttached());

    TranslationService.setCurrentLocale("fr_fr");

    assertEquals("Failed to translate depdendent scoped widget", "bonjour", app1.getWidget().getInlineLabelText());
  }

  /**
   * Test that newly created Dependent scoped beans will be translated after manual locale change.
   */
  @Test
  public void testDepScopeTestReplacement() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    I18nDepInDepScopeTestApp app1 = IOC.getBeanManager().lookupBean(I18nDepInDepScopeTestApp.class).getInstance();

    assertEquals("Failed to load default text", "hello", app1.getWidget().getInlineLabelText());

    TranslationService.setCurrentLocale("fr_fr");

    I18nDepInDepScopeTestApp app2 = IOC.getBeanManager().lookupBean(I18nDepInDepScopeTestApp.class).getInstance();

    assertEquals("Failed to translate depdendent scoped widget", "bonjour", app2.getWidget().getInlineLabelText());
  }

  /*
   * This tests for in-place translation of unattached dependent-scoped beans in case we wish to
   * support this feature one day.
   */
  // @Test
  // public void testDepScopeBeanNotInDom() throws Exception {
  // assertEquals("en_us", TranslationService.currentLocale());
  //
  // DepScopedWidget depWidget =
  // IOC.getBeanManager().lookupBean(DepScopedWidget.class).getInstance();
  //
  // assertTrue("This widget should not be attached to the DOM!", !depWidget.isAttached());
  //
  // TranslationService.setCurrentLocale("fr_fr");
  //
  // RootPanel.get().add(depWidget);
  //
  // assertEquals("Failed to translate dependent unattached widget", "bonjour",
  // depWidget.getInlineLabelText());
  // }

  @Test
  public void testAppScopeBeanNotInDom() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    AppScopedWidget appWidget = IOC.getBeanManager().lookupBean(AppScopedWidget.class).getInstance();

    /*
     * Have to do both these because there is no method that both physically
     * detaches and makes isAttached == false.
     */
    appWidget.removeFromParent();
    appWidget.getElement().removeFromParent();

    assertFalse("This widget should not be attached", appWidget.isAttached());
    assertFalse("This widget's element should not be attached to the DOM!", appWidget.getElement().hasParentElement());

    TranslationService.setCurrentLocale("fr_fr");

    RootPanel.get().add(appWidget);

    assertEquals("Failed to translate dependent unattached widget", "bonjour", appWidget.getInlineLabelText());
  }

  /**
   * Make sure that re-translation does not clobber overridden parts of template.
   */
  @Test
  public void testTemplatedInTemplated() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    TemplatedParent parent = IOC.getBeanManager().lookupBean(TemplatedParent.class).getInstance();

    RootPanel.get().add(parent);

    TranslationService.setCurrentLocale("fr_fr");

    // Check values through DOM
    Element element = parent.getElement();
    Element firstChild = element.getFirstChildElement();
    assertEquals("Parent template leaf element was not properly translated", "bonjour", firstChild.getInnerText());
    assertEquals("Non-keyed child template was not translated", "bonjour", firstChild.getNextSiblingElement().getInnerText());
    assertEquals("Keyed child template was not translated", "bonjour", firstChild
            .getNextSiblingElement().getNextSiblingElement().getInnerText());

    // Check values through widgets
    assertEquals("Parent template leaf element was not properly translated", "bonjour", parent.greeting.getInnerText());
    assertEquals("Non-keyed child template was not translated", "bonjour", parent.templatedChildNoI18nKey.getText());
    assertEquals("Keyed child template was not translated", "bonjour", parent.templatedChildWithI18nKey.getText());
  }

  /**
   * Test that a templated data-field (with more than a singleton-node tree) is retranslated
   * properly.
   */
  @Test
  public void testComplexTemplatedInTemplated() throws Exception {
    assertEquals("en_us", TranslationService.currentLocale());

    ComplexTemplatedParent parent = IOC.getBeanManager().lookupBean(ComplexTemplatedParent.class).getInstance();
    RootPanel.get().add(parent);

    TranslationService.setCurrentLocale("fr_fr");

    assertEquals("Greeting was not retranslated", "bonjour", parent.greeting.getInnerText());
    checkComplexTemplatedChild(parent.templatedChildNoI18nKey);
    checkComplexTemplatedChild(parent.templatedChildWithI18nKey);
  }

  private void checkComplexTemplatedChild(ComplexTemplatedChild child) {
    String[] expected = new String[] { "bonjour", "rouge", "anglais", "de rien" };
    String[] res = new String[expected.length];
    Element element = child.getElement();

    res[0] = element.getFirstChildElement().getInnerText();
    res[1] = element.getFirstChildElement().getNextSiblingElement().getInnerText().trim();
    res[2] = element.getFirstChildElement().getNextSiblingElement().getNextSiblingElement().getFirstChildElement()
            .getInnerText();
    res[3] = element.getFirstChildElement().getNextSiblingElement().getNextSiblingElement().getFirstChildElement()
            .getNextSiblingElement().getInnerText();

    for (int i = 0; i < expected.length; i++) {
      assertEquals("Value " + i + " was improperly translated", expected[i], res[i]);
    }
  }

}
