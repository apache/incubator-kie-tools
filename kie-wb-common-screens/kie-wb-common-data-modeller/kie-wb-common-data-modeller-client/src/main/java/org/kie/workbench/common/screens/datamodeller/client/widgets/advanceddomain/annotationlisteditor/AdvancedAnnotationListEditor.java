/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item.AnnotationListItem;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorPopup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorPopupView;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.Command;

@Dependent
public class AdvancedAnnotationListEditor
        implements IsWidget,
                   AdvancedAnnotationListEditorView.Presenter {

    private AdvancedAnnotationListEditorView view;

    private AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler;

    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private AdvancedAnnotationListEditorView.ValuePairChangeHandler valuePairChangeHandler;

    private AdvancedAnnotationListEditorView.AddAnnotationHandler addAnnotationHandler;

    private Caller<DataModelerService> modelerService;

    private Instance<ValuePairEditorPopup> valuePairEditorInstance;

    private Instance<AnnotationListItem> itemInstance;

    private Map<String, AnnotationSource> annotationSources;

    private List<Annotation> annotations;

    private Map<Annotation, AnnotationListItem> annotationItems = new HashMap<>();

    private Map<String, Boolean> annotationStatus = new HashMap<>();

    private KieProject project;

    private ElementType elementType;

    private boolean readonly = false;

    private Callback<Annotation> addAnnotationCallback;

    @Inject
    public AdvancedAnnotationListEditor(AdvancedAnnotationListEditorView view,
                                        Caller<DataModelerService> modelerService,
                                        Instance<ValuePairEditorPopup> valuePairEditorInstance,
                                        Instance<AnnotationListItem> itemInstance) {
        this.view = view;
        view.init(this);
        this.modelerService = modelerService;
        this.valuePairEditorInstance = valuePairEditorInstance;
        this.itemInstance = itemInstance;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init(final KieProject project,
                     final ElementType elementType) {
        this.project = project;
        this.elementType = elementType;
    }

    public void loadAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
        if (annotations != null && annotations.size() > 0) {
            AnnotationSourceRequest sourceRequest = new AnnotationSourceRequest();
            sourceRequest.withAnnotations(annotations);
            modelerService.call(getLoadAnnotationSourcesSuccessCallback())
                    .resolveSourceRequest(sourceRequest);
        }
    }

    public void loadAnnotations(List<Annotation> annotations,
                                Map<String, AnnotationSource> annotationSources) {
        view.clear();
        clearListItems();
        this.annotationSources = annotationSources;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                final Annotation currentAnnotation = annotation;
                final AnnotationListItem annotationListItem = createListItem();
                annotationListItem.loadAnnotation(annotation,
                                                  annotationSources != null ?
                                                          annotationSources.get(annotation.getClassName()) : null);
                annotationListItem.setCollapsed(!isExpanded(annotation.getClassName()));
                annotationListItem.setDeleteAnnotationHandler(AdvancedAnnotationListEditor.this::onDeleteAnnotation);
                annotationListItem.setClearValuePairHandler(AdvancedAnnotationListEditor.this::onClearValuePair);
                annotationListItem.setEditValuePairHandler(AdvancedAnnotationListEditor.this::onEditValuePair);
                annotationListItem.setCollapseChangeHandler(() -> AdvancedAnnotationListEditor.this.onCollapseChange(currentAnnotation,
                                                                                                                     annotationListItem.isCollapsed()));
                annotationListItem.setReadonly(readonly);
                annotationItems.put(annotation,
                                    annotationListItem);
                view.addItem(annotationListItem);
            }
        }
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        view.setReadonly(readonly);
        for (AnnotationListItem annotationListItem : annotationItems.values()) {
            annotationListItem.setReadonly(readonly);
        }
    }

    @Override
    public void onAddAnnotation() {
        view.invokeCreateAnnotationWizard(getAddAnnotationCallback(),
                                          project,
                                          elementType);
    }

    protected Callback<Annotation> getAddAnnotationCallback() {
        return annotation -> {
            if (annotation != null && addAnnotationHandler != null) {
                addAnnotationHandler.onAddAnnotation(annotation);
            }
        };
    }

    protected void onDeleteAnnotation(final Annotation annotation) {
        String message = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_confirm_annotation_deletion(
                annotation.getClassName(),
                (elementType != null ? elementType.name() : " object/field"));
        view.showYesNoDialog(message,
                             getDeleteAnnotationCommand(annotation),
                             getNoActionCommand(),
                             getNoActionCommand());
    }

    protected Command getDeleteAnnotationCommand(final Annotation annotation) {
        return () -> {
            if (deleteAnnotationHandler != null) {
                deleteAnnotationHandler.onDeleteAnnotation(annotation);
            }
        };
    }

    protected Command getNoActionCommand() {
        return () -> {
            //do nothing
        };
    }

    protected void onEditValuePair(Annotation annotation,
                                   String valuePair) {
        ValuePairEditorPopup valuePairEditor = createValuePairEditor(annotation,
                                                                     valuePair);
        if (valuePairEditor.isGenericEditor()) {
            AnnotationSource annotationSource = annotationSources.get(annotation.getClassName());
            String valuePairSource = annotationSource != null ? annotationSource.getValuePairSource(valuePair) : null;
            valuePairEditor.setValue(valuePairSource);
        } else {
            valuePairEditor.setValue(annotation.getValue(valuePair));
        }
        valuePairEditor.show();
    }

    protected void onClearValuePair(Annotation annotation,
                                    String valuePair) {

        AnnotationDefinition annotationDefinition = annotation.getAnnotationDefinition();
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair(valuePair);
        if (valuePairDefinition.getDefaultValue() == null) {
            //if the value pair has no default value, it should be applied wherever the annotation is applied, if not
            //the resulting code won't compile.
            String message = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_pair_has_no_default_value(valuePair,
                                                                                                                               annotation.getClassName());
            view.showYesNoDialog(message,
                                 getNoActionCommand());
        } else if (clearValuePairHandler != null) {
            clearValuePairHandler.onClearValuePair(annotation,
                                                   valuePair);
        }
    }

    protected void onCollapseChange(Annotation currentAnnotation,
                                    boolean collapsed) {
        setExpanded(currentAnnotation.getClassName(),
                    !collapsed);
    }

    public void addDeleteAnnotationHandler(AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler) {
        this.deleteAnnotationHandler = deleteAnnotationHandler;
    }

    public void addClearValuePairHandler(AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler) {
        this.clearValuePairHandler = clearValuePairHandler;
    }

    public void addValuePairChangeHandler(AdvancedAnnotationListEditorView.ValuePairChangeHandler valuePairChangeHandler) {
        this.valuePairChangeHandler = valuePairChangeHandler;
    }

    public void addAddAnnotationHandler(AdvancedAnnotationListEditorView.AddAnnotationHandler addAnnotationHandler) {
        this.addAnnotationHandler = addAnnotationHandler;
    }

    public void clear() {
        view.clear();
        clearListItems();
    }

    public void removeAnnotation(Annotation annotation) {
        AnnotationListItem listItem = annotationItems.get(annotation);
        if (listItem != null) {
            view.removeItem(listItem);
            annotationItems.remove(annotation);
            dispose(listItem);
        }
    }

    private RemoteCallback<AnnotationSourceResponse> getLoadAnnotationSourcesSuccessCallback() {
        return annotationSourceResponse -> loadAnnotations(annotations,
                                                           annotationSourceResponse.getAnnotationSources());
    }

    protected void doValuePairChange(final ValuePairEditorPopup valuePairEditor,
                                     final Object value) {

        if (valuePairEditor.isGenericEditor()) {
            String strValue = value != null ? value.toString() : null;
            modelerService.call(getValuePairChangeSuccessCallback(valuePairEditor))
                    .resolveParseRequest(new AnnotationParseRequest(valuePairEditor.getAnnotationClassName(),
                                                                    elementType,
                                                                    valuePairEditor.getValuePairDefinition().getName(),
                                                                    strValue),
                                         project);
        } else {
            applyValuePairChange(valuePairEditor,
                                 value);
        }
    }

    protected void doValuePairNoAction(final ValuePairEditorPopup valuePairEditor) {
        valuePairEditor.hide();
        dispose(valuePairEditor);
    }

    private RemoteCallback<AnnotationParseResponse> getValuePairChangeSuccessCallback(
            final ValuePairEditorPopup valuePairEditor) {
        return annotationParseResponse -> {
            if (!annotationParseResponse.hasErrors() && annotationParseResponse.getAnnotation() != null) {
                Object newValue = annotationParseResponse.getAnnotation().getValue(valuePairEditor.getValuePairDefinition().getName());
                applyValuePairChange(valuePairEditor,
                                     newValue);
            } else {

                //TODO improve this error handling
                String errorMessage = "";
                for (DriverError error : annotationParseResponse.getErrors()) {
                    errorMessage = errorMessage + "\n" + error.getMessage();
                }
                valuePairEditor.setErrorMessage(errorMessage);
            }
        };
    }

    private void applyValuePairChange(ValuePairEditorPopup valuePairEditor,
                                      Object newValue) {

        if (!valuePairEditor.isValid()) {
            valuePairEditor.setErrorMessage(
                    Constants.INSTANCE.advanced_domain_annotation_list_editor_message_invalid_value_for_value_pair(valuePairEditor.getValuePairDefinition().getName())
            );
        } else {
            if (!valuePairEditor.getValuePairDefinition().hasDefaultValue() && newValue == null) {
                valuePairEditor.setErrorMessage(
                        Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_pair_cant_be_null(valuePairEditor.getValuePairDefinition().getName())
                );
            } else {
                valuePairChangeHandler.onValuePairChange(valuePairEditor.getAnnotationClassName(),
                                                         valuePairEditor.getValuePairDefinition().getName(),
                                                         newValue);
                valuePairEditor.hide();
                dispose(valuePairEditor);
            }
        }
    }

    private ValuePairEditorPopup createValuePairEditor(Annotation annotation,
                                                       String valuePair) {
        final ValuePairEditorPopup valuePairEditor = createValuePairEditor();
        valuePairEditor.init(annotation.getClassName(),
                             annotation.getAnnotationDefinition().getValuePair(valuePair));
        valuePairEditor.addPopupHandler(new ValuePairEditorPopupView.ValuePairEditorPopupHandler() {

            @Override
            public void onOk() {
                doValuePairChange(valuePairEditor,
                                  valuePairEditor.getValue());
            }

            @Override
            public void onCancel() {
                doValuePairNoAction(valuePairEditor);
            }

            @Override
            public void onClose() {
                doValuePairNoAction(valuePairEditor);
            }
        });

        return valuePairEditor;
    }

    @PreDestroy
    protected void destroy() {
        clearListItems();
    }

    protected AnnotationListItem createListItem() {
        return itemInstance.get();
    }

    private void clearListItems() {
        for (AnnotationListItem item : annotationItems.values()) {
            dispose(item);
        }
        annotationItems.clear();
    }

    private void dispose(AnnotationListItem listItem) {
        itemInstance.destroy(listItem);
    }

    protected ValuePairEditorPopup createValuePairEditor() {
        return valuePairEditorInstance.get();
    }

    private void dispose(ValuePairEditorPopup valuePairEditor) {
        valuePairEditorInstance.destroy(valuePairEditor);
    }

    private boolean isExpanded(String annotation) {
        return annotationStatus.get(annotation) != null ? annotationStatus.get(annotation) : false;
    }

    private void setExpanded(String annotation,
                             boolean expanded) {
        annotationStatus.put(annotation,
                             expanded);
    }
}