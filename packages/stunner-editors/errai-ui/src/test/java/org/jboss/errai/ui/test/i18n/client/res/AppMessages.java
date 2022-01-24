package org.jboss.errai.ui.test.i18n.client.res;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public class AppMessages {

  @TranslationKey(defaultValue = "not-translated")
  public static final String MESSAGE = "app.message";

  @TranslationKey(defaultValue = "")
  public static final String ENGLISH_MESSAGE = "app.english.message";
  
}
