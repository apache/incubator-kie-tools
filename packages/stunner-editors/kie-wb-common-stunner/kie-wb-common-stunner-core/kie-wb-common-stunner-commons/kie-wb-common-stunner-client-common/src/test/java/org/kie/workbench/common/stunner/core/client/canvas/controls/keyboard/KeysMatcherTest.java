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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class KeysMatcherTest {

    @Parameterized.Parameters(name = "{index}: matches[{0}] : {1}={2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, new Keys(Key.Z), new Keys(Key.Z)},
                {false, new Keys(Key.Z,
                                 Key.SHIFT), new Keys(Key.Z)},
                {false, new Keys(Key.Z), new Keys(Key.Z,
                                                  Key.SHIFT)},
                {true, new Keys(Key.Z,
                                Key.SHIFT), new Keys(Key.Z,
                                                     Key.SHIFT)},
                {true, new Keys(Key.SHIFT,
                                Key.Z), new Keys(Key.Z,
                                                 Key.SHIFT)},
                {false, new Keys(Key.SHIFT,
                                 Key.DELETE), new Keys(Key.SHIFT,
                                                       Key.Z)}
        });
    }

    @Parameterized.Parameter(0)
    public boolean matches;

    @Parameterized.Parameter(1)
    public Keys keysActual;

    @Parameterized.Parameter(2)
    public Keys keysExpected;

    @Test
    public void checkMatch() {
        final boolean match = KeysMatcher.doKeysMatch(keysActual.keys,
                                                      keysExpected.keys);
        assertEquals(matches,
                     match);
    }

    private static class Keys {

        Key[] keys;

        Keys(final Key... keys) {
            this.keys = keys;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (Key key : keys) {
                sb.append(key).append(" ,");
            }
            sb.setLength(sb.length() - 2);
            sb.append("}");
            return sb.toString();
        }
    }
}
