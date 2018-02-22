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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
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
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectExplorerContentResolverTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectExplorerContentResolver resolver;
    private GitRepository repository;
    private Set<Module> masterModules;
    private Set<Module> devModules;

    private HelperWrapper helperWrapper;
    private Branch masterBranch;
    private Branch devBranch;
    private Space space;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private ExplorerServiceHelper explorerServiceHelper;

    @Before
    public void setUp() throws Exception {

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final KieModuleService moduleService = mock(KieModuleService.class);
        final ExplorerServiceHelper helper = mock(ExplorerServiceHelper.class);

        space = new Space("test-realm");

        repository = getGitRepository();

        final UserExplorerData userExplorerData = new UserExplorerData();

        masterModules = new HashSet<>();
        masterModules.add(createModule("master",
                                       masterBranch,
                                       "module 1"));
        masterModules.add(createModule("master",
                                       masterBranch,
                                       "module 2"));

        devModules = new HashSet<>();
        devModules.add(createModule("dev-1.0.0",
                                    devBranch,
                                    "module 1"));
        devModules.add(createModule("dev-1.0.0",
                                    devBranch,
                                    "module 2"));

        helperWrapper = new HelperWrapper(helper);

        when(helper.getLastContent()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return helperWrapper.getUserExplorerLastData();
            }
        });
        when(helper.loadUserContent()).thenReturn(userExplorerData);
        when(
                helper.getFolderListing(any(FolderItem.class),
                                        any(Module.class),
                                        any(Package.class),
                                        any(ActiveOptions.class))
        ).thenReturn(
                new FolderListing(getFileItem(),
                                  Collections.EMPTY_LIST,
                                  Collections.EMPTY_LIST)
        );

        when(moduleService.getAllModules(masterBranch)).thenReturn(masterModules);
        when(moduleService.getAllModules(devBranch)).thenReturn(devModules);
        when(moduleService.resolveDefaultPackage(any(Module.class))).thenReturn(new Package());

        resolver = spy(new ProjectExplorerContentResolver(
                moduleService,
                helper,
                explorerServiceHelper,
                projectService));
    }

    private FolderItem getFileItem() {
        Path path = mock(Path.class);
        return new FolderItem(path,
                              "someitem",
                              FolderItemType.FILE);
    }

    @Test
    public void testResolveWithOUWithRepositoryNullQueryBusinessView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery(repository,
                                                                                  masterBranch);
        final ActiveOptions options = new ActiveOptions();
        options.add(Option.TREE_NAVIGATOR);
        options.add(Option.EXCLUDE_HIDDEN_ITEMS);
        options.add(Option.BUSINESS_CONTENT);
        query.setOptions(options);
        resolver.resolve(query);
    }

    @Test
    public void testResolveWithOUWithRepositoryNullQueryTechnicalView() throws Exception {
        final ProjectExplorerContentQuery query = new ProjectExplorerContentQuery(repository,
                                                                                  masterBranch);
        final ActiveOptions options = new ActiveOptions();
        options.add(Option.TREE_NAVIGATOR);
        options.add(Option.EXCLUDE_HIDDEN_ITEMS);
        options.add(Option.TECHNICAL_CONTENT);
        query.setOptions(options);
        resolver.resolve(query);
    }

    @Test
    public void testChangeProjectOnBusinessView() throws Exception {

        ProjectExplorerContent content = resolver.resolve(getContentQuery(masterBranch,
                                                                          createModule("master",
                                                                                       masterBranch,
                                                                                       "module 1"),
                                                                          Option.BUSINESS_CONTENT));
        helperWrapper.reset();

        assertEquals("master",
                     content.getProject().getBranch().getName());
        assertNotNull(content.getModule()); // This will be the default module
        assertEquals("master@module 1",
                     content.getModule().getRootPath().toURI());

        content = resolver.resolve(getContentQuery(devBranch,
                                                   createModule("dev-1.0.0",
                                                                devBranch,
                                                                "module 1"),
                                                   Option.BUSINESS_CONTENT));
        helperWrapper.reset();

        assertEquals("dev-1.0.0",
                     content.getProject().getBranch().getName());
        assertEquals("module 1",
                     content.getModule().getModuleName());
        assertEquals("dev-1.0.0@module 1",
                     content.getModule().getRootPath().toURI());

        content = resolver.resolve(getContentQuery(devBranch,
                                                   createModule("dev-1.0.0",
                                                                devBranch,
                                                                "module 2"),
                                                   Option.BUSINESS_CONTENT));
        helperWrapper.reset();

        assertEquals("dev-1.0.0",
                     content.getProject().getBranch().getName());
        assertEquals("module 2",
                     content.getModule().getModuleName());
        assertEquals("dev-1.0.0@module 2",
                     content.getModule().getRootPath().toURI());

        content = resolver.resolve(getContentQuery(masterBranch,
                                                   createModule("master",
                                                                masterBranch,
                                                                "module 2"),
                                                   Option.BUSINESS_CONTENT));
        helperWrapper.reset();

        assertEquals("master",
                     content.getProject().getBranch().getName());
        assertEquals("module 2",
                     content.getModule().getModuleName());
        assertEquals("master@module 2",
                     content.getModule().getRootPath().toURI());
    }

    @Test
    public void testChangeModuleOnTechnicalView() {

        resolver.resolve(getContentQuery(masterBranch,
                                         createModule("master",
                                                      masterBranch,
                                                      "module 1"),
                                         Option.TECHNICAL_CONTENT));
        helperWrapper.reset();

        Content content = resolver.setupSelectedItems(getContentQuery(masterBranch,
                                                                      createModule("master",
                                                                                   masterBranch,
                                                                                   "module 2"),
                                                                      Option.TECHNICAL_CONTENT,
                                                                      null,
                                                                      getFileItem()));
        helperWrapper.reset();

        assertEquals("master",
                     content.getSelectedProject().getBranch().getName());
        assertEquals("master@module 2",
                     content.getSelectedModule().getRootPath().toURI());
        assertNull(content.getSelectedItem());
        assertNull(content.getSelectedPackage());
    }

    @Test
    public void testChangeModuleOnTechnicalViewWhenThereIsAFolderItemButNoActiveModuleOrganizationalUnitOrRepository() {

        resolver.resolve(getContentQuery(masterBranch,
                                         createModule("master",
                                                      masterBranch,
                                                      "module 1"),
                                         Option.TECHNICAL_CONTENT));
        helperWrapper.reset();

        ProjectExplorerContentQuery projectExplorerContentQuery = new ProjectExplorerContentQuery(
                null,
                null,
                null,
                null,
                getFileItem()
        );

        ActiveOptions options = new ActiveOptions();
        options.add(Option.TECHNICAL_CONTENT);
        projectExplorerContentQuery.setOptions(options);

        helperWrapper.excludePackage();
        Content content = resolver.setupSelectedItems(projectExplorerContentQuery);
        helperWrapper.reset();

        assertEquals("master",
                     content.getSelectedProject().getBranch().getName());
        assertEquals("master@module 1",
                     content.getSelectedModule().getRootPath().toURI());
        assertNotNull(content.getSelectedItem());
        assertNull(content.getSelectedPackage());
    }

    @Test
    public void testChangeFromBusinessToTechnicalView() {
        resolver.resolve(getContentQuery(masterBranch,
                                         createModule("master",
                                                      masterBranch,
                                                      "project 1"),
                                         Option.BUSINESS_CONTENT));
        helperWrapper.reset();

        final Content content = resolver.setupSelectedItems(getContentQuery(masterBranch,
                                                                            createModule("master",
                                                                                         masterBranch,
                                                                                         "project 1"),
                                                                            Option.TECHNICAL_CONTENT,
                                                                            null,
                                                                            getFileItem()));

        assertEquals("demo",
                     content.getSelectedProject().getOrganizationalUnit().getName());
        assertEquals("master",
                     content.getSelectedProject().getBranch().getName());
        assertEquals("file://master@module/",
                     content.getSelectedProject().getRootPath().toURI());
        assertNull(content.getSelectedItem());
        assertNull(content.getSelectedPackage());
    }

    @Test
    public void getSegmentSiblingsRootTest() {
        doAnswer(invocationOnMock -> {
            final Path p = Paths.convert((org.uberfire.java.nio.file.Path) invocationOnMock.getArguments()[0]);
            return new FolderItem(p,
                                  p.getFileName(),
                                  FolderItemType.FOLDER);
        }).when(explorerServiceHelper).toFolderItem(any(org.uberfire.java.nio.file.Path.class));

        Path path = PathFactory.newPath("/",
                                        "default://master@myproject/");

        final List<FolderItem> siblings = resolver.getSegmentSiblings(path);

        assertEquals(1,
                     siblings.size());
        assertEquals("/",
                     siblings.get(0).getFileName());
    }

    @Test
    public void getSegmentSiblingsTest() {
        doAnswer(invocationOnMock -> {
            final Path p = Paths.convert((org.uberfire.java.nio.file.Path) invocationOnMock.getArguments()[0]);
            return new FolderItem(p,
                                  p.getFileName(),
                                  FolderItemType.FOLDER);
        }).when(explorerServiceHelper).toFolderItem(any(org.uberfire.java.nio.file.Path.class));

        Path path = PathFactory.newPath("src",
                                        "default://master@myproject/src");

        final List<org.uberfire.java.nio.file.Path> mockedSiblings = new ArrayList<>();
        mockedSiblings.add(Paths.convert(path));
        mockedSiblings.add(Paths.convert(PathFactory.newPath("src",
                                                             "default://master@myproject/pom.xml")));
        doReturn(mockedSiblings).when(resolver).getDirectoryIterator(any(org.uberfire.java.nio.file.Path.class));

        final List<FolderItem> siblings = resolver.getSegmentSiblings(path);

        assertEquals(2,
                     siblings.size());
        assertEquals("src",
                     siblings.get(0).getFileName());
        assertEquals("pom.xml",
                     siblings.get(1).getFileName());
    }

    private Module createModule(final String branchName,
                                final Branch branch,
                                final String moduleName) {
        final POM pom = mock(POM.class);
        when(pom.getName()).thenReturn(moduleName);

        final Module module = new Module(createMockPath(branchName,
                                                        moduleName),
                                         createMockPath(branchName,
                                                        moduleName),
                                         pom);

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("demo").when(organizationalUnit).getName();

        doReturn(new WorkspaceProject(organizationalUnit,
                                      repository,
                                      branch,
                                      module
        )).when(projectService).resolveProject(space,
                                               branch);

        return module;
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

    private ProjectExplorerContentQuery getContentQuery(final Branch branch,
                                                        final Module module,
                                                        final Option content) {
        return getContentQuery(branch,
                               module,
                               content,
                               null,
                               null);
    }

    private ProjectExplorerContentQuery getContentQuery(final Branch branch,
                                                        final Module module,
                                                        final Option content,
                                                        final Package pkg,
                                                        final FolderItem item) {

        final ProjectExplorerContentQuery projectExplorerContentQuery = new ProjectExplorerContentQuery(
                repository,
                branch,
                module,
                pkg,
                item
        );

        final ActiveOptions options = new ActiveOptions();
        options.add(Option.TREE_NAVIGATOR);
        options.add(Option.EXCLUDE_HIDDEN_ITEMS);
        options.add(content);
        projectExplorerContentQuery.setOptions(options);

        return projectExplorerContentQuery;
    }

    private GitRepository getGitRepository() {
        final GitRepository repository = new GitRepository("alias",
                                                           space);

        final HashMap<String, Branch> branches = new HashMap<>();
        masterBranch = new Branch("master",
                                  PathFactory.newPath("/",
                                                      "file://master@module/"));
        devBranch = new Branch("dev-1.0.0",
                               PathFactory.newPath("/",
                                                   "file://dev-1.0.0@module/"));

        branches.put("master",
                     masterBranch);
        branches.put("dev-1.0.0",
                     devBranch);

        repository.setBranches(branches);

        return repository;
    }
}