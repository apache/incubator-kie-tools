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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.marshalling.DMNPathsHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectsServiceImplTest {

    @Mock
    private DMNPathsHelper pathsHelper;

    private DataObjectsServiceImpl service;

    @Before
    public void setup() {
        service = new DataObjectsServiceImpl(pathsHelper);
    }

    @Test
    public void testLoadDataObjects() {

        final WorkspaceProject project = mock(WorkspaceProject.class);
        final String file1 = "file1.java";
        final String file2 = "file2.java";

        final List<Path> files = Arrays.asList(getPath(file1), getPath(file2));
        when(pathsHelper.getDataObjectsPaths(project)).thenReturn(files);

        final List<DataObject> dataObjects = service.loadDataObjects(project);

        assertEquals(2, dataObjects.size());
        assertEquals(file1, dataObjects.get(0).getClassType());
        assertEquals(file2, dataObjects.get(1).getClassType());
    }

    private Path getPath(final String filename) {

        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn(filename);
        return path;
    }

    public void testLoadDataObjectsWhenThereIsNoJavaFilesAvailable() {

        final WorkspaceProject project = mock(WorkspaceProject.class);

        final List<Path> files = new ArrayList<>();
        when(pathsHelper.getDataObjectsPaths(project)).thenReturn(files);

        final List<DataObject> dataObjects = service.loadDataObjects(project);

        assertEquals(0, dataObjects.size());
    }
}