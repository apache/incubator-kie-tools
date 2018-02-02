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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.AdvancedAnnotationListEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.AdvancedAnnotationListEditorView;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.shared.project.KieModule;

@Dependent
public class AdvancedDataObjectFieldEditorViewImpl
        extends Composite
        implements AdvancedDataObjectFieldEditorView {

    interface AdvancedDataObjectFieldEditorViewImplUiBinder
            extends UiBinder<Widget, AdvancedDataObjectFieldEditorViewImpl> {

    }

    private static AdvancedDataObjectFieldEditorViewImplUiBinder uiBinder = GWT.create(AdvancedDataObjectFieldEditorViewImplUiBinder.class);

    @UiField
    SimplePanel annotationEditorPanel;

    private AdvancedAnnotationListEditor annotationListEditor;

    private Presenter presenter;

    @Inject
    public AdvancedDataObjectFieldEditorViewImpl(AdvancedAnnotationListEditor annotationListEditor) {
        initWidget(uiBinder.createAndBindUi(this));
        this.annotationListEditor = annotationListEditor;
    }

    @PostConstruct
    void init() {
        annotationEditorPanel.add(annotationListEditor);
        annotationListEditor.addDeleteAnnotationHandler(new AdvancedAnnotationListEditorView.DeleteAnnotationHandler() {
            @Override
            public void onDeleteAnnotation(Annotation annotation) {
                presenter.onDeleteAnnotation(annotation);
            }
        });
        annotationListEditor.addClearValuePairHandler(new AdvancedAnnotationListEditorView.ClearValuePairHandler() {
            @Override
            public void onClearValuePair(Annotation annotation, String valuePair) {
                presenter.onClearValuePair(annotation, valuePair);
            }
        });
        annotationListEditor.addValuePairChangeHandler(new AdvancedAnnotationListEditorView.ValuePairChangeHandler() {
            @Override
            public void onValuePairChange(String annotationClassName, String valuePairName, Object newValue) {
                presenter.onValuePairChange(annotationClassName, valuePairName, newValue);
            }
        });
        annotationListEditor.addAddAnnotationHandler(new AdvancedAnnotationListEditorView.AddAnnotationHandler() {
            @Override
            public void onAddAnnotation(Annotation annotation) {
                presenter.onAddAnnotation(annotation);
            }
        });
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void init(KieModule project, ElementType elementType) {
        annotationListEditor.init(project, elementType);
    }

    @Override
    public void loadAnnotations(List<Annotation> annotations) {
        annotationListEditor.loadAnnotations(annotations);
    }

    @Override
    public void removeAnnotation(Annotation annotation) {
        annotationListEditor.removeAnnotation(annotation);
    }

    @Override
    public void setReadonly(boolean readonly) {
        annotationListEditor.setReadonly(readonly);
    }

    public void clear() {
        annotationListEditor.clear();
    }
}

