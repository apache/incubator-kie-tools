/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.client.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.dtablexls.client.editor.URLHelper;
import org.drools.workbench.screens.dtablexls.client.resources.DecisionTableXLSResources;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSResourceType;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSXResourceType;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new XLS Decision Tables
 */
@ApplicationScoped
public class NewDecisionTableXLSHandler extends DefaultNewResourceHandler {

    private ClientMessageBus clientMessageBus;
    private PlaceManager placeManager;
    private DecisionTableXLSResourceType decisionTableXLSResourceType;
    private DecisionTableXLSXResourceType decisionTableXLSXResourceType;
    private BusyIndicatorView busyIndicatorView;

    private AttachmentFileWidget uploadWidget;

    public NewDecisionTableXLSHandler() {
    }

    @Inject
    public NewDecisionTableXLSHandler( final PlaceManager placeManager,
                                       final DecisionTableXLSResourceType decisionTableXLSResourceType,
                                       final DecisionTableXLSXResourceType decisionTableXLSXResourceType,
                                       final BusyIndicatorView busyIndicatorView,
                                       final ClientMessageBus clientMessageBus ) {
        this.placeManager = placeManager;
        this.decisionTableXLSResourceType = decisionTableXLSResourceType;
        this.decisionTableXLSXResourceType = decisionTableXLSXResourceType;
        this.busyIndicatorView = busyIndicatorView;
        this.clientMessageBus = clientMessageBus;
    }

    void setUploadWidget( final AttachmentFileWidget uploadWidget ) {
        this.uploadWidget = uploadWidget;
    }

    @PostConstruct
    private void setupExtensions() {
        uploadWidget = new AttachmentFileWidget(
                new String[]{
                        decisionTableXLSResourceType.getSuffix(),
                        decisionTableXLSXResourceType.getSuffix() } );

        extensions.add(Pair.newPair(DecisionTableXLSEditorConstants.INSTANCE.Upload(),
                                    uploadWidget));
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        uploadWidget.reset();
        return super.getExtensions();
    }

    @Override
    public String getDescription() {
        return DecisionTableXLSEditorConstants.INSTANCE.NewDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( DecisionTableXLSResources.INSTANCE.images().typeXLSDecisionTable() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return decisionTableXLSResourceType;
    }

    @Override
    public void validate(final String baseFileName, final ValidatorWithReasonCallback callback) {
        if(uploadWidget.getFilenameSelectedToUpload() == null || uploadWidget.getFilenameSelectedToUpload().isEmpty()) {
            uploadWidget.addStyleName(ValidationState.ERROR.getCssName());
        } else {
            super.validate(baseFileName, callback);
        }
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( DecisionTableXLSEditorConstants.INSTANCE.Uploading() );

        final ResourceTypeDefinition type =
                uploadWidget.getFilenameSelectedToUpload().endsWith(decisionTableXLSResourceType.getSuffix()) ?
                        decisionTableXLSResourceType : decisionTableXLSXResourceType;

        final Path path = pkg.getPackageMainResourcesPath();
        final String fileName = buildFileName( baseFileName,
                                               type );
        //Package Path is already encoded, fileName needs to be encoded
        final Path newPath = PathFactory.newPathBasedOn( fileName,
                                                         path.toURI() + "/" + encode( fileName ),
                                                         path );

        uploadWidget.submit( path,
                             fileName,
                             getServletUrl(),
                             new Command() {

                                 @Override
                                 public void execute() {
                                     busyIndicatorView.hideBusyIndicator();
                                     presenter.complete();
                                     notifySuccess();
                                     newResourceSuccessEvent.fire( new NewResourceSuccessEvent( newPath ) );
                                     placeManager.goTo( newPath );
                                 }

                             },
                             new Command() {

                                 @Override
                                 public void execute() {
                                     busyIndicatorView.hideBusyIndicator();
                                 }
                             }
                           );
    }

    protected String getServletUrl() {
        return URLHelper.getServletUrl( getClientId() );
    }

    protected String getClientId() {
        return clientMessageBus.getClientId();
    }

    protected String encode( final String fileName ) {
        return URL.encode( fileName );
    }

}
