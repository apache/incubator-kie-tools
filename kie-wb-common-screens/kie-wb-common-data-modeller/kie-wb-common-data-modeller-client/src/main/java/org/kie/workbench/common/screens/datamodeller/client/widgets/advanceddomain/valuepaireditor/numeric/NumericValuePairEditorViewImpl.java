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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;

public class NumericValuePairEditorViewImpl
        extends Composite
        implements NumericValuePairEditorView {

    interface NumericValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, NumericValuePairEditorViewImpl> {

    }

    private static NumericValuePairEditorViewImplUiBinder uiBinder = GWT.create( NumericValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    TextBox textBox;

    @UiField
    HelpBlock helpInline;

    private Presenter presenter;

    public NumericValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        textBox.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                presenter.onValueChanged();
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @UiHandler( "textBox" )
    void onValueChanged( ChangeEvent event ) {
        presenter.onValueChanged();
    }

    @Override
    public void setValue( String value ) {
        textBox.setValue( value );
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public void setValuePairLabel( String valuePairLabel ) {
        this.valuePairLabel.setText( valuePairLabel );
    }

    @Override
    public void showValuePairName( boolean show ) {
        this.valuePairLabel.setVisible( show );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        helpInline.setText( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        helpInline.setText( null );
    }

    @Override
    public void clear() {
        textBox.setText( null );
    }
}
