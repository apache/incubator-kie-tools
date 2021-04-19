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

package org.jboss.errai.ui.shared.api;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Simple data object representing a Locale for i18n
 * @author edewit@redhat.com
 */
@Bindable
public class Locale {
  private String locale;
  private String label;

  public Locale() {
  }

  public Locale(@MapsTo("locale") String locale, @MapsTo("label") String label) {
    this.locale = locale;
    this.label = label;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
