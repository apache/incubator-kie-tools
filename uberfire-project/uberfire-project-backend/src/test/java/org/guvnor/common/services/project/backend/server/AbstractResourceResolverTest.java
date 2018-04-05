/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractResourceResolverTest {

    protected static final int PROJECT_RESOURCE_PATH_RESOLVERS_SIZE = 5;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    protected IOService ioService;
    @Mock
    protected POMService pomService;
    @Mock
    protected CommentedOptionFactory commentedOptionFactory;
    @Mock
    protected Instance<ModuleResourcePathResolver> resourcePathResolversInstance;
    protected ResourceResolver resourceResolver;
    protected List<ModuleResourcePathResolver> projectResourcePathResolvers = new ArrayList<>();

    @Before
    public void setUp() {
        initModuleResourcePathResolvers(PROJECT_RESOURCE_PATH_RESOLVERS_SIZE);
        when(resourcePathResolversInstance.iterator()).thenReturn(projectResourcePathResolvers.iterator());

        resourceResolver = spy(new ResourceResolver(ioService,
                                                    pomService,
                                                    commentedOptionFactory,
                                                    resourcePathResolversInstance) {
            @Override
            public Module resolveModule(Path resource, boolean loadPOM) {
                return null;
            }

            @Override
            public Module simpleModuleInstance(org.uberfire.java.nio.file.Path nioModuleRootPath) {
                return null;
            }
        });
    }

    @Test
    public void resolveDefaultPathSuccessful() {
        Package pkg = mock(Package.class);
        String resourceType = "any";
        Path defaultPath = mock(Path.class);

        // pick one of the configured resolvers as the one that accepts the given resource type.
        ModuleResourcePathResolver blessedModuleResourcePathResolver = projectResourcePathResolvers.get(3);
        when(blessedModuleResourcePathResolver.accept(resourceType)).thenReturn(true);
        when(blessedModuleResourcePathResolver.resolveDefaultPath(pkg)).thenReturn(defaultPath);

        assertEquals(defaultPath,
                     resourceResolver.resolveDefaultPath(pkg,
                                                         resourceType));

        verify(blessedModuleResourcePathResolver,
               times(1)).resolveDefaultPath(pkg);
        projectResourcePathResolvers.forEach(projectResourcePathResolver -> {
            if (projectResourcePathResolver != blessedModuleResourcePathResolver) {
                verify(projectResourcePathResolver,
                       never()).resolveDefaultPath(any(Package.class));
            }
        });
    }

    @Test
    public void resolveDefaultPathWithErrors() {
        Package pkg = mock(Package.class);
        String resourceType = "any";
        // if none of the configured ModuleResourcePathResolvers accepts the resourceType, and exception must have been
        // thrown.
        expectedException.expectMessage("No ModuleResourcePathResolver has been defined for resourceType: " + resourceType);
        resourceResolver.resolveDefaultPath(pkg,
                                            resourceType);
    }

    @Test
    public void resolveDefaultWorkspacePackageTest() {
        final GAV gav = mock(GAV.class);
        doReturn("com.group").when(gav).getGroupId();
        doReturn("package").when(gav).getArtifactId();

        assertEquals("com/group/_package",
                     resourceResolver.getDefaultWorkspacePath(gav));
    }

    @Test
    public void getDefaultWorkspacePathTest() {
        final Module project = mock(Module.class);

        final POM pom = mock(POM.class);
        doReturn(pom).when(project).getPom();

        final GAV gav = mock(GAV.class);
        doReturn(gav).when(pom).getGav();

        final Path path = mock(Path.class);
        doReturn(path).when(project).getRootPath();

        when(path.toURI()).thenReturn("file:///myproject/");
        doReturn("com.group").when(gav).getGroupId();
        doReturn("package").when(gav).getArtifactId();

        final ArgumentCaptor<Path> packagePathArgumentCaptor = ArgumentCaptor.forClass(Path.class);

        resourceResolver.resolveDefaultWorkspacePackage(project);

        verify(resourceResolver).resolvePackage(packagePathArgumentCaptor.capture());
        final Path packagePath = packagePathArgumentCaptor.getValue();
        assertEquals("file:///myproject/src/main/resources/com/group/_package",
                     packagePath.toURI());
    }

    private void initModuleResourcePathResolvers(int size) {
        for (int i = 0; i < size; i++) {
            projectResourcePathResolvers.add(mock(ModuleResourcePathResolver.class));
        }
    }
}