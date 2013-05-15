/*
 * Copyright 2012 JBoss Inc
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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessage;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.kie.workbench.common.widgets.client.widget.WidthCalculator;

/**
 * A widget to display a single conversion result message
 */
public class ConversionMessageWidget extends Composite {

    private static WidthCalculator<String> widthCalculator = new WidthCalculator<String>( new TextCell() );

    @UiField
    protected Image image;

    @UiField
    protected Label label;

    interface ConversionMessageWidgetBinder
            extends
            UiBinder<Widget, ConversionMessageWidget> {

    }

    private static ConversionMessageWidgetBinder uiBinder = GWT.create( ConversionMessageWidgetBinder.class );

    public ConversionMessageWidget( final ConversionMessage message ) {
        initWidget( uiBinder.createAndBindUi( this ) );

        switch ( message.getMessageType() ) {
            case ERROR:
                this.image.setResource( CommonImages.INSTANCE.error() );
                break;
            case INFO:
                this.image.setResource( CommonImages.INSTANCE.information() );
                break;
            case WARNING:
                this.image.setResource( CommonImages.INSTANCE.warning() );
                break;
        }
        this.label.setText( message.getMessage() );

        //Make containing Panel the width of the content to ensure scroll bars operate correctly
        int width = widthCalculator.getElementWidth( message.getMessage() ) + 32;
        setWidth( width + "px" );
    }

}
