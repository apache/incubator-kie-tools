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
package org.kie.workbench.common.forms.editor.client.handler;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.client.type.FormDefinitionResourceType;
import org.kie.workbench.common.forms.editor.service.FormEditorService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class NewFormDefinitionlHandler extends DefaultNewResourceHandler {

    private Caller<FormEditorService> modelerService;

    private PlaceManager placeManager;

    private FormDefinitionResourceType resourceType;

    private Event<NotificationEvent> notificationEvent;

    private TranslationService translationService;

    @Inject
    public NewFormDefinitionlHandler( Caller<FormEditorService> modelerService,
                                      PlaceManager placeManager,
                                      FormDefinitionResourceType resourceType,
                                      Event<NotificationEvent> notificationEvent,
                                      TranslationService translationService ) {
        this.modelerService = modelerService;
        this.placeManager = placeManager;
        this.resourceType = resourceType;
        this.notificationEvent = notificationEvent;
        this.translationService = translationService;
    }

    @Override
    public String getDescription() {
        return translationService.getTranslation( FormEditorConstants.NewFormDefinitionlHandlerForm );
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( org.guvnor.common.services.project.model.Package pkg,
                        String baseFileName,
                        final NewResourcePresenter presenter ) {
        BusyPopup.showMessage( translationService.getTranslation( FormEditorConstants.NewFormDefinitionlHandlerCreatingNewForm ) );

        modelerService.call( new RemoteCallback<Path>() {
                                 @Override
                                 public void callback( final Path path ) {
                                     BusyPopup.close();
                                     presenter.complete();
                                     notifySuccess();
                                     PlaceRequest place = new PathPlaceRequest( path, "FormEditor" );
                                     placeManager.goTo( place );

                                 }
                             }, new ErrorCallback<Message>() {
                                 @Override
                                 public boolean error( Message message,
                                                       Throwable throwable ) {
                                     BusyPopup.close();
                                     ErrorPopup.showMessage( CommonConstants.INSTANCE.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother() );
                                     return true;
                                 }
                             }
                           ).createForm( pkg.getPackageMainResourcesPath(), buildFileName( baseFileName,
                                                                                           resourceType ) );
    }

}
