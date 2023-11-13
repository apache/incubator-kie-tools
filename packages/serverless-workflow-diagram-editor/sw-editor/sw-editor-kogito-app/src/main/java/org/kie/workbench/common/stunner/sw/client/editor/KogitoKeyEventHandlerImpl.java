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


package org.kie.workbench.common.stunner.sw.client.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import elemental2.dom.DomGlobal;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsApi;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyShortcutCallback;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyShortcutKeyDownThenUp;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControlImpl.SessionKeyShortcutCallback;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;

// TODO: This shoud be no necessary!
// TODO: This is just a copy/paste of org.kie.workbench.common.stunner.kogito.client.session.command.impl.KogitoKeyEventHandlerImpl from stunner-kogito-client

@Dependent
@Alternative
public class KogitoKeyEventHandlerImpl implements KeyEventHandler {

    @Inject
    private KeyboardShortcutsApi keyboardShortcutsApi;

    private final List<Integer> registeredShortcutsIds = new ArrayList<>();

    private boolean enabled = true;

    public KeyEventHandler addKeyShortcutCallback(final KeyboardControl.KeyShortcutCallback shortcutCallback) {

        final Optional<KogitoKeyShortcutCallback> possibleKogitoShortcutCallback = getAssociatedKogitoKeyShortcutCallback(shortcutCallback);
        if (!possibleKogitoShortcutCallback.isPresent() || possibleKogitoShortcutCallback.get().getKeyCombination().length == 0) {
            DomGlobal.console.debug("Not registering: " + shortcutCallback.getClass().getCanonicalName());
            return this;
        }

        final KogitoKeyShortcutCallback kogitoShortcutCallback = possibleKogitoShortcutCallback.get();
        DomGlobal.console.debug("Registering: " + shortcutCallback.getClass().getCanonicalName() + " - " + kogitoShortcutCallback.getLabel());

        //Normal
        if (shortcutCallback instanceof KogitoKeyShortcutKeyDownThenUp) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyDownThenUp(
                    asStringCodes(kogitoShortcutCallback.getKeyCombination()),
                    kogitoShortcutCallback.getLabel(),
                    target -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    target -> runIfEnabled(() -> shortcutCallback.onKeyUp(null)),
                    kogitoShortcutCallback.getOpts()));
        } else if (shortcutCallback instanceof KogitoKeyPress) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyPress(
                    asStringCodes(kogitoShortcutCallback.getKeyCombination()),
                    kogitoShortcutCallback.getLabel(),
                    target -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    kogitoShortcutCallback.getOpts()));
        }

        //Session
        else if (shortcutCallback instanceof SessionKeyShortcutCallback && ((SessionKeyShortcutCallback) shortcutCallback).getDelegate() instanceof KogitoKeyShortcutKeyDownThenUp) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyDownThenUp(
                    asStringCodes(kogitoShortcutCallback.getKeyCombination()),
                    kogitoShortcutCallback.getLabel(),
                    target -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    target -> runIfEnabled(() -> shortcutCallback.onKeyUp(null)),
                    kogitoShortcutCallback.getOpts()));
        } else if (shortcutCallback instanceof SessionKeyShortcutCallback && ((SessionKeyShortcutCallback) shortcutCallback).getDelegate() instanceof KogitoKeyPress) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyPress(
                    asStringCodes(kogitoShortcutCallback.getKeyCombination()),
                    kogitoShortcutCallback.getLabel(),
                    target -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    kogitoShortcutCallback.getOpts()));
        }

        //Default
        else {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyPress(
                    asStringCodes(kogitoShortcutCallback.getKeyCombination()),
                    kogitoShortcutCallback.getLabel(),
                    target -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    KeyboardShortcutsApiOpts.DEFAULT));
        }

        return this;
    }

    private String asStringCodes(final KeyboardEvent.Key[] keyCombination) {
        return Arrays.stream(keyCombination).map(KeyboardEvent.Key::getStringCode).collect(Collectors.joining("+"));
    }

    private void runIfEnabled(final Runnable runnable) {
        if (this.enabled) {
            runnable.run();
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @PreDestroy
    public void clear() {
        registeredShortcutsIds.forEach(keyboardShortcutsApi::deregister);
    }
}
