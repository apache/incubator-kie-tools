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

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectExplorerContentResolverDefaultSelectionsTest {

    private SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectExplorerContentResolver resolver;

    private GitRepository repository1;
    private Module repository1Module1;
    private Set<Module> repository1Modules;

    private GitRepository repository2;
    private Module repository2Module1;
    private Set<Module> repository2Modules;

    private UserExplorerData userExplorerData;

    private HelperWrapper helperWrapper;

    @Mock
    private WorkspaceProjectService projectService;

    @Before
    public void setUp() throws Exception {
        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final KieModuleService moduleService = mock(KieModuleService.class);
        final ExplorerServiceHelper helper = mock(ExplorerServiceHelper.class);
        final ExplorerServiceHelper explorerServiceHelper = mock(ExplorerServiceHelper.class);

        repository1 = getGitRepository("repo1");
        repository2 = getGitRepository("repo2");

        repository1Module1 = createModule("master",
                                          "r1p1");
        repository1Modules = new HashSet<Module>() {{
            add(repository1Module1);
        }};

        repository2Module1 = createModule("master",
                                          "r2p1");
        repository2Modules = new HashSet<Module>() {{
            add(repository2Module1);
        }};

        userExplorerData = new UserExplorerData();

        helperWrapper = new HelperWrapper(helper);

        when(helper.getLastContent()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return helperWrapper.getUserExplorerLastData();
            }
        });
        when(helper.loadUserContent()).thenReturn(userExplorerData);
        when(helper.getFolderListing(any(FolderItem.class),
                                     any(Module.class),
                                     any(Package.class),
                                     any(ActiveOptions.class))).thenReturn(
                new FolderListing(createFileItem(),
                                  Collections.EMPTY_LIST,
                                  Collections.EMPTY_LIST));

        when(moduleService.getAllModules(repository1.getDefaultBranch().get())).thenReturn(repository1Modules);
        when(moduleService.getAllModules(repository2.getDefaultBranch().get())).thenReturn(repository2Modules);

        doReturn(createPackage("master",
                               repository1Module1.getModuleName())).when(moduleService).resolveDefaultPackage(repository1Module1);
        doReturn(createPackage("master",
                               repository2Module1.getModuleName())).when(moduleService).resolveDefaultPackage(repository2Module1);

        resolver = new ProjectExplorerContentResolver(moduleService,
                                                      helper,
                                                      explorerServiceHelper,
                                                      projectService);
    }

    @Test
    public void testSelectionsModule() throws Exception {
        final ProjectExplorerContent content = resolver.resolve(getContentQuery(repository1,
                                                                                "master",
                                                                                repository1Module1));

        assertEquals(repository1,
                     content.getProject().getRepository());
        assertEquals(repository1Module1,
                     content.getModule());
    }

    private FolderItem createFileItem() {
        Path path = mock(Path.class);
        return new FolderItem(path,
                              "someitem",
                              FolderItemType.FILE);
    }

    private Module createModule(final String branch,
                                final String moduleName) {
        final POM pom = mock(POM.class);
        when(pom.getName()).thenReturn(moduleName);

        return new Module(createMockPath(branch,
                                         moduleName),
                          createMockPath(branch,
                                         moduleName),
                          pom);
    }

    private Package createPackage(final String branch,
                                  final String moduleName) {
        return new Package(createMockPath(branch,
                                          moduleName),
                           createMockPath(branch,
                                          moduleName),
                           createMockPath(branch,
                                          moduleName),
                           createMockPath(branch,
                                          moduleName),
                           createMockPath(branch,
                                          moduleName),
                           "default",
                           "default",
                           "default");
    }

    private Path createMockPath(final String branch,
                                final String moduleName) {

        return new Path() {
            @Override
            public String getFileName() {
                return moduleName;
            }

            @Override
            public String toURI() {
                return branch + "@" + moduleName;
            }

            @Override
            public int compareTo(Path o) {
                return toURI().compareTo(o.toURI());
            }
        };
    }

    private ProjectExplorerContentQuery getContentQuery(final Repository repository,
                                                        final String branchName,
                                                        final Module module) {
        final Branch branch = new Branch(branchName,
                                         mock(Path.class));
        final ProjectExplorerContentQuery moduleExplorerContentQuery = new ProjectExplorerContentQuery(repository,
                                                                                                       branch,
                                                                                                       module);

        doReturn(new WorkspaceProject(mock(OrganizationalUnit.class),
                                      repository,
                                      branch,
                                      module)).when(projectService).resolveProject(repository.getSpace(), branch);

        final ActiveOptions options = new ActiveOptions();
        options.add(Option.TREE_NAVIGATOR);
        options.add(Option.EXCLUDE_HIDDEN_ITEMS);
        options.add(Option.BUSINESS_CONTENT);
        moduleExplorerContentQuery.setOptions(options);

        return moduleExplorerContentQuery;
    }

    private GitRepository getGitRepository(final String alias) {
        final GitRepository repository = new GitRepository(alias,
                                                           new Space("scheme"));
        final HashMap<String, Branch> branches = new HashMap<>();
        final Path path = PathFactory.newPath("/",
                                              "file://master@module/");
        branches.put("master",
                     new Branch("master",
                                path));

        repository.setBranches(branches);
        return repository;
    }
}