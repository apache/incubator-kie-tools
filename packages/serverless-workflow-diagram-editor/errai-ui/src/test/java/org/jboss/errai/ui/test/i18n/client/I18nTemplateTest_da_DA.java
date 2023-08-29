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
import org.junit.Test;

public class I18nTemplateTest_da_DA extends AbstractErraiCDITest {

  /**
   * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
   */
  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  /**
   * @see org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest#gwtSetUp()
   */
  @Override
  protected void gwtSetUp() throws Exception {
    TranslationService.setCurrentLocale("da_DA");
    super.gwtSetUp();
  }

  /**
   * Tests that the bundle is created and is accessible.
   */
  @Test
  public void testBundleAccess() {
    I18nTemplateTestApp app = IOC.getBeanManager().lookupBean(org.jboss.errai.ui.test.i18n.client.CompositeI18nTemplateTestApp.class).getInstance();
    assertNotNull(app.getComponent());
    assertEquals("Velkommen til errai-ui i18n demo.", app.getComponent().getWelcome_p().getInnerText());
    assertEquals("label 1:", app.getComponent().getLabel1().getText());
    assertEquals("værdi, en", app.getComponent().getVal1().getText());
    assertEquals("label 2:", app.getComponent().getLabel2().getText());
    assertEquals("værdi to", app.getComponent().getVal2().getText());
    assertEquals("Email:", app.getComponent().getEmailLabel().getText());
    assertEquals("Indtast din e-mailadresse...", app.getComponent().getEmail().getElement().getAttribute("placeholder"));
    assertEquals("adgangskode:", app.getComponent().getPasswordLabel().getText());
    assertEquals("Din adgangskode går her.", app.getComponent().getPassword().getElement().getAttribute("title"));
  }

}
