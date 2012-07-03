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

package org.drools.guvnor.client.editors.admin1;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.Title;
import org.drools.guvnor.client.annotations.WorkbenchWidget;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchWidget(nameToken = "MyAdminArea")
public class MyAdminAreaPresenter {

    public interface View
        extends
        IsWidget {

        void setName(String name);
    }

    @Inject
    public View view;

    public MyAdminAreaPresenter() {
    }

    @OnStart
    public void onStart() {
        view.setName( "MyAdminArea" );
    }
    
    @Title
    public String getTitle() {
        return "MyAdminArea";
    }

    @org.drools.guvnor.client.annotations.View
    public IsWidget getView() {
        return view;
    }



}