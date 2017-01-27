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

package org.kie.workbench.common.screens.library.client.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceUtilsTest {

    @Mock
    private Classifier classifier;

    private ResourceUtils resourceUtils;

    @Before
    public void setup() {
        resourceUtils = new ResourceUtils( classifier );

        ClientResourceType clientResourceType = mock( ClientResourceType.class );
        doReturn( "java" ).when( clientResourceType ).getSuffix();

        doReturn( clientResourceType ).when( classifier ).findResourceType( any( Path.class ) );
    }

    @Test
    public void getBaseFileNameTest() {
        assertEquals( "MyClass", resourceUtils.getBaseFileName( getPath( "MyClass.java" ) ) );
        assertEquals( "MyClass.txt", resourceUtils.getBaseFileName( getPath( "MyClass.txt.java" ) ) );
    }

    private Path getPath( final String fileName ) {
        final Path path = mock( Path.class );
        doReturn( fileName ).when( path ).getFileName();

        return path;
    }
}
