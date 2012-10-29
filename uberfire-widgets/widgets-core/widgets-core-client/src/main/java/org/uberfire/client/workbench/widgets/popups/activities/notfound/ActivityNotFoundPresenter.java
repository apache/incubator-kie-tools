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
package org.uberfire.client.workbench.widgets.popups.activities.notfound;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 */
@Dependent
@WorkbenchPopup(identifier = "workbench.activity.notfound")
public class ActivityNotFoundPresenter {

    public interface View
        extends
        IsWidget {

        void setRequestedPlaceIdentifier(final String requestedPlaceIdentifier);

        void show();
    }

    @Inject
    private View         view;

    @Inject
    private PlaceManager placeManager;

    @OnReveal
    public void onReveal() {
        final String identifier = placeManager.getCurrentPlaceRequest().getParameter( "requestedPlaceIdentifier",
                                                                                      null );
        view.setRequestedPlaceIdentifier( identifier );
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return (PopupPanel) view;
    }

}
