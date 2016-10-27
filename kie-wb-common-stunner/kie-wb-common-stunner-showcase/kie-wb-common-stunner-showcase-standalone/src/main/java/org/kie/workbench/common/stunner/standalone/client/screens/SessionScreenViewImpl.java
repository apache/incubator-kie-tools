/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.screens;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class SessionScreenViewImpl extends FlowPanel implements SessionScreenView {

    private final FlowPanel emptyViewPanel = new FlowPanel();
    private final FlowPanel screenViewPanel = new FlowPanel();

    public SessionScreenViewImpl() {
        init();
    }

    public SessionScreenViewImpl( final String tag ) {
        super( tag );
        init();
    }

    @Override
    public void showEmptySession() {
        emptyViewPanel.setVisible( true );
        screenViewPanel.setVisible( false );
    }

    @Override
    public void showScreenView( final IsWidget viewWidget ) {
        emptyViewPanel.setVisible( false );
        setScreenView( viewWidget );
        screenViewPanel.setVisible( true );
    }

    @Override
    public void setScreenViewBgColor( final String color ) {
        screenViewPanel.getElement().getStyle().setBackgroundColor( color );
    }

    @Override
    public void setMarginTop( final int px ) {
        this.emptyViewPanel.getElement().getStyle().setMarginTop( px, Style.Unit.PX );
        this.screenViewPanel.getElement().getStyle().setMarginTop( px, Style.Unit.PX );
    }

    @Override
    public void setPaddingTop( final int px ) {
        this.emptyViewPanel.getElement().getStyle().setPaddingTop( px, Style.Unit.PX );
        this.screenViewPanel.getElement().getStyle().setPaddingTop( px, Style.Unit.PX );
    }

    @Override
    public IsWidget getView() {
        return this;
    }

    private void init() {
        this.add( emptyViewPanel );
        this.add( screenViewPanel );
        this.setHeight( "100%" );
        this.emptyViewPanel.setHeight( "100%" );
        this.screenViewPanel.setHeight( "100%" );
        showEmptySession();
    }

    private SessionScreenView setScreenView( final IsWidget view ) {
        this.screenViewPanel.clear();
        this.screenViewPanel.add( view );
        return this;
    }

}
