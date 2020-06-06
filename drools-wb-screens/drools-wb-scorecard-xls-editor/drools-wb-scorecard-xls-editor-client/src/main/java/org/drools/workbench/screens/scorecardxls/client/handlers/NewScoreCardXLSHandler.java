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

package org.drools.workbench.screens.scorecardxls.client.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scorecardxls.client.editor.URLHelper;
import org.drools.workbench.screens.scorecardxls.client.resources.ScoreCardXLSEditorResources;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.kie.workbench.common.services.shared.resources.EditorIds;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewScoreCardXLSHandler extends DefaultNewResourceHandler {

    private PlaceManager placeManager;
    private ScoreCardXLSResourceType resourceType;
    private BusyIndicatorView busyIndicatorView;
    private ClientMessageBus clientMessageBus;
    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;

    private AttachmentFileWidget uploadWidget;

    public NewScoreCardXLSHandler() {
    }

    @Inject
    public NewScoreCardXLSHandler(final PlaceManager placeManager,
                                  final ScoreCardXLSResourceType resourceType,
                                  final BusyIndicatorView busyIndicatorView,
                                  final ClientMessageBus clientMessageBus,
                                  final AuthorizationManager authorizationManager,
                                  final SessionInfo sessionInfo) {
        this.placeManager = placeManager;
        this.resourceType = resourceType;
        this.busyIndicatorView = busyIndicatorView;
        this.clientMessageBus = clientMessageBus;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
    }

    void setUploadWidget(final AttachmentFileWidget uploadWidget) {
        this.uploadWidget = uploadWidget;
    }

    @PostConstruct
    private void setupExtensions() {
        uploadWidget = new AttachmentFileWidget(new String[]{resourceType.getSuffix()});
        extensions.add(new Pair<String, AttachmentFileWidget>(ScoreCardXLSEditorConstants.INSTANCE.Upload(),
                                                              uploadWidget));
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        uploadWidget.reset();
        return super.getExtensions();
    }

    @Override
    public String getDescription() {
        return ScoreCardXLSEditorConstants.INSTANCE.NewScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(ScoreCardXLSEditorResources.INSTANCE.images().typeXLSScoreCard());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public boolean canCreate() {
        return authorizationManager.authorize(new ResourceRef(EditorIds.XLS_SCORE_CARD,
                                                              ActivityResourceType.EDITOR),
                                              ResourceAction.READ,
                                              sessionInfo.getIdentity());
    }

    @Override
    public void create(final Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {
        busyIndicatorView.showBusyIndicator(ScoreCardXLSEditorConstants.INSTANCE.Uploading());

        final Path path = pkg.getPackageMainResourcesPath();
        final String fileName = buildFileName(baseFileName,
                                              resourceType);
        //Package Path is already encoded, fileName needs to be encoded
        final Path newPath = PathFactory.newPathBasedOn(fileName,
                                                        path.toURI() + "/" + encode(fileName),
                                                        path);
        uploadWidget.submit(path,
                            fileName,
                            getServletUrl(),
                            new Command() {

                                @Override
                                public void execute() {
                                    busyIndicatorView.hideBusyIndicator();
                                    presenter.complete();
                                    notifySuccess();
                                    newResourceSuccessEvent.fire(new NewResourceSuccessEvent(path));
                                    placeManager.goTo(newPath);
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
        return URLHelper.getServletUrl(getClientId());
    }

    protected String getClientId() {
        return clientMessageBus.getClientId();
    }

    protected String encode(final String fileName) {
        return URL.encode(fileName);
    }
}
