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

package org.uberfire.client.editors.test;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A stand-alone (i.e. devoid of Workbench dependencies) View
 */
public class TestView extends Composite
    implements
    RequiresResize,
    TestPresenter.View {

    interface ViewBinder
            extends
            UiBinder<Widget, TestView> {
    }

    private static ViewBinder        uiBinder = GWT.create( ViewBinder.class );

    @UiField
    public SimplePanel               panel;

    @Inject
    private Event<NotificationEvent> notification;

    private TestPresenter            presenter;

    @Override
    public void init(final TestPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        panel.setPixelSize( width,
                            height );
    }

    @UiHandler("notificationButton")
    public void onClickNotificationButton(final ClickEvent event) {
        notification.fire( new NotificationEvent( "Something happened" ) );
    }

    @UiHandler("launchWithPlaceRequestAndCallback")
    public void onClickLaunchWithCallbackPlaceRequest(final ClickEvent event) {
        presenter.launchWithCallbackPlaceRequest();
    }

}