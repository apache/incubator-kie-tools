/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.util;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;

public class ButtonGroups {

    public static class Builder {

        private ButtonGroup buttonGroup;

        public Builder() {
            buttonGroup = new ButtonGroup();
        }

        public Builder add(Button button) {
            buttonGroup.add(button);
            return this;
        }

        public Builder add(DropDownMenu dropDownMenu) {
            buttonGroup.add(dropDownMenu);
            return this;
        }

        public ButtonGroup build() {
            return buttonGroup;
        }
    }
}
