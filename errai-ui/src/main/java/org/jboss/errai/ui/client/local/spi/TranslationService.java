/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.common.client.util.Properties;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.async.AsyncBeanDef;
import org.jboss.errai.ui.shared.DomVisit;
import org.jboss.errai.ui.shared.JSONMap;
import org.jboss.errai.ui.shared.TranslationDomRevisitor;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.wrapper.ElementWrapper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Composite;

/**
 * A base class for a generated translation service that includes all of the translation visible at
 * compile time.
 *
 * @author eric.wittmann@redhat.com
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class TranslationService {

  private static final Logger logger = Logger.getLogger(TranslationService.class.getName());
  private static String currentLocale = null;

  private final Dictionary dictionary = new Dictionary();

  private static boolean shouldSearchKeyOnDefaultLocale = Boolean.parseBoolean(System.getProperty("errai.i18n.default_per_key"));

  /**
   * Constructor.
   */
  public TranslationService() {}

  /**
   * @return true if the translation service is enabled/should be used
   */
  public boolean isEnabled() {
    return !dictionary.getSupportedLocals().isEmpty();
  }

  public Collection<String> getSupportedLocales() {
    return dictionary.getSupportedLocals();
  }

  /**
   * Registers the bundle with the translation service.
   */
  public void registerJsonBundle(final String data, final String locale) {
    registerJSON(JSONMap.create(data), locale);
  }

  /**
   * Registers the bundle with the translation service.
   */
  public void registerPropertiesBundle(final String data, final String locale) {
    final Map<String, String> translation = Properties.load(data);

    for (final Entry<String, String> entry : translation.entrySet()) {
      registerTranslation(entry.getKey(), entry.getValue(), locale);
    }
  }

  /**
   * Registers a single translation.
   */
  public void registerTranslation(final String key, final String value, String locale) {
    if (locale != null) {
      locale = locale.toLowerCase();
    }
    dictionary.put(locale, key, value);
  }

  /**
   * Registers some i18n data with the translation service. This is called for each discovered
   * bundle file.
   */
  public void registerJSON(final JSONMap data, final String locale) {
    logger.fine("Registering translation data for locale: " + locale);
    final Set<String> keys = data.keys();
    for (final String key : keys) {
      final String value = data.get(key);
      registerTranslation(key, value, locale);
    }
    logger.fine("Registered " + keys.size() + " translation keys.");
  }

  /**
   * Gets the translation for the given i18n translation key.
   */
  public String getTranslation(final String translationKey) {
    final String localeName = getActiveLocale();
    return getTranslation(translationKey, localeName, null);
  }

  protected String getTranslation(final String translationKey, final String localeName, final String defaultValue) {
    logger.fine("Translating key: " + translationKey + "  into locale: " + localeName);
    final Map<String, String> translationData = dictionary.get(localeName);
    if (translationData.containsKey(translationKey)) {
      logger.fine("Translation found in locale map: " + localeName);
      return translationData.get(translationKey);
    } else {
      final String nonNamespacedKey = translationKey.substring(translationKey.indexOf('.')+1);
      if (!nonNamespacedKey.equals(translationKey) && translationData.containsKey(nonNamespacedKey)) {
        logger.fine("Global translation found in locale map: " + localeName);
        return translationData.get(nonNamespacedKey);
      }
    }
    if (localeName != null && shouldSearchKeyOnDefaultLocale) {
      // Nothing? Tries to find translation in default locale.
      logger.fine("Translation not found in locale map: " + localeName);
      return getTranslation(translationKey,
                            null,
                            defaultValue);
    }
    // Nothing in the default locale? Then return the default value.
    logger.fine("Translation not found in any locale map, leaving unchanged.");
    return defaultValue;
  }

  /**
   * Look up a message in the i18n resource message bundle by key, then format the message with the
   * given arguments and return the result.
   */
  public String format(final String key, final Object... args) {
    final String pattern = getTranslation(key, getActiveLocale(), "!!!" + key + "!!!"); //$NON-NLS-1$ //$NON-NLS-2$
    if (args.length == 0)
      return pattern;

    // TODO add support for actually using { in a message
    final StringBuilder builder = new StringBuilder(pattern);
    int argId = 0;
    for (final Object arg : args) {
      final String rcode = "{" + (argId++) + "}";
      final int startIdx = builder.indexOf(rcode);
      final int endIdx = startIdx + rcode.length();
      builder.replace(startIdx, endIdx, String.valueOf(arg));
    }
    return builder.toString();
  }

  public String getActiveLocale() {
    final String localeName = currentLocale();
    if (!dictionary.get(localeName).isEmpty()) {
      return localeName;
    }
    if (localeName != null && localeName.contains("_")
            && !dictionary.get(localeName.substring(0, localeName.indexOf('_'))).isEmpty()) {
      return localeName.substring(0, localeName.indexOf('_'));
    }
    return null;
  }

  /**
   * @return the currently configured locale
   */
  public static String currentLocale() {
    if (currentLocale == null) {
      String locale = com.google.gwt.user.client.Window.Location.getParameter("locale");
      if (locale == null || locale.trim().length() == 0) {
        locale = getBrowserLocale();
        if (locale != null) {
          if (locale.indexOf('-') != -1) {
            locale = locale.replace('-', '_');
          }
        }
      }
      if (locale == null) {
        locale = "default";
      }
      currentLocale = locale.toLowerCase();
      logger.fine("Discovered the current locale (either via query string or navigator) of: " + currentLocale);
    }
    return currentLocale;
  }

  /**
   * Gets the browser's configured locale.
   */
  public static native String getBrowserLocale() /*-{
    if ($wnd.navigator.language) {
      return $wnd.navigator.language;
    }
    if ($wnd.navigator.userLanguage) {
      return $wnd.navigator.userLanguage;
    }
    if ($wnd.navigator.browserLanguage) {
      return $wnd.navigator.browserLanguage;
    }
    if ($wnd.navigator.systemLanguage) {
      return $wnd.navigator.systemLanguage;
    }
    return null;
   }-*/;

  /**
   * Forcibly set the current locale and re-translate all instantiated {@link Templated} beans.
   */
  public static void setCurrentLocale(final String locale) {
    setCurrentLocaleWithoutUpdate(locale);
    retranslateTemplatedBeans();
  }

  /**
   * Forcibly set if the default locale should be searched when a translation key does not have a
   * translation registered.
   */
  public static void setShouldSearchKeyOnDefaultLocale(final boolean newShouldSearchKeyOnDefaultLocale) {
    shouldSearchKeyOnDefaultLocale = newShouldSearchKeyOnDefaultLocale;
  }

  /**
   * Forcibly set the current locale but do not re-translate existing templated instances. Mostly
   * useful for testing.
   */
  public static void setCurrentLocaleWithoutUpdate(final String locale) {
    currentLocale = locale;
  }

  /**
   * Re-translate displayed {@link Templated} beans to the current locale.
   */
  public static void retranslateTemplatedBeans() {
    // Translate DOM-attached templates
    DomVisit.revisit(new ElementWrapper(Document.get().getBody()), new TranslationDomRevisitor());

    // Translate DOM-detached Singleton templates
    for (final AsyncBeanDef<Composite> beanDef : IOC.getAsyncBeanManager().lookupBeans(Composite.class)) {
      final Class<? extends Annotation> scope = beanDef.getScope();
      if (scope != null
              && (scope.equals(ApplicationScoped.class)))
        beanDef.getInstance(new CreationalCallback<Composite>() {
          @Override
          public void callback(final Composite beanInstance) {
            /*
             * Only translate parent-less widgets to avoid re-translating a single widget multiple
             * times (the call to revisit will traverse the whole subtree rooted at this widget).
             */
            if (beanInstance.getParent() == null)
              DomVisit.revisit(new ElementWrapper(beanInstance.getElement()), new TranslationDomRevisitor());
          }
        });
    }
  }
}
