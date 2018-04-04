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

package org.kie.workbench.common.forms.editor.client.editor.changes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element.ConflictElement;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.impl.FormModelConflictHandler;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.impl.NestedFormsConflictHandler;
import org.kie.workbench.common.forms.editor.client.editor.changes.displayers.ModelChangeDisplayerTestFieldProvider;
import org.kie.workbench.common.forms.editor.client.editor.changes.displayers.conflicts.ConflictsDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.changes.displayers.newProperties.NewPropertiesDisplayer;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.impl.FormModelSynchronizationResultImpl;
import org.kie.workbench.common.forms.editor.model.impl.TypeConflictImpl;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangesNotificationDisplayerTest {

    @Mock
    private HTMLElement htmlElement;

    @Mock
    private ChangesNotificationDisplayerView view;

    @Mock
    private NewPropertiesDisplayer newPropertiesDisplayer;

    @Mock
    private ConflictsDisplayer conflictsDisplayer;

    @Mock
    private FormModelSynchronizationUtil formModelSynchronizationUtil;

    @Mock
    private Command command;

    @Mock
    private ManagedInstance managedInstance;

    @Mock
    private ConflictElement conflictElement;

    @Mock
    private TranslationService translationService;

    @Mock
    private FormEditorRenderingContext context;

    @Mock
    private Map contextForms;

    private NestedFormsConflictHandler nestedFormsConflictHandler;

    private FormModelConflictHandler formModelConflictHandler;

    private FormModelSynchronizationResultImpl synchronizationResult = new FormModelSynchronizationResultImpl();

    private FormModelerContent content = new FormModelerContent();

    private FormDefinition form = new FormDefinition();

    private List<FieldDefinition> fields;

    private ChangesNotificationDisplayer presenter;

    @Before
    public void init() {

        when(view.getElement()).thenReturn(htmlElement);

        content.setSynchronizationResult(synchronizationResult);

        content.setDefinition(form);

        content.setRenderingContext(context);

        when(context.getAvailableForms()).thenReturn(contextForms);

        when(contextForms.get(any())).thenReturn(mock(FormDefinition.class));

        fields = ModelChangeDisplayerTestFieldProvider.getFields();

        when(managedInstance.get()).thenReturn(conflictElement);

        nestedFormsConflictHandler = spy(new NestedFormsConflictHandler(managedInstance,
                                                                        translationService));

        formModelConflictHandler = spy(new FormModelConflictHandler(formModelSynchronizationUtil,
                                                                    managedInstance,
                                                                    translationService));

        presenter = new ChangesNotificationDisplayer(view,
                                                     conflictsDisplayer,
                                                     newPropertiesDisplayer,
                                                     new TestFieldManager()) {
            {
                register(nestedFormsConflictHandler);
                register(formModelConflictHandler);
            }
        };

        verify(view).init(presenter);
    }

    @Test
    public void testShowEmptySynchronization() {
        presenter.show(content,
                       command);

        verify(view, never()).show();
    }

    @Test
    public void testShowNewFields() {

        synchronizationResult.getNewProperties().addAll(getModelProperties());

        presenter.show(content,
                       command);

        verify(nestedFormsConflictHandler).checkConflicts(any(),
                                                          any());

        verify(formModelConflictHandler).checkConflicts(any(),
                                                        any());

        verify(newPropertiesDisplayer).showAvailableFields(any());

        verify(conflictsDisplayer,
               never()).showConflict(any());

        verify(view).getElement();

        verify(htmlElement).appendChild(any());

        verify(view).show();

        presenter.close();

        verify(nestedFormsConflictHandler,
               never()).onAccept();
        verify(formModelConflictHandler,
               never()).onAccept();
        verify(formModelSynchronizationUtil,
               never()).fixRemovedFields();
        verify(formModelSynchronizationUtil,
               never()).resolveConflicts();
    }

    @Test
    public void testShowConflicts() {
        synchronizationResult.getConflicts().putAll(getConflicts());

        form.getFields().addAll(fields);

        presenter.show(content,
                       command);

        verify(newPropertiesDisplayer,
               never()).showAvailableFields(anyList());

        verify(view).getElement();

        verify(htmlElement).appendChild(any());

        verify(view).show();

        presenter.close();

        verify(nestedFormsConflictHandler,
               never()).onAccept();
        verify(formModelConflictHandler).onAccept();
        verify(formModelSynchronizationUtil,
               never()).fixRemovedFields();
        verify(formModelSynchronizationUtil).resolveConflicts();
    }

    @Test
    public void testShowRemovedFields() {
        synchronizationResult.getRemovedProperties().addAll(getModelProperties());

        form.getFields().addAll(fields);

        presenter.show(content,
                       command);

        verify(nestedFormsConflictHandler).checkConflicts(any(),
                                                          any());

        verify(formModelConflictHandler).checkConflicts(any(),
                                                        any());

        verify(newPropertiesDisplayer,
               never()).showAvailableFields(anyList());

        verify(conflictsDisplayer,
               times(fields.size())).showConflict(any());

        verify(htmlElement).appendChild(any());

        verify(view).show();

        presenter.close();

        verify(nestedFormsConflictHandler,
               never()).onAccept();
        verify(formModelConflictHandler).onAccept();
        verify(formModelSynchronizationUtil).fixRemovedFields();
        verify(formModelSynchronizationUtil,
               never()).resolveConflicts();

        verify(conflictsDisplayer).clear();
        verify(newPropertiesDisplayer).clear();

        verify(command).execute();
    }

    @Test
    public void testNestedFormsConflicts() {

        when(contextForms.get(any())).thenReturn(null);

        form.getFields().addAll(fields);

        presenter.show(content,
                       command);

        verify(nestedFormsConflictHandler).checkConflicts(any(),
                                                          any());

        verify(formModelConflictHandler).checkConflicts(any(),
                                                        any());

        verify(newPropertiesDisplayer,
               never()).showAvailableFields(anyList());

        verify(conflictsDisplayer,
               times(3)).showConflict(any());

        verify(htmlElement).appendChild(any());

        verify(view).show();

        presenter.close();

        verify(nestedFormsConflictHandler).onAccept();
        verify(formModelConflictHandler,
               never()).onAccept();
        verify(formModelSynchronizationUtil,
               never()).fixRemovedFields();
        verify(formModelSynchronizationUtil,
               never()).resolveConflicts();

        verify(conflictsDisplayer).clear();
        verify(newPropertiesDisplayer).clear();

        verify(command).execute();
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
                                                                            new TypeInfoImpl(fieldDefinition instanceof EntityRelationField ? TypeKind.OBJECT : TypeKind.BASE, fieldDefinition.getStandaloneClassName(), fieldDefinition instanceof MultipleSubFormFieldDefinition))).collect(Collectors.toList());
    }
}
