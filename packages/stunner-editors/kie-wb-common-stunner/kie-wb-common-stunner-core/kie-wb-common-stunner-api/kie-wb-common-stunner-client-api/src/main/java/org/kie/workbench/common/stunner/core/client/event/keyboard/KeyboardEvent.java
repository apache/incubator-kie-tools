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


package org.kie.workbench.common.stunner.core.client.event.keyboard;

import com.google.gwt.event.dom.client.KeyCodes;

public interface KeyboardEvent {

    Key getKey();

    enum Key {
        ESC("esc", KeyCodes.KEY_ESCAPE),
        CONTROL("ctrl", KeyCodes.KEY_CTRL),
        KEY_BACKSPACE("backspace", KeyCodes.KEY_BACKSPACE),
        ALT("alt", KeyCodes.KEY_ALT),
        SHIFT("shift", KeyCodes.KEY_SHIFT),
        DELETE("delete", KeyCodes.KEY_DELETE),
        ARROW_UP("up", KeyCodes.KEY_UP),
        ARROW_DOWN("down", KeyCodes.KEY_DOWN),
        ARROW_LEFT("left", KeyCodes.KEY_LEFT),
        ARROW_RIGHT("right", KeyCodes.KEY_RIGHT),
        C("c", KeyCodes.KEY_C),
        D("d", KeyCodes.KEY_D),
        E("e", KeyCodes.KEY_E),
        G("g", KeyCodes.KEY_G),
        S("s", KeyCodes.KEY_S),
        T("t", KeyCodes.KEY_T),
        V("v", KeyCodes.KEY_V),
        X("x", KeyCodes.KEY_X),
        Z("z", KeyCodes.KEY_Z);
        private final int unicharCode;

        private final String stringCode;

        Key(final String stringCode, final int unicharCode) {
            this.stringCode = stringCode;
            this.unicharCode = unicharCode;
        }

        public int getUnicharCode() {
            return unicharCode;
        }

        public String getStringCode() {
            return stringCode;
        }
    }
}
