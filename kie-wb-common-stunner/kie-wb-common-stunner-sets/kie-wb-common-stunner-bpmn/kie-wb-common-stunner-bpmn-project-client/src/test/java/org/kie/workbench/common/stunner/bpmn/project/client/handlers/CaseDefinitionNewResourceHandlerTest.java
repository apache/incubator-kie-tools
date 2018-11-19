/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.handlers;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.bpmn.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseDefinitionNewResourceHandlerTest {

    public static final String DESCRIPTION = "description";
    private CaseDefinitionNewResourceHandler tested;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private ClientProjectDiagramService projectDiagramService;

    @Mock
    private BusyIndicatorView indicatorView;

    @Mock
    private BPMNDiagramResourceType projectDiagramResourceType;

    @Mock
    private ClientTranslationService translationService;

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

    @Before
    public void setUp() throws Exception {
        when(translationService.getDefinitionDescription(CaseDefinitionNewResourceHandler.CASE_DEFINITION)).thenReturn(DESCRIPTION);
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(workspaceProject));
        when(workspaceProject.getRootPath()).thenReturn(rootPath);

        tested = new CaseDefinitionNewResourceHandler(definitionManager, projectDiagramService, indicatorView,
                                                      projectDiagramResourceType, translationService,
                                                      new CallerMock<>(bpmnDiagramService), projectContext);
    }

    @Test
    public void getDescription() {
        final String description = tested.getDescription();
        verify(translationService).getDefinitionDescription(CaseDefinitionNewResourceHandler.CASE_DEFINITION);
        assertThat(description).isEqualTo(DESCRIPTION);
    }

    @Test
    public void getIcon() {
        final IsWidget icon = tested.getIcon();
        assertThat(icon).isEqualTo(CaseDefinitionNewResourceHandler.ICON);
    }

    @Test
    public void getDefinitionSetType() {
        final Class<?> definitionSetType = tested.getDefinitionSetType();
        assertThat(definitionSetType).isEqualTo(BPMNDefinitionSet.class);
    }

    @Test
    public void createDiagram() {
        Package pkg = mock(Package.class);
        String name = "project";
        NewResourcePresenter presenter = mock(NewResourcePresenter.class);
        Path path = mock(Path.class);
        String setId = BPMNDefinitionSet.class.getName();
        String moduleName = "module";
        Optional<String> projectType = Optional.of(ProjectType.CASE.name());

        tested.createDiagram(pkg, name, presenter, path, setId, moduleName, projectType);

        verify(projectDiagramService).create(eq(path), eq(name), eq(setId), eq(moduleName), eq(pkg), eq(projectType),
                                             any());
    }

    @Test
    public void acceptContextTrue() {
        when(bpmnDiagramService.getProjectType(rootPath)).thenReturn(ProjectType.CASE);

        tested.acceptContext(callback);
        verify(bpmnDiagramService).getProjectType(rootPath);
        verify(callback).onSuccess(true);
    }

    @Test
    public void acceptContextFalse() {
        when(bpmnDiagramService.getProjectType(rootPath)).thenReturn(ProjectType.BPMN);

        tested.acceptContext(callback);
        verify(bpmnDiagramService).getProjectType(rootPath);
        verify(callback, never()).onSuccess(anyBoolean());
    }
}