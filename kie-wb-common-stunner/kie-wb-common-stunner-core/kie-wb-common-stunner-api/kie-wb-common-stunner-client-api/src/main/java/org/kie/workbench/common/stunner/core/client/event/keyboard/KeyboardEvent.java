/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.event.keyboard;

import com.google.gwt.event.dom.client.KeyCodes;

public interface KeyboardEvent {

    enum Key {
        ESC(KeyCodes.KEY_ESCAPE),
        CONTROL(KeyCodes.KEY_CTRL),
        ALT(KeyCodes.KEY_ALT),
        SHIFT(KeyCodes.KEY_SHIFT),
        DELETE(KeyCodes.KEY_DELETE),
        ARROW_UP(KeyCodes.KEY_UP),
        ARROW_DOWN(KeyCodes.KEY_DOWN),
        ARROW_LEFT(KeyCodes.KEY_LEFT),
        ARROW_RIGHT(KeyCodes.KEY_RIGHT),
        C(KeyCodes.KEY_C),
        D(KeyCodes.KEY_D),
        E(KeyCodes.KEY_E),
        G(KeyCodes.KEY_G),
        S(KeyCodes.KEY_S),
        T(KeyCodes.KEY_T),
        V(KeyCodes.KEY_V),
        X(KeyCodes.KEY_X),
        Z(KeyCodes.KEY_Z);

        private final int unicharCode;

        Key(final int unicharCode) {
            this.unicharCode = unicharCode;
        }

        public int getUnicharCode() {
            return unicharCode;
        }
    }

    Key getKey();
}
