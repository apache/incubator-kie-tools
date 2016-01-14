/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.search.client.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;

@Dependent
public class SearchMenuView extends Composite implements SearchMenuPresenter.View {

    interface ViewBinder extends UiBinder<Widget, SearchMenuView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    Button searchButton;

    @UiField
    TextBox search;

    private SearchMenuPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        if ( Window.Location.getParameterMap().containsKey( "no_search" ) ) {
            this.setVisible( false );
        }
        search.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                if ( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER ) {
                    search( null );
                }
            }
        } );
    }

    @Override
    public void init( final SearchMenuPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        setupClearButton();
    }

    public native void setupClearButton() /*-{
        // Hide the clear button if the search input is empty
        $wnd.jQuery(".search-pf .has-clear .clear").each(function () {
            if (!$wnd.jQuery(this).prev('.form-control').val()) {
                $wnd.jQuery(this).hide();
            }
        });
        // Show the clear button upon entering text in the search input
        $wnd.jQuery(".search-pf .has-clear .form-control").keyup(function () {
            var t = $wnd.jQuery(this);
            t.next('button').toggle(Boolean(t.val()));
        });
        // Upon clicking the clear button, empty the entered text and hide the clear button
        $wnd.jQuery(".search-pf .has-clear .clear").click(function () {
            $wnd.jQuery(this).prev('.form-control').val('').focus();
            $wnd.jQuery(this).hide();
        });
    }-*/;

    @UiHandler( "searchButton" )
    public void search( ClickEvent e ) {
        presenter.search( search.getText() );
    }

    @Override
    public void setText( final String text ) {
        search.setText( text );
    }
}