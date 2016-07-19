/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;

public class BooleanValuePairEditorViewImpl
        extends Composite
        implements BooleanValuePairEditorView {

    interface BooleanValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, BooleanValuePairEditorViewImpl> {

    }

    private static BooleanValuePairEditorViewImplUiBinder uiBinder = GWT.create( BooleanValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    Select listBox;

    @UiField
    HelpBlock helpBlock;

    String currentValuePairLabel = null;

    boolean showRequiredIndicator = true;

    private Presenter presenter;

    public BooleanValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setSelectedValue( String value ) {
        UIUtil.setSelectedValue( listBox, value );
    }

    @Override
    public String getSelectedValue() {
        return listBox.getValue();
    }

    public void setValuePairLabel( String valuePairLabel ) {
        this.valuePairLabel.setText( valuePairLabel );
        currentValuePairLabel = valuePairLabel;
    }

    @Override
    public void showValuePairName( boolean show ) {
        if ( !show ) {
            currentValuePairLabel = valuePairLabel.getText();
            showRequiredIndicator = valuePairLabel.getShowRequiredIndicator();
            valuePairLabel.setText( null );
            valuePairLabel.setShowRequiredIndicator( false );
        } else {
            valuePairLabel.setText( currentValuePairLabel );
            valuePairLabel.setShowRequiredIndicator( showRequiredIndicator );
        }
    }

    @Override
    public void showValuePairRequiredIndicator( boolean required ) {
        this.valuePairLabel.setShowRequiredIndicator( required );
        showRequiredIndicator = required;
    }

    @Override
    public void initOptions( List<Pair<String, String>> options ) {
        UIUtil.initList( listBox, options, true );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        helpBlock.setText( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        helpBlock.setText( null );
    }

    @UiHandler( "listBox" )
    void onValueChange( ValueChangeEvent<String> event ) {
        presenter.onValueChange();
    }
}