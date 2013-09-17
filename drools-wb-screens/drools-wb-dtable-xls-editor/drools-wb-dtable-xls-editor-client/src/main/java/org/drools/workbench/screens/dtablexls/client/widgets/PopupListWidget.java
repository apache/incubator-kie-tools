/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.dtablexls.client.widgets;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.uberfire.client.common.popups.footers.ModalFooterOKButton;

/**
 * A popup that can contain a list of items
 */
public class PopupListWidget extends Modal {

    interface PopupListWidgetBinder
            extends
            UiBinder<Widget, PopupListWidget> {

    }

    private static PopupListWidgetBinder uiBinder = GWT.create( PopupListWidgetBinder.class );

    @UiField
    protected VerticalPanel list;

    public PopupListWidget() {
        setTitle( DecisionTableXLSEditorConstants.INSTANCE.ConversionResults() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setWidth( "900px" );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );
    }

    public void addListItem( Widget w ) {
        this.list.add( w );
    }

}
