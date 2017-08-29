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
package org.kie.workbench.common.forms.editor.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.impl.TypeConflictImpl;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorPresenterFieldSynchronizationTest extends FormEditorPresenterAbstractTest {

    enum SyncMode {
        NEW_FIELDS,
        REMOVED_FIELDS,
        CONFLICTS,
        NONE
    }

    protected SyncMode syncMode = SyncMode.NONE;

    @Test
    public void testNoChangesOnModel() {
        loadContent();

        verify(modelChangesDisplayer).show(any(),
                                           any());
    }

    @Test
    public void testNewPropertiesOnModel() {
        syncMode = SyncMode.NEW_FIELDS;

        loadContent();

        verify(modelChangesDisplayer).show(any(),
                                           any());
    }

    @Test
    public void testConflictPropertiesOnModel() {
        syncMode = SyncMode.CONFLICTS;

        loadContent();

        verify(modelChangesDisplayer).show(any(),
                                           any());
    }

    @Test
    public void testRemovedPropertiesOnModel() {
        syncMode = SyncMode.REMOVED_FIELDS;

        loadContent();

        verify(modelChangesDisplayer).show(any(),
                                           any());
    }

    @Override
    public FormModelerContent serviceLoad() {
        FormModelerContent content = super.serviceLoad();
        if (syncMode.equals(SyncMode.NEW_FIELDS)) {
            synchronizationResult.getNewProperties().addAll(modelProperties);
        } else if (syncMode.equals(SyncMode.CONFLICTS)) {
            modelProperties.stream().map(property -> new TypeConflictImpl(property.getName(),
                                                                          property.getTypeInfo(),
                                                                          property.getTypeInfo())).forEach(typeConflict -> {
                synchronizationResult.getConflicts().put(typeConflict.getPropertyName(),
                                                         typeConflict);
            });
        } else if (syncMode.equals(SyncMode.REMOVED_FIELDS)) {
            content.getDefinition().getFields().addAll(employeeFields);
            synchronizationResult.getRemovedProperties().addAll(modelProperties);
        }
        return content;
    }
}
