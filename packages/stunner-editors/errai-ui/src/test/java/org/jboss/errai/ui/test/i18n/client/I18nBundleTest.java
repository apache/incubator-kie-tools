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

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.rebind.TranslationServiceGenerator;
import org.jboss.errai.ui.test.i18n.client.res.CompositeI18nComponent;
import org.jboss.errai.ui.test.i18n.client.res.I18nNestedComponent;
import org.jboss.errai.ui.test.i18n.client.res.I18nNotRootTemplatedWidget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the generated i18n json bundles.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class I18nBundleTest {

  @Test
  public void testAllBundleFileContainsAllKeys() throws Exception {

    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(CompositeI18nComponent.class));
    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(I18nNestedComponent.class));
    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(CompositeI18nTemplateTestApp.class));
    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(TranslationService.class));

    new TranslationServiceGenerator().generate(null, null);

    ObjectMapper mapper = new ObjectMapper();
    @SuppressWarnings("unchecked")
    Map<String, String> translations =
      mapper.readValue(new File(".errai", "errai-bundle-all.json").getAbsoluteFile(), Map.class);

    assertEquals("Email:", translations.get("I18nComponent.Email_"));
    assertEquals("Label 1.1:", translations.get("I18nComponent.Label_1.1_"));
    assertEquals("Label 1:", translations.get("I18nComponent.Label_1_"));
    assertEquals("Label 2:", translations.get("I18nComponent.Label_2_"));
    assertEquals("Password:", translations.get("I18nComponent.Password_"));
    assertEquals("Enter your email address...", translations.get("I18nComponent.email-placeholder"));
    assertEquals("Your password goes here.", translations.get("I18nComponent.password-title"));
    assertEquals("value one", translations.get("I18nComponent.value_one"));
    assertEquals("value one.one", translations.get("I18nComponent.value_one.one"));
    assertEquals("value two", translations.get("I18nComponent.value_two"));
    assertEquals("Welcome to the errai-ui i18n demo.", translations.get("I18nComponent.welcome"));
    assertEquals("Text in a non-data-field element", translations.get("I18nComponent.Text_in_a_non-data-field_element"));

    int i18nComponentStringCount = 0;
    for (String key : translations.keySet()) {
      if (key.startsWith("I18nComponent.")) {
        i18nComponentStringCount++;
      }
    }
    assertEquals("Too many translation keys for I18nComponent: " + translations, 13, i18nComponentStringCount);
  }

  @Test
  public void testAllBundleFileOnlyContainsNestedTemplateKeys() throws Exception {

    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(I18nNotRootTemplatedWidget.class));
    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(CompositeI18nTemplateTestApp.class));
    MetaClassFactory.getMetaClassCache().pushCache(MetaClassFactory.get(TranslationService.class));

    new TranslationServiceGenerator().generate(null, null);

    ObjectMapper mapper = new ObjectMapper();
    @SuppressWarnings("unchecked")
    Map<String, String> translations =
      mapper.readValue(new File(".errai", "errai-bundle-all.json").getAbsoluteFile(), Map.class);

    // ensure the ONLY translation key for I18nNotRootTemplatedWidget is the one under the #root node
    boolean found = false;
    for (String key : translations.keySet()) {
      if (key.startsWith("I18nNotRootTemplatedWidget")) {
        assertEquals("I18nNotRootTemplatedWidget.inside", key);
        assertEquals("inside", translations.get(key));
        found = true;
      }
    }

    assertTrue("Couldn't find translation key for I18nNotRootTemplatedWidget:" + translations , found);
  }
}
