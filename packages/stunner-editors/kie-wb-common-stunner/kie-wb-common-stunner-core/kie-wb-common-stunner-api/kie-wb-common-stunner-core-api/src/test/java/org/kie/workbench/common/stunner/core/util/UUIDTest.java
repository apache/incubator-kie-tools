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


package org.kie.workbench.common.stunner.core.util;

import org.junit.Test;

import static org.junit.Assert.fail;

public class UUIDTest {

    private static final int TEST_COUNT = 100000;

    private static final String EXPECTED_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Test
    public void testUUID() {
        for (int j = 0; j < TEST_COUNT; j++) {
            final String uuid = UUID.uuid();
            for (int i = 0; i < uuid.length(); i++) {
                final char currentChar = uuid.charAt(i);
                boolean unexpected;
                if (i == 0) {
                    unexpected = '_' != currentChar;
                } else if (i == 9 || i == 14 || i == 19 || i == 24) {
                    unexpected = '-' != currentChar;
                } else {
                    unexpected = EXPECTED_CHARS.indexOf(uuid.charAt(i)) < 0;
                }
                if (unexpected) {
                    fail("Unexpected character: '" + currentChar + "' at position: " + i + " in UUID = " + uuid);
                }
            }
        }
    }
}
