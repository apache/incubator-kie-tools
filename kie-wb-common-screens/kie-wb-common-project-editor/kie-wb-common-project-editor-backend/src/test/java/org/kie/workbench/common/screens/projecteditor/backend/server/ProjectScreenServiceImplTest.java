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

package org.kie.workbench.common.screens.projecteditor.backend.server;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ProjectScreenServiceImplTest {

    @Mock
    private ProjectScreenModelLoader loader;

    @Mock
    private ProjectScreenModelSaver saver;

    private ProjectScreenServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new ProjectScreenServiceImpl( mock( KieProjectService.class ),
                                                loader,
                                                saver );
    }

    @Test
    public void testSave() throws Exception {
        final Path pathToPomXML = mock( Path.class );
        final ProjectScreenModel model = new ProjectScreenModel();
        final String message = "message";

        service.save( pathToPomXML,
                      model,
                      message );

        verify( saver ).save( pathToPomXML,
                              model,
                              message );
    }

    @Test
    public void testLoad() throws Exception {
        final Path pathToPom = mock( Path.class );
        final ProjectScreenModel expected = new ProjectScreenModel();
        when( loader.load( pathToPom ) ).thenReturn( expected );

        final ProjectScreenModel model = service.load( pathToPom );

        assertEquals( expected, model );
    }
}