/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;

import com.google.gwt.core.client.Callback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.client.handlers.util.CaseHelper;
import org.kie.workbench.common.stunner.bpmn.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.project.client.editor.CaseManagementDiagramEditor;
import org.kie.workbench.common.stunner.cm.project.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CaseManagementDiagramNewResourceHandlerTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private ClientProjectDiagramService projectDiagramService;

    @Mock
    private BusyIndicatorView indicatorView;

    @Mock
    private CaseManagementDiagramResourceType projectDiagramResourceType;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @Mock
    private BPMNDiagramService bpmnDiagramService;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Path rootPath;

    @Mock
    private Callback<Boolean, Void> callback;

    @Captor
    private ArgumentCaptor<ResourceRef> refArgumentCaptor;

    private CaseManagementDiagramNewResourceHandler tested;

    private CaseHelper caseHelper;

    @Before
    public void setUp() throws Exception {
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(workspaceProject));
        when(workspaceProject.getRootPath()).thenReturn(rootPath);
        when(sessionInfo.getIdentity()).thenReturn(user);

        caseHelper = new CaseHelper(new CallerMock<>(bpmnDiagramService), projectContext);

        tested = new CaseManagementDiagramNewResourceHandler(definitionManager,
                                                             projectDiagramService,
                                                             indicatorView,
                                                             projectDiagramResourceType,
                                                             authorizationManager,
                                                             sessionInfo,
                                                             caseHelper);
    }

    @Test
    public void testGetDefinitionSetType() throws Exception {
        assertEquals(CaseManagementDefinitionSet.class, tested.getDefinitionSetType());
    }

    @Test
    public void testCanCreate_disabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(false);

        assertFalse(tested.canCreate());
    }

    @Test
    public void testCanCreate_enabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(true);

        assertTrue(tested.canCreate());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateDiagram() throws Exception {
        tested.createDiagram(null, null, null, null, null, null, Optional.empty());

        verify(projectDiagramService).create(isNull(Path.class), isNull(String.class), isNull(String.class), isNull(String.class),
                                             isNull(Package.class), eq(Optional.of(ProjectType.CASE.name())), any(ServiceCallback.class));
    }

    @Test
    public void acceptContext_true() {
        when(bpmnDiagramService.getProjectType(rootPath)).thenReturn(ProjectType.CASE);

        tested.acceptContext(callback);
        verify(bpmnDiagramService).getProjectType(rootPath);
        verify(callback).onSuccess(true);
    }

    @Test
    public void acceptContext_false() {
        when(bpmnDiagramService.getProjectType(rootPath)).thenReturn(ProjectType.BPMN);

        tested.acceptContext(callback);
        verify(bpmnDiagramService).getProjectType(rootPath);
        verify(callback, never()).onSuccess(anyBoolean());
    }

    @Test
    public void checkCanCreateWhenFeatureDisabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(false);

        assertFalse(tested.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkCanCreateWhenFeatureEnabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(true);

        assertTrue(tested.canCreate());
        assertResourceRef();
    }

    private void assertResourceRef() {
        verify(authorizationManager).authorize(refArgumentCaptor.capture(),
                                               eq(ResourceAction.READ),
                                               eq(user));
        assertEquals(CaseManagementDiagramEditor.EDITOR_ID,
                     refArgumentCaptor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.EDITOR,
                     refArgumentCaptor.getValue().getResourceType());
    }
}