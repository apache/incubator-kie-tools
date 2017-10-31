/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.drltext.backend.server;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.OpenOption;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DRLTextEditorServiceImplTest {

    private IOService ioService = mock(IOService.class);
    private KieProjectService projectService = mock(KieProjectService.class);

    @InjectMocks
    private DRLTextEditorServiceImpl drlService = new DRLTextEditorServiceImpl();

    private String filename = "test.drl";
    private Path path2 = PathFactory.newPath("contextpath", "file:///contextpath");

    @Test
    public void testCreateAlreadyExisting() throws URISyntaxException {
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);

        try {
            drlService.create(path2, filename, "", "");
            fail("FileAlreadyExistsException was not thrown when expected!");
        } catch (FileAlreadyExistsException faee) {
            // this is correct behavior, anz other exception is a problem
        }
        verify(ioService, never()).write(any(org.uberfire.java.nio.file.Path.class), anyString(), any(OpenOption.class));
    }
}
