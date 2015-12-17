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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;



@ApplicationScoped
@WorkbenchScreen ( identifier = "AdvancedDomainScreen")
public class AdvancedDomainScreen {

    AdvancedDomainScreenView view;

    public AdvancedDomainScreen() {
    }

    @Inject
    public AdvancedDomainScreen( AdvancedDomainScreenView view ) {
        this.view = view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.advanced_domain_screen_name();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
