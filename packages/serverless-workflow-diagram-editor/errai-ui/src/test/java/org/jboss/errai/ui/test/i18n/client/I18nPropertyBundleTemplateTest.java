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
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.test.i18n.client.res.AppMessages;
import org.jboss.errai.ui.test.i18n.client.res.PropertyBundleTemplate;
import org.junit.Test;

public class I18nPropertyBundleTemplateTest extends AbstractErraiCDITest {

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
    TranslationService.setCurrentLocaleWithoutUpdate("en");
  }

  @Test
  public void testTemplateTranslationWithPropertyBundle() {
    final PropertyBundleTemplate template = IOC.getBeanManager().lookupBean(PropertyBundleTemplate.class).getInstance();
    assertEquals("Hello", template.hello());

    TranslationService.setCurrentLocale("de");
    assertEquals("Hallo", template.hello());
  }

  @Test
  public void testTranslationKeyWithPropertyBundle() {
    final TranslationService translationService = IOC.getBeanManager().lookupBean(TranslationService.class).getInstance();
    assertEquals("Testing is fun!", translationService.format(AppMessages.MESSAGE));

    TranslationService.setCurrentLocale("de");
    assertEquals("Testen macht Spass!", translationService.format(AppMessages.MESSAGE));
    assertNull(translationService.getTranslation(AppMessages.ENGLISH_MESSAGE));
  }

  @Test
  public void testTranslationKeyWithPropertyBundleSearchingDefaultKey() {
    final TranslationService translationService = IOC.getBeanManager().lookupBean(TranslationService.class).getInstance();
    TranslationService.setCurrentLocale("de");
    TranslationService.setShouldSearchKeyOnDefaultLocale(true);

    assertEquals("English message!", translationService.format(AppMessages.ENGLISH_MESSAGE));
  }

}