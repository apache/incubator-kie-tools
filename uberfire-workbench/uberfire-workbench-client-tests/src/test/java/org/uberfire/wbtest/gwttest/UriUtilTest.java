/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
