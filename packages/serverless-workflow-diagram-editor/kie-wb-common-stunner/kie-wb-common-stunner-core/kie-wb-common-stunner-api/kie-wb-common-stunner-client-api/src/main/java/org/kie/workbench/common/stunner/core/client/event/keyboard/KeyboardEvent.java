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

    // This class is taken from GWT's com.google.gwt.event.dom.client.KeyCodes
    class KeyCodes {

        /**
         * Key code for A
         */
        public static final int KEY_A = 65;
        /**
         * Key code for B
         */
        public static final int KEY_B = 66;
        /**
         * Key code for C
         */
        public static final int KEY_C = 67;
        /**
         * Key code for D
         */
        public static final int KEY_D = 68;
        /**
         * Key code for E
         */
        public static final int KEY_E = 69;
        /**
         * Key code for F
         */
        public static final int KEY_F = 70;
        /**
         * Key code for G
         */
        public static final int KEY_G = 71;
        /**
         * Key code for H
         */
        public static final int KEY_H = 72;
        /**
         * Key code for I
         */
        public static final int KEY_I = 73;
        /**
         * Key code for J
         */
        public static final int KEY_J = 74;
        /**
         * Key code for K
         */
        public static final int KEY_K = 75;
        /**
         * Key code for L
         */
        public static final int KEY_L = 76;
        /**
         * Key code for M
         */
        public static final int KEY_M = 77;
        /**
         * Key code for N
         */
        public static final int KEY_N = 78;
        /**
         * Key code for O
         */
        public static final int KEY_O = 79;
        /**
         * Key code for P
         */
        public static final int KEY_P = 80;
        /**
         * Key code for Q
         */
        public static final int KEY_Q = 81;
        /**
         * Key code for R
         */
        public static final int KEY_R = 82;
        /**
         * Key code for S
         */
        public static final int KEY_S = 83;
        /**
         * Key code for T
         */
        public static final int KEY_T = 84;
        /**
         * Key code for U
         */
        public static final int KEY_U = 85;
        /**
         * Key code for V
         */
        public static final int KEY_V = 86;
        /**
         * Key code for W
         */
        public static final int KEY_W = 87;
        /**
         * Key code for X
         */
        public static final int KEY_X = 88;
        /**
         * Key code for Y
         */
        public static final int KEY_Y = 89;
        /**
         * Key code for Z
         */
        public static final int KEY_Z = 90;

        /**
         * Key code number 0
         */
        public static final int KEY_ZERO = 48;
        /**
         * Key code number 1
         */
        public static final int KEY_ONE = 49;
        /**
         * Key code number 2
         */
        public static final int KEY_TWO = 50;
        /**
         * Key code number 3
         */
        public static final int KEY_THREE = 51;
        /**
         * Key code number 4
         */
        public static final int KEY_FOUR = 52;
        /**
         * Key code number 5
         */
        public static final int KEY_FIVE = 53;
        /**
         * Key code number 6
         */
        public static final int KEY_SIX = 54;
        /**
         * Key code number 7
         */
        public static final int KEY_SEVEN = 55;
        /**
         * Key code number 8
         */
        public static final int KEY_EIGHT = 56;
        /**
         * Key code number 9
         */
        public static final int KEY_NINE = 57;

        /**
         * Key code for number 0 on numeric keyboard
         */
        public static final int KEY_NUM_ZERO = 96;
        /**
         * Key code for number 1 on numeric keyboard
         */
        public static final int KEY_NUM_ONE = 97;
        /**
         * Key code for number 2 on numeric keyboard
         */
        public static final int KEY_NUM_TWO = 98;
        /**
         * Key code for number 3 on numeric keyboard
         */
        public static final int KEY_NUM_THREE = 99;
        /**
         * Key code for number 4 on numeric keyboard
         */
        public static final int KEY_NUM_FOUR = 100;
        /**
         * Key code for number 5 on numeric keyboard
         */
        public static final int KEY_NUM_FIVE = 101;
        /**
         * Key code for number 6 on numeric keyboard
         */
        public static final int KEY_NUM_SIX = 102;
        /**
         * Key code for number 7 on numeric keyboard
         */
        public static final int KEY_NUM_SEVEN = 103;
        /**
         * Key code for number 8 on numeric keyboard
         */
        public static final int KEY_NUM_EIGHT = 104;
        /**
         * Key code for number 9 on numeric keyboard
         */
        public static final int KEY_NUM_NINE = 105;
        /**
         * Key code for multiply on numeric keyboard
         */
        public static final int KEY_NUM_MULTIPLY = 106;
        /**
         * Key code for plus on numeric keyboard
         */
        public static final int KEY_NUM_PLUS = 107;
        /**
         * Key code for minus on numeric keyboard
         */
        public static final int KEY_NUM_MINUS = 109;
        /**
         * Key code for period on numeric keyboard
         */
        public static final int KEY_NUM_PERIOD = 110;
        /**
         * Key code for division on numeric keyboard
         */
        public static final int KEY_NUM_DIVISION = 111;
        /**
         * Alt key code.
         */
        public static final int KEY_ALT = 18;

        /**
         * Backspace key code.
         */
        public static final int KEY_BACKSPACE = 8;
        /**
         * Control key code.
         */
        public static final int KEY_CTRL = 17;

        /**
         * Delete key code (also numeric keypad delete).
         */
        public static final int KEY_DELETE = 46;

        /**
         * Down arrow code (Also numeric keypad down).
         */
        public static final int KEY_DOWN = 40;

        /**
         * Escape key code.
         */
        public static final int KEY_ESCAPE = 27;

        /**
         * Left key code (Also numeric keypad west).
         */
        public static final int KEY_LEFT = 37;

        /**
         * Right arrow key code (Also numeric keypad east).
         */
        public static final int KEY_RIGHT = 39;

        /**
         * Shift key code.
         */
        public static final int KEY_SHIFT = 16;

        /**
         * Up Arrow key code (Also numeric keypad north).
         */
        public static final int KEY_UP = 38;

        private KeyCodes() {
        }
    }
}
