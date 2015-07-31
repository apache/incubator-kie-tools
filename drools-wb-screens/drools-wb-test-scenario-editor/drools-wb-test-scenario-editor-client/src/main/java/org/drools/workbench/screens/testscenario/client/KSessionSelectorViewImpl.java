/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;

public class KSessionSelectorViewImpl
        extends Composite
        implements KSessionSelectorView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionSelectorViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    ListBox kbases;

    @UiField
    ListBox ksessions;

    @UiField
    Label warning;

    public KSessionSelectorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( final Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setSelected( final String kbase,
                             final String ksession ) {
        setSelectedValue( kbases, kbase );
        setSelectedValue( ksessions, ksession );
    }

    void setSelectedValue( final ListBox listbox,
                           final String value ) {
        for ( int i = 0; i < listbox.getItemCount(); i++ ) {
            if ( listbox.getValue( i ).equals( value ) ) {
                listbox.setSelectedIndex( i );
                return;
            }
        }
    }

    @Override
    public void addKBase( final String name ) {
        kbases.addItem( name );
    }

    @Override
    public void setKSessions( final List<String> ksessions ) {
        this.ksessions.clear();
        for ( String ksession : ksessions ) {
            this.ksessions.addItem( ksession );
        }
    }

    @Override
    public void showWarningSelectedKSessionDoesNotExist() {
        warning.setVisible( true );
    }

    @Override
    public String getSelectedKBase() {
        return kbases.getItemText( kbases.getSelectedIndex() );
    }

    @UiHandler("kbases")
    public void onKBaseSelected( final ChangeEvent event ) {
        presenter.onKBaseSelected( getSelectedKBase() );
    }

    @UiHandler("ksessions")
    public void handleChange( final ChangeEvent event ) {
        presenter.onKSessionSelected( ksessions.getItemText( ksessions.getSelectedIndex() ) );
    }

}
