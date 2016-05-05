/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.apps.client.home.components.popup;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.apps.api.Directory;

import static org.junit.Assert.*;

public class DirectoryNameValidatorTest {

    private DirectoryNameValidator directoryNameValidator;

    @Before
    public void setUp() throws Exception {
        directoryNameValidator = new DirectoryNameValidator( new Directory() );
    }

    @Test
    public void isValidTest() throws Exception {
        assertTrue( directoryNameValidator.isValid( "app" ) );
        assertTrue( directoryNameValidator.isValid( "my app" ) );
        assertTrue( directoryNameValidator.isValid( "日本国" ) );
        assertFalse( directoryNameValidator.isValid( "" ) );
        assertFalse( directoryNameValidator.isValid( " " ) );
        assertFalse( directoryNameValidator.isValid( "app\\" ) );
        assertFalse( directoryNameValidator.isValid( "app/" ) );
        assertFalse( directoryNameValidator.isValid( "app:" ) );
        assertFalse( directoryNameValidator.isValid( "*app" ) );
        assertFalse( directoryNameValidator.isValid( "a<pp" ) );
        assertFalse( directoryNameValidator.isValid( "app>" ) );
        assertFalse( directoryNameValidator.isValid( "ap|p" ) );
        assertFalse( directoryNameValidator.isValid( "ap*p" ) );
        assertFalse( directoryNameValidator.isValid( "ap?p" ) );
    }

    @Test
    public void isValidTestDuplicatedDir() throws Exception {

        final Directory currentDirectory = new Directory( "parent", "", "", new HashMap<String, List<String>>() );
        directoryNameValidator = new DirectoryNameValidator( currentDirectory );
        assertTrue( directoryNameValidator.isValid( "app" ) );
        currentDirectory.addChildDirectory( new Directory( "app", "", "", currentDirectory ) );
        assertFalse( directoryNameValidator.isValid( "app" ) );
    }
}