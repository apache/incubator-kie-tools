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


package org.appformer.kogito.bridge.client.keyboardshortcuts;

import elemental2.dom.EventTarget;
import jsinterop.annotations.JsFunction;
import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;

/**
 * API for registering Keyboard Shortcuts.
 */
public interface KeyboardShortcutsApi {

    /**
     * Register a Keyboard Shortcuts for a keypress event.
     * @param combination The combination of keys that trigger 'onKeyDown' action. This is shown on the Keyboard Shortcuts panel.
     * @param label The label for this Keyboard Shortcut. This is shown on the Keyboard Shortcuts panel. Use a `|` character to separate its section from its description. (e.g. "Moving | Arrow up")
     * @param onKeyDown The action to  be executed when 'combination' is pressed.
     * @param opts Options of this registration.
     *
     * @return An id representing this registration. This id can be used to 'deregister' the Keyboard Shortcut.
     */
    int registerKeyPress(String combination, String label, KeyboardShortcutsApi.Action onKeyDown, KeyboardShortcutsApiOpts opts);

    /**
     * Register a Keyboard Shortcuts for a keypress event.
     * @param combination The combination of keys that trigger 'onKeyDown' action when they're pressed and 'onKeyUp' when they're released. This is shown on the Keyboard Shortcuts panel.
     * @param label The label for this Keyboard Shortcut. This is shown on the Keyboard Shortcuts panel. Use a `|` character to separate its section from its description. (e.g. "Moving | Arrow up")
     * @param onKeyDown The action to  be executed when 'combination' is pressed.
     * @param onKeyUp The action to  be executed when 'combination' is released.
     * @param opts Options of this registration.
     *
     * @return An id representing this registration. This id can be used to 'deregister' the Keyboard Shortcut.
     */
    int registerKeyDownThenUp(String combination, String label, KeyboardShortcutsApi.Action onKeyDown, KeyboardShortcutsApi.Action onKeyUp, KeyboardShortcutsApiOpts opts);

    /**
     * Deregister a Keyboard Shortcut.
     *
     * @param id The id obtained when registering a Keyboard Shortcut.
     */
    void deregister(int id);

    @JsFunction
    @FunctionalInterface
    interface Action {

        void execute(final EventTarget target);
    }
}
