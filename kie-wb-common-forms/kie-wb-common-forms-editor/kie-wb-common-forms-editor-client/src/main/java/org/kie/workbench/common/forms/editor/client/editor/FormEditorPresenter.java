/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor;

import java.util.List;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.forms.model.DefaultFormModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "FormEditor", supportedTypes = {FormDefinitionResourceType.class})
public class FormEditorPresenter extends KieEditor {

    public interface FormEditorView extends KieEditorView {

        public void init(FormEditorPresenter presenter);

        public void setupLayoutEditor(LayoutEditor layoutEditor);
    }

    @Inject
    protected LayoutEditor layoutEditor;

    @Inject
    protected HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    protected BusyIndicatorView busyIndicatorView;

    @Inject
    protected FormEditorHelper editorContext;

    protected ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    private FormEditorView view;
    private FormDefinitionResourceType resourceType;
    private Caller<FormEditorService> editorService;
    private TranslationService translationService;

    @Inject
    public FormEditorPresenter(FormEditorView view,
                               FormDefinitionResourceType resourceType,
                               Caller<FormEditorService> editorService,
                               TranslationService translationService,
                               ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents) {
        super(view);
        this.view = view;
        this.resourceType = resourceType;
        this.editorService = editorService;
        this.translationService = translationService;
        this.editorFieldLayoutComponents = editorFieldLayoutComponents;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {

        init(path,
             place,
             resourceType);
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
        editorService.call(getSaveSuccessCallback(editorContext.getContent().getDefinition().hashCode()))
                .save(versionRecordManager.getCurrentPath(),
                      editorContext.getContent(),
                      metadata,
                      commitMessage);
    }

    protected void synchronizeFormLayout() {
        editorContext.getFormDefinition().setLayoutTemplate(layoutEditor.getLayout());
    }

    public void doLoadContent(FormModelerContent content) {
        busyIndicatorView.hideBusyIndicator();

        // Clear LayoutEditor before loading new content.
        if (editorContext.getContent() != null) {
            layoutEditor.clear();
        }

        editorContext.initHelper(content);

        layoutEditor.init(content.getDefinition().getName(),
                          getLayoutComponent(),
                          translationService
                                  .getTranslation(FormEditorConstants.FormEditorPresenterLayoutTitle),
                          translationService
                                  .getTranslation(FormEditorConstants.FormEditorPresenterLayoutSubTitle));

        if (content.getDefinition().getLayoutTemplate() == null) {
            content.getDefinition().setLayoutTemplate(new LayoutTemplate());
        }

        loadAvailableFields(content);

        layoutEditor.loadLayout(content.getDefinition().getLayoutTemplate());

        resetEditorPages(content.getOverview());

        setOriginalHash(content.getDefinition().hashCode());

        view.init(this);

        view.setupLayoutEditor(layoutEditor);
    }

    protected LayoutDragComponentGroup getLayoutComponent() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup("Tools");
        group.addLayoutDragComponent("id",
                                     htmlLayoutDragComponent);
        for (EditorFieldLayoutComponent drag : editorContext.getBaseFieldsDraggables()) {
            group.addLayoutDragComponent(drag.getFieldId(),
                                         drag);
        }
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
        menus = menuBuilder
                .addSave(versionRecordManager.newSaveMenuItem(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                }))
                .addCopy(versionRecordManager.getCurrentPath(),
                         fileNameValidator)
                .addRename(versionRecordManager.getPathToLatest(),
                           fileNameValidator)
                .addDelete(versionRecordManager.getPathToLatest())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                /*.addCommand( "PREVIEW",
                             () -> {
                                 synchronizeFormLayout();
                                 IOC.getBeanManager().lookupBean( PreviewFormPresenter.class ).newInstance().preview( getRenderingContext() );
                             } )*/
                .build();
    }

    public LayoutTemplate getFormTemplate() {
        return layoutEditor.getLayout();
    }

    public FormDefinition getFormDefinition() {
        return editorContext.getFormDefinition();
    }

    private void loadAvailableFields(FormModelerContent content) {
        if (content.getDefinition().getModel() instanceof DefaultFormModel || content.getAvailableFields() == null) {
            return;
        }

        for (String modelName : content.getAvailableFields().keySet()) {
            List<FieldDefinition> availableFields = content.getAvailableFields().get(modelName);
            addAvailableFields(modelName,
                               availableFields);
        }
    }

    protected void addAvailableFields(String model,
                                      List<FieldDefinition> fields) {
        editorContext.addAvailableFields(fields);

        LayoutDragComponentGroup group = new LayoutDragComponentGroup(model);

        for (FieldDefinition field : fields) {
            EditorFieldLayoutComponent layoutFieldComponent = editorFieldLayoutComponents.get();
            if (layoutFieldComponent != null) {
                layoutFieldComponent.setDisabled(true);
                layoutFieldComponent.init(editorContext.getRenderingContext(),
                                          field);
                group.addLayoutDragComponent(field.getId(),
                                             layoutFieldComponent);
            }
        }

        layoutEditor.addDraggableComponentGroup(group);
    }

    public void onDropComponent(@Observes ComponentDropEvent event) {
        if (editorContext == null || editorContext.getContent() == null) {
            return;
        }

        String formId = event.getComponent().getProperties().get(FieldLayoutComponent.FORM_ID);

        if (editorContext.getFormDefinition().getId().equals(formId)) {
            String fieldId = event.getComponent().getProperties().get(FieldLayoutComponent.FIELD_ID);

            FieldDefinition field = editorContext.getDroppedField(fieldId);
            if (field != null && editorContext.getContent().getModelProperties().contains(field.getBinding())) {
                layoutEditor.removeDraggableGroupComponent(getFormDefinition().getModel().getName(),
                                                           field.getId());
            }
        }
    }

    public void onRemoveComponent(@Observes ComponentRemovedEvent event) {
        if (editorContext == null || editorContext.getContent() == null) {
            return;
        }

        String formId = event.getLayoutComponent().getProperties().get(FieldLayoutComponent.FORM_ID);

        if (editorContext.getFormDefinition().getId().equals(formId)) {
            String fieldId = event.getLayoutComponent().getProperties().get(FieldLayoutComponent.FIELD_ID);
            FieldDefinition field = editorContext.removeField(fieldId,
                                                              true);
            if (field != null && editorContext.getContent().getModelProperties().contains(field.getBinding())) {
                EditorFieldLayoutComponent layoutFieldComponent = editorFieldLayoutComponents.get();
                if (layoutFieldComponent != null) {
                    layoutFieldComponent.setDisabled(true);
                    layoutFieldComponent.init(editorContext.getRenderingContext(),
                                              field);
                    layoutEditor
                            .addDraggableComponentToGroup(getFormDefinition().getModel().getName(),
                                                          field.getId(),
                                                          layoutFieldComponent);
                }
            }
        }
    }

    @OnMayClose
    public Boolean onMayClose() {
        return mayClose(editorContext.getContent().getDefinition().hashCode());
    }

    @PreDestroy
    public void destroy() {
        editorFieldLayoutComponents.destroyAll();
    }
}
