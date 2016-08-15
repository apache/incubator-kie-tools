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

package org.kie.workbench.common.screens.explorer.client.utils;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class UtilsTest {

    @Mock
    private ClientResourceType clientResourceType;

    @Test
    public void getBaseFileNameSimpleExtension() {
        when( clientResourceType.getSuffix() ).thenReturn( "xml" );
        String result = Utils.getBaseFileName( "filename.xml", clientResourceType.getSuffix() );
        assertEquals( "filename", result );
    }

    @Test
    public void getBaseFileNameComplexExtension() {
        when( clientResourceType.getSuffix() ).thenReturn( "suffix.xml" );
        String result = Utils.getBaseFileName( "filename.suffix.xml", clientResourceType.getSuffix() );
        assertEquals( "filename", result );
    }
}
