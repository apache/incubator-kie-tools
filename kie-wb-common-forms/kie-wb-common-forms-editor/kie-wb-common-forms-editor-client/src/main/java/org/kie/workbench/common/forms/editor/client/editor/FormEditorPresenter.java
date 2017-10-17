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

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.changes.ChangesNotificationDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "FormEditor", supportedTypes = {FormDefinitionResourceType.class})
public class FormEditorPresenter extends KieEditor {

    @Inject
    protected LayoutEditor layoutEditor;
    @Inject
    protected HTMLLayoutDragComponent htmlLayoutDragComponent;
    @Inject
    protected BusyIndicatorView busyIndicatorView;
    @Inject
    protected FormEditorHelper editorHelper;
    protected ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;
    @Inject
    private Caller<MetadataService> metadataService;
    private ShowAssetUsagesDisplayer showAssetUsagesDisplayer;
    private FormEditorView view;
    private ChangesNotificationDisplayer changesNotificationDisplayer;
    private FormDefinitionResourceType resourceType;
    private Caller<FormEditorService> editorService;
    private TranslationService translationService;

    @Inject
    public FormEditorPresenter(FormEditorView view,
                               ChangesNotificationDisplayer changesNotificationDisplayer,
                               FormDefinitionResourceType resourceType,
                               Caller<FormEditorService> editorService,
                               TranslationService translationService,
                               ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents,
                               ShowAssetUsagesDisplayer showAssetUsagesDisplayer) {
        super(view);
        this.view = view;
        this.changesNotificationDisplayer = changesNotificationDisplayer;
        this.resourceType = resourceType;
        this.editorService = editorService;
        this.translationService = translationService;
        this.editorFieldLayoutComponents = editorFieldLayoutComponents;
        this.showAssetUsagesDisplayer = showAssetUsagesDisplayer;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {

        init(path,
             place,
             resourceType);
    }

    @OnFocus
    public void onFocus() {
        FormEditorContext.get().setActiveEditorHelper(editorHelper);
    }

    @Override
    protected void loadContent() {
        editorService.call(new RemoteCallback<FormModelerContent>() {
                               @Override
                               public void callback(FormModelerContent content) {
                                   doLoadContent(content);
                               }
                           },
                           getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected void save(String commitMessage) {
        synchronizeFormLayout();
        editorService.call(getSaveSuccessCallback(editorHelper.getContent().getDefinition().hashCode()))
                .save(versionRecordManager.getCurrentPath(),
                      editorHelper.getContent(),
                      metadata,
                      commitMessage);
    }

    protected void synchronizeFormLayout() {
        editorHelper.getFormDefinition().setLayoutTemplate(layoutEditor.getLayout());
    }

    public void doLoadContent(FormModelerContent content) {
        busyIndicatorView.hideBusyIndicator();

        // Clear LayoutEditor before loading new content.
        if (editorHelper.getContent() != null) {
            layoutEditor.clear();
        }

        editorHelper.initHelper(content);

        if (content.getDefinition().getLayoutTemplate() == null) {
            content.getDefinition().setLayoutTemplate(new LayoutTemplate());
        }

        resetEditorPages(content.getOverview());

        setOriginalHash(content.getDefinition().hashCode());

        view.init(this);

        loadLayoutEditor();

        view.setupLayoutEditor(layoutEditor);

        changesNotificationDisplayer.show(content,
                                          this::synchronizeLayoutEditor);
    }

    protected void loadLayoutEditor() {
        layoutEditor.clear();

        loadAvailableFields();

        layoutEditor.init(editorHelper.getContent().getDefinition().getName(),
                          getLayoutComponent(),
                          translationService
                                  .getTranslation(FormEditorConstants.FormEditorPresenterLayoutTitle),
                          translationService
                                  .getTranslation(FormEditorConstants.FormEditorPresenterLayoutSubTitle));

        layoutEditor.loadLayout(editorHelper.getContent().getDefinition().getLayoutTemplate());
    }

    protected void synchronizeLayoutEditor() {
        if (editorHelper.getContent().getSynchronizationResult().hasConflicts()) {
            loadLayoutEditor();
        }
    }

    protected LayoutDragComponentGroup getLayoutComponent() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(translationService.getTranslation(FormEditorConstants.FormEditorPresenterComponentsPalette));
        group.addLayoutDragComponent("html",
                                     htmlLayoutDragComponent);

        editorHelper.getBaseFieldsDraggables().forEach(component -> group.addLayoutDragComponent(component.getFieldId(),
                                                                                                 component));

        return group;
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        String fileName = FileNameUtil.removeExtension(versionRecordManager.getCurrentPath(),
                                                       resourceType);
        return translationService.format(FormEditorConstants.FormEditorPresenterTitle,
                                         fileName);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if (menus == null) {
            makeMenuBar();
        }
        return menus;
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @Override
    protected void makeMenuBar() {
        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(() -> saveAction()))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             assetUpdateValidator)
                    .addRename(this::safeRename)
                    .addDelete(this::safeDelete);
        }

        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu());
                /*.addCommand( "PREVIEW",
                             () -> {
                                 synchronizeFormLayout();
                                 IOC.getBeanManager().lookupBean( PreviewFormPresenter.class ).newInstance().preview( getRenderingContext() );
                             } )*/
    }

    protected void safeRename() {

        if (this.isDirty(editorHelper.getContent().getDefinition().hashCode())) {

            view.showSavePopup(versionRecordManager.getCurrentPath(),
                               () -> rename(true),
                               () -> rename(false));
        } else {
            rename(false);
        }
    }

    public void rename(boolean save) {
        if (save) {
            synchronizeFormLayout();
        }
        renamePopUpPresenter.show(versionRecordManager.getPathToLatest(),
                                  assetUpdateValidator,
                                  getRenameCommand(save));
    }

    protected CommandWithFileNameAndCommitMessage getRenameCommand(boolean save) {
        return details -> renameCommand(details,
                                        save);
    }

    protected void renameCommand(FileNameAndCommitMessage details,
                                 boolean save) {
        view.showBusyIndicator(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Renaming());

        editorService.call(getRenameSuccessCallback(renamePopUpPresenter.getView()),
                           getRenameErrorCallback(renamePopUpPresenter.getView())).rename(versionRecordManager.getPathToLatest(),
                                                                                          details.getNewFileName(),
                                                                                          details.getCommitMessage(),
                                                                                          save,
                                                                                          editorHelper.getContent(),
                                                                                          metadata);
    }

    protected RemoteCallback<FormModelerContent> getRenameSuccessCallback(final RenamePopUpPresenter.View renamePopupView) {
        return content -> {
            renamePopupView.hide();
            view.hideBusyIndicator();
            notification.fire(new NotificationEvent(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemRenamedSuccessfully(),
                                                    NotificationEvent.NotificationType.SUCCESS));
            doLoadContent(content);
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback(final RenamePopUpPresenter.View renamePopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(view) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                renamePopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    public LayoutTemplate getFormTemplate() {
        return layoutEditor.getLayout();
    }

    public FormDefinition getFormDefinition() {
        return editorHelper.getFormDefinition();
    }

    protected void loadAvailableFields() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(translationService.getTranslation(FormEditorConstants.FormEditorPresenterModelFields));

        editorHelper.getAvailableFields().values().forEach(fieldDefinition -> {
            EditorFieldLayoutComponent layoutFieldComponent = editorFieldLayoutComponents.get();
            if (layoutFieldComponent != null) {
                layoutFieldComponent.init(editorHelper.getRenderingContext(),
                                          fieldDefinition);
                group.addLayoutDragComponent(fieldDefinition.getId(),
                                             layoutFieldComponent);
            }
        });

        layoutEditor.addDraggableComponentGroup(group);
    }

    public void onRemoveComponent(@Observes ComponentRemovedEvent event) {
        if (editorHelper == null || editorHelper.getContent() == null || event.getLayoutComponent() == null) {
            return;
        }

        String formId = event.getLayoutComponent().getProperties().get(FieldLayoutComponent.FORM_ID);

        if (editorHelper.getFormDefinition().getId().equals(formId)) {
            String fieldId = event.getLayoutComponent().getProperties().get(FieldLayoutComponent.FIELD_ID);
            editorHelper.removeField(fieldId,
                                     true);
            onSyncPalette(formId);
        }
    }

    protected void removeAllDraggableGroupComponent(Collection<FieldDefinition> fields) {
        String groupId = translationService.getTranslation(FormEditorConstants.FormEditorPresenterModelFields);
        Iterator<FieldDefinition> it = fields.iterator();
        while (it.hasNext()) {
            FieldDefinition field = it.next();
            if (layoutEditor.hasDraggableGroupComponent(groupId,
                                                        field.getId())) {
                layoutEditor.removeDraggableGroupComponent(groupId,
                                                           field.getId());
            }
        }
    }

    protected void addAllDraggableGroupComponent(Collection<FieldDefinition> fields) {
        String groupId = translationService.getTranslation(FormEditorConstants.FormEditorPresenterModelFields);

        Iterator<FieldDefinition> it = fields.iterator();
        while (it.hasNext()) {
            FieldDefinition field = it.next();
            EditorFieldLayoutComponent layoutFieldComponent = editorFieldLayoutComponents.get();

            if (layoutFieldComponent != null) {
                layoutFieldComponent.init(editorHelper.getRenderingContext(),
                                          field);
                layoutEditor.addDraggableComponentToGroup(groupId,
                                                          field.getId(),
                                                          layoutFieldComponent);
            }
        }
    }

    public void onSyncPalette(@Observes FormEditorSyncPaletteEvent event) {
        onSyncPalette(event.getFormId());
    }

    public void onSyncPalette(String formId) {
        if (editorHelper == null || editorHelper.getContent() == null) {
            return;
        }
        if (editorHelper.getFormDefinition().getId().equals(formId)) {
            removeAllDraggableGroupComponent(getFormDefinition().getFields());
            removeAllDraggableGroupComponent(editorHelper.getAvailableFields().values());
            addAllDraggableGroupComponent(editorHelper.getAvailableFields().values());
        }
    }

    public void safeDelete() {
        showAssetUsagesDisplayer.showAssetUsages(translationService.format(FormEditorConstants.FormEditorPresenterFormUsages,
                                                                           versionRecordManager.getCurrentPath().getFileName()),
                                                 versionRecordManager.getCurrentPath(),
                                                 editorHelper.getContent().getDefinition().getId(),
                                                 ResourceType.FORM,
                                                 () -> onDelete(versionRecordManager.getPathToLatest()),
                                                 () -> {
                                                 });
    }

    private void onDelete(ObservablePath pathToLatest) {
        deletePopUpPresenter.show(assetUpdateValidator,
                                  comment -> {
                                      view.showBusyIndicator(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Deleting());
                                      editorService.call(response -> {
                                          view.hideBusyIndicator();
                                          notification.fire(new NotificationEvent(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemDeletedSuccessfully(),
                                                                                  NotificationEvent.NotificationType.SUCCESS));
                                      }).delete(pathToLatest,
                                                comment);
                                  });
    }

    @OnMayClose
    public Boolean onMayClose() {
        return mayClose(editorHelper.getContent().getDefinition().hashCode());
    }

    @PreDestroy
    public void destroy() {
        editorFieldLayoutComponents.destroyAll();
    }

    public interface FormEditorView extends KieEditorView {

        public void init(FormEditorPresenter presenter);

        public void setupLayoutEditor(LayoutEditor layoutEditor);

        void showSavePopup(Path path,
                           Command saveCommand,
                           Command cancelCommand);
    }
}
