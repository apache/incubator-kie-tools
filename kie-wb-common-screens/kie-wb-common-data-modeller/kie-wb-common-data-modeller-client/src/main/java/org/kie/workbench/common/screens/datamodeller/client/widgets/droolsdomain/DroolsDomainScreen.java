/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@ApplicationScoped
@WorkbenchScreen( identifier = "DroolsDomainScreen" )
public class DroolsDomainScreen {

    private DroolsDomainScreenView view;

    public DroolsDomainScreen() {
    }

    @Inject
    public DroolsDomainScreen( DroolsDomainScreenView view ) {
        this.view = view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Drools & jBPM";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
