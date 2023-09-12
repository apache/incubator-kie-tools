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


package org.jboss.errai.ui.rebind;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.rebind.res.TranslatableComponent1;
import org.jboss.errai.ui.rebind.res.TranslatableComponent2;
import org.junit.Assert;
import org.junit.Test;

/**
 * Junit test for {@link TranslationService};
 * 
 * @author eric.wittmann@redhat.com
 */
public class TranslationServiceGeneratorTest {

  /**
   * Test method for
   * {@link org.jboss.errai.ui.rebind.TranslationServiceGenerator#getLocaleFromBundlePath(java.lang.String)}
   * .
   */
  @Test
  public void testGetLocaleFromBundleFilename() {
    Assert.assertEquals(null, TranslationServiceGenerator.getLocaleFromBundlePath("myBundle.json"));
    Assert.assertEquals(null, TranslationServiceGenerator.getLocaleFromBundlePath("myBundle_en_US.other"));

    Assert.assertEquals("en_US", TranslationServiceGenerator.getLocaleFromBundlePath("myBundle_en_US.json"));
    Assert.assertEquals("en_GB", TranslationServiceGenerator.getLocaleFromBundlePath("myBundle_en_GB.json"));
    Assert.assertEquals("en", TranslationServiceGenerator.getLocaleFromBundlePath("myBundle_en.json"));

    Assert.assertEquals("fr_CA", TranslationServiceGenerator.getLocaleFromBundlePath("Some-Other-Bundle_fr_CA.json"));
    Assert.assertEquals("fr_FR", TranslationServiceGenerator.getLocaleFromBundlePath("Some-Other-Bundle_fr_FR.json"));
    Assert.assertEquals("fr", TranslationServiceGenerator.getLocaleFromBundlePath("Some-Other-Bundle_fr.json"));

    Assert.assertEquals("en_US", TranslationServiceGenerator
        .getLocaleFromBundlePath("org/example/ui/client/local/myBundle_en_US.json"));
    Assert.assertEquals("en_GB", TranslationServiceGenerator
        .getLocaleFromBundlePath("org/example/ui/client/local/myBundle_en_GB.json"));
    Assert.assertEquals("en", TranslationServiceGenerator
        .getLocaleFromBundlePath("org/example/ui/client/local/myBundle_en.json"));
  }

  /**
   * Test method for
   * {@link org.jboss.errai.ui.rebind.TranslationServiceGenerator#recordBundleKeys(java.util.Map, String, String)}
   * .
   */
  @Test
  public void testRecordBundleKeys() {
    String jsonResourcePath = "org/jboss/errai/ui/test/i18n/client/I18nTemplateTest.json";
    Map<String, Set<String>> result = new HashMap<String, Set<String>>();
    TranslationServiceGenerator.recordBundleKeys(result, null, jsonResourcePath);
    Assert.assertEquals(1, result.keySet().size());
    Set<String> defaultKeys = result.get(null);
    Assert.assertEquals(11, defaultKeys.size());

    jsonResourcePath = "org/jboss/errai/ui/test/i18n/client/I18nTemplateTest_fr_FR.json";
    TranslationServiceGenerator.recordBundleKeys(result, "fr_FR", jsonResourcePath);
    Assert.assertEquals(2, result.keySet().size());
    defaultKeys = result.get(null);
    Assert.assertEquals(11, defaultKeys.size());
    Set<String> fr_FR_Keys = result.get("fr_FR");
    Assert.assertEquals(10, fr_FR_Keys.size());

    jsonResourcePath = "org/jboss/errai/ui/test/i18n/client/I18nTemplateTest_da.json";
    TranslationServiceGenerator.recordBundleKeys(result, "da", jsonResourcePath);
    Assert.assertEquals(3, result.keySet().size());
    defaultKeys = result.get(null);
    Assert.assertEquals(11, defaultKeys.size());
    fr_FR_Keys = result.get("fr_FR");
    Assert.assertEquals(10, fr_FR_Keys.size());
    Set<String> da_Keys = result.get("da");
    Assert.assertEquals(9, da_Keys.size());
  }

  /**
   * Test method for
   * {@link org.jboss.errai.ui.rebind.TranslationServiceGenerator#generateI18nHelperFilesInto(Map, java.io.File)}
   * .
   */
  @Test
  public void testHelperFileGeneration() throws IOException {
    // This will cause the two component classes to be cached and discoverable
    // when we are doing the translating.
    MetaClassFactory.get(TranslatableComponent1.class);
    MetaClassFactory.get(TranslatableComponent2.class);
    Map<String, Set<String>> i18nKeys = new HashMap<String, Set<String>>();
    // Record the bundle keys found in the test JSON bundles
    TranslationServiceGenerator.recordBundleKeys(i18nKeys, null,
        "org/jboss/errai/ui/rebind/res/TranslationServiceGeneratorTest.json");

    File outputDir = File.createTempFile(getClass().getSimpleName(), "_tst");
    if (outputDir.isFile())
      outputDir.delete();
    outputDir.mkdirs();
    try {
      HashMap<String, String> translationKeyFieldMap = new HashMap<String, String>();
      translationKeyFieldMap.put("additional-key-1", "some value 1");
      translationKeyFieldMap.put("additional-key-2", "some value 2");
      TranslationServiceGenerator.generateI18nHelperFilesInto(i18nKeys, translationKeyFieldMap, outputDir);
      
      // Should be a "errai-bundle-all.json" with *all* keys/values
      File allBundle = new File(outputDir, "errai-bundle-all.json");
      String actual = FileUtils.readFileToString(allBundle);
      Assert.assertEquals(EXPECTED_ALL_BUNDLE, actual);

      // Should be a "errai-bundle-extra.json" with the extra/unneeded keys
      File extraBundle = new File(outputDir, "errai-bundle-extra.json");
      actual = FileUtils.readFileToString(extraBundle);
      Assert.assertEquals(EXPECTED_EXTRA_BUNDLE, actual);

      // Should be a "errai-bundle-missing.json" with only the missing keys
      File missingBundle = new File(outputDir, "errai-bundle-missing.json");
      actual = FileUtils.readFileToString(missingBundle);
      Assert.assertEquals(EXPECTED_MISSING_BUNDLE, actual);

    }
    finally {
      FileUtils.deleteDirectory(outputDir);
    }
  }

  private static final String NEWLINE = System.getProperty("line.separator");

  private static final String EXPECTED_ALL_BUNDLE =
        "{" + NEWLINE +
            "  \"TranslatableComponent1.Email_\" : \"Email:\"," + NEWLINE +
            "  \"TranslatableComponent1.Label_1_\" : \"Label 1:\"," + NEWLINE +
            "  \"TranslatableComponent1.Label_2_\" : \"Label 2:\"," + NEWLINE +
            "  \"TranslatableComponent1.Password_\" : \"Password:\"," + NEWLINE +
            "  \"TranslatableComponent1.email-placeholder\" : \"Enter your email address...\"," + NEWLINE +
            "  \"TranslatableComponent1.password-title\" : \"Your password goes here.\"," + NEWLINE +
            "  \"TranslatableComponent1.value_one\" : \"value one\"," + NEWLINE +
            "  \"TranslatableComponent1.value_two\" : \"value two\"," + NEWLINE +
            "  \"TranslatableComponent1.welcome\" : \"Welcome to the errai-ui i18n demo.\"," + NEWLINE +
            "  \"additional-key-1\" : \"some value 1\"," + NEWLINE +
            "  \"additional-key-2\" : \"some value 2\"," + NEWLINE +
            "  \"component2.Cancel\" : \"Cancel\"," + NEWLINE +
            "  \"component2.Log_in\" : \"Log in\"," + NEWLINE +
            "  \"component2.Log_in_to_your_account\" : \"Log in to your account\"," + NEWLINE +
            "  \"component2.Password\" : \"Password\"," + NEWLINE +
            "  \"component2.Remember_me\" : \"Remember me\"," + NEWLINE +
            "  \"component2.Username\" : \"Username\"," + NEWLINE +
            "  \"component2.password-placeholder\" : \"Password\"," + NEWLINE +
            "  \"component2.username-placeholder\" : \"Username\"" + NEWLINE +
            "}";
  private static final String EXPECTED_EXTRA_BUNDLE =
        "{" + NEWLINE +
            "  \"component2.extra-key\" : \"\"" + NEWLINE +
            "}";
  private static final String EXPECTED_MISSING_BUNDLE =
        "{" + NEWLINE +
            "  \"TranslatableComponent1.password-title\" : \"Your password goes here.\"," + NEWLINE +
            "  \"TranslatableComponent1.welcome\" : \"Welcome to the errai-ui i18n demo.\"," + NEWLINE +
            "  \"additional-key-2\" : \"some value 2\"," + NEWLINE +
            "  \"component2.Log_in_to_your_account\" : \"Log in to your account\"" + NEWLINE +
            "}";
}
