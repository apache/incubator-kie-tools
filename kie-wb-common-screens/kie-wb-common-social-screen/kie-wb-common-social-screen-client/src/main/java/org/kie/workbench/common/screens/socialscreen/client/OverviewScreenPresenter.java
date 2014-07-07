/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.socialscreen.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.PlaceRequest;

public abstract class OverviewScreenPresenter
        implements IsWidget {

    protected ObservablePath path;
    protected PlaceRequest place;
    protected boolean isReadOnly = false;
    protected String version;
    protected OverviewScreenView view;

    public OverviewScreenPresenter(OverviewScreenView view) {
        this.view = view;
    }


}
