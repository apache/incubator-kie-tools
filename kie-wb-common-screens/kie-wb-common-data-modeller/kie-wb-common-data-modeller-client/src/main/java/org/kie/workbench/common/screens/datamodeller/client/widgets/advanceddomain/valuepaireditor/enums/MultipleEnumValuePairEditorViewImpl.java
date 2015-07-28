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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.uberfire.commons.data.Pair;

public class MultipleEnumValuePairEditorViewImpl
        extends Composite
        implements MultipleEnumValuePairEditorView {

    interface MultipleEnumValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, MultipleEnumValuePairEditorViewImpl> {

    }

    private static MultipleEnumValuePairEditorViewImplUiBinder uiBinder = GWT.create( MultipleEnumValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    FlowPanel controlsContainer;

    private Map<String, CheckBox> valueToCheckBox = new HashMap<String, CheckBox>();

    private Presenter presenter;

    public MultipleEnumValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initItems( List<Pair<String, String>> options ) {
        controlsContainer.clear();
        CheckBox checkBox;
        if ( options != null ) {
            for ( final Pair<String, String> option : options ) {
                checkBox = new CheckBox( option.getK2() );
                valueToCheckBox.put( option.getK2(), checkBox );
                checkBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange( ValueChangeEvent<Boolean> event ) {
                        presenter.onValueChanged( option.getK2(), event.getValue() );
                        if ( !EMPTY_ARRAY.equals( option.getK2() ) && event.getValue() ) {
                            valueToCheckBox.get( EMPTY_ARRAY ).setValue( false );
                        }
                    }
                } );
                controlsContainer.add( checkBox );
            }
        }
        checkBox = new CheckBox( "{ }" );
        controlsContainer.add( checkBox );
        valueToCheckBox.put( EMPTY_ARRAY, checkBox );
        checkBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                presenter.onValueChanged( EMPTY_ARRAY, event.getValue() );
                if ( event.getValue() ) {
                    uncheckOthers( EMPTY_ARRAY );
                }
            }
        } );
    }

    @Override
    public void setSelectedValues( List<String> values ) {

        for ( CheckBox checkBox : valueToCheckBox.values() ) {
            checkBox.setValue( false );
        }

        if ( values != null ) {
            if ( values.size() == 0 ) {
                valueToCheckBox.get( EMPTY_ARRAY ).setValue( true );
            } else {
                for ( String value : values ) {
                    CheckBox checkBox = valueToCheckBox.get( value );
                    if ( checkBox != null ) {
                        checkBox.setValue( true );
                    }
                }
            }
        }
    }

    @Override
    public void setValuePairLabel( String label ) {
        valuePairLabel.setText( label );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        //TODO implement the error message in case it's needed
    }

    @Override
    public void clearErrorMessage() {
        //TODO implement the error message in case it's needed
    }

    private void uncheckOthers( String value ) {
        for ( String key : valueToCheckBox.keySet() ) {
            if ( !key.equals( value ) ) {
                valueToCheckBox.get( key ).setValue( false );
            }
        }
    }

}
