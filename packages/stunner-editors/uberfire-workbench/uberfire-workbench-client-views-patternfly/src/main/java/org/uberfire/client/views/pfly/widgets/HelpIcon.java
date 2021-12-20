/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Placement;

public class HelpIcon extends Composite {

    private Icon icon;
    private SimplePanel panel;
    private Popover popover;

    public HelpIcon() {
        this(new Icon(),
             new SimplePanel(),
             new Popover());
    }

    // Defined for testing purposes
    HelpIcon(Icon icon,
             SimplePanel panel,
             Popover popover) {
        this.icon = icon;
        this.popover = popover;
        this.panel = panel;

        init();
    }

    private void init() {
        initWidget(panel);
        addStyleName("uf-help-icon");

        icon.setType(IconType.INFO_CIRCLE);

        popover.setWidget(icon);
        popover.setContainer("body");
        popover.setIsHtml(true);
        popover.setPlacement(Placement.AUTO);

        panel.setWidget(popover);
    }

    public void setHelpTitle(final String title) {
        popover.setTitle(title);
        popover.reconfigure();
    }

    public void setHelpContent(final String content) {
        popover.setContent(content);
        popover.reconfigure();
    }
}
