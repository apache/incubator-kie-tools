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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;

public class WhiteListCell extends AbstractCell<String> {

    public static final String UPDATE = "update";
    public static final String TOGGLE = "toggle";

    private static CellBinder cellRenderer = GWT.create( CellBinder.class );

    interface CellBinder extends UiRenderer {

        void render( SafeHtmlBuilder sb,
                     String value );

        void onBrowserEvent( WhiteListCell cell,
                             NativeEvent e,
                             Element p,
                             ValueUpdater valueUpdater,
                             String value );
    }

    public WhiteListCell() {
        super( BrowserEvents.CLICK );
    }

    @Override
    public void onBrowserEvent( Context context,
                                Element parent,
                                String value,
                                NativeEvent event,
                                ValueUpdater valueUpdater ) {
        cellRenderer.onBrowserEvent( this, event, parent, valueUpdater, value );
    }

    @Override
    public void render( Context context,
                        String value,
                        SafeHtmlBuilder sb ) {
        cellRenderer.render( sb, value );

    }

    @UiHandler( {"addAll", "addNone"} )
    void onActionGotPressed( ClickEvent event,
                             Element parent,
                             ValueUpdater valueUpdater,
                             String value ) {
        valueUpdater.update( TOGGLE );
    }
}
