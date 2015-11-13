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

package org.uberfire.ext.layout.editor.client.dnd;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GridValueValidatorTest {

    @Test
    public void testIsValid() throws Exception {
        GridValueValidator gridValueValidator = new GridValueValidator();
        assertTrue(gridValueValidator.isValid( "12" ));
        assertTrue(gridValueValidator.isValid( "6 6" ));
        assertTrue(gridValueValidator.isValid( "4 4 4" ));
        assertTrue(gridValueValidator.isValid( "4 2 6" ));
        assertFalse(gridValueValidator.isValid( "" ));
        assertFalse(gridValueValidator.isValid( " " ));
        assertFalse(gridValueValidator.isValid( " 12a" ));
        assertFalse(gridValueValidator.isValid( "invalid" ));
        assertFalse(gridValueValidator.isValid( "4 3 4" ));
        assertFalse(gridValueValidator.isValid( "4 3 2 2" ));
        assertFalse(gridValueValidator.isValid( null ));
    }
}