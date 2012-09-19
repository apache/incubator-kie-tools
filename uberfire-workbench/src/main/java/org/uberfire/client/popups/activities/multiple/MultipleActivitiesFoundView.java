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
package org.uberfire.client.popups.activities.multiple;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class MultipleActivitiesFoundView extends PopupPanel
    implements
    MultipleActivitiesFoundPresenter.View {

    interface MultipleActivitiesFoundViewBinder
            extends
            UiBinder<Widget, MultipleActivitiesFoundView> {
    }

    private static MultipleActivitiesFoundViewBinder  uiBinder = GWT.create(MultipleActivitiesFoundViewBinder.class);;

    @UiField
    public Label                                      requestedPlaceIdentifierLabel;

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );
        setGlassEnabled( true );
    }

    @Override
    public void setRequestedPlaceIdentifier(String requestedPlaceIdentifier) {
        requestedPlaceIdentifierLabel.setText( requestedPlaceIdentifier );
    }

    @Override
    public void show() {
        super.show();
        center();
    }

    @UiHandler("okButton")
    public void onClickOkButton(final ClickEvent event) {
        hide();
    }

}
