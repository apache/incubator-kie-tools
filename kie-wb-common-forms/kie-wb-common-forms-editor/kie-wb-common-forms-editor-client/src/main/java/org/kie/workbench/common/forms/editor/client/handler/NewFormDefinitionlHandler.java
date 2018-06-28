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
package org.kie.workbench.common.forms.editor.client.handler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelsPresenter;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewFormDefinitionlHandler extends DefaultNewResourceHandler {

    private Caller<FormEditorService> modelerService;

    private FormDefinitionResourceType resourceType;

    private TranslationService translationService;

    private FormModelsPresenter formModelsPresenter;

    @Inject
    public NewFormDefinitionlHandler(final Caller<FormEditorService> modelerService,
                                     final FormDefinitionResourceType resourceType,
                                     final TranslationService translationService,
                                     final FormModelsPresenter formModelsPresenter,
                                     final WorkspaceProjectContext context,
                                     final Caller<KieModuleService> moduleService,
                                     final Caller<ValidationService> validationService,
                                     final PlaceManager placeManager,
                                     final Event<NotificationEvent> notificationEvent,
                                     final Event<NewResourceSuccessEvent> newResourceSuccessEvent,
                                     final BusyIndicatorView busyIndicatorView) {
        this.modelerService = modelerService;
        this.resourceType = resourceType;
        this.translationService = translationService;
        this.formModelsPresenter = formModelsPresenter;
        this.context = context;
        this.moduleService = moduleService;
        this.validationService = validationService;
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.newResourceSuccessEvent = newResourceSuccessEvent;
        this.busyIndicatorView = busyIndicatorView;
    }

    @PostConstruct
    protected void setupExtensions() {
        extensions.add(Pair.newPair("", // not needed FormModelsPresenter describes the extension itself
                                    formModelsPresenter));
    }

    @Override
    public String getDescription() {
        return translationService.getTranslation(FormEditorConstants.NewFormDefinitionlHandlerForm);
    }

    @Override
    public IsWidget getIcon() {
        return resourceType.getIcon();
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        formModelsPresenter.initialize(context.getActiveModule()
                                               .orElseThrow(() -> new IllegalStateException("Cannot get module root path without an active module."))
                                               .getRootPath());

        return super.getExtensions();
    }

    @Override
    public void validate(String baseFileName,
                         ValidatorWithReasonCallback callback) {

        boolean isValid = formModelsPresenter.isValid();

        if (!isValid) {
            callback.onFailure();
        } else {
            super.validate(baseFileName, callback);
        }
    }

    @Override
    public void create(final org.guvnor.common.services.project.model.Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {

        busyIndicatorView.showBusyIndicator(translationService.getTranslation(FormEditorConstants.NewFormDefinitionlHandlerSelectFormUse));

        modelerService.call(getSuccessCallback(presenter), getErrorCallback()).createForm(pkg.getPackageMainResourcesPath(),
                                                                                          buildFileName(baseFileName, resourceType),
                                                                                          formModelsPresenter.getFormModel());
    }

    protected ErrorCallback<Message> getErrorCallback() {
        return new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView);
    }
}
