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


package org.kie.workbench.common.forms.adf.engine.client.formGeneration;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractI18nHelper;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;

public class ClientI18nHelper extends AbstractI18nHelper {

    private TranslationService translationService;

    public ClientI18nHelper(I18nSettings settings,
                            TranslationService translationService) {
        super(settings);
        this.translationService = translationService;
    }

    @Override
    public String getTranslation(String key) {
        return translationService.getTranslation(key);
    }
}
