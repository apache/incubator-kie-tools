package org.uberfire.wbtest.gwttest;

import org.junit.Test;
import org.uberfire.util.URIUtil;

/**
 * Tests for the visibility of menus based on the current user's role.
 */
public class UriUtilTest extends AbstractUberFireGwtTest {

    @Test
    public void testUriEncode() throws Exception {
        assertEquals( "one%20two!@#$%5E&*()?", URIUtil.encode( "one two!@#$^&*()?" ) );
    }

    @Test
    public void testUriNotValid() throws Exception {
        // not sure why this is reported as invalid.. behaviour is up to the uri.js library
        assertFalse( URIUtil.isValid( "yup/valid" ) );
    }

    @Test
    public void testUriIsValid() throws Exception {
        assertTrue( URIUtil.isValid( "http://uberfireframework.org/" ) );
    }

}
