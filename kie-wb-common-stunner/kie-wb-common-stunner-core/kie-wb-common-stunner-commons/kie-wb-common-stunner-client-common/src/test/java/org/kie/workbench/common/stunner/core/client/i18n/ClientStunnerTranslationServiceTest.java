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
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.CAPTION_SUFFIX;
import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.DESCRIPTION_SUFFIX;
import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.LABEL_SUFFIX;
import static org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService.TITLE_SUFFIX;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientStunnerTranslationServiceTest {

    public static final String DEFINITION_SET = "org.kie.workbench.common.stunner.core.test.DefinitionSet";
    public static final String PROPERTY_SET = "org.kie.workbench.common.stunner.core.test.PropertySet";
    public static final String DEFINITION = "org.kie.workbench.common.stunner.core.test.Definition";
    public static final String PROPERTY = "org.kie.workbench.common.stunner.core.test.Property";

    @Mock
    private TranslationService translationService;

    private ClientTranslationService stunnerTranslationService;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Before
    public void init() {
        stunnerTranslationService = new ClientTranslationService(translationService, sessionManager, definitionUtils);
    }

    @Test
    public void testFunctionality() {

        stunnerTranslationService.getDefinitionSetDescription(DEFINITION_SET);

        verify(translationService).getTranslation(getKey(DEFINITION_SET,
                                                         DESCRIPTION_SUFFIX));

        stunnerTranslationService.getDefinitionTitle(DEFINITION);

        verify(translationService).getTranslation(getKey(DEFINITION,
                                                         TITLE_SUFFIX));

        stunnerTranslationService.getDefinitionDescription(DEFINITION);

        verify(translationService).getTranslation(getKey(DEFINITION,
                                                         DESCRIPTION_SUFFIX));

        stunnerTranslationService.getPropertySetName(PROPERTY_SET);

        verify(translationService).getTranslation(getKey(PROPERTY_SET,
                                                         CAPTION_SUFFIX));

        stunnerTranslationService.getPropertyCaption(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY,
                                                         CAPTION_SUFFIX));

        stunnerTranslationService.getPropertyDescription(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY,
                                                         DESCRIPTION_SUFFIX));
    }

    @Test
    public void testGetPropertyCaptionNull() {
        when(translationService.getTranslation(getKey(PROPERTY, CAPTION_SUFFIX))).thenReturn(null);
        when(translationService.getTranslation(getKey(PROPERTY, LABEL_SUFFIX))).thenReturn("labelCaption");

        final String propertyCaption = stunnerTranslationService.getPropertyCaption(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY, CAPTION_SUFFIX));
        verify(translationService).getTranslation(getKey(PROPERTY, LABEL_SUFFIX));
        verify(translationService, never()).getTranslation(getKey(PROPERTY, TITLE_SUFFIX));

        assertEquals(propertyCaption, "labelCaption");
    }

    @Test
    public void testGetPropertyCaptionLabelNull() {
        when(translationService.getTranslation(getKey(PROPERTY, CAPTION_SUFFIX))).thenReturn(null);
        when(translationService.getTranslation(getKey(PROPERTY, LABEL_SUFFIX))).thenReturn(null);
        when(translationService.getTranslation(getKey(PROPERTY, TITLE_SUFFIX))).thenReturn("titleCaption");

        final String propertyCaption = stunnerTranslationService.getPropertyCaption(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY, CAPTION_SUFFIX));
        verify(translationService).getTranslation(getKey(PROPERTY, LABEL_SUFFIX));
        verify(translationService).getTranslation(getKey(PROPERTY, TITLE_SUFFIX));
        assertEquals(propertyCaption, "titleCaption");
    }

    @Test
    public void testGetPropertyCaptionTitleNull() {
        when(translationService.getTranslation(getKey(PROPERTY, CAPTION_SUFFIX))).thenReturn(null);
        when(translationService.getTranslation(getKey(PROPERTY, LABEL_SUFFIX))).thenReturn(null);
        when(translationService.getTranslation(getKey(PROPERTY, TITLE_SUFFIX))).thenReturn(null);

        final String propertyCaption = stunnerTranslationService.getPropertyCaption(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY, CAPTION_SUFFIX));
        verify(translationService).getTranslation(getKey(PROPERTY, LABEL_SUFFIX));
        verify(translationService).getTranslation(getKey(PROPERTY, TITLE_SUFFIX));

        assertEquals(propertyCaption, PROPERTY);
    }

    @Test
    public void testGetPropertyCaption() {
        when(translationService.getTranslation(getKey(PROPERTY, CAPTION_SUFFIX))).thenReturn("caption");

        final String propertyCaption = stunnerTranslationService.getPropertyCaption(PROPERTY);

        verify(translationService).getTranslation(getKey(PROPERTY, CAPTION_SUFFIX));
        verify(translationService, never()).getTranslation(getKey(PROPERTY, LABEL_SUFFIX));
        verify(translationService, never()).getTranslation(getKey(PROPERTY, TITLE_SUFFIX));

        assertEquals(propertyCaption, "caption");
    }

    protected String getKey(String modelName,
                            String suffix) {
        return modelName + ClientTranslationService.I18N_SEPARATOR + suffix;
    }
}
