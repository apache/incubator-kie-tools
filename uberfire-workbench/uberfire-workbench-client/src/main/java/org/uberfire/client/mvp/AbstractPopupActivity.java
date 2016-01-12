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
package org.uberfire.client.mvp;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchPopup.WorkbenchPopupSize;
import org.uberfire.client.workbench.widgets.popup.PopupView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * Implementation of behaviour common to all popup activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchPopup}.
 */
public abstract class AbstractPopupActivity extends AbstractActivity implements PopupActivity {

    private final PopupView popup;

    /**
     * Interlock to prevent a call back into PlaceManager.closePlace() when this activity is already in the process of
     * closing.
     */
    private boolean placeManagerIsClosingUs = false;

    /**
     * Interlock to prevent a call back into PopupView.hide() when the view is already in the process of hiding.
     */
    private boolean popupAlreadyHiding = false;

    /**
     * MVP constructor that allows caller to provide the PopupView instance.
     */
    protected AbstractPopupActivity( final PlaceManager placeManager, final PopupView popupView ) {
        super( placeManager );
        popup = checkNotNull( "popupView", popupView );
    }

    @Override
    public abstract String getTitle();

    @Override
    public WorkbenchPopupSize getSize(){
        return WorkbenchPopupSize.MEDIUM;
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public abstract IsWidget getWidget();

    @Override
    public void onOpen() {
        super.onOpen();

        popup.addCloseHandler( new CloseHandler<PopupView>() {
            @Override
            public void onClose( CloseEvent<PopupView> event ) {
                if ( !placeManagerIsClosingUs ) {
                    try {
                        popupAlreadyHiding = true;
                        placeManager.closePlace( place );
                    } finally {
                        popupAlreadyHiding = false;
                    }
                }
            }
        } );

        final IsWidget widget = getWidget();

        popup.setContent( widget );
        popup.setSize( getSize() );
        popup.setTitle( getTitle() );
        popup.show();
    }

    @Override
    public void onClose() {
        super.onClose();
        if ( !popupAlreadyHiding ) {
            try {
                placeManagerIsClosingUs = true;
                popup.hide();
            } finally {
                placeManagerIsClosingUs = false;
            }
        }
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

}
