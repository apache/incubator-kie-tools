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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class MultipleValuePairEditorViewImpl
        extends Composite
        implements MultipleValuePairEditorView {

    interface MultipleValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, MultipleValuePairEditorViewImpl> {

    }

    private static MultipleValuePairEditorViewImplUiBinder uiBinder = GWT.create( MultipleValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    Column itemsPanel;

    @UiField
    HelpBlock helpBlock;

    @UiField
    Column addItemPanel;

    private Map<Integer, ValuePairEditor<?>> indexToEditor = new TreeMap<Integer, ValuePairEditor<?>>();

    private HashMap<Integer, Widget> indexToEditorWidget = new HashMap<Integer, Widget>();

    private Presenter presenter;

    private ValuePairEditor<?> addItemEditor;

    private AnnotationValuePairDefinition valuePairDefinition;

    int itemIds = 0;

    public MultipleValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;
        initAddItemPanel();
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
    public void showValuePairRequiredIndicator( boolean required ) {
        this.valuePairLabel.setShowRequiredIndicator( required );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        helpBlock.setText( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        helpBlock.setText( null );
    }

    @Override
    public void clear() {
        itemsPanel.clear();
        indexToEditor.clear();
        indexToEditorWidget.clear();
    }

    @Override
    public Integer addItemEditor( ValuePairEditor<?> valuePairEditor ) {

        Row itemEditorRow = new Row();
        Column itemEditorColumn = new Column( ColumnSize.MD_10 );

        final Integer itemId = nextItemId();
        valuePairEditor.showValuePairName( false );
        valuePairEditor.addEditorHandler( new ValuePairEditorHandler() {
            @Override
            public void onValidate() {

            }

            @Override
            public void onValueChange() {
                presenter.onValueChange( itemId );
            }
        } );

        itemEditorColumn.add( valuePairEditor );
        itemEditorRow.add( itemEditorColumn );

        Column deleteButtonColumn = new Column( ColumnSize.MD_2 );

        Button deleteButton = new Button( Constants.INSTANCE.advanced_domain_multiple_value_pair_editor_action_delete() );
        deleteButton.setType( ButtonType.LINK );
        deleteButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onRemoveItem( itemId );
            }
        } );

        deleteButtonColumn.add( deleteButton );
        itemEditorRow.add( deleteButtonColumn );


        itemsPanel.add( itemEditorRow );

        indexToEditor.put( itemId, valuePairEditor );
        indexToEditorWidget.put( itemId, itemEditorRow );
        return itemId;
    }

    @Override
    public void removeItemEditor( Integer itemId ) {
        Widget widget = indexToEditorWidget.get( itemId );
        if ( widget != null ) {
            itemsPanel.remove( widget );
        }
        indexToEditorWidget.remove( itemId );
        indexToEditor.remove( itemId );
    }

    @Override
    public ValuePairEditor<?> getItemEditor( Integer itemId ) {
        return indexToEditor.get( itemId );
    }

    @Override
    public List<ValuePairEditor<?>> getItemEditors() {
        List<ValuePairEditor<?>> editors = new ArrayList<ValuePairEditor<?>>();
        for ( Integer index : indexToEditor.keySet() ) {
            editors.add( indexToEditor.get( index ) );
        }
        return editors;
    }

    public ValuePairEditor<?> getAddItemEditor() {
        return addItemEditor;
    }

    @Override
    public void showAlert( String message ) {
        Window.alert( message );
    }

    private void initAddItemPanel() {

        Row addItemRow = new Row();
        addItemPanel.add( addItemRow );

        Column addItemEditorColumn = new Column( ColumnSize.MD_10 );

        //addItemContainer.setVerticalAlignment( HasVerticalAlignment.ALIGN_BOTTOM );
        addItemEditor = presenter.createValuePairEditor( valuePairDefinition );
        addItemEditor.showValuePairName( false );

        addItemEditorColumn.add( addItemEditor );
        addItemRow.add( addItemEditorColumn );

        Column addItemButtonColumn = new Column( ColumnSize.MD_2 );

        Button addItemButton = new Button( Constants.INSTANCE.advanced_domain_multiple_value_pair_editor_action_add() );
        addItemButton.setType( ButtonType.PRIMARY );
        addItemButton.setIcon( IconType.PLUS );
        addItemButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onAddItem();
            }
        } );

        addItemButtonColumn.add( addItemButton );
        addItemRow.add( addItemButtonColumn );

        addItemPanel.add( addItemRow );
    }

    private Integer nextItemId() {
        return itemIds++;
    }

}
