/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.experimental.client.disabled.component;

import org.assertj.core.api.Assertions;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DisabledFeatureComponentTest {

    private String EXPERIMENTAL_FEATURE_ID = "a random id";

    @Mock
    private DisabledFeatureComponentView view;

    @Mock
    private ExperimentalFeatureDefRegistry registry;

    @Mock
    private TranslationService translationService;

    private DisabledFeatureComponent component;

    @Before
    public void init() {
        component = new DisabledFeatureComponent(view, registry, translationService);
    }

    @Test
    public void testShowMissingExperimentalFeature() {
        Assertions.assertThatThrownBy(() -> component.show(EXPERIMENTAL_FEATURE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'experimentalFeature' should be not null!");

        verify(translationService, never()).getTranslation(anyString());
        verify(translationService, never()).format(anyString(), anyString());
        verify(view, never()).show(anyString());
    }

    @Test
    public void testShowExperimentalFeature() {
        testShowExperimentalFeature(false);
    }

    @Test
    public void testShowGlobalExperimentalFeature() {
        testShowExperimentalFeature(true);
    }

    private void testShowExperimentalFeature(boolean global) {
        ExperimentalFeatureDefinition definition = new ExperimentalFeatureDefinition(EXPERIMENTAL_FEATURE_ID, global, "", EXPERIMENTAL_FEATURE_ID, EXPERIMENTAL_FEATURE_ID);

        when(registry.getFeatureById(eq(EXPERIMENTAL_FEATURE_ID))).thenReturn(definition);

        component.show(EXPERIMENTAL_FEATURE_ID);

        verify(translationService).getTranslation(EXPERIMENTAL_FEATURE_ID);

        String i18nConstant = global ? UberfireExperimentalConstants.disabledGlobalExperimentalFeature : UberfireExperimentalConstants.disabledExperimentalFeature;

        verify(translationService).format(eq(i18nConstant), anyString());

        verify(view).show(anyString());
    }
}
