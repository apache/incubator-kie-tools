/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.explorer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;

public class BranchSelectorViewImpl
        extends Composite
        implements BranchSelectorView {

    interface Binder
            extends
            UiBinder<Widget, BranchSelectorViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private Presenter presenter;

    @UiField
    Button button;

    @UiField
    DropDownMenu menu;

    public BranchSelectorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setCurrentBranch( String currentBranch ) {
        button.setText( currentBranch );
    }

    @Override
    public void addBranch( final String branch ) {
        AnchorListItem widget = new AnchorListItem( branch );
        widget.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onBranchSelected( branch );
            }
        } );
        menu.add( widget );
    }

    @Override
    public void clear() {
        button.clear();
    }

    @Override
    public void show() {
        setVisible( true );
    }

    @Override
    public void hide() {
        setVisible( false );
    }
}
