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

package org.uberfire.client.workbench.widgets.listbar;

public class ListbarPreferences {

    private boolean contextEnabled;

    private boolean hideTitleDropDownOnSingleElement = true;

    public ListbarPreferences() {

    }

    public ListbarPreferences( boolean contextEnabled ) {
        this.contextEnabled = contextEnabled;
    }

    public boolean isContextEnabled() {
        return contextEnabled;
    }

    public boolean isHideTitleDropDownOnSingleElement() {
        return hideTitleDropDownOnSingleElement;
    }

    public void setHideTitleDropDownOnSingleElement( boolean hideTitleDropDownOnSingleElement ) {
        this.hideTitleDropDownOnSingleElement = hideTitleDropDownOnSingleElement;
    }
}
