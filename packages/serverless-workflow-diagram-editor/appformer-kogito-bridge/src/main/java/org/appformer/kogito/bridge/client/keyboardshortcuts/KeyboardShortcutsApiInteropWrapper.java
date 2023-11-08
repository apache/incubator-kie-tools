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

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;

/**
 * Javascript bridge to access actual KeyboardShortcutsApi available in the envelope namespace
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "envelope")
public class KeyboardShortcutsApiInteropWrapper {

    public native int registerKeyPress(final String combination,
                                       final String label,
                                       final KeyboardShortcutsApi.Action onKeyDown,
                                       final KeyboardShortcutsApiOpts opts);

    public native int registerKeyDownThenUp(final String combination,
                                            final String label,
                                            final KeyboardShortcutsApi.Action onKeyDown,
                                            final KeyboardShortcutsApi.Action onKeyUp,
                                            final KeyboardShortcutsApiOpts opts);

    public native void deregister(final int id);

    @JsProperty(name = "keyboardShortcutsService")
    public static native KeyboardShortcutsApiInteropWrapper get();
}
