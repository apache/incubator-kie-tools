/*
 * Copyright 2014 JBoss Inc
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

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import javax.swing.filechooser.FileSystemView;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class ProjectExplorerContentResolverTest {

    @Test
    public void testBranchChange() throws Exception {

        FileSystemProviders.getDefaultProvider();

        IOService ioService = mock(IOService.class);
        KieProjectService projectService = mock(KieProjectService.class);
        UserServicesImpl userServices = mock(UserServicesImpl.class);
        ExplorerServiceHelper helper = mock(ExplorerServiceHelper.class);
        AuthorizationManager authorizationManager = mock(AuthorizationManager.class);
        OrganizationalUnitService organizationalUnitService = mock(OrganizationalUnitService.class);

        ProjectExplorerContentResolver resolver = new ProjectExplorerContentResolver(
                ioService,
                projectService,
                userServices,
                helper,
                authorizationManager,
                organizationalUnitService);

        OrganizationalUnitImpl organizationalUnit = mock(OrganizationalUnitImpl.class);
        ProjectExplorerContentQuery contentQuery = new ProjectExplorerContentQuery(
                organizationalUnit,
                getGitRepository("dev-1.0.0")
        );

        HashSet<Option> options = new HashSet<Option>();
        options.add(Option.TREE_NAVIGATOR);
        options.add(Option.EXCLUDE_HIDDEN_ITEMS);
        options.add(Option.BUSINESS_CONTENT);
        contentQuery.setOptions(options);
        contentQuery.setBranchChangeFlag(true);

        UserExplorerLastData userExplorerLastData = new UserExplorerLastData();
        userExplorerLastData.setPackage(organizationalUnit, getGitRepository("master"), null, null);

        ArrayList<OrganizationalUnit> organizationalUnits = new ArrayList<OrganizationalUnit>();
        organizationalUnits.add(organizationalUnit);

        when(organizationalUnitService.getOrganizationalUnits()).thenReturn(organizationalUnits);

        when(authorizationManager.authorize(any(Resource.class), any(User.class))).thenReturn(true);

        when(helper.getLastContent()).thenReturn(userExplorerLastData);

        when(helper.loadUserContent()).thenReturn(new UserExplorerData());

        when(ioService.newDirectoryStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(new DirectoryStream<org.uberfire.java.nio.file.Path>() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public Iterator<org.uberfire.java.nio.file.Path> iterator() {
                return Collections.<org.uberfire.java.nio.file.Path>emptyList().iterator();
            }
        });

        when(organizationalUnitService.getOrganizationalUnit(anyString())).thenReturn(organizationalUnit);
        ArrayList<Repository> repositories = new ArrayList<Repository>();
        repositories.add(getGitRepository("master"));
        when(organizationalUnit.getRepositories()).thenReturn(repositories);

        ProjectExplorerContent content = resolver.resolve(contentQuery);
        assertEquals("dev-1.0.0", content.getRepository().getCurrentBranch());
    }

    private GitRepository getGitRepository(String selectedBranchName) {
        GitRepository repository = new GitRepository();

        HashMap<String, Path> branches = new HashMap<String, Path>();
        Path pathToMaster = PathFactory.newPath("/", "file://master@project/");
        branches.put("master", pathToMaster);
        Path pathToDev = PathFactory.newPath("/", "file://dev-1.0.0@project/");
        branches.put("dev-1.0.0", pathToDev);

        repository.setBranches(branches);
        repository.changeBranch(selectedBranchName);
        return repository;
    }
}