/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.workbench.screens.activities.multiple;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchScreen;
import org.drools.guvnor.client.mvp.PlaceManager;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
@Dependent
@WorkbenchScreen(identifier = "org.drools.guvnor.client.workbench.screens.activities.multiple")
public class MultipleActivitiesFoundPresenter {

    public interface View
        extends
        IsWidget {

        void setIdentifer(final String identifier);
    }

    @Inject
    private View         view;

    @Inject
    private PlaceManager placeManager;

    @OnStart
    public void onStart() {
        final String identifier = placeManager.getCurrentPlaceRequest().getParameter( "identifier",
                                                                                      null );
        view.setIdentifer( identifier );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Warning - Multiple";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
