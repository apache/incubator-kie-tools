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
package org.kie.workbench.common.dmn.project.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.project.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
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
public class DMNDiagramNewResourceHandler extends AbstractProjectDiagramNewResourceHandler<DMNDiagramResourceType> {

    private final AuthorizationManager authorizationManager;
    private final SessionInfo sessionInfo;

    protected DMNDiagramNewResourceHandler() {
        this(null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DMNDiagramNewResourceHandler(final DefinitionManager definitionManager,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final BusyIndicatorView busyIndicatorView,
                                        final DMNDiagramResourceType projectDiagramResourceType,
                                        final AuthorizationManager authorizationManager,
                                        final SessionInfo sessionInfo) {
        super(definitionManager,
              projectDiagramServices,
              busyIndicatorView,
              projectDiagramResourceType);
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public boolean canCreate() {
        return authorizationManager.authorize(new ResourceRef(DMNDiagramEditor.EDITOR_ID,
                                                              ActivityResourceType.EDITOR),
                                              ResourceAction.READ,
                                              sessionInfo.getIdentity());
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return DMNDefinitionSet.class;
    }

    @Override
    protected String getEditorIdentifier() {
        return DMNDiagramEditor.EDITOR_ID;
    }

    @Override
    public String getDescription() {
        return getDiagramResourceType().getDescription();
    }

    @Override
    public IsWidget getIcon() {
        return getDiagramResourceType().getIcon();
    }

    private DMNDiagramResourceType getDiagramResourceType() {
        return (DMNDiagramResourceType) super.getResourceType();
    }
}
