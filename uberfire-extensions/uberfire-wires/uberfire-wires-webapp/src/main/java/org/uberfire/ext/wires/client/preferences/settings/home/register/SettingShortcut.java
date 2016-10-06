/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.client.preferences.settings.home.register;

import org.uberfire.mvp.Command;

public class SettingShortcut {

    private String identifier;

    private String title;

    private String iconCss;

    private String category;

    private Command command;

    public SettingShortcut() {
    }

    public SettingShortcut( final String identifier,
                            final String title,
                            final String iconCss,
                            final String category,
                            final Command command ) {
        this.identifier = identifier;
        this.title = title;
        this.iconCss = iconCss;
        this.category = category;
        this.command = command;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getIconCss() {
        return iconCss;
    }

    public String getCategory() {
        return category;
    }

    public Command getCommand() {
        return command;
    }
}
