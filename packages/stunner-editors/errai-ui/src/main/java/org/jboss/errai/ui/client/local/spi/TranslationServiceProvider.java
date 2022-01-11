/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Provider;
import javax.inject.Singleton;

import org.jboss.errai.ioc.client.api.IOCProvider;

import com.google.gwt.core.shared.GWT;

/**
 * Provides a way to inject the {@link TranslationService}.
 * 
 * @author eric.wittmann@redhat.com
 */
@IOCProvider
@Singleton
public class TranslationServiceProvider implements Provider<TranslationService> {

  private static final TranslationService _translationService = GWT.create(TranslationService.class);

  /**
   * Constructor.
   */
  public TranslationServiceProvider() {}

  /**
   * @see javax.inject.Provider#get()
   */
  @Override
  public TranslationService get() {
    return _translationService;
  }

}
