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

import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.client.widget.LocaleListBox;
import org.jboss.errai.ui.client.widget.LocaleSelector;
import org.jboss.errai.ui.shared.api.Locale;
import org.jboss.errai.ui.test.i18n.client.res.I18nPrefixingTestBean;
import org.junit.Test;

public class I18nTemplateTest extends AbstractErraiCDITest {

  /**
   * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
   */
  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    TranslationService.setCurrentLocale("en");
  }

  /**
   * Tests that the bundle is created and is accessible.
   */
  @Test
  public void testBundleAccessWithCompositeComponent() {
    final I18nTemplateTestApp app = IOC.getBeanManager().lookupBean(CompositeI18nTemplateTestApp.class).getInstance();
    bundleAccessAssertions(app);
    IOC.getBeanManager().destroyBean(app);
  }

  /**
   * Tests that the bundle is created and is accessible.
   */
  @Test
  public void testBundleAccessWithNonCompositeComponent() {
    final I18nTemplateTestApp app = IOC.getBeanManager().lookupBean(NonCompositeI18nTemplateTestApp.class).getInstance();
    bundleAccessAssertions(app);
    IOC.getBeanManager().destroyBean(app);
  }

  private void bundleAccessAssertions(final I18nTemplateTestApp app) {
    assertNotNull(app.getComponent());
    assertEquals("Welcome to the errai-ui i18n demo.", app.getComponent().getWelcome_p().getInnerText());
    assertEquals("Label 1:", app.getComponent().getLabel1().getText());
    assertEquals("value one", app.getComponent().getVal1().getText());
    assertEquals("Label 1.1:", app.getComponent().getNestedLabel().getText());
    assertEquals("value one.one", app.getComponent().getVal1_1().getText());
    assertEquals("Label 2:", app.getComponent().getLabel2().getText());
    assertEquals("value two", app.getComponent().getVal2().getText());
    assertEquals("This is a really really really really really really really really really" +
    		" really really really really really really really really really really really" +
    		" long string that exceeds the cutoff length for adding a hash to its internationalization key.",
    		app.getComponent().getLongTextLabel().getText());

    assertEquals("Email:", app.getComponent().getEmailLabel().getText());
    assertEquals("Enter your email address...", app.getComponent().getEmail().getElement().getAttribute("placeholder"));
    assertEquals("Password:", app.getComponent().getPasswordLabel().getText());
    assertEquals("Your password goes here.", app.getComponent().getPassword().getElement().getAttribute("title"));
  }

  @Test
  public void testShouldCreateLocaleListBoxContainingAllLanguageOptionsWithCompositeTemplate() {
    final I18nTemplateTestApp app = IOC.getBeanManager().lookupBean(CompositeI18nTemplateTestApp.class).getInstance();
    localeListBoxAssertions(app);
    IOC.getBeanManager().destroyBean(app);
  }

  @Test
  public void testShouldCreateLocaleListBoxContainingAllLanguageOptionsWithNonCompositeTemplate() {
    final I18nTemplateTestApp app = IOC.getBeanManager().lookupBean(NonCompositeI18nTemplateTestApp.class).getInstance();
    localeListBoxAssertions(app);
    IOC.getBeanManager().destroyBean(app);
  }

  @Test
  public void testPrefixedKeyUsedWhenAvailableForLocale() throws Exception {
    TranslationService.setCurrentLocale("fr_fr");
    final I18nPrefixingTestBean bean = IOCUtil.getInstance(I18nPrefixingTestBean.class);
    assertEquals("Accueil spécifique!", bean.welcome.getTextContent());
  }

  @Test
  public void testUnprefixedKeyUsedWhenNoPrefixedKeyDefinedForLocale() throws Exception {
    TranslationService.setCurrentLocale("fr_fr");
    final I18nPrefixingTestBean bean = IOCUtil.getInstance(I18nPrefixingTestBean.class);
    assertEquals("Au revoir générique!", bean.farewell.getTextContent());
  }

  @Test
  public void testPrefixedDefaultBundleUsedBeforeUnprefixedDefaultBundle() throws Exception {
    final I18nPrefixingTestBean bean = IOCUtil.getInstance(I18nPrefixingTestBean.class);
    assertEquals("Specific goodbye!", bean.farewell.getTextContent());
  }

  @Test
  public void testUnprefixedDefaultBundleWhenNoPrefixedKeyExists() throws Exception {
    final I18nPrefixingTestBean bean = IOCUtil.getInstance(I18nPrefixingTestBean.class);
    assertEquals("Translation of UI element was wrong.", "Generic welcome!", bean.welcome.getTextContent());
  }

  private void localeListBoxAssertions(final I18nTemplateTestApp app) {
    // given
    final LocaleSelector selector = IOC.getBeanManager().lookupBean(LocaleSelector.class).getInstance();
    final LocaleListBox localeListBox = app.getComponent().getListBox();
    localeListBox.init();

    // when - then
    assertNull(localeListBox.getValue());
    assertEquals(5, selector.getSupportedLocales().size());

    localeListBox.setValue(new Locale("da", "Danish"), true);

    assertEquals("da", TranslationService.currentLocale());
    assertNotNull(localeListBox.getValue());
  }
}
