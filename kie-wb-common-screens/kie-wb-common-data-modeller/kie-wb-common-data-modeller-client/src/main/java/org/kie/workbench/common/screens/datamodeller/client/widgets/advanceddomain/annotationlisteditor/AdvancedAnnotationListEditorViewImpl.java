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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item.AnnotationListItem;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard.CreateAnnotationWizard;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

@Dependent
public class AdvancedAnnotationListEditorViewImpl
        extends Composite
        implements AdvancedAnnotationListEditorView {

    interface AdvancedAnnotationListEditorViewImplUiBinder
            extends
            UiBinder<Widget, AdvancedAnnotationListEditorViewImpl> {

    }

    private static AdvancedAnnotationListEditorViewImplUiBinder uiBinder = GWT.create(AdvancedAnnotationListEditorViewImplUiBinder.class);

    @UiField
    Button addAnnotationButton;

    @UiField
    PanelGroup accordionsContainer;

    private Presenter presenter;

    private CreateAnnotationWizard createAnnotationWizard;

    @Inject
    public AdvancedAnnotationListEditorViewImpl(CreateAnnotationWizard createAnnotationWizard) {
        initWidget(uiBinder.createAndBindUi(this));
        this.createAnnotationWizard = createAnnotationWizard;
    }

    @PostConstruct
    protected void init() {
        accordionsContainer.setId(DOM.createUniqueId());
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        accordionsContainer.clear();
    }

    @Override
    public void addItem(AnnotationListItem listItem) {
        accordionsContainer.add(listItem);
    }

    @Override
    public void removeItem(AnnotationListItem listItem) {
        accordionsContainer.remove(listItem);
    }

    @Override
    public void showYesNoDialog(String message,
                                Command yesCommand,
                                Command noCommand,
                                Command cancelCommand) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(
                CommonConstants.INSTANCE.Information(), message, yesCommand, noCommand, cancelCommand);

        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }

    @Override
    public void showYesNoDialog(String message, Command cancelCommand) {
        showYesNoDialog(message, null, null, cancelCommand);
    }

    @Override
    public void invokeCreateAnnotationWizard(final Callback<Annotation> callback,
                                             final KieModule kieModule,
                                             final ElementType elementType) {
        createAnnotationWizard.init(kieModule, elementType);
        createAnnotationWizard.onCloseCallback(callback);
        createAnnotationWizard.start();
    }

    @Override
    public void setReadonly(boolean readonly) {
        addAnnotationButton.setEnabled(!readonly);
    }

    @UiHandler("addAnnotationButton")
    void onAddAnnotation(ClickEvent event) {
        presenter.onAddAnnotation();
    }
}