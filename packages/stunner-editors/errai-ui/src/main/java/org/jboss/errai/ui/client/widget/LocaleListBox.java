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

import java.io.IOException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.Locale;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ListBox that contains the available language options. A user of your application can select one of these options by
 * hand. You can add a key in your bundle that is equal to the locale key (default for the one without locale key) to
 * add translation of the label.
 *
 * <pre>
 *   {
 *      "default": "English",
 *      "nl": "Nederlands"
 *
 *   }
 * </pre>
 *
 * @author edewit@redhat.com
 */
@Dependent
public class LocaleListBox extends ValueListBox<Locale> {
  @Inject
  LocaleSelector selector;

  public LocaleListBox() {
    super(new LocaleRenderer(), new LocaleProvidesKey());
  }

  @AfterInitialization
  public void init() {
    setAcceptableValues(selector.getSupportedLocales());
    addValueChangeHandler(new ValueChangeHandler<Locale>() {
      @Override
      public void onValueChange(ValueChangeEvent<Locale> event) {
        selector.select(event.getValue().getLocale());
      }
    });
  }

  private static class LocaleRenderer implements Renderer<Locale> {
    @Override
    public String render(Locale locale) {
      return locale.getLabel();
    }

    @Override
    public void render(Locale locale, Appendable appendable) throws IOException {
      appendable.append(render(locale));
    }
  }

  private static class LocaleProvidesKey implements ProvidesKey<Locale> {

    @Override
    public Object getKey(Locale item) {
      final String activeLocale = TemplateUtil.getTranslationService().getActiveLocale();
      String defaultLanguage = activeLocale != null ? activeLocale : LocaleSelector.DEFAULT;
      return item == null || item.getLocale() == null ? defaultLanguage : item.getLocale();
    }
  }
}
