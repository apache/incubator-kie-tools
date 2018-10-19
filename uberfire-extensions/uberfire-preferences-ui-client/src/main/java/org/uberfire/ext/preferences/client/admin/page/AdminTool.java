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

package org.uberfire.ext.preferences.client.admin.page;

import java.util.Set;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class AdminTool {

    private String title;

    private Set<String> iconCss;

    private String category;

    private Command onClickCommand;

    private ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand;

    public AdminTool() {
    }

    public AdminTool(final String title,
                     final Set<String> iconCss,
                     final String category,
                     final Command onClickCommand) {
        this(title,
             iconCss,
             category,
             onClickCommand,
             null);
    }

    public AdminTool(final String title,
                     final Set<String> iconCss,
                     final String category,
                     final Command onClickCommand,
                     final ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand) {
        this.title = title;
        this.iconCss = iconCss;
        this.category = category;
        this.onClickCommand = onClickCommand;
        this.counterCommand = counterCommand;
    }

    public String getTitle() {
        return title;
    }

    public Set<String> getIconCss() {
        return iconCss;
    }

    public String getCategory() {
        return category;
    }

    public Command getOnClickCommand() {
        return onClickCommand;
    }

    public boolean hasCounter() {
        return this.counterCommand != null;
    }

    public void fetchCounter(ParameterizedCommand<Integer> callback) {
        counterCommand.execute(callback);
    }
}
