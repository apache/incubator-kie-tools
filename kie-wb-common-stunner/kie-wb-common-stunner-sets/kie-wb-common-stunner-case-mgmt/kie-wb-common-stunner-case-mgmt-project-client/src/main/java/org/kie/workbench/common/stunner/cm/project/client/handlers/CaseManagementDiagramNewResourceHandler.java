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

package org.kie.workbench.common.stunner.cm.project.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.project.client.editor.CaseManagementDiagramEditor;
import org.kie.workbench.common.stunner.cm.project.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.project.client.handlers.AbstractProjectDiagramNewResourceHandler;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
public class CaseManagementDiagramNewResourceHandler extends AbstractProjectDiagramNewResourceHandler<CaseManagementDiagramResourceType> {

    private final AuthorizationManager authorizationManager;
    private final SessionInfo sessionInfo;

    protected CaseManagementDiagramNewResourceHandler() {
        this(null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public CaseManagementDiagramNewResourceHandler(final DefinitionManager definitionManager,
                                                   final ClientProjectDiagramService projectDiagramServices,
                                                   final BusyIndicatorView indicatorView,
                                                   final CaseManagementDiagramResourceType projectDiagramResourceType,
                                                   final AuthorizationManager authorizationManager,
                                                   final SessionInfo sessionInfo) {
        super(definitionManager,
              projectDiagramServices,
              indicatorView,
              projectDiagramResourceType);
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    public String getDescription() {
        return getCaseManagementDiagramResourceType().getDescription();
    }

    @Override
    public IsWidget getIcon() {
        return getCaseManagementDiagramResourceType().getIcon();
    }

    private CaseManagementDiagramResourceType getCaseManagementDiagramResourceType() {
        return (CaseManagementDiagramResourceType) super.getResourceType();
    }

    @Override
    public boolean canCreate() {
        return authorizationManager.authorize(new ResourceRef(CaseManagementDiagramEditor.EDITOR_ID,
                                                              ActivityResourceType.EDITOR),
                                              ResourceAction.READ,
                                              sessionInfo.getIdentity());
    }
}
