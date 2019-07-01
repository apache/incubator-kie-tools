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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.editor.changes.ChangesNotificationDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.errorMessage.ErrorMessageDisplayer;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
import org.kie.workbench.common.forms.editor.client.editor.groupProviders.FormEditorFieldGroupsProvider;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.model.FormModelerContentError;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.kie.workbench.common.workbench.client.events.LayoutEditorFocusEvent;
import org.kie.workbench.common.workbench.client.events.LayoutEditorLostFocusEvent;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
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
@WorkbenchEditor(identifier = FormEditorPresenter.ID, supportedTypes = {FormDefinitionResourceType.class})
public class FormEditorPresenter extends KieEditor<FormModelerContent> {

    public static final String ID = "FormEditor";

    @Inject
    protected LayoutEditor layoutEditor;
    @Inject
    protected HTMLLayoutDragComponent htmlLayoutDragComponent;
    @Inject
    protected BusyIndicatorView busyIndicatorView;
    @Inject
    protected FormEditorContext formEditorContext;
    @Inject
    protected FormEditorHelper editorHelper;
    protected ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;
    @Inject
    private Caller<MetadataService> metadataService;
    @Inject
    protected LayoutDragComponentPalette layoutDragComponentPalette;
    @Inject
    protected Event<LayoutEditorFocusEvent> layoutFocusEvent;
    @Inject
    protected Event<LayoutEditorLostFocusEvent> layoutLostFocusEvent;
    
    private ShowAssetUsagesDisplayer showAssetUsagesDisplayer;
    private FormEditorView view;
    private ChangesNotificationDisplayer changesNotificationDisplayer;
    private FormDefinitionResourceType resourceType;
    private Caller<FormEditorService> editorService;
    private TranslationService translationService;
    private ErrorMessageDisplayer errorMessageDisplayer;
    private LayoutEditorPropertiesPresenter layoutEditorPropertiesPresenter;

    protected boolean setActiveOnLoad = false;

    @Inject
    public FormEditorPresenter(FormEditorView view,
                               ChangesNotificationDisplayer changesNotificationDisplayer,
                               FormDefinitionResourceType resourceType,
                               Caller<FormEditorService> editorService,
                               TranslationService translationService,
                               ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents,
                               ShowAssetUsagesDisplayer showAssetUsagesDisplayer,
                               ErrorMessageDisplayer errorMessageDisplayer,
                               LayoutEditorPropertiesPresenter layoutEditorPropertiesPresenter) {
        super(view);
        this.view = view;
        this.changesNotificationDisplayer = changesNotificationDisplayer;
        this.resourceType = resourceType;
        this.editorService = editorService;
        this.translationService = translationService;
        this.editorFieldLayoutComponents = editorFieldLayoutComponents;
        this.showAssetUsagesDisplayer = showAssetUsagesDisplayer;
        this.errorMessageDisplayer = errorMessageDisplayer;
        this.layoutEditorPropertiesPresenter = layoutEditorPropertiesPresenter;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {

        init(path,
             place,
             resourceType);
        layoutEditorPropertiesPresenter.edit(layoutEditor);
    }

    @OnFocus
    public void onFocus() {
        if (editorHelper.getContent() == null) {
            this.setActiveOnLoad = true;
        } else {
            setActiveInstance();
        }
    }
    
    @Override
    public void hideDocks() {
        super.hideDocks();
        layoutLostFocusEvent.fire(new LayoutEditorLostFocusEvent(ID));
    }
    
    @Override
    protected String getEditorIdentifier() {
        return ID;
    }

    private void setActiveInstance() {
        formEditorContext.setActiveEditorHelper(editorHelper);

        initLayoutDragComponentPalette();

        layoutFocusEvent.fire(new LayoutEditorFocusEvent(ID));
    }

    @Override
    protected void loadContent() {
        editorService.call((RemoteCallback<FormModelerContent>) content -> doLoadContent(content),
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

    public void doLoadContent(final FormModelerContent content) {
        busyIndicatorView.hideBusyIndicator();

        // Clear LayoutEditor before loading new content.
        if (editorHelper.getContent() != null) {
            layoutEditor.clear();
        }

        editorHelper.initHelper(content);

        resetEditorPages(content.getOverview());

        view.init(this);

        if (content.getError() != null) {
            FormModelerContentError error = content.getError();

            errorMessageDisplayer.show(error, () -> placeManager.forceClosePlace(place));

            errorMessageDisplayer.enableContinue(content.getDefinition() != null);
        }

        if (content.getDefinition() != null) {
            loadEditor(content);
        }
    }

    private void loadEditor(FormModelerContent content) {
        if (content.getDefinition() != null) {
            if (content.getDefinition().getLayoutTemplate() == null) {
                content.getDefinition().setLayoutTemplate(new LayoutTemplate());
            }

            setOriginalHash(content.getDefinition().hashCode());

            loadLayoutEditor();

            view.setupLayoutEditor(layoutEditor);

            changesNotificationDisplayer.show(content,
                                              this::synchronizeLayoutEditor);
        }

        if (setActiveOnLoad) {
            setActiveInstance();
            setActiveOnLoad = false;
        }
    }

    protected void loadLayoutEditor() {
        layoutEditor.clear();

        layoutEditor.init(editorHelper.getContent().getDefinition().getName(),
                          translationService.getTranslation(FormEditorConstants.FormEditorPresenterLayoutTitle),
                          translationService.getTranslation(FormEditorConstants.FormEditorPresenterLayoutSubTitle),
                          LayoutTemplate.Style.FLUID);

        layoutEditor.loadLayout(editorHelper.getContent().getDefinition().getLayoutTemplate());
        layoutEditor.setElementSelectionEnabled(true);
    }

    protected void synchronizeLayoutEditor() {
        if (editorHelper.getContent().getSynchronizationResult().hasConflicts()) {
            loadLayoutEditor();
        }
    }

    protected void initLayoutDragComponentPalette() {
        layoutDragComponentPalette.clear();

        loadAvailableFields();
        loadFormControls();
    }

    protected void loadFormControls() {
        String groupName = translationService.getTranslation(FormEditorConstants.FormEditorPresenterComponentsPalette);

        List<LayoutDragComponent> components = new ArrayList<>();

        components.add(htmlLayoutDragComponent);

        editorHelper.getBaseFieldsDraggables().forEach(components::add);

        layoutDragComponentPalette.addDraggableGroup(new FormEditorFieldGroupsProvider(groupName, components));
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
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @Override
    protected Promise<Void> makeMenuBar() {
        if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
            final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
            return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (canUpdateProject) {
                    fileMenuBuilder
                            .addSave(versionRecordManager.newSaveMenuItem(() -> saveAction()))
                            .addCopy(this::safeCopy)
                            .addRename(this::safeRename)
                            .addDelete(this::safeDelete);
                }
                addDownloadMenuItem(fileMenuBuilder);

                fileMenuBuilder
                        .addNewTopLevelMenu(versionRecordManager.buildMenu())
                        .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
                        /*.addCommand( "PREVIEW",
                                     () -> {
                                         synchronizeFormLayout();
                                         IOC.getBeanManager().lookupBean( PreviewFormPresenter.class ).newInstance().preview( getRenderingContext() );
                                     } )*/

                return promises.resolve();
            });
        }

        return promises.resolve();
    }

    protected void safeCopy() {
        if (this.isDirty(editorHelper.getContent().getDefinition().hashCode())) {

            view.showSavePopup(versionRecordManager.getCurrentPath(),
                               () -> copy(true),
                               () -> copy(false));
        } else {
            copy(false);
        }
    }

    public void copy(boolean save) {
        if (save) {
            synchronizeFormLayout();
        }
        copyPopUpPresenter.show(versionRecordManager.getPathToLatest(), assetUpdateValidator, getCopyCommand(save));
    }

    protected CommandWithFileNameAndCommitMessage getCopyCommand(boolean save) {
        return details -> copyCommand(details, save);
    }

    protected void copyCommand(FileNameAndCommitMessage details, boolean save) {
        view.showBusyIndicator(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Copying());

        editorService.call(getCopySuccessCallback(copyPopUpPresenter.getView()),
                           getCopyErrorCallback(copyPopUpPresenter.getView())).copy(versionRecordManager.getPathToLatest(),
                                                                                    details.getNewFileName(),
                                                                                    details.getCommitMessage(),
                                                                                    save,
                                                                                    editorHelper.getContent(),
                                                                                    metadata);
    }

    private RemoteCallback<Path> getCopySuccessCallback(final CopyPopUpPresenter.View copyPopupView) {
        return response -> {
            copyPopupView.hide();
            view.hideBusyIndicator();
            notification.fire(new NotificationEvent(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemCopiedSuccessfully(),
                                                    NotificationEvent.NotificationType.SUCCESS));
        };
    }

    protected DefaultErrorCallback getCopyErrorCallback(final CopyPopUpPresenter.View copyPopupView) {
        return new DefaultErrorCallback() {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                copyPopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
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
        return details -> renameCommand(details, save);
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
        String groupName = translationService.getTranslation(FormEditorConstants.FormEditorPresenterModelFields);

        List<LayoutDragComponent> fieldComponents = editorHelper.getAvailableFields().values().stream()
                .map(fieldDefinition -> {
                    EditorFieldLayoutComponent layoutFieldComponent = editorFieldLayoutComponents.get();
                    if (layoutFieldComponent != null) {
                        layoutFieldComponent.init(editorHelper.getRenderingContext(), fieldDefinition);
                        return layoutFieldComponent;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        layoutDragComponentPalette.addDraggableGroup(new FormEditorFieldGroupsProvider(groupName, fieldComponents));
    }

    public void onRemoveComponent(@Observes ComponentRemovedEvent event) {
        if (editorHelper == null || editorHelper.getContent() == null || event.getLayoutComponent() == null) {
            return;
        }

        String formId = event.getLayoutComponent().getProperties().get(FieldLayoutComponent.FORM_ID);

        if (editorHelper.getFormDefinition().getId().equals(formId)) {
            String fieldId = event.getLayoutComponent().getProperties().get(FieldLayoutComponent.FIELD_ID);

            // If the event is caused by a element move we must hold the field on the form.
            // If not it means that it should be removed.
            if (!event.getFromMove()) {
                editorHelper.removeField(fieldId,
                                         true);
                onSyncPalette(formId);
            }
        }
    }
    
    public void onLayoutEditorElementSelectEvent(@Observes LayoutEditorElementSelectEvent event) {
        LayoutEditorElement element = event.getElement();
        if (element instanceof ComponentColumn) {
            ComponentColumn componentColumn = (ComponentColumn) element;
            LayoutComponent layoutComponent = componentColumn.getLayoutComponent();
            LayoutDragComponent layoutDragComponent = componentColumn.getLayoutDragComponent();
            boolean containsElement = getFormDefinition().getLayoutTemplate().contains(layoutComponent);
            if (layoutDragComponent instanceof EditorFieldLayoutComponent && containsElement) {
                ((EditorFieldLayoutComponent) layoutDragComponent).addComponentParts(layoutComponent);
            }
            componentColumn.setupParts();
        }
    }
    
    protected void removeAllDraggableGroupComponent(Collection<FieldDefinition> fields) {
        String groupId = translationService.getTranslation(FormEditorConstants.FormEditorPresenterModelFields);
        Iterator<FieldDefinition> it = fields.iterator();
        while (it.hasNext()) {
            FieldDefinition field = it.next();
            if (layoutDragComponentPalette.hasDraggableComponent(groupId,
                                                                 field.getId())) {
                layoutDragComponentPalette.removeDraggableComponent(groupId,
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
                layoutDragComponentPalette.addDraggableComponent(groupId,
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

        void init(FormEditorPresenter presenter);

        void setupLayoutEditor(LayoutEditor layoutEditor);

        void showSavePopup(Path path,
                           Command saveCommand,
                           Command cancelCommand);
    }
}
