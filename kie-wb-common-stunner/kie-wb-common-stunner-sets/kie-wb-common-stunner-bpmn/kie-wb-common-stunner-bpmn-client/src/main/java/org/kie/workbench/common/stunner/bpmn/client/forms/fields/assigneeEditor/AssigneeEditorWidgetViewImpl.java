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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssigneeRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated( "AssigneeEditorWidget.html#widget" )
public class AssigneeEditorWidgetViewImpl extends Composite implements AssigneeEditorWidgetView, HasValue<String> {

    ListBoxValues nameListBoxValues;

    private String sAssignees;

    private Presenter presenter;

    @Inject
    @DataField
    protected Button addButton;

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @DataField
    protected TableCellElement nameth = Document.get().createTHElement();

    List<String> names;

    /**
     * The list of assigneeRows that currently exist.
     */
    @Inject
    @DataField
    @Table( root = "tbody" )
    protected ListWidget<AssigneeRow, AssigneeListItemWidgetViewImpl> assigneeRows;

    @Inject
    protected Event<NotificationEvent> notification;

    @Override
    public String getValue() {
        return sAssignees;
    }

    @Override
    public void setValue( String value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( String value, boolean fireEvents ) {
        String oldValue = sAssignees;
        sAssignees = value;
        // TODO: get names from server
        if ( names == null ) {
            names = new ArrayList<String>( Arrays.asList( "user1", "user2", "user3", "user4", "user5" ) );
            presenter.setNames( names );
        }
        initView();
        if ( fireEvents ) {
            ValueChangeEvent.fireIfNotEqual( this, oldValue, sAssignees );
        }
    }

    @Override
    public void doSave() {
        String newValue = presenter.serializeAssignees( getAssigneeRows() );
        setValue( newValue, true );
    }

    protected void initView() {
        List<AssigneeRow> arrAssigneeRows = presenter.deserializeAssignees( sAssignees );
        setAssigneeRows( arrAssigneeRows );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    /**
     * Tests whether a AssigneeRow name occurs more than once in the list of rows
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
        addButton.setIcon( IconType.PLUS );
        nameth.setInnerText( "Name" );
    }

    @Override
    public int getAssigneeRowsCount() {
        return assigneeRows.getValue().size();
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
    public void setAssigneeRows( List<AssigneeRow> rows ) {
        assigneeRows.setValue( rows );
        for ( int i = 0; i < getAssigneeRowsCount(); i++ ) {
            AssigneeListItemWidgetView widget = getAssigneeWidget( i );
            widget.setNames( nameListBoxValues );
            widget.setParentWidget( presenter );
        }
    }

    @Override
    public List<AssigneeRow> getAssigneeRows() {
        return assigneeRows.getValue();
    }

    @Override
    public AssigneeListItemWidgetView getAssigneeWidget( int index ) {
        return assigneeRows.getComponent( index );
    }

    public void setAssigneesNames( ListBoxValues nameListBoxValues ) {
        this.nameListBoxValues = nameListBoxValues;
        for ( int i = 0; i < getAssigneeRowsCount(); i++ ) {
            getAssigneeWidget( i ).setNames( nameListBoxValues );
        }
    }

    @EventHandler( "addButton" )
    public void handleAddButton( ClickEvent e ) {
        presenter.addAssignee();
    }
}
