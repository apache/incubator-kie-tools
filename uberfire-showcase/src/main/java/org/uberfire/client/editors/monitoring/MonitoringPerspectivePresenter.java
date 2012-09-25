/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.editors.monitoring;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.security.annotations.Roles;

@Dependent
@WorkbenchScreen(identifier = "Monitoring")
@Roles({"ADMIN", "NON_EXISTENT"})
public class MonitoringPerspectivePresenter {

    public interface MyView
        extends
        IsWidget {
    }

    @Inject
    MyView view;

    public MonitoringPerspectivePresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Monitoring perspective";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
