/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ui.client.widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.Locale;

/**
 * Util component to get all locales that your application supports and set a specific one.
 *
 * @author edewit@redhat.com
 */
@Singleton
public class LocaleSelector {

  public static final String DEFAULT = "default";

  /**
   * Get all locales that your application supports, all the bundles that you have provided.
   * @return supported locales
   */
  public Collection<Locale> getSupportedLocales() {
    Set<Locale> supportedLocales = new HashSet<Locale>();
    final TranslationService translationService = TemplateUtil.getTranslationService();
    final Collection<String> locales = translationService.getSupportedLocales();
    for (String localeKey : locales) {
      localeKey = localeKey == null ? DEFAULT : localeKey;
      supportedLocales.add(new Locale(localeKey, translationService.getTranslation(localeKey)));
    }

    return supportedLocales;
  }

  /**
   * Set the <code>locale</code> as the current locale
   * @param locale the locale to set as the current
   */
  public void select(String locale) {
    TranslationService.setCurrentLocale(locale);
  }
}
