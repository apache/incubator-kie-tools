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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.verification.VerificationMode;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormEditorPresenterTest extends FormEditorPresenterAbstractTest {

    @Test
    public void testLoad() {
        loadContent();

        verify(layoutEditorMock).loadLayout(content.getDefinition().getLayoutTemplate());
        verify(view).init(presenter);
        verify(view).setupLayoutEditor(layoutEditorMock);
    }

    @Test
    public void testLoadWithContent() {
        testLoad();

        reset(layoutDragComponentPaletteMock);
        FormEditorPresenter presenterSpy = spy(presenter);
        presenterSpy.setActiveOnLoad = true;
        presenterSpy.loadContent();

        verify(presenterSpy).loadAvailableFields();
        verify(layoutDragComponentPaletteMock,
               times(2)).addDraggableGroup(any());

        verify(layoutEditorMock,
               times(3)).clear();
        verify(layoutEditorMock,
               times(2)).loadLayout(content.getDefinition().getLayoutTemplate());
        verify(view,
               times(2)).init(any());
        verify(view,
               times(2)).setupLayoutEditor(layoutEditorMock);
    }

    @Test
    public void testMayClose() {
        testLoad();

        assertTrue(presenter.onMayClose());
        verify(view,
               never()).confirmClose();

        testOnRemoveComponentWithContext();

        assertFalse(presenter.onMayClose());
    }

    @Test
    public void testDataObjectsFields() {
        loadContent();

        testAddRemoveDataTypeFields();

        testDataTypeFieldProperties();
    }

    public void testAddRemoveDataTypeFields() {
        testAddFields(true);
        testRemoveFields(true);
    }

    protected void testAddFields(boolean checkAvailable) {
        int formFields = editorHelper.getFormDefinition().getFields().size();
        int availableFields = editorHelper.getAvailableFields().size();

        presenter.onSyncPalette(presenter.getFormDefinition().getId());
        for (FieldDefinition field : employeeFields) {
            addField(field);

            availableFields--;
            formFields++;
            checkExpectedFields(availableFields,
                                formFields,
                                checkAvailable);
        }
    }

    protected void testRemoveFields(boolean checkAvailable) {
        int formFields = editorHelper.getFormDefinition().getFields().size();

        assertTrue("Form should have fields.",
                   formFields > 0);
        assertEquals("Form should contain '" + employeeFields.size() + "' fields.",
                     formFields,
                     employeeFields.size());

        int availableFields = editorHelper.getAvailableFields().size();
        assertTrue("There should not exist available fields.",
                   availableFields == 0);

        List<FieldDefinition> formFieldsList = new ArrayList<>(editorHelper.getFormDefinition().getFields());

        for (FieldDefinition field : formFieldsList) {
            presenter.onRemoveComponent(createComponentRemovedEvent(editorHelper.getFormDefinition(),
                                                                    field));
            availableFields++;
            formFields--;
            checkExpectedFields(availableFields,
                                formFields,
                                checkAvailable);
        }
    }

    public void testDataTypeFieldProperties() {
        testFieldProperties("name",
                            true);
    }

    public void testUnboundFieldProperties() {

        testFieldProperties(TextBoxFieldDefinition.FIELD_TYPE.getTypeName(),
                            false);
    }

    protected void testFieldProperties(String fieldId,
                                       boolean bound) {

        FormDefinition form = editorHelper.getFormDefinition();

        addField(editorHelper.getAvailableFields().values().stream().filter(fieldDefinition -> fieldDefinition.getBinding().equals(fieldId)).findFirst().get());

        checkExpectedFields(editorHelper.getAvailableFields().size(),
                            1,
                            bound);

        FieldDefinition field = editorHelper.getFormDefinition().getFields().get(0);

        checkFieldType(field,
                       TextBoxFieldDefinition.class);

        Collection<String> compatibleTypes = editorHelper.getCompatibleFieldTypes(field);

        assertNotNull("No compatibles types found!",
                      compatibleTypes);
        assertTrue("There should exist more than one compatible types for TextBoxFieldDefinition!",
                   compatibleTypes.size() > 1);
        assertTrue("Missing TextAreaFieldDefinition as a compatible type for TextBoxFieldDefinition",
                   compatibleTypes.contains(TextAreaFieldDefinition.FIELD_TYPE.getTypeName()));

        field = editorHelper.switchToFieldType(field,
                                               TextAreaFieldDefinition.FIELD_TYPE.getTypeName());
        checkFieldType(field,
                       TextAreaFieldDefinition.class);

        List<String> compatibleFields = editorHelper.getCompatibleModelFields(field);

        assertNotNull("No compatibles fields found!",
                      compatibleFields);

        assertEquals("There should exist 1 compatible fields for " + field.getName() + "!",
                     1,
                     compatibleFields.size());

        String expectedBindingExpression = "lastName";

        field = editorHelper.switchToField(field,
                                           expectedBindingExpression);

        assertEquals("Wrong binding expression after switch field!",
                     field.getBinding(),
                     expectedBindingExpression);
    }

    protected ComponentRemovedEvent createComponentRemovedEvent(FormDefinition form,
                                                                FieldDefinition field) {

        return new ComponentRemovedEvent(createLayoutComponent(form,
                                                               field),
                                         false);
    }

    protected LayoutComponent createLayoutComponent(FormDefinition form,
                                                    FieldDefinition field) {
        LayoutComponent component = new LayoutComponent("");
        component.addProperty(FieldLayoutComponent.FORM_ID,
                              form.getId());
        component.addProperty(FieldLayoutComponent.FIELD_ID,
                              field.getId());
        return component;
    }

    protected void checkFieldType(FieldDefinition field,
                                  Class<? extends FieldDefinition> type) {
        assertTrue("Field " + field.getName() + " should be of type " + type.getClass().getName(),
                   field.getClass() == type);
    }

    protected void checkExpectedFields(int expectedAvailable,
                                       int expectedFormFields,
                                       boolean checkAvailable) {
        if (checkAvailable) {
            assertEquals("There should be " + expectedAvailable + " available fields",
                         expectedAvailable,
                         editorHelper.getAvailableFields().size());
        }
        assertEquals("The form must contain " + expectedFormFields + " fields ",
                     expectedFormFields,
                     editorHelper.getFormDefinition().getFields().size());
    }

    @Test
    public void testOnSyncPaletteEventHandler() {
        loadContent();
        FormEditorPresenter presenterSpy = spy(presenter);
        String formId = presenterSpy.getFormDefinition().getId();
        FormEditorSyncPaletteEvent event = new FormEditorSyncPaletteEvent(formId);
        presenterSpy.onSyncPalette(event);
        verify(presenterSpy).onSyncPalette(formId);
    }

    @Test
    public void testOnSyncPaletteWithContext() {
        testOnSyncPalette(false);
    }

    @Test
    public void testOnSyncPaletteNoContext() {
        testOnSyncPalette(true);
    }

    private void testOnSyncPalette(boolean noContext) {

        loadContent();

        VerificationMode count = times(1);
        if (noContext) {
            when(editorHelper.getContent()).thenReturn(null);
            count = never();
        }
        FormEditorPresenter presenterSpy = spy(presenter);
        String formId = presenterSpy.getFormDefinition().getId();
        presenterSpy.onSyncPalette(formId);

        Collection<FieldDefinition> availableFieldsValues = editorHelper.getAvailableFields().values();

        verify(presenterSpy,
               count).removeAllDraggableGroupComponent(presenter.getFormDefinition().getFields());
        verify(presenterSpy,
               count).removeAllDraggableGroupComponent(availableFieldsValues);
        verify(presenterSpy,
               count).addAllDraggableGroupComponent(availableFieldsValues);
    }

    @Test
    public void testRemoveAllDraggableGroupComponent() {
        loadContent();
        addAllFields();
        when(layoutDragComponentPaletteMock.hasDraggableComponent(anyString(),
                                                                  anyString())).thenReturn(true);
        List<FieldDefinition> fieldList = presenter.getFormDefinition().getFields();

        presenter.removeAllDraggableGroupComponent(fieldList);

        verify(translationService,
               times(1)).getTranslation(FormEditorConstants.FormEditorPresenterModelFields);

        verify(layoutDragComponentPaletteMock,
               times(fieldList.size())).removeDraggableComponent(anyString(),
                                                                 anyString());
    }

    @Test
    public void testAddAllDraggableGroupComponent() {
        loadContent();

        List<FieldDefinition> fieldList = presenter.getFormDefinition().getFields();
        presenter.addAllDraggableGroupComponent(fieldList);
        verify(layoutDragComponentPaletteMock,
               times(fieldList.size())).addDraggableComponent(anyString(),
                                                              anyString(),
                                                              any());
    }

    @Test
    public void testOnRemoveComponentWithContext() {
        testOnRemoveComponent(false);
    }

    @Test
    public void testOnRemoveComponentWithoutContext() {
        testOnRemoveComponent(true);
    }

    public void testOnRemoveComponent(boolean noContext) {
        loadContent();
        loadAvailableFields();
        addAllFields();
        VerificationMode count = times(1);
        if (noContext) {
            when(editorHelper.getContent()).thenReturn(null);
            count = never();
        }
        FormEditorPresenter presenterSpy = spy(presenter);
        String formId = presenterSpy.getFormDefinition().getId();
        FieldDefinition field = editorHelper.getFormDefinition().getFields().get(0);

        ComponentRemovedEvent event = new ComponentRemovedEvent(createLayoutComponent(presenter.getFormDefinition(),
                                                                                      field),
                                                                false);
        presenterSpy.onRemoveComponent(event);

        verify(presenterSpy,
               count).onSyncPalette(formId);
        verify(editorHelper,
               count).removeField(anyString(),
                                  anyBoolean());
    }

    @Test
    public void testOnRemoveComponentWithoutLayoutComponent() {
        loadContent();
        loadAvailableFields();
        addAllFields();

        FormEditorPresenter presenterSpy = spy(presenter);

        ComponentRemovedEvent event = new ComponentRemovedEvent(null);
        presenterSpy.onRemoveComponent(event);

        verify(presenterSpy,
               never()).onSyncPalette(anyString());
        verify(editorHelper,
               never()).removeField(anyString(),
                                    anyBoolean());
    }

    @Test
    public void testRemoveEventWhenMovingFieldOnLayout() {
        loadContent();
        loadAvailableFields();
        addAllFields();

        FormEditorPresenter presenterSpy = spy(presenter);

        FieldDefinition field = editorHelper.getFormDefinition().getFields().get(0);

        ComponentRemovedEvent event = new ComponentRemovedEvent(createLayoutComponent(presenter.getFormDefinition(),
                                                                                      field),
                                                                true);
        presenterSpy.onRemoveComponent(event);

        assertNotNull(editorHelper.getFormDefinition().getFieldById(field.getId()));

        verify(presenterSpy,
               never()).onSyncPalette(anyString());
        verify(editorHelper,
               never()).removeField(anyString(),
                                    anyBoolean());
    }

    @Test
    public void testDestroy() {
        loadContent();
        presenter.destroy();
        verify(editorFieldLayoutComponents).destroyAll();
    }

    @Test
    public void testLoadAvailableFieldsNoContent() {
        loadContent();

        presenter.doLoadContent(content);
    }

    @Test
    public void testGetFormTemplate() {
        loadContent();

        presenter.getFormTemplate();

        verify(layoutEditorMock).getLayout();
    }

    @Test
    public void testSave() {
        loadContent();
        presenter.editorHelper.getContent().getDefinition().setLayoutTemplate(mock(LayoutTemplate.class));
        presenter.save("");
    }

    @Test
    public void testGetTitleText() {
        loadContent();
        presenter.getTitleText();
        verify(translationService).format(anyString(),
                                          any());
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        loadContent();

        verify(menuBuilderMock).addSave(any(MenuItem.class));
        verify(menuBuilderMock).addCopy(any(Command.class));
        verify(menuBuilderMock).addRename(any(Command.class));
        verify(menuBuilderMock).addDelete(any(Command.class));
        verify(menuBuilderMock).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(menuBuilderMock).addNewTopLevelMenu(downloadMenuItem);

        presenter.getMenus(Assert::assertNotNull);
        verify(menuBuilderMock,
               atLeastOnce()).build();
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectController).canUpdateProject(any());

        loadContent();

        verify(menuBuilderMock,
               never()).addSave(any(MenuItem.class));
        verify(menuBuilderMock,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(menuBuilderMock,
               never()).addRename(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(menuBuilderMock,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(menuBuilderMock).addNewTopLevelMenu(alertsButtonMenuItem);

        presenter.getMenus(Assert::assertNotNull);
        verify(menuBuilderMock,
               atLeastOnce()).build();
    }

    @Test
    public void testSafeDeleteWithoutUsages() {
        loadContent();

        presenter.safeDelete();

        verify(showAssetUsagesDisplayer).showAssetUsages(anyString(),
                                                         any(),
                                                         any(),
                                                         any(),
                                                         any(),
                                                         any());

        verify(deletePopUpPresenter).show(any(AssetUpdateValidator.class),
                                          any());

        deletePopUpPresenter.delete();

        verify(formEditorService).delete(any(),
                                         any());
        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSafeDeleteWithUsages() {
        loadContent();

        assetUsages.add(mock(Path.class));

        presenter.safeDelete();

        verify(showAssetUsagesDisplayer).showAssetUsages(anyString(),
                                                         any(),
                                                         any(),
                                                         any(),
                                                         any(),
                                                         any());

        verify(deletePopUpPresenter,
               never()).show(any());

        showAssetUsagesDisplayer.onOk();
        showAssetUsagesDisplayer.onClose();

        verify(deletePopUpPresenter).show(any(AssetUpdateValidator.class),
                                          any());

        deletePopUpPresenter.delete();

        verify(formEditorService).delete(any(),
                                         any());
        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSafeRenameDirtySaving() {
        FormEditorPresenter presenterSpy = triggerSafeRename(true,
                                                             true);
        verify(view).showSavePopup(any(Path.class),
                                   any(Command.class),
                                   any(Command.class));
        verify(presenterSpy).rename(eq(true));
    }

    @Test
    public void testSafeRenameDirtyNotSaving() {
        FormEditorPresenter presenterSpy = triggerSafeRename(true,
                                                             false);

        verify(view).showSavePopup(any(Path.class),
                                   any(Command.class),
                                   any(Command.class));
        verify(presenterSpy).rename(eq(false));
    }

    @Test
    public void testSafeRenameNotDirty() {
        FormEditorPresenter presenterSpy = triggerSafeRename(false,
                                                             false);

        verify(view,
               never()).showSavePopup(any(Path.class),
                                      any(Command.class),
                                      any(Command.class));
        verify(presenterSpy).rename(eq(false));
    }

    private FormEditorPresenter triggerSafeRename(boolean dirty,
                                                  boolean saving) {
        loadContent();
        FormEditorPresenter presenterSpy = spy(presenter);
        doNothing().when(presenterSpy).rename(anyBoolean());
        doReturn(dirty).when(presenterSpy).isDirty(anyInt());

        presenterSpy.safeRename();

        if (dirty) {
            presenterSpy.rename(saving);
        }
        return presenterSpy;
    }

    @Test
    public void testSafeCopyDirtySaving() {
        FormEditorPresenter presenterSpy = triggerSafeCopy(true, true);

        verify(view).showSavePopup(any(Path.class), any(Command.class), any(Command.class));

        verify(presenterSpy).copy(eq(true));
    }

    @Test
    public void testSafeCopyDirtyNotSaving() {
        FormEditorPresenter presenterSpy = triggerSafeCopy(true, false);

        verify(view).showSavePopup(any(Path.class), any(Command.class), any(Command.class));

        verify(presenterSpy).copy(eq(false));
    }

    @Test
    public void testSafeCopyNotDirty() {
        FormEditorPresenter presenterSpy = triggerSafeCopy(false, false);

        verify(view, never()).showSavePopup(any(Path.class), any(Command.class), any(Command.class));

        verify(presenterSpy).copy(eq(false));
    }

    private FormEditorPresenter triggerSafeCopy(boolean dirty, boolean saving) {
        loadContent();

        FormEditorPresenter presenterSpy = spy(presenter);

        doNothing().when(presenterSpy).copy(anyBoolean());

        doReturn(dirty).when(presenterSpy).isDirty(anyInt());

        presenterSpy.safeCopy();

        if (dirty) {
            presenterSpy.copy(saving);
        }
        return presenterSpy;
    }

    @Test
    public void testRenameSaving() {
        testRename(true);
    }

    @Test
    public void testRenameNotSaving() {
        testRename(false);
    }

    private void testRename(boolean saving) {
        loadContent();
        presenter.rename(saving);
        verify(renamePopUpPresenter).show(any(Path.class),
                                          any(DefaultFileNameValidator.class),
                                          any(CommandWithFileNameAndCommitMessage.class));
    }

    @Test
    public void testRenameCommand() {
        loadContent();
        FormEditorPresenter presenterSpy = spy(presenter);
        FileNameAndCommitMessage details = mock(FileNameAndCommitMessage.class);
        when(renamePopUpPresenter.getView()).thenReturn(mock(RenamePopUpPresenter.View.class));
        doNothing().when(presenterSpy).doLoadContent(any(FormModelerContent.class));

        presenterSpy.renameCommand(details,
                                   true);

        verify(view).showBusyIndicator(anyString());
        verify(presenterSpy).getRenameErrorCallback(any(RenamePopUpPresenter.View.class));
        verify(presenterSpy).getRenameSuccessCallback(any(RenamePopUpPresenter.View.class));
    }
}
