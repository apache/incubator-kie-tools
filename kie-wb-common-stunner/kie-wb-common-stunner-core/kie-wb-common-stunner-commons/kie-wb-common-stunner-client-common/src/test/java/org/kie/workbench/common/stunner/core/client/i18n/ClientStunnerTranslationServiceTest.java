/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.i18n;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.CAPTION_SUFFIX;
import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.DESCRIPTION_SUFFIX;
import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.TITLE_SUFFIX;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClientStunnerTranslationServiceTest {

    public static final String DEFINITION_SET = "org.kie.workbench.common.stunner.core.test.DefinitionSet";
    public static final String PROPERTY_SET = "org.kie.workbench.common.stunner.core.test.PropertySet";
    public static final String DEFINITION = "org.kie.workbench.common.stunner.core.test.Definition";
    public static final String PROPERTY = "org.kie.workbench.common.stunner.core.test.Property";

    @Mock
    private TranslationService translationService;

    private ClientTranslationService stunnerTranlationService;

    @Before
    public void init() {
        stunnerTranlationService = new ClientTranslationService(translationService);
    }

    @Test
    public void testFunctionallity() {

        stunnerTranlationService.getDefinitionSetDescription(DEFINITION_SET);

        verify(translationService).getTranslation(getKey(DEFINITION_SET,
                                                         DESCRIPTION_SUFFIX));

        stunnerTranlationService.getDefinitionTitle(DEFINITION);

        verify(translationService).getTranslation(getKey(DEFINITION,
                                                         TITLE_SUFFIX));

        stunnerTranlationService.getDefinitionDescription(DEFINITION);

        verify(translationService).getTranslation(getKey(DEFINITION,
                                                         DESCRIPTION_SUFFIX));

        stunnerTranlationService.getPropertySetName(PROPERTY_SET);

        verify(translationService).getTranslation(getKey(PROPERTY_SET,
                                                         CAPTION_SUFFIX));

        stunnerTranlationService.getPropertyCaption(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY,
                                                         CAPTION_SUFFIX));

        stunnerTranlationService.getPropertyDescription(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY,
                                                         DESCRIPTION_SUFFIX));
    }

    protected String getKey(String modelName,
                            String suffix) {
        return modelName + ClientTranslationService.I18N_SEPARATOR + suffix;
    }
}
