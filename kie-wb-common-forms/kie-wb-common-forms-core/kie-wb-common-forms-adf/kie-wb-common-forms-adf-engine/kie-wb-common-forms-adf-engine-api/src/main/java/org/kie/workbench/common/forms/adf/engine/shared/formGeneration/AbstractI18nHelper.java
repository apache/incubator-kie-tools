/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.engine.shared.formGeneration;

import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;

public abstract class AbstractI18nHelper implements I18nHelper {

    private static final String defaultSeparator = ".";

    private String keyPreffix = "";

    public AbstractI18nHelper(I18nSettings settings) {
        if (settings.getKeyPreffix() != null && !settings.getKeyPreffix().isEmpty()) {
            String separator = settings.getSeparator() != null ? settings.getSeparator() : defaultSeparator;
            this.keyPreffix = settings.getKeyPreffix() + separator;
        }
    }

    @Override
    public String getTranslation(String key) {
        return translate(keyPreffix + key);
    }

    protected abstract String translate(String key);
}
