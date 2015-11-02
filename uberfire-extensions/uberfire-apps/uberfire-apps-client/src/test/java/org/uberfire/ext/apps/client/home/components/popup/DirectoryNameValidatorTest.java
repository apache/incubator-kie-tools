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