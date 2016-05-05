/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import javax.enterprise.context.Dependent;
import java.util.Map;

@Dependent
public class UserAttributesEditorView extends Composite implements UserAttributesEditor.View {

    interface UserAttributesEditorViewBinder
            extends
            UiBinder<Widget, UserAttributesEditorView> {

    }

    private static UserAttributesEditorViewBinder uiBinder = GWT.create(UserAttributesEditorViewBinder.class);

    @UiField
    Row userEmptyAttributesRow;

    @UiField
    FlowPanel newUserAttributePanel;
    
    @UiField(provided = true)
    NewUserAttributeEditor.View newUserAttributeView;
    
    @UiField
    Row userAttributesRow;

    @UiField(provided = true)
    CellTable attributesGrid;

    @UiField(provided = true)
    SimplePager attributesGridPager;

    private UserAttributesEditor presenter;

    @Override
    public void init(final UserAttributesEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public UserAttributesEditor.View initWidgets(NewUserAttributeEditor.View newUserAttributeEditorView) {
        this.newUserAttributeView = newUserAttributeEditorView;

        // Init the image list grid.
        attributesGrid = new CellTable<Map.Entry<String, String>>(presenter.KEY_PROVIDER);
        attributesGrid.setWidth("100%", true);

        // Do not refresh the headers and footers every time the data is updated.
        attributesGrid.setAutoHeaderRefreshDisabled(true);
        attributesGrid.setAutoFooterRefreshDisabled(true);

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        attributesGridPager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        attributesGridPager.setDisplay(attributesGrid);

        // Add a selection model so we can select cells.
        final SelectionModel<Map.Entry<String, String>> selectionModel = new MultiSelectionModel<Map.Entry<String, String>>(presenter.KEY_PROVIDER);
        attributesGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<Map.Entry<String, String>>createCheckboxManager());

        presenter.addDataDisplay(attributesGrid);

        // Bind this view and initialize the widget.
        initWidget( uiBinder.createAndBindUi( this ) );
        
        return this;
    }

    @Override
    public UserAttributesEditor.View setCanCreate(boolean isCreateAllowed) {
        newUserAttributePanel.setVisible(isCreateAllowed);
        return this;
    }

    @Override
    public UserAttributesEditor.View setColumnSortHandler(ColumnSortEvent.ListHandler<Map.Entry<String, String>> sortHandler) {
        attributesGrid.addColumnSortHandler(sortHandler);
        return this;
    }

    @Override
    public UserAttributesEditor.View addColumn(Column<Map.Entry<String, String>, String> column, String name) {
        attributesGrid.addColumn(column, name);
        attributesGrid.setColumnWidth(column, 5, Style.Unit.PCT);
        return this;
    }

    @Override
    public UserAttributesEditor.View removeColumn(int index) {
        attributesGrid.removeColumn(index);
        return this;
    }

    @Override
    public int getColumnCount() {
        return attributesGrid.getColumnCount();
    }

    @Override
    public UserAttributesEditor.View showEmpty() {
        userAttributesRow.setVisible(false);
        userEmptyAttributesRow.setVisible(true);
        return this;
    }

    @Override
    public UserAttributesEditor.View redraw() {
        attributesGrid.redraw();
        return this;
    }
    
}