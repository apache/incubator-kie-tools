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

package org.kie.workbench.common.forms.editor.client.editor.modelChanges;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.ModelChangeDisplayerTestFieldProvider;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.conflicts.PropertiesConflictsDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.modelChanges.displayers.newProperties.NewPropertiesDisplayer;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.impl.FormModelSynchronizationResultImpl;
import org.kie.workbench.common.forms.editor.model.impl.TypeConflictImpl;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.Object;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModelChangesDisplayerTest {

    @Mock
    HTMLElement htmlElement;

    @Mock
    ModelChangesDisplayerView view;

    @Mock
    NewPropertiesDisplayer newPropertiesDisplayer;

    @Mock
    PropertiesConflictsDisplayer propertiesConflictsDisplayer;

    @Mock
    FormModelSynchronizationUtil formModelSynchronizationUtil;

    @Mock
    Command command;

    FormModelSynchronizationResultImpl synchronizationResult = new FormModelSynchronizationResultImpl();

    FormModelerContent content = new FormModelerContent();

    FormDefinition form = new FormDefinition();

    FieldManager fieldManager = new TestFieldManager();

    List<FieldDefinition> fields;

    ModelChangesDisplayer presenter;

    boolean evaluateClose = true;

    @Before
    public void init() {

        when(view.getElement()).thenReturn(htmlElement);

        content.setSynchronizationResult(synchronizationResult);

        content.setDefinition(form);

        fields = ModelChangeDisplayerTestFieldProvider.getFields();

        presenter = new ModelChangesDisplayer(view,
                                              propertiesConflictsDisplayer,
                                              newPropertiesDisplayer,
                                              fieldManager,
                                              formModelSynchronizationUtil);

        verify(view).init(presenter);
    }

    @Test
    public void testShowEmptySynchronization() {
        try {
            evaluateClose = false;
            presenter.show(content,
                           command);
            fail("Synchronization is empty, we shouldn't be here!");
        } catch (IllegalStateException ex) {

        }
    }

    @Test
    public void testShowNewFields() {
        content.setAvailableFields(fields);

        synchronizationResult.getNewProperties().addAll(getModelProperties());

        presenter.show(content,
                       command);

        verify(newPropertiesDisplayer).showAvailableFields(fields);

        verify(propertiesConflictsDisplayer,
               never()).showRemovedFields(anyList());

        verify(propertiesConflictsDisplayer,
               never()).showTypeConflictFields(anyList());

        verify(view).getElement();

        verify(htmlElement).appendChild(any());

        verify(view).show();
    }

    @Test
    public void testShowConflicts() {
        synchronizationResult.getConflicts().putAll(getConflicts());

        form.getFields().addAll(fields);

        presenter.show(content,
                       command);

        verify(newPropertiesDisplayer,
               never()).showAvailableFields(anyList());

        verify(propertiesConflictsDisplayer).showRemovedFields(anyList());

        verify(propertiesConflictsDisplayer).showTypeConflictFields(anyList());

        verify(view).getElement();

        verify(htmlElement).appendChild(any());

        verify(view).show();
    }

    @Test
    public void testShowRemovedFields() {
        synchronizationResult.getRemovedProperties().addAll(getModelProperties());

        form.getFields().addAll(fields);

        presenter.show(content,
                       command);

        verify(newPropertiesDisplayer,
               never()).showAvailableFields(anyList());

        verify(propertiesConflictsDisplayer).showRemovedFields(anyList());

        verify(propertiesConflictsDisplayer).showTypeConflictFields(anyList());

        verify(view).getElement();

        verify(htmlElement).appendChild(any());

        verify(view).show();
    }

    public Map<String, TypeConflictImpl> getConflicts() {
        return fields.stream().map(fieldDefinition -> new TypeConflictImpl(fieldDefinition.getBinding(),
                                                                           new TypeInfoImpl(fieldDefinition.getStandaloneClassName()),
                                                                           new TypeInfoImpl(TypeKind.OBJECT,
                                                                                            Object.class.getName(),
                                                                                            false))).collect(Collectors.toMap(TypeConflictImpl::getPropertyName,
                                                                                                                              typeConflict -> typeConflict));
    }

    public List<ModelProperty> getModelProperties() {
        return fields.stream().map(fieldDefinition -> new ModelPropertyImpl(fieldDefinition.getBinding(),
                                                                            new TypeInfoImpl(fieldDefinition.getStandaloneClassName()))).collect(Collectors.toList());
    }

    @After
    public void after() {
        if (!evaluateClose) {
            return;
        }
        presenter.close();

        verify(propertiesConflictsDisplayer).clear();
        verify(newPropertiesDisplayer).clear();

        verify(formModelSynchronizationUtil).fixRemovedFields();
        verify(formModelSynchronizationUtil).resolveConflicts();

        verify(command).execute();
    }
}
