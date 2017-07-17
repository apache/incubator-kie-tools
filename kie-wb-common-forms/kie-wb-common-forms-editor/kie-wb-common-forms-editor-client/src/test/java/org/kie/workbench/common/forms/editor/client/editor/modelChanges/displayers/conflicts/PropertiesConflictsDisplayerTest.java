/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.conflicts;

import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.ModelChangeDisplayerTestFieldProvider;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.conflicts.elements.ConflictElement;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesConflictsDisplayerTest {

    List<FieldDefinition> fields;

    @Mock
    PropertiesConflictsDisplayerView view;

    @Mock
    TranslationService translationService;

    @Mock
    ManagedInstance managedInstance;

    @Mock
    ConflictElement conflictElement;

    PropertiesConflictsDisplayer presenter;

    @Before
    public void init() {
        fields = ModelChangeDisplayerTestFieldProvider.getFields();

        when(managedInstance.get()).thenReturn(conflictElement);

        presenter = new PropertiesConflictsDisplayer(view,
                                                     managedInstance,
                                                     translationService);
    }

    @Test
    public void testShowRemovedFunctionallity() {
        verify(view).init(presenter);

        presenter.getElement();

        verify(view).getElement();

        presenter.showRemovedFields(fields);

        verify(view,
               times(fields.size())).showConflict(conflictElement);

        verify(translationService,
               times(fields.size())).format(anyString(),
                                            anyString());

        verify(managedInstance,
               times(fields.size())).get();

        presenter.clear();

        verify(view).clear();

        verify(managedInstance).destroyAll();
    }

    @Test
    public void testShowConflictFunctionallity() {
        verify(view).init(presenter);

        presenter.getElement();

        verify(view).getElement();

        presenter.showTypeConflictFields(fields);

        verify(view,
               times(fields.size())).showConflict(conflictElement);

        verify(translationService,
               times(fields.size())).format(anyString(),
                                            anyString());

        verify(managedInstance,
               times(fields.size())).get();

        presenter.clear();

        verify(view).clear();

        verify(managedInstance).destroyAll();
    }
}
