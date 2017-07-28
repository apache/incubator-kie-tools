/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.home.model;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.commons.validation.PortablePreconditions;

public class HomeModel {

    private String welcome;

    private String description;

    private String backgroundImageUrl;

    private List<HomeShortcut> shortcuts = new ArrayList<>();

    // For proxying
    protected HomeModel() {
    }

    public HomeModel(final String welcome,
                     final String description,
                     final String backgroundImageUrl) {
        this.welcome = PortablePreconditions.checkNotNull("welcome",
                                                          welcome);
        this.description = PortablePreconditions.checkNotNull("description",
                                                              description);
        this.backgroundImageUrl = PortablePreconditions.checkNotNull("backgroundImageUrl",
                                                                     backgroundImageUrl);
    }

    public String getWelcome() {
        return welcome;
    }

    public String getDescription() {
        return description;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void addShortcut(final HomeShortcut shortcut) {
        shortcuts.add(PortablePreconditions.checkNotNull("shortcut",
                                                         shortcut));
    }

    public List<HomeShortcut> getShortcuts() {
        return shortcuts;
    }
}
