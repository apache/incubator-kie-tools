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

import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;

/**
 * This {@link KeyboardShortcutsApi} implementation is used when the envelope API is not available
 */
public class NoOpKeyboardShortcutsService implements KeyboardShortcutsApi {

    @Override
    public int registerKeyPress(String combination, String label, KeyboardShortcutsApi.Action onKeyDown, KeyboardShortcutsApiOpts opts) {
        return 0;
    }

    @Override
    public int registerKeyDownThenUp(String combination, String label, KeyboardShortcutsApi.Action onKeyDown, KeyboardShortcutsApi.Action onKeyUp, KeyboardShortcutsApiOpts opts) {
        return 0;
    }

    @Override
    public void deregister(int id) {
    }
}
