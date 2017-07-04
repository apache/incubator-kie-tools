/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentScreenPopupViewImpl;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentScreenPopupViewImpl.ValidateExistingContainerCallback;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DeploymentPopupBuilderTest {

    @Mock
    private BuildExecutor buildExecutor;

    @Mock
    private DeploymentScreenPopupViewImpl popupView;

    private DeploymentPopupBuilder builder;

    @BeforeClass
    public static void setupPreferences() {
        // Prevent runtime GWT.create() error
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(buildExecutor.getDeploymentScreenPopupViewImpl()).thenReturn(popupView);

        builder = spy(new DeploymentPopupBuilder(buildExecutor));
    }

    @Test
    public void testBuildDeployWithMultipleServerTemplates() throws Exception {
        final List<ServerTemplate> serverTemplates = new ArrayList<>();
        final ValidateExistingContainerCallback callback = mock(ValidateExistingContainerCallback.class);
        final ParameterizedCommand<DeploymentScreenPopupViewImpl> onSuccess = (v) -> {
        };

        doReturn(callback).when(builder).multipleServerTemplatesValidation(serverTemplates);

        builder.buildDeployWithMultipleServerTemplates(serverTemplates,
                                                       onSuccess);

        verify(builder).setViewFields(onSuccess,
                                      callback);
    }

    @Test
    public void testMultipleServerTemplatesValidationWhenContainerNameDoesNotExist() throws Exception {
        final List<ServerTemplate> serverTemplates = new ArrayList<ServerTemplate>() {{
            add(serverTemplate("serverTemplate1",
                               "container1",
                               "container2"));
            add(serverTemplate("serverTemplate2",
                               "container1",
                               "container2",
                               "container3"));
            add(serverTemplate("serverTemplate3",
                               "container1",
                               "container2"));
        }};

        doNothing().when(popupView).addServerTemplates(any());
        doReturn("serverTemplate1").when(popupView).getServerTemplate();

        final ValidateExistingContainerCallback validation = builder.multipleServerTemplatesValidation(serverTemplates);

        assertFalse(validation.containerNameExists("container3"));
    }

    @Test
    public void testMultipleServerTemplatesValidationWhenContainerNameExists() throws Exception {
        final List<ServerTemplate> serverTemplates = new ArrayList<ServerTemplate>() {{
            add(serverTemplate("serverTemplate1",
                               "container1",
                               "container2"));
            add(serverTemplate("serverTemplate2",
                               "container1",
                               "container2",
                               "container3"));
            add(serverTemplate("serverTemplate3",
                               "container1",
                               "container2"));
        }};

        doNothing().when(popupView).addServerTemplates(any());
        doReturn("serverTemplate2").when(popupView).getServerTemplate();

        final ValidateExistingContainerCallback validation = builder.multipleServerTemplatesValidation(serverTemplates);

        assertTrue(validation.containerNameExists("container3"));
    }

    @Test
    public void testBuildDeployWithOneServerTemplate() throws Exception {
        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        final ValidateExistingContainerCallback callback = mock(ValidateExistingContainerCallback.class);
        final ParameterizedCommand<DeploymentScreenPopupViewImpl> onSuccess = (v) -> {
        };

        doReturn(callback).when(builder).singleServerTemplatesValidation(serverTemplate);

        builder.buildDeployWithOneServerTemplate(serverTemplate,
                                                 onSuccess);

        verify(builder).setViewFields(onSuccess,
                                      callback);
    }

    @Test
    public void testSingleServerTemplatesValidationContainerNameDoesNotExist() throws Exception {
        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        final Set<String> containers = new HashSet<String>() {{
            add("container1");
            add("container2");
            add("container3");
        }};

        doReturn(containers).when(buildExecutor).existingContainers(serverTemplate);

        final ValidateExistingContainerCallback validation = builder.singleServerTemplatesValidation(serverTemplate);

        assertFalse(validation.containerNameExists("container4"));
    }

    @Test
    public void testSingleServerTemplatesValidationContainerNameExists() throws Exception {
        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        final Set<String> containers = new HashSet<String>() {{
            add("container1");
            add("container2");
            add("container3");
        }};

        doReturn(containers).when(buildExecutor).existingContainers(serverTemplate);

        final ValidateExistingContainerCallback validation = builder.singleServerTemplatesValidation(serverTemplate);

        assertTrue(validation.containerNameExists("container3"));
    }

    private ServerTemplate serverTemplate(final String id,
                                          final String... containerNames) {
        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        final ArrayList<ContainerSpec> containersSpec = new ArrayList<ContainerSpec>() {{
            for (final String containerName : containerNames) {
                add(containerSpec(containerName));
            }
        }};

        doReturn(id).when(serverTemplate).getId();
        doReturn(containersSpec).when(serverTemplate).getContainersSpec();

        return serverTemplate;
    }

    private ContainerSpec containerSpec(final String containerName) {
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        doReturn(containerName).when(containerSpec).getContainerName();

        return containerSpec;
    }
}
