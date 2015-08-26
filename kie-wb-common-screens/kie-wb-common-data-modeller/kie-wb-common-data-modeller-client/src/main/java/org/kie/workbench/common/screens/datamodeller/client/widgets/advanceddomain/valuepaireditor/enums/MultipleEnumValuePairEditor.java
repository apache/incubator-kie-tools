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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.uberfire.commons.data.Pair;

public class MultipleEnumValuePairEditor
        implements IsWidget,
        MultipleEnumValuePairEditorView.Presenter,
        ValuePairEditor<List<String>> {

    private MultipleEnumValuePairEditorView view;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    public MultipleEnumValuePairEditor() {
        view = GWT.create( MultipleEnumValuePairEditorViewImpl.class );
        view.setPresenter( this );
    }

    private List<String> currentValues = null;

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;
        view.initItems( createItemList( valuePairDefinition.getClassName(), valuePairDefinition.enumValues() ) );
        view.setValuePairLabel( ValuePairEditorUtil.buildValuePairLabel( valuePairDefinition ) );
    }

    private List<Pair<String, String>> createItemList( String className, String[] enumValues ) {
        List<Pair<String, String>> items = new ArrayList<Pair<String, String>>(  );
        for ( int i = 0; i < enumValues.length; i++ ) {
            items.add( new Pair( enumValues[i], enumValues[i] ) );
        }
        return items;
    }

    @Override
    public void onValueChanged( String valueName, boolean isChecked ) {
        if ( MultipleEnumValuePairEditorView.EMPTY_ARRAY.equals( valueName ) ) {
            currentValues = isChecked ? new ArrayList<String>( ) : null;
        } else if ( !isChecked ) {
                safeRemoveValue( valueName );
            } else {
                safeAddValue( valueName );
            }

        if ( editorHandler != null ) {
            editorHandler.onValueChanged();
        }
    }

    //value pair editor interface
    @Override
    public void clear() {
        view.setSelectedValues( new ArrayList<String>(  ) );
    }

    @Override
    public void addEditorHandler( ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
    }

    @Override
    public void setValue( List<String> value ) {
        this.currentValues = value;
        view.setSelectedValues( currentValues );
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
        //TODO implement this if needed
    }

    @Override
    public void clearErrorMessage() {
        //TODO implement this if needed
    }

    @Override
    public void showValidateButton( boolean show ) {
        //This editor doesn't need the validate button.
    }

    @Override
    public void showValuePairName( boolean show ) {
        //TODO implement if needed
        //this editor doesn't need to hide the label
    }

    @Override
    public void refresh() {
        //This editor doesn't need the validate button.
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
}
