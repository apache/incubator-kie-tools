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

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

public class NavigatorOptions {

    private boolean showFiles = true;
    private boolean showHiddenFiles = false;
    private boolean showDirectories = true;
    private boolean allowUpLink = true;
    private boolean showItemAge = true;
    private boolean showItemLastUpdater = true;
    private boolean showItemMessage = true;

    public boolean showFiles() {
        return showFiles;
    }

    public boolean showHiddenFiles() {
        return showHiddenFiles;
    }

    public boolean showDirectories() {
        return showDirectories;
    }

    public boolean allowUpLink() {
        return allowUpLink;
    }

    public boolean showItemAge() {
        return showItemAge;
    }

    public boolean showItemLastUpdater() {
        return showItemLastUpdater;
    }

    public void showFiles( boolean value ) {
        this.showFiles = value;
    }

    public boolean showItemMessage() {
        return showItemMessage;
    }

    public void showHiddenFiles( boolean value ) {
        this.showHiddenFiles = value;
    }

    public void showDirectories( boolean value ) {
        this.showDirectories = value;
    }

    public void allowUpLink( boolean value ) {
        this.allowUpLink = value;
    }

    public void showItemAge( boolean value ) {
        this.showItemAge = value;
    }

    public void showItemMessage( boolean value ) {
        this.showItemMessage = value;
    }

    public void showItemLastUpdater( boolean value ) {
        this.showItemLastUpdater = value;
    }

}
