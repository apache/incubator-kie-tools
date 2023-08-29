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


package org.appformer.client.keyboardShortcuts;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class KeyboardShortcutsApiOpts {

    public static final KeyboardShortcutsApiOpts DEFAULT = new KeyboardShortcutsApiOpts(Repeat.NO_REPEAT);

    private final Repeat repeat;

    public KeyboardShortcutsApiOpts(final Repeat repeat) {
        this.repeat = repeat;
    }

    @JsProperty
    public boolean getRepeat() {
        return Repeat.REPEAT.equals(repeat);
    }

    /**
     * Repetition mode for Keyboard Shortcuts.
     * <p>
     * REPEAT - When pressing and holding a key, the same action will be fired multiple times.
     * NO_REPEAT - When pressing and holding a key, the action will be only fired once.
     */
    public enum Repeat {
        REPEAT,
        NO_REPEAT
    }
}
