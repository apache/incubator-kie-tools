package org.uberfire.security.server;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class UrlResourceManagerTest {

    private URLResourceManager manager;

    @Before
    public void setup() {
        manager = new URLResourceManager( "testing-resource-manager-config-file.yaml" );
    }

    @Test
    public void shouldRequireAuthenticationForRegularResources() throws Exception {
        assertTrue( manager.requiresAuthentication( new URLResource( "/requires/authentication.html" ) ) );
    }

    @Test
    public void shouldNotRequireAuthenticationForExcludedResources() throws Exception {
        assertFalse( manager.requiresAuthentication( new URLResource( "/login.jsp" ) ) );
    }

    @Test
    public void resourceMatchingShouldIgnoreQueryParams() throws Exception {
        assertFalse( manager.requiresAuthentication( new URLResource( "/login.jsp?foo=bar" ) ) );
    }
}
