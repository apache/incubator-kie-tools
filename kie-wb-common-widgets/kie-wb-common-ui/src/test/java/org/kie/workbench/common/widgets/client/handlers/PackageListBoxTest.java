/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.handlers;

import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PackageListBoxTest {

    private KieProjectService projectService;
    private CallerMock<KieProjectService> projectServiceCaller;

    private ProjectContext projectContext;

    private PackageListBox packageListBox;

    @Before
    public void setup() {
        setupProjectService();
        setupProjectContext();
        setupPackageListBox();
    }

    @Test
    public void setContextCallsPackageLoadedCommandTest() {
        final Command packagesLoadedCommand = spy( new Command() {
            @Override
            public void execute() {
            }
        } );

        packageListBox.setContext( projectContext, true, packagesLoadedCommand );

        verify( packagesLoadedCommand ).execute();
    }

    private void setupProjectService() {
        projectService = mock( KieProjectService.class );
        projectServiceCaller = new CallerMock<>( projectService );
        final HashSet<Package> packages = new HashSet<>();
        packages.add( mock( Package.class ) );
        doReturn( packages ).when( projectService ).resolvePackages( any( Project.class ) );
    }

    private void setupProjectContext() {
        projectContext = new ProjectContext();
        projectContext.setActiveProject( mock( Project.class ) );
        projectContext.setActivePackage( mock( Package.class ) );
    }

    private void setupPackageListBox() {
        packageListBox = spy( new PackageListBox( projectServiceCaller ) );
        doNothing().when( packageListBox ).addPackage( any( Package.class ), any( Package.class ) );
        doNothing().when( packageListBox ).noPackage();
        doNothing().when( packageListBox ).clearSelect();
        doNothing().when( packageListBox ).refreshSelect();
    }
}
