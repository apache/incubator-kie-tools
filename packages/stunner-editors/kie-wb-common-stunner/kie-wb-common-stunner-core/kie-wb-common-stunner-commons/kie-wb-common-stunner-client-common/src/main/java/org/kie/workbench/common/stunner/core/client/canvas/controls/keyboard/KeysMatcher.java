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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;

public class KeysMatcher {

    public static boolean doKeysMatch(final KeyboardEvent.Key[] actualKeys,
                                      final KeyboardEvent.Key... expectedKeys) {
        if (actualKeys == null) {
            return expectedKeys == null;
        } else if (expectedKeys == null) {
            return false;
        }
        if (actualKeys.length != expectedKeys.length) {
            return false;
        }
        final Set<KeyboardEvent.Key> matches = new HashSet<>();
        matches.addAll(Arrays.asList(actualKeys));
        matches.retainAll(Arrays.asList(expectedKeys));

        return matches.size() == expectedKeys.length;
    }

    public static boolean isKeyMatch(final KeyboardEvent.Key[] actualKeys,
                                     final KeyboardEvent.Key... expectedKey) {
        if (actualKeys == null) {
            return expectedKey == null;
        } else if (expectedKey == null) {
            return false;
        }

        final Set<KeyboardEvent.Key> matches = new HashSet<>();
        matches.addAll(Arrays.asList(actualKeys));
        matches.retainAll(Arrays.asList(expectedKey));

        return matches.size() == 1;
    }
}
