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
package org.drools.workbench.screens.guided.dtable.type;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GuidedDTableGraphResourceTypeDefinitionTest {

    private GuidedDTableGraphResourceTypeDefinition type;

    @Before
    public void setup() {
        type = new GuidedDTableGraphResourceTypeDefinition();
    }

    @Test
    public void checkPrefix() {
        assertEquals( "",
                      type.getPrefix() );
    }

    @Test
    public void checkSuffix() {
        assertEquals( "gdst-set",
                      type.getSuffix() );
    }

    @Test
    public void checkSimpleWildcardPattern() {
        assertEquals( "*." + type.getSuffix(),
                      type.getSimpleWildcardPattern() );
    }

    @Test
    public void checkAcceptGraphPath() {
        final Path path = mock( Path.class );
        when( path.getFileName() ).thenReturn( "a-file." + type.getSuffix() );
        assertTrue( type.accept( path ) );
    }

    @Test
    public void checkNotAcceptNonGraphPath() {
        final Path path = mock( Path.class );
        when( path.getFileName() ).thenReturn( "a-file.txt" );
        assertFalse( type.accept( path ) );
    }

}
