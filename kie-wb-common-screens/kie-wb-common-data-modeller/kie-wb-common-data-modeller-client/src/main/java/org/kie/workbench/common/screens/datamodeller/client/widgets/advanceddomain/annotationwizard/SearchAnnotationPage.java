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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionResponse;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class SearchAnnotationPage
        extends CreateAnnotationWizardPage
        implements SearchAnnotationPageView.Presenter {

    private SearchAnnotationPageView.SearchAnnotationHandler searchAnnotationHandler;

    private SearchAnnotationPageView view;

    @Inject
    public SearchAnnotationPage(SearchAnnotationPageView view,
                                Caller<DataModelerService> modelerService,
                                Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        super(modelerService, wizardPageStatusChangeEvent);
        this.view = view;
        view.init(this);
        setTitle(Constants.INSTANCE.advanced_domain_wizard_search_page_title());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init(KieModule project, ElementType target) {
        this.project = project;
        this.target = target;
        this.status = PageStatus.NOT_VALIDATED;
        view.setClassName(null);
        view.clearHelpMessage();
    }

    @Override
    public void onSearchClass() {
        AnnotationDefinitionRequest definitionRequest = new AnnotationDefinitionRequest(
                DataModelerUtils.trim(view.getClassName()));
        modelerService.call(getOnSearchClassSuccessCallback(definitionRequest)).resolveDefinitionRequest(definitionRequest, project);
    }

    private RemoteCallback<AnnotationDefinitionResponse> getOnSearchClassSuccessCallback(final AnnotationDefinitionRequest definitionRequest) {
        return new RemoteCallback<AnnotationDefinitionResponse>() {
            @Override
            public void callback(AnnotationDefinitionResponse definitionResponse) {
                processAnnotationDefinitionRequest(definitionRequest, definitionResponse);
            }
        };
    }

    private void processAnnotationDefinitionRequest(AnnotationDefinitionRequest definitionRequest,
                                                    AnnotationDefinitionResponse definitionResponse) {

        annotationDefinition = definitionResponse.getAnnotationDefinition();
        if (definitionResponse.hasErrors() || definitionResponse.getAnnotationDefinition() == null) {
            setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_search_page_message_class_not_found(definitionRequest.getClassName()));
        } else {
            setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_search_page_message_annotation_is_loaded());
        }

        setStatus(annotationDefinition != null ? PageStatus.VALIDATED : PageStatus.NOT_VALIDATED);
        if (searchAnnotationHandler != null) {
            searchAnnotationHandler.onAnnotationDefinitionChange(annotationDefinition);
        }
    }

    @Override
    public void onSearchClassChanged() {
        setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_search_page_message_annotation_not_loaded());
        annotationDefinition = null;
        if (searchAnnotationHandler != null) {
            searchAnnotationHandler.onSearchClassChanged();
        }
        setStatus(PageStatus.NOT_VALIDATED);
    }

    public void requestFocus() {
        view.setClassNameFocus(true);
    }

    @Override
    public void addSearchAnnotationHandler(SearchAnnotationPageView.SearchAnnotationHandler searchAnnotationHandler) {
        this.searchAnnotationHandler = searchAnnotationHandler;
    }

    void clearHelpMessage() {
        view.clearHelpMessage();
    }

    void setHelpMessage(String helpMessage) {
        view.setHelpMessage(helpMessage);
    }
}
