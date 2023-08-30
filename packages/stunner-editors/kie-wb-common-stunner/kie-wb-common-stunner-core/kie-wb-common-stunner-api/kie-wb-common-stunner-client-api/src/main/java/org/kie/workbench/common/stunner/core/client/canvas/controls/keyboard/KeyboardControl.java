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

import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

public interface KeyboardControl<C extends Canvas, S extends ClientSession> extends CanvasControl<C>,
                                                                                    CanvasControl.SessionAware<S> {

    KeyboardControl<C, S> addKeyShortcutCallback(final KeyShortcutCallback shortcutCallback);

    void setKeyEventHandlerEnabled(final boolean enabled);

    interface KeyShortcutCallback {

        void onKeyShortcut(final KeyboardEvent.Key... keys);

        default void onKeyUp(final KeyboardEvent.Key key) {
        }
    }

    //
    //
    //Kogito

    interface KogitoKeyShortcutCallback extends KeyShortcutCallback {

        KeyboardShortcutsApiOpts getOpts();

        KeyboardEvent.Key[] getKeyCombination();

        /**
         * @return The label of this shortcut. Use a `|` character to separate its section from its description. (e.g. "Moving | Up")
         */
        String getLabel();
    }

    class KogitoKeyPress implements KogitoKeyShortcutCallback {

        private KeyboardEvent.Key[] combination;
        private String label;
        private Runnable onKeyDown;
        private KeyboardShortcutsApiOpts opts;

        public KogitoKeyPress() {
        }

        public KogitoKeyPress(final KeyboardEvent.Key[] combination, final String label, final Runnable onKeyDown) {
            this(combination, label, onKeyDown, KeyboardShortcutsApiOpts.DEFAULT);
        }

        public KogitoKeyPress(final KeyboardEvent.Key[] combination, final String label, final Runnable onKeyDown, final KeyboardShortcutsApiOpts opts) {
            this.combination = combination;
            this.label = label;
            this.onKeyDown = onKeyDown;
            this.opts = opts;
        }

        @Override
        public final void onKeyShortcut(final KeyboardEvent.Key... keys) {
            onKeyDown();
        }

        @Override
        public final void onKeyUp(final KeyboardEvent.Key key) {
            throw new RuntimeException("Keyup shouldn't be called on KeyPress events");
        }

        @Override
        public KeyboardEvent.Key[] getKeyCombination() {
            return combination;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public void onKeyDown() {
            onKeyDown.run();
        }

        @Override
        public KeyboardShortcutsApiOpts getOpts() {
            return opts;
        }
    }

    class KogitoKeyShortcutKeyDownThenUp implements KogitoKeyShortcutCallback {

        private final KeyboardEvent.Key[] combination;
        private final String label;
        private final Runnable onKeyDown;
        private final Runnable onKeyUp;
        private final KeyboardShortcutsApiOpts opts;

        public KogitoKeyShortcutKeyDownThenUp(final KeyboardEvent.Key[] combination, final String label, final Runnable onKeyDown, final Runnable onKeyUp) {
            this(combination, label, onKeyDown, onKeyUp, KeyboardShortcutsApiOpts.DEFAULT);
        }

        public KogitoKeyShortcutKeyDownThenUp(final KeyboardEvent.Key[] combination, final String label, final Runnable onKeyDown, final Runnable onKeyUp, final KeyboardShortcutsApiOpts opts) {
            this.combination = combination;
            this.label = label;
            this.onKeyDown = onKeyDown;
            this.onKeyUp = onKeyUp;
            this.opts = opts;
        }

        @Override
        public final void onKeyShortcut(final KeyboardEvent.Key... keys) {
            onKeyDown();
        }

        @Override
        public final void onKeyUp(final KeyboardEvent.Key key) {
            onKeyUp();
        }

        @Override
        public KeyboardEvent.Key[] getKeyCombination() {
            return combination;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public void onKeyDown() {
            onKeyDown.run();
        }

        public void onKeyUp() {
            onKeyUp.run();
        }

        @Override
        public KeyboardShortcutsApiOpts getOpts() {
            return opts;
        }
    }
}
