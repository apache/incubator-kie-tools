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

package org.jboss.errai.ui.client.local.spi;

import java.util.*;

/**
 * Dictionary that holds translation key/value pairs for all supported locales.
 * 
 * @author edewit@redhat.com
 */
public class Dictionary {
  private Map<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();

  public Map<String, String> get(String locale) {
    final Map<String, String> translationValues = translations.get(locale);
    if (translationValues != null) {
      return translationValues;
    }
    return Collections.emptyMap();
  }

  public Collection<String> getSupportedLocals() {
    return new HashSet<String>(translations.keySet());
  }

  public void put(String locale, String key, String message) {
    getTranslation(locale).put(key, message);
  }

  private Map<String, String> getTranslation(String locale) {
    Map<String, String> translation = translations.get(locale);
    if (translation == null) {
      translation = new HashMap<String, String>();
      translations.put(locale, translation);
    }
    return translation;
  }
}
