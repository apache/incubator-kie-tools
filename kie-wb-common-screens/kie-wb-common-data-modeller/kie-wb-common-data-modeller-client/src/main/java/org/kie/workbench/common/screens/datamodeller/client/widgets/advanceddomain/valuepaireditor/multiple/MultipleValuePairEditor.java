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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.multiple;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public abstract class MultipleValuePairEditor
        implements MultipleValuePairEditorView.Presenter,
        ValuePairEditor<List<?>> {

    private MultipleValuePairEditorView view;

    private List<Object> currentValue = null;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    boolean valid = true;

    public MultipleValuePairEditor() {
        this( ( MultipleValuePairEditorView ) GWT.create( MultipleValuePairEditorViewImpl.class ) );
    }

    public MultipleValuePairEditor( MultipleValuePairEditorView view ) {
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
        view.init( valuePairDefinition );
        view.setValuePairLabel( ValuePairEditorUtil.buildValuePairLabel( valuePairDefinition ) );
        view.showValuePairRequiredIndicator( !valuePairDefinition.hasDefaultValue() );
    }

    public List<?> getValue( ) {
        currentValue = null;
        if ( calculateStatus() ) {
            for ( ValuePairEditor<?> itemEditor : view.getItemEditors() ) {
                addSafeValue( itemEditor.getValue() );
            }
        }
        return currentValue;
    }

    public void setValue( List<?> value ) {
        this.currentValue = new ArrayList<Object>(  );
        valid = true; //a bit optimistic. By now we can assume that when set programmatically the value is valid.
        ValuePairEditor<?> valuePairEditor;

        if ( value != null ) {
            for ( Object itemValue : value ) {
                valuePairEditor = createValuePairEditor( valuePairDefinition );
                view.addItemEditor( valuePairEditor );
                setEditorValue( valuePairEditor, itemValue );
            }
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public void addEditorHandler( ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
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
        //This editor doesn't need the refresh method.
    }

    @Override
    public void onRemoveItem( Integer itemId ) {
        view.removeItemEditor( itemId );
        valid = calculateStatus();
        notifyChange();
    }

    @Override
    public void onAddItem( ) {
        ValuePairEditor<?> addItemEditor = view.getAddItemEditor();
        if ( !addItemEditor.isValid() || addItemEditor.getValue() == null ) {
            view.showAlert( Constants.INSTANCE.advanced_domain_multiple_value_pair_editor_message_null_or_invalid() );
        } else {

            ValuePairEditor<?>  valuePairEditor = createValuePairEditor( valuePairDefinition );
            view.addItemEditor( valuePairEditor );
            setEditorValue( valuePairEditor, addItemEditor.getValue() );
            addItemEditor.clear();
            addItemEditor.clearErrorMessage();
            notifyChange();
        }
    }

    @Override
    public void onValueChange( Integer itemId ) {
        valid = calculateStatus();
        notifyChange();
    }

    public abstract ValuePairEditor<?> createValuePairEditor( AnnotationValuePairDefinition valuePairDefinition );

    public abstract void setEditorValue( ValuePairEditor<?> valuePairEditor, Object value );

    private boolean calculateStatus() {
        for ( ValuePairEditor<?> itemEditor : view.getItemEditors() ) {
            if ( !itemEditor.isValid() || itemEditor.getValue() == null ) {
                return false;
            }
        }
        return true;
    }

    private void addSafeValue( Object value ) {
        if ( currentValue == null ) {
            currentValue = new ArrayList<Object>(  );
        }
        currentValue.add( value );
    }

    private void notifyChange( ) {
        if ( editorHandler != null ) {
            editorHandler.onValueChange();
        }
    }
}
