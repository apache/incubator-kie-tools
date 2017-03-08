/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.BaseScreen;

@Dependent
@WorkbenchScreen(identifier = SecurityManagementHomeScreen.SCREEN_ID)
public class SecurityManagementHomeScreen {

    public static final String SCREEN_ID = "SecurityManagementHomeScreen";
    @Inject
    BaseScreen baseScreen;
    @Inject
    View view;

    @PostConstruct
    public void init() {
        view.init(this);
        baseScreen.init(view);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return UsersManagementWorkbenchConstants.INSTANCE.securityManagement();
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return baseScreen.asWidget();
    }

    public interface View extends UberView<SecurityManagementHomeScreen> {

    }
}
