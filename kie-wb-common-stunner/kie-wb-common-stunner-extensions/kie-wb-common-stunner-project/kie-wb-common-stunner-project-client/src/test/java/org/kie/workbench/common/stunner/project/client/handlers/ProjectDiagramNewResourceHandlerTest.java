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
package org.kie.workbench.common.stunner.project.client.handlers;

import java.util.Optional;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectDiagramNewResourceHandlerTest {

    private static final String DEFSET_ID = "ds1";
    private static final String PROJ_ROOT_FILENAME = "rootFileName";
    private static final String MODULE_NAME = "moduleName";

    @Mock
    private DefinitionManager definitionManager;
    @Mock
    private ClientProjectDiagramService projectDiagramServices;
    @Mock
    private BusyIndicatorView indicatorView;
    @Mock
    private ClientResourceType projectDiagramResourceType;
    @Mock
    private TypeDefinitionSetRegistry definitionSetRegistry;
    @Mock
    private AdapterManager adapterManager;
    @Mock
    private DefinitionSetAdapter definitionSetAdapter;
    @Mock
    private Object definitionSet;
    @Mock
    private Package aPackage;
    @Mock
    private NewResourcePresenter presenter;
    @Mock
    private WorkspaceProjectContext context;
    @Mock
    private Path path;
    @Mock
    private Path moduleRootPath;
    @Mock
    private Module module;

    private ProjectDiagramNewResourceHandlerStub tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(definitionSetRegistry.getDefinitionSetByType(any(Class.class))).thenReturn(definitionSet);
        when(definitionSetRegistry.getDefinitionSetByType(any(Class.class))).thenReturn(definitionSet);
        when(definitionSetAdapter.getId(eq(definitionSet))).thenReturn(DEFSET_ID);
        when(aPackage.getPackageMainResourcesPath()).thenReturn(path);
        when(aPackage.getPackageName()).thenReturn("packageName");
        when(aPackage.getModuleRootPath()).thenReturn(moduleRootPath);
        when(moduleRootPath.getFileName()).thenReturn(PROJ_ROOT_FILENAME);
        when(context.getActiveModule()).thenReturn(Optional.of(module));
        when(module.getModuleName()).thenReturn(MODULE_NAME);
        when(projectDiagramResourceType.getSuffix()).thenReturn("bpmn2");
        when(projectDiagramResourceType.getPrefix()).thenReturn("");
        this.tested = new ProjectDiagramNewResourceHandlerStub(definitionManager,
                                                               projectDiagramServices,
                                                               indicatorView,
                                                               projectDiagramResourceType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreate() {
        final String baseFileName = "file1";
        tested.create(aPackage,
                      baseFileName,
                      presenter);
        verify(projectDiagramServices,
               times(1)).create(eq(path),
                                eq("file1"),
                                eq(DEFSET_ID),
                                eq(MODULE_NAME),
                                any(Package.class),
                                any(ServiceCallback.class));
    }

    private class ProjectDiagramNewResourceHandlerStub extends AbstractProjectDiagramNewResourceHandler<ClientResourceType> {

        static final String EDITOR_ID = "mockEditorId";
        static final String EDITOR_DESC = "mockEditorDesc";

        public ProjectDiagramNewResourceHandlerStub(DefinitionManager definitionManager,
                                                    ClientProjectDiagramService projectDiagramServices,
                                                    BusyIndicatorView indicatorView,
                                                    ClientResourceType projectDiagramResourceType) {
            super(definitionManager,
                  projectDiagramServices,
                  indicatorView,
                  projectDiagramResourceType);
            context = ProjectDiagramNewResourceHandlerTest.this.context;
        }

        @Override
        protected Class<?> getDefinitionSetType() {
            return ProjectDiagramNewResourceHandlerStub.class;
        }

        @Override
        protected String getEditorIdentifier() {
            return EDITOR_ID;
        }

        @Override
        public String getDescription() {
            return EDITOR_DESC;
        }

        @Override
        public IsWidget getIcon() {
            return null;
        }
    }
}
