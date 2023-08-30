/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uberfire.commons;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UUIDTest {

    @Test
    public void hasFiveGroups() throws
            Exception {
        final String uuid = UUID.uuid();
        final String[] split = uuid.split("-");
        assertEquals(5,
                     split.length);
    }

    @Test
    public void generateWithGivenLength() throws
            Exception {
        assertEquals(12,
                     UUID.uuid(12)
                             .length());
        assertEquals(6,
                     UUID.uuid(6)
                             .length());
        assertEquals(100,
                     UUID.uuid(100)
                             .length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongRadix() throws
            Exception {
        UUID.uuid(10,
                  63);
    }

    @Test
    public void generateWithGivenLengthAndRadix() throws
            Exception {
        assertOnlyContainsCharacters(UUID.uuid(10,
                                               2),
                                     "0",
                                     "1");
    }

    private void assertOnlyContainsCharacters(final String uuid,
                                              final String... chars) {

        String tmp = uuid;
        for (final String aChar : chars) {
            tmp = tmp.replaceAll(aChar,
                                 "");
        }

        assertTrue("Found illegal characters: " + tmp,
                   tmp.isEmpty());
    }
}