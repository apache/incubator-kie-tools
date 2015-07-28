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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string;

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
import org.gwtbootstrap3.client.ui.TextBox;

public class StringValuePairEditorViewImpl
        extends Composite
        implements StringValuePairEditorView {

    interface StringValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, StringValuePairEditorViewImpl> {

    }

    private static StringValuePairEditorViewImplUiBinder uiBinder = GWT.create( StringValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    TextBox textBox;

    private Presenter presenter;

    public StringValuePairEditorViewImpl() {
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

    @Override
    public void setValue( String value ) {
        textBox.setText( value );
    }

    @Override
    public String getValue() {
        return textBox.getText();
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
    public void clear() {
        textBox.setText( null );
    }

    @UiHandler( "textBox" )
    void onValueChanged( ChangeEvent event ) {
        presenter.onValueChanged();
    }
}
