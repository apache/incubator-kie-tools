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
package org.uberfire.workbench.model.toolbar.impl;

import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.toolbar.IconType;
import org.uberfire.workbench.model.toolbar.ToolBarIcon;
import org.uberfire.workbench.model.toolbar.ToolBarItem;
import org.uberfire.workbench.model.toolbar.ToolBarTypeIcon;
import org.uberfire.workbench.model.toolbar.ToolBarUrlIcon;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Default implementation of ToolBarItem
 */
public class DefaultToolBarItem
        implements
        ToolBarItem {

    private final ToolBarIcon icon;

    private final String tooltip;

    private final Command command;

    private boolean isEnabled = true;

    public DefaultToolBarItem(final String url,
                              final String tooltip,
                              final Command command) {
        this.tooltip = checkNotNull("tooltip",
                                    tooltip);
        this.command = checkNotNull("command",
                                    command);
        checkNotNull("url",
                     url);
        this.icon = new ToolBarUrlIcon() {
            @Override
            public String getUrl() {
                return url;
            }
        };
    }

    public DefaultToolBarItem(final IconType iconType,
                              final String tooltip,
                              final Command command) {
        this.tooltip = checkNotNull("tooltip",
                                    tooltip);
        this.command = checkNotNull("command",
                                    command);
        checkNotNull("iconType",
                     iconType);
        this.icon = new ToolBarTypeIcon() {
            @Override
            public IconType getType() {
                return iconType;
            }
        };
    }

    @Override
    public String getIdentifier() {
        return tooltip;
    }

    @Override
    public String getTooltip() {
        return this.tooltip;
    }

    @Override
    public ToolBarIcon getIcon() {
        return icon;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public Command getCommand() {
        return this.command;
    }
}
