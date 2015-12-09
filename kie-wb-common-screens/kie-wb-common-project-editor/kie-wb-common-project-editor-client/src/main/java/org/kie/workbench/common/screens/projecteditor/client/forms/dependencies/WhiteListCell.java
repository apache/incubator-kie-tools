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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.AbstractPopupEditCell;

public class WhiteListCell
        extends AbstractPopupEditCell<String, String> {

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    LinkedGroupItem addAll;

    @UiField
    LinkedGroupItem addNone;

    public WhiteListCell() {
        super( false );
        vPanel.add( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void render( final Context context,
                        final String value,
                        final SafeHtmlBuilder safeHtmlBuilder ) {
        renderer.render( value,
                         safeHtmlBuilder );
    }

    @Override
    protected void commit() {

    }

    @Override
    protected void startEditing( final Context context,
                                 final Element parent,
                                 final String value ) {

        panel.setPopupPositionAndShow( new PopupPanel.PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                panel.setPopupPosition( parent.getAbsoluteLeft()
                                                + offsetX,
                                        parent.getAbsoluteTop()
                                                + offsetY );
            }
        } );
    }

    @UiHandler( {"addAll"} )
    public void onAddAll( final ClickEvent event ) {

        valueUpdater.update( "" );

        panel.hide();
    }

    @UiHandler( {"addNone"} )
    public void onAddNone( final ClickEvent event ) {

        valueUpdater.update( "" );

        panel.hide();
    }

    interface Binder
            extends
            UiBinder<Widget, WhiteListCell> {

    }
}
