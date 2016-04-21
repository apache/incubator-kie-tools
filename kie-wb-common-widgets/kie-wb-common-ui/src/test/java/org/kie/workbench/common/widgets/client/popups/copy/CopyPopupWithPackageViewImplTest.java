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

package org.kie.workbench.common.widgets.client.popups.copy;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.utils.ProjectResourcePaths;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class CopyPopupWithPackageViewImplTest {

    private static final String PATH_PREFIX = "git://repo/";

    @Mock
    private PackageListBox packageListBox;

    @Mock
    private ProjectContext context;

    @Mock
    private Package selectedPackage;

    @Mock
    private CopyPopup presenter;

    private CopyPopupWithPackageViewImpl view;

    @Before
    public void setup() {
        doReturn( selectedPackage ).when( packageListBox ).getSelectedPackage();

        view = new CopyPopupWithPackageViewImpl( packageListBox, context );
        view.init( presenter );
    }

    @Test
    public void testTargetMainResourcesPath() {
        final Path file = createFile( ProjectResourcePaths.MAIN_RESOURCES_PATH );
        doReturn( file ).when( presenter ).getPath();

        view.getTargetPath();

        verify( selectedPackage ).getPackageMainResourcesPath();
    }

    @Test
    public void testTargetMainSourcesPath() {
        final Path file = createFile( ProjectResourcePaths.MAIN_SRC_PATH );
        doReturn( file ).when( presenter ).getPath();

        view.getTargetPath();

        verify( selectedPackage ).getPackageMainSrcPath();
    }

    @Test
    public void testTargetTestResourcesPath() {
        final Path file = createFile( ProjectResourcePaths.TEST_RESOURCES_PATH );
        doReturn( file ).when( presenter ).getPath();

        view.getTargetPath();

        verify( selectedPackage ).getPackageTestResourcesPath();
    }

    @Test
    public void testTargetTestSourcesPath() {
        final Path file = createFile( ProjectResourcePaths.TEST_SRC_PATH );
        doReturn( file ).when( presenter ).getPath();

        view.getTargetPath();

        verify( selectedPackage ).getPackageTestSrcPath();
    }

    @Test
    public void testTargetInvalidPath() {
        final Path file = createFile( "global" );
        doReturn( file ).when( presenter ).getPath();

        final Path targetPath = view.getTargetPath();

        verify( selectedPackage, never() ).getPackageMainResourcesPath();
        verify( selectedPackage, never() ).getPackageMainSrcPath();
        verify( selectedPackage, never() ).getPackageTestResourcesPath();
        verify( selectedPackage, never() ).getPackageTestSrcPath();

        assertNull( targetPath );
    }

    private Path createFile( String projectPath ) {
        return PathFactory.newPath( "file.txt", PATH_PREFIX + projectPath + "/file.txt" );
    }
}
