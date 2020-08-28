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

package org.dashbuilder.displayer.client.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.external.model.ComponentParameter;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ExternalComponentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalComponentPropertiesEditorTest {

    @Mock
    ExternalComponentService externalComponentServiceMock;

    @Mock
    ExternalComponentPropertiesEditor.View view;

    @Mock
    BusyIndicatorView loading;

    @Captor
    ArgumentCaptor<Collection<PropertyEditorCategory>> categoriesCaptor;

    CallerMock<ExternalComponentService> externalComponentService;

    @InjectMocks
    ExternalComponentPropertiesEditor externalComponentPropertiesEditor;

    @Before
    public void init() {
        externalComponentService = new CallerMock<>(externalComponentServiceMock);
        externalComponentPropertiesEditor.setExternalComponentService(externalComponentService);
    }

    @Test
    public void testComponentNotFound() {
        String c1 = "c1";
        when(externalComponentServiceMock.byId(matches(c1))).thenReturn(Optional.empty());
        externalComponentPropertiesEditor.init(c1, Collections.emptyMap(), props -> {
        });
        verify(view).componentNotFound();
    }

    @Test
    public void testNoProperties() {
        ExternalComponent c1 = new ExternalComponent("c1", "c1 name", "c1 icon", false, Collections.emptyList());

        when(externalComponentServiceMock.byId(matches(c1.getId()))).thenReturn(Optional.of(c1));
        externalComponentPropertiesEditor.init(c1.getId(), Collections.emptyMap(), props -> {
        });

        verify(view).noPropertiesComponent();
    }

    @Test
    public void testLoadProperties() {
        ComponentParameter p1 = param("P1 CAT", "p1val", "text", "P1 Field", "p1", emptyList());
        ComponentParameter p2 = param("P2 CAT", "p2val", "combo", "P2 Field", "p2", asList("V1", "V2"));
        ExternalComponent c1 = new ExternalComponent("c1", "c1 name", "c1 icon", false, asList(p1, p2));

        when(externalComponentServiceMock.byId(matches(c1.getId()))).thenReturn(Optional.of(c1));
        externalComponentPropertiesEditor.init(c1.getId(), new HashMap<>(), props -> {
        });

        verify(view).addCategories(categoriesCaptor.capture());

        Collection<PropertyEditorCategory> categories = categoriesCaptor.getValue();

        assertEquals(3, categories.size());
    }

    private ComponentParameter param(String category, String defaultValue, String type, String label, String name, List<String> comboValues) {
        return new ComponentParameter(name, type, category, defaultValue, label, comboValues);
    }

}