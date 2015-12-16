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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.uberfire.commons.data.Pair;

public class MultipleEnumValuePairEditor
        implements MultipleEnumValuePairEditorView.Presenter,
        ValuePairEditor<List<String>> {

    private MultipleEnumValuePairEditorView view;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    private Map<String, EnumValuePairOptionEditor> valueToEditor = new HashMap<String, EnumValuePairOptionEditor>();

    private static final String EMPTY_ARRAY = "_EMPTY_ARRAY_";

    private List<String> currentValues = null;

    public MultipleEnumValuePairEditor() {
        this( ( MultipleEnumValuePairEditorView ) GWT.create( MultipleEnumValuePairEditorViewImpl.class ) );
    }

    public MultipleEnumValuePairEditor( MultipleEnumValuePairEditorView view ) {
        this.view = view;
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;
        view.setValuePairLabel( ValuePairEditorUtil.buildValuePairLabel( valuePairDefinition ) );
        view.showValuePairRequiredIndicator( !valuePairDefinition.hasDefaultValue() );
        initOptionEditors( createOptions( valuePairDefinition.enumValues() ) );
    }

    @Override
    public void clear() {
        setSelectedValues( new ArrayList<String>() );
    }

    @Override
    public void addEditorHandler( ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
    }

    @Override
    public void setValue( List<String> value ) {
        this.currentValues = value;
        setSelectedValues( value );
    }

    public List<String> getValue( ) {
        return currentValues;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public AnnotationValuePairDefinition getValuePairDefinition() {
        return valuePairDefinition;
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        view.setErrorMessage( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        view.clearErrorMessage();
    }

    @Override
    public void showValidateButton( boolean show ) {
        //This editor doesn't need the validate button.
    }

    @Override
    public void showValuePairName( boolean show ) {
        //this editor doesn't need to hide the label
    }

    @Override
    public void refresh() {
        //This editor doesn't need the refresh implementation.
    }

    private List<Pair<String, String>> createOptions( String[] enumValues ) {
        List<Pair<String, String>> items = new ArrayList<Pair<String, String>>(  );
        for ( int i = 0; i < enumValues.length; i++ ) {
            items.add( new Pair( enumValues[i], enumValues[i] ) );
        }
        return items;
    }

    private void initOptionEditors( List<Pair<String, String>> options ) {
        view.clear();
        if ( options != null ) {
            for ( final Pair<String, String> option : options ) {
                final EnumValuePairOptionEditor optionEditor = createOptionEditor( option.getK2() );
                valueToEditor.put( option.getK2(), optionEditor );
                optionEditor.addEnumValuePairOptionEditorHandler( new EnumValuePairOptionEditorView.EnumValuePairOptionEditorHandler() {
                    @Override
                    public void onValueChange() {
                        doOnValueChange( option.getK2(), optionEditor.getValue() );
                        if ( !EMPTY_ARRAY.equals( option.getK2() ) && optionEditor.getValue() ) {
                            valueToEditor.get( EMPTY_ARRAY ).setValue( false );
                        }
                    }
                } );
                view.addOptionEditor( optionEditor );
            }
        }
        final EnumValuePairOptionEditor emptyArrayEditor = createOptionEditor( "{}" );
        view.addOptionEditor( emptyArrayEditor );
        valueToEditor.put( EMPTY_ARRAY, emptyArrayEditor );
        emptyArrayEditor.addEnumValuePairOptionEditorHandler( new EnumValuePairOptionEditorView.EnumValuePairOptionEditorHandler() {
            @Override
            public void onValueChange() {
                doOnValueChange( EMPTY_ARRAY, emptyArrayEditor.getValue() );
                if ( emptyArrayEditor.getValue() ) {
                    uncheckOthers( EMPTY_ARRAY );
                }
            }
        } );
    }

    //protected for testing purposes
    protected EnumValuePairOptionEditor createOptionEditor( String option ) {
        return new EnumValuePairOptionEditor( option );
    }

    private void doOnValueChange( String valueName, boolean isChecked ) {
        if ( EMPTY_ARRAY.equals( valueName ) ) {
            currentValues = isChecked ? new ArrayList<String>( ) : null;
        } else if ( !isChecked ) {
            safeRemoveValue( valueName );
        } else {
            safeAddValue( valueName );
        }

        if ( editorHandler != null ) {
            editorHandler.onValueChange();
        }
    }
    
    private void setSelectedValues( List<String> values ) {
        for ( EnumValuePairOptionEditor optionEditor : valueToEditor.values() ) {
            optionEditor.setValue( false );
        }

        if ( values != null ) {
            if ( values.size() == 0 ) {
                valueToEditor.get( EMPTY_ARRAY ).setValue( true );
            } else {
                for ( String value : values ) {
                    EnumValuePairOptionEditor optionEditor = valueToEditor.get( value );
                    if ( optionEditor != null ) {
                        optionEditor.setValue( true );
                    }
                }
            }
        }
    }

    private void safeRemoveValue( String value ) {
        if ( currentValues != null ) {
            currentValues.remove( value );
            if ( currentValues.size() == 0 ) {
                currentValues = null;
            }
        }
    }

    private void safeAddValue( String value ) {
        if ( currentValues == null ) {
            currentValues = new ArrayList<String>( );
        }
        if ( !currentValues.contains( value )) {
            currentValues.add( value );
        }
    }

    private void uncheckOthers( String value ) {
        for ( String key : valueToEditor.keySet() ) {
            if ( !key.equals( value ) ) {
                valueToEditor.get( key ).setValue( false );
            }
        }
    }
}
