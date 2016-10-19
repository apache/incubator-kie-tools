/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated( "VariablesEditorWidget.html#widget" )
public class VariablesEditorWidgetViewImpl extends Composite implements VariablesEditorWidgetView, HasValue<String> {

    ListBoxValues dataTypeListBoxValues;

    private String sVariables;

    private Presenter presenter;

    @Inject
    @DataField
    protected Button addVarButton;

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @DataField
    protected TableCellElement nameth = Document.get().createTHElement();

    @DataField
    protected TableCellElement datatypeth = Document.get().createTHElement();

    List<String> dataTypes;
    List<String> dataTypeDisplayNames;

    /**
     * The list of variableRows that currently exist.
     */
    @Inject
    @DataField
    @Table( root = "tbody" )
    protected ListWidget<VariableRow, VariableListItemWidgetViewImpl> variableRows;

    @Inject
    protected Event<NotificationEvent> notification;

    @Override
    public String getValue() {
        return sVariables;
    }

    @Override
    public void setValue( String value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( String value, boolean fireEvents ) {
        String oldValue = sVariables;
        sVariables = value;
        // TODO: get DataTypes from server
        if ( dataTypes == null ) {
            dataTypes = new ArrayList<String>( Arrays.asList( "Boolean", "Float", "Integer", "Object", "String" ) );
            dataTypeDisplayNames = new ArrayList<String>( Arrays.asList( "Boolean", "Float", "Integer", "Object", "String" ) );
            presenter.setDataTypes( dataTypes, dataTypeDisplayNames );
        }
        initView();
        if ( fireEvents ) {
            ValueChangeEvent.fireIfNotEqual( this, oldValue, sVariables );
        }
    }

    @Override
    public void doSave() {
        String newValue = presenter.serializeVariables( getVariableRows() );
        setValue( newValue, true );
    }

    protected void initView() {
        List<VariableRow> arrVariableRows = presenter.deserializeVariables( sVariables );
        setVariableRows( arrVariableRows );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    /**
     * Tests whether a VariableRow name occurs more than once in the list of rows
     *
     * @param name
     * @return
     */
    public boolean isDuplicateName( String name ) {
        return presenter.isDuplicateName( name );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
        addVarButton.setIcon( IconType.PLUS );
        nameth.setInnerText( "Name" );
        datatypeth.setInnerText( "Data Type" );
    }

    @Override
    public int getVariableRowsCount() {
        return variableRows.getValue().size();
    }

    @Override
    public void setTableDisplayStyle() {
        table.getStyle().setDisplay( Style.Display.TABLE );
    }

    @Override
    public void setNoneDisplayStyle() {
        table.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void setVariableRows( List<VariableRow> rows ) {
        variableRows.setValue( rows );
        for ( int i = 0; i < getVariableRowsCount(); i++ ) {
            VariableListItemWidgetView widget = getVariableWidget( i );
            widget.setDataTypes( dataTypeListBoxValues );
            widget.setParentWidget( presenter );
        }
    }

    @Override
    public List<VariableRow> getVariableRows() {
        return variableRows.getValue();
    }

    @Override
    public VariableListItemWidgetView getVariableWidget( int index ) {
        return variableRows.getComponent( index );
    }

    public void setVariablesDataTypes( ListBoxValues dataTypeListBoxValues ) {
        this.dataTypeListBoxValues = dataTypeListBoxValues;
        for ( int i = 0; i < getVariableRowsCount(); i++ ) {
            getVariableWidget( i ).setDataTypes( dataTypeListBoxValues );
        }
    }

    @EventHandler( "addVarButton" )
    public void handleAddVarButton( ClickEvent e ) {
        presenter.addVariable();
    }

    public void removeVariable( VariableRow variableRow ) {
        presenter.removeVariable( variableRow );
        if ( getVariableRows().isEmpty() ) {
            setNoneDisplayStyle();
        }
    }

}
