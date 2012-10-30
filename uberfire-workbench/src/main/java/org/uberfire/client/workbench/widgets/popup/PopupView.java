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

package org.uberfire.client.workbench.widgets.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.mvp.UberView;

/**
 * Skeleton for popups
 */
@Dependent
public class PopupView extends DecoratedPopupPanel implements UberView<AbstractPopupActivity> {

    interface PopupViewBinder
            extends
            UiBinder<Panel, PopupView> {

    }

    private AbstractPopupActivity presenter;

    private static PopupViewBinder uiBinder = GWT.create( PopupViewBinder.class );

    @UiField
    public FlowPanel titleContainer;

    @UiField
    public SimplePanel container;

    @UiField
    public FocusPanel closeImage;

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );
        setGlassEnabled( true );
    }

    @Override
    public void init( final AbstractPopupActivity presenter ) {
        this.presenter = presenter;
    }

    public void setContent( final IsWidget widget ) {
        this.container.setWidget( widget );
    }

    public void setTitle( final IsWidget titleWidget ) {
        this.titleContainer.insert( titleWidget,
                                    0 );
    }

    @UiHandler("closeImage")
    public void onClickCloseImage( final ClickEvent event ) {
        presenter.close();
    }

}