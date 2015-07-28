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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
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
    VerticalPanel itemsPanel;

    @UiField
    FlowPanel addItemPanel;

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
    public void setPresenter( Presenter presenter ) {
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
    public void setErrorMessage( String errorMessage ) {
    }

    @Override
    public void clearErrorMessage() {
    }

    @Override
    public void clear() {
        itemsPanel.clear();
        indexToEditor.clear();
        indexToEditorWidget.clear();
    }

    @Override
    public Integer addItemEditor( ValuePairEditor<?> valuePairEditor ) {

        HorizontalPanel itemContainer = new HorizontalPanel();
        final Integer itemId = nextItemId();

        valuePairEditor.showValuePairName( false );
        valuePairEditor.addEditorHandler( new ValuePairEditorHandler() {
            @Override
            public void onValidate() {

            }

            @Override
            public void onValueChanged() {
                presenter.onValueChanged( itemId );
            }
        } );
        itemContainer.add( valuePairEditor );

        Button deleteButton = new Button( Constants.INSTANCE.advanced_domain_multiple_value_pair_editor_action_delete() );
        deleteButton.setType( ButtonType.LINK );
        deleteButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onRemoveItem( itemId );
            }
        } );
        itemContainer.add( deleteButton );

        itemsPanel.add( itemContainer );

        indexToEditor.put( itemId, valuePairEditor );
        indexToEditorWidget.put( itemId, itemContainer );
        return itemId;
    }

    @Override
    public void removeItem( Integer itemId ) {
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

    private void initAddItemPanel() {

        HorizontalPanel addItemContainer = new HorizontalPanel();
        addItemContainer.setVerticalAlignment( HasVerticalAlignment.ALIGN_BOTTOM );
        addItemEditor = presenter.createValuePairEditor( valuePairDefinition );
        addItemEditor.showValuePairName( false );

        addItemContainer.add( addItemEditor );

        Button addItemButton = new Button( Constants.INSTANCE.advanced_domain_multiple_value_pair_editor_action_add() );
        addItemButton.setType( ButtonType.PRIMARY );
        addItemButton.setIcon( IconType.PLUS );
        addItemButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onAddItem();
            }
        } );
        addItemContainer.add( addItemButton );

        addItemPanel.add( addItemContainer );
    }

    private Integer nextItemId() {
        return itemIds++;
    }

}
