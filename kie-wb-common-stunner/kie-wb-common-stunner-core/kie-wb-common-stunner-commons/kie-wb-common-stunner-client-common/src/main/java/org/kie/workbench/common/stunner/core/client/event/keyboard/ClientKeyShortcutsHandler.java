/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.event.keyboard;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.Timer;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A helper class for component that listen to keyboard events.
 * It provides keyboard shortcuts support by listening for
 * multiple key events.
 */
@Dependent
public class ClientKeyShortcutsHandler {

    private final static int KEYS_TIMER_DELAY = 250;

    public interface KeyShortcutCallback {

        void onKeyShortcut(final KeyboardEvent.Key... keys);
    }

    private final List<KeyboardEvent.Key> keys = new ArrayList<>();
    private KeyShortcutCallback shortcutCallback;
    private KeyboardEvent.Key[] _keys;
    private Timer timer;

    public void setKeyShortcutCallback(final KeyShortcutCallback shortcutCallback) {
        this.shortcutCallback = shortcutCallback;
    }

    public static boolean isSameShortcut(final KeyboardEvent.Key[] keys1,
                                         final KeyboardEvent.Key... keys2) {
        for (int i = 0; i < keys1.length; i++) {
            if (!keys2[i].equals(keys1[i])) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        if (null != timer && timer.isRunning()) {
            timer.cancel();
            timer = null;
        }
        shortcutCallback = null;
        keys.clear();
        _keys = null;
    }

    void onKeyUpEvent(final @Observes KeyUpEvent event) {
        checkNotNull("event",
                     event);
        onKeyUp(event.getKey());
    }

    void onKeyDownEvent(final @Observes KeyDownEvent event) {
        checkNotNull("event",
                     event);
        onKeyDown(event.getKey());
    }

    private void onKeyDown(final KeyboardEvent.Key key) {
        if (null != shortcutCallback) {
            startKeysTimer(key);
        }
    }

    private void onKeyUp(final KeyboardEvent.Key key) {
        keys.remove(key);
    }

    private void startKeysTimer(final KeyboardEvent.Key key) {
        keys.add(key);
        this._keys = keys.toArray(new KeyboardEvent.Key[this.keys.size()]);
        if (null == timer) {
            timer = new Timer() {
                @Override
                public void run() {
                    ClientKeyShortcutsHandler.this.keysTimerTimeIsUp();
                }
            };
        }
        timer.schedule(KEYS_TIMER_DELAY);
    }

    void keysTimerTimeIsUp() {
        if (null != shortcutCallback && null != _keys) {
            shortcutCallback.onKeyShortcut(_keys);
        }
        _keys = null;
        timer.cancel();
        timer = null;
    }
}
