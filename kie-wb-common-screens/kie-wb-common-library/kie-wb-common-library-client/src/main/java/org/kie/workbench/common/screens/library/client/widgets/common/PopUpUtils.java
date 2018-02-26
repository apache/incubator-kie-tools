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

package org.kie.workbench.common.screens.library.client.widgets.common;

import java.util.Arrays;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

public class PopUpUtils {

    public static Button button(final String text,
                                final Command command,
                                final ButtonType type) {
        Button button = new Button(text,
                                   event -> command.execute());
        button.setType(type);
        return button;
    }

    public static ModalFooter footer(Widget... buttons) {
        GenericModalFooter footer = new GenericModalFooter();
        Arrays.stream(buttons).forEach(widget -> footer.add(widget));
        return footer;
    }
}
