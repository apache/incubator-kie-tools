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

package org.kie.workbench.common.screens.explorer.client.widgets.tagSelector;

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

public class TagSelectorViewImpl
        extends Composite
        implements TagSelectorView {

    interface Binder
            extends
            UiBinder<Widget, TagSelectorViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private TagSelector presenter;

    @UiField
    Button tagListButton;

    @UiField
    DropDownMenu tagListDropdown;

    public TagSelectorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( TagSelector presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addTag( final String text ) {
        AnchorListItem widget = new AnchorListItem( text );
        widget.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.selectTag( text );
            }
        } );
        tagListDropdown.add( widget );
    }

    @Override
    public void setCurrentTag( String tag ) {
        tagListButton.setText( tag );
    }

    @Override
    public void clear() {
        tagListDropdown.clear();
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
