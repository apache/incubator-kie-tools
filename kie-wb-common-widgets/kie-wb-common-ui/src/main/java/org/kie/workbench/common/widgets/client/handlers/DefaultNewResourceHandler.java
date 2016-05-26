/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.handlers;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Items that require a Name and Path
 */
public abstract class DefaultNewResourceHandler implements NewResourceHandler,
                                                           PackageContextProvider {

    protected final List<Pair<String, ? extends IsWidget>> extensions = new LinkedList<Pair<String, ? extends IsWidget>>();

    @Inject
    protected PackageListBox packagesListBox;

    @Inject
    protected ProjectContext context;

    @Inject
    protected Caller<KieProjectService> projectService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Event<NotificationEvent> notificationEvent;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    //Package-protected constructor for tests. In an ideal world we'd move to Constructor injection
    //however that would require every sub-class of this abstract class to also have Constructor
    //injection.. and that's a lot of refactoring just to be able to test.
    DefaultNewResourceHandler( final PackageListBox packagesListBox,
                               final ProjectContext context,
                               final Caller<KieProjectService> projectService,
                               final Caller<ValidationService> validationService,
                               final PlaceManager placeManager,
                               final Event<NotificationEvent> notificationEvent,
                               final BusyIndicatorView busyIndicatorView ) {
        this.packagesListBox = packagesListBox;
        this.context = context;
        this.projectService = projectService;
        this.validationService = validationService;
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.busyIndicatorView = busyIndicatorView;
    }

    public DefaultNewResourceHandler() {
        //Zero argument constructor for CDI proxies
    }

    @PostConstruct
    private void setupExtensions() {
        this.extensions.add( Pair.newPair( CommonConstants.INSTANCE.ItemPathSubheading(),
                                           packagesListBox ) );
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        this.packagesListBox.setContext( context,
                                         true );
        return this.extensions;
    }

    @Override
    public void validate( final String baseFileName,
                          final ValidatorWithReasonCallback callback ) {
        if ( packagesListBox.getSelectedPackage() == null ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.MissingPath() );
            callback.onFailure();
            return;
        }

        final String fileName = buildFileName( baseFileName,
                                               getResourceType() );

        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                if ( Boolean.TRUE.equals( response ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure( CommonConstants.INSTANCE.InvalidFileName0( baseFileName ) );
                }
            }
        } ).isFileNameValid( fileName );
    }

    @Override
    public void acceptContext( final Callback<Boolean, Void> callback ) {
        if ( context == null ) {
            callback.onSuccess( false );
        } else {
            callback.onSuccess( context.getActiveProject() != null );
        }
    }

    @Override
    public Command getCommand( final NewResourcePresenter newResourcePresenter ) {
        return new Command() {
            @Override
            public void execute() {
                newResourcePresenter.show( DefaultNewResourceHandler.this );
            }
        };
    }

    @Override
    public Package getPackage() {
        return packagesListBox.getSelectedPackage();
    }

    protected String buildFileName( final String baseFileName,
                                    final ResourceTypeDefinition resourceType ) {
        final String suffix = resourceType.getSuffix();
        final String prefix = resourceType.getPrefix();
        final String extension = !( suffix == null || "".equals( suffix ) ) ? "." + resourceType.getSuffix() : "";
        if ( baseFileName.endsWith( extension ) ) {
            return prefix + baseFileName;
        }
        return prefix + baseFileName + extension;
    }

    protected void notifySuccess() {
        notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCreatedSuccessfully(), NotificationEvent.NotificationType.SUCCESS ) );
    }

    protected RemoteCallback<Path> getSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                presenter.complete();
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path );
                placeManager.goTo( place );
            }

        };
    }

    @Override
    public boolean canCreate() {
        return true;
    }
}