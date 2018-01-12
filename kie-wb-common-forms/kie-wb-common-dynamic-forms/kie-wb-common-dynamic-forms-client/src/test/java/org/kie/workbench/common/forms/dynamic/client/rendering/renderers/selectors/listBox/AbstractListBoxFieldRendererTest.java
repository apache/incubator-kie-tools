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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.common.rendering.client.widgets.util.DefaultValueListBoxRenderer;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.shared.BackendSelectorDataProviderService;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.SelectorDataProviderManager;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.ListBoxBaseDefinition;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractListBoxFieldRendererTest<RENDERER extends AbstractListBoxFieldRenderer<FIELD, OPTION, TYPE>, FIELD extends ListBoxBaseDefinition<OPTION, TYPE>, OPTION extends SelectorOption<TYPE>, TYPE> {

    @GwtMock
    protected ValueListBox<TYPE> valueListBox;

    @Mock
    protected SelectorDataProviderManager clientProviderManager;

    @Mock
    protected CallerMock<BackendSelectorDataProviderService> backendSelectorDataProviderService;

    @Spy
    protected DefaultValueListBoxRenderer<TYPE> optionsRenderer = new DefaultValueListBoxRenderer<>();

    @Mock
    protected TranslationService translationService;

    @Mock
    protected FIELD field;

    @Mock
    protected FormRenderingContext context;

    protected RENDERER renderer;

    protected Map<TYPE, String> options = new HashMap<>();

    @Before
    public void init() {
        renderer = getRenderer(translationService,
                               clientProviderManager,
                               backendSelectorDataProviderService,
                               optionsRenderer);

        renderer.init(context,
                      field);

        fillOptions(options);
    }

    @Test
    public void testRefreshInputDontAddNullOption() {
        when(field.getAddEmptyOption()).thenReturn(false);

        renderer.refreshInput(options,
                              null);

        verify(field).getAddEmptyOption();

        verify(translationService,
               never()).getTranslation(FormRenderingConstants.ListBoxFieldRendererEmptyOptionText);

        assertFalse(options.containsValue(null));

        verify(optionsRenderer).setValues(options);

        verify(valueListBox).setAcceptableValues(any());
    }

    @Test
    public void testRefreshInputAddNullOption() {
        when(field.getAddEmptyOption()).thenReturn(true);

        renderer.refreshInput(options,
                              null);

        verify(field).getAddEmptyOption();

        verify(translationService).getTranslation(FormRenderingConstants.ListBoxFieldRendererEmptyOptionText);

        assertTrue(options.containsValue(null));

        verify(optionsRenderer).setValues(options);

        verify(valueListBox).setAcceptableValues(any());
    }

    @Test
    public void testRefreshInputAddExistingNullOption() {
        when(field.getAddEmptyOption()).thenReturn(true);

        options.put(null,
                    "null");

        renderer.refreshInput(options,
                              null);

        verify(field).getAddEmptyOption();

        verify(translationService,
               never()).getTranslation(FormRenderingConstants.ListBoxFieldRendererEmptyOptionText);

        verify(optionsRenderer).setValues(options);

        verify(valueListBox).setAcceptableValues(any());
    }

    protected abstract RENDERER getRenderer(TranslationService translationServiceParam,
                                            SelectorDataProviderManager clientProviderManagerParam,
                                            CallerMock<BackendSelectorDataProviderService> backendSelectorDataProviderServiceParam,
                                            DefaultValueListBoxRenderer<TYPE> optionsRendererParam);

    protected abstract void fillOptions(Map<TYPE, String> options);
}
