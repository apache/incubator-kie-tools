/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.grouped;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.BranchChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.View;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * Repository, Package, Folder and File explorer
 */
@ApplicationScoped
public class GroupedViewPresenterImpl extends BaseViewPresenter {

    private Set<Option> options = new HashSet<Option>( Arrays.asList( Option.GROUPED_CONTENT, Option.TREE_NAVIGATOR, Option.EXCLUDE_HIDDEN_ITEMS ) );

    protected GroupedViewWidget view;

    @Inject
    public GroupedViewPresenterImpl( final User identity,
                                     final RuntimeAuthorizationManager authorizationManager,
                                     final Caller<ExplorerService> explorerService,
                                     final Caller<BuildService> buildService,
                                     final Caller<VFSService> vfsService,
                                     final Caller<ValidationService> validationService,
                                     final PlaceManager placeManager,
                                     final Event<BuildResults> buildResultsEvent,
                                     final Event<ProjectContextChangeEvent> contextChangedEvent,
                                     final Event<NotificationEvent> notification,
                                     final SessionInfo sessionInfo,
                                     final GroupedViewWidget view ) {
        super( identity,
               authorizationManager,
               explorerService,
               buildService,
               vfsService,
               validationService,
               placeManager,
               buildResultsEvent,
               contextChangedEvent,
               notification,
               sessionInfo );
        this.view = view;
    }

    @Override
    protected void setOptions( final Set<Option> options ) {
        this.options = new HashSet<Option>( options );
    }

    @Override
    public Set<Option> getActiveOptions() {
        return options;
    }

    @Override
    protected View getView() {
        return view;
    }

    @Override
    public void addOption( Option option ) {
        options.add( option );
    }

    public void addBranchChangeHandler( BranchChangeHandler branchChangeHandler ) {
        view.addBranchChangeHandler( branchChangeHandler );
    }
}
