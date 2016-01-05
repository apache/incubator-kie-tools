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

package org.uberfire.client.views.pfly.menu;

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter;
import org.uberfire.mvp.Command;

/**
 * Created by Cristiano Nicolai.
 */
@Dependent
public class WorkbenchViewModeSwitcherView implements WorkbenchViewModeSwitcherPresenter.View {


    private final AnchorListItem menu = new AnchorListItem();
    private WorkbenchViewModeSwitcherPresenter presenter;

    @Override
    public void setText( final String text ){
        menu.setText( text );
    }

    @Override
    public void init( final WorkbenchViewModeSwitcherPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addClickHandler( final Command command ) {
        menu.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                command.execute();
            }
        } );
    }

    @Override
    public Widget asWidget() {
        return menu;
    }

    @Override
    public void enable() {
        menu.setEnabled( true );
    }

    @Override
    public void disable() {
        menu.setEnabled( false );
    }
}
