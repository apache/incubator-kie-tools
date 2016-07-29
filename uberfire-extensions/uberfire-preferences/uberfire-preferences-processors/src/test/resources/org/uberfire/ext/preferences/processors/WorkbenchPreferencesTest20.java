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

package org.uberfire.ext.preferences.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.preferences.client.mvp.WorkbenchPreferences;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchPreferences(identifier = "test20")
public class WorkbenchPreferencesTest20 {

    @WorkbenchPartView
    private IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    private String getTitle() {
        return "title";
    }

    @OnStartup
    private void onStartup() {
    }

    @OnMayClose
    private boolean onMayClose() {
        return true;
    }

    @OnClose
    private void onClose() {
    }

    @OnOpen
    private void onOpen() {
    }

    @OnLostFocus
    private void onLostFocus() {
    }

    @OnFocus
    private void onFocus() {
    }

}