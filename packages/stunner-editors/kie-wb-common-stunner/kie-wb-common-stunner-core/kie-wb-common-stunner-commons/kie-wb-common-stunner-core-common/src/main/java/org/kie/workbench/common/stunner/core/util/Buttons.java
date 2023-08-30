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


package org.kie.workbench.common.stunner.core.util;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

public class Buttons {

    public static class Builder {

        private Button button;

        public Builder() {
            button = new Button();
        }

        public Button build() {
            return button;
        }

        public Builder setToggleCaret(boolean toggleCaret) {
            button.setToggleCaret(toggleCaret);
            return this;
        }

        public Builder setDataToggle(Toggle toggle) {
            button.setDataToggle(toggle);
            return this;
        }

        public Builder setIcon(IconType iconType) {
            button.setIcon(iconType);
            return this;
        }

        public Builder setSize(ButtonSize buttonSize) {
            button.setSize(buttonSize);
            return this;
        }

        public Builder setTitle(String title) {
            button.setTitle(title);
            return this;
        }

        public Builder setText(String text) {
            button.setText(text);
            return this;
        }
    }
}
