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
package org.uberfire.client.mvp;

import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.workbench.widgets.popup.PopupView;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Implementation of behaviour common to all popup activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchPopup}.
 */
public abstract class AbstractPopupActivity extends AbstractActivity implements PopupActivity {

    private final PopupView popup = new PopupView();

    public AbstractPopupActivity( final PlaceManager placeManager ) {
        super( placeManager );

        // this handler notifies PlaceManager to clean up after the popup's view has been closed
        popup.addCloseHandler( new CloseHandler<PopupView>() {
            @Override
            public void onClose( CloseEvent<PopupView> event ) {
                placeManager.closePlace( place );
            }
        } );
    }

    @Override
    public abstract String getTitle();

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public abstract IsWidget getWidget();

    @Override
    public boolean onMayClose() {
        return true;
    }

}
