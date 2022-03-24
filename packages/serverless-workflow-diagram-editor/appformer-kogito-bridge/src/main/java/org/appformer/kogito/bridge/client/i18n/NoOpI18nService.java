/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.i18n;

import elemental2.promise.Promise;

/**
 * This {@link I18nApi} implementation is used when the envelope API is not available
 */
public class NoOpI18nService implements I18nApi {

    @Override
    public void onLocaleChange(LocaleChangeCallback callback) {

    }

    @Override
    public Promise<String> getLocale() {
        return Promise.resolve("en");
    }
}
