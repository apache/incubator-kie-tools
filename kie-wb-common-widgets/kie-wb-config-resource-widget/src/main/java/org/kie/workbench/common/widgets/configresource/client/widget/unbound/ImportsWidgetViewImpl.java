/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.BuiltInTypeImportHelper;
import org.kie.workbench.common.widgets.configresource.client.widget.Sorters;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.ParameterizedCommand;

public class ImportsWidgetViewImpl
        extends Composite
        implements ImportsWidgetView {

    interface Binder
            extends UiBinder<Widget, ImportsWidgetViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Button addImportButton;

    @UiField(provided = true)
    CellTable<Import> table = new CellTable<>();

    private AddImportPopup addImportPopup;
    private javax.enterprise.event.Event<LockRequiredEvent> lockRequired;

    private List<Import> importTypes = new ArrayList<>();
    private ListDataProvider<Import> dataProvider = new ListDataProvider<>();
    private final Command addImportCommand = makeAddImportCommand();
    private final ParameterizedCommand<Import> removeImportCommand = makeRemoveImportCommand();

    private ImportsWidgetView.Presenter presenter;

    private boolean isReadOnly = false;

    final ButtonCell deleteImportButton = new ButtonCell(IconType.TRASH,
                                                         ButtonType.DANGER,
                                                         ButtonSize.SMALL);
    final Column<Import, String> deleteImportColumn = new Column<Import, String>(deleteImportButton) {
        @Override
        public String getValue(final Import importType) {
            return ImportConstants.INSTANCE.remove();
        }

        @Override
        public void render(final Cell.Context context,
                           final Import object,
                           final SafeHtmlBuilder sb) {
            if (!BuiltInTypeImportHelper.isBuiltIn(object)) {
                super.render(context, object, sb);
            }
        }
    };

    public ImportsWidgetViewImpl() {
        //CDI proxy
    }

    @Inject
    public ImportsWidgetViewImpl(final AddImportPopup addImportPopup,
                                 final Event<LockRequiredEvent> lockRequired) {
        this.addImportPopup = addImportPopup;
        this.lockRequired = lockRequired;

        setup();
        initWidget(uiBinder.createAndBindUi(this));

        //Disable until content is loaded
        addImportButton.setEnabled(false);
    }

    private void setup() {
        //Setup table
        table.setStriped(true);
        table.setCondensed(true);
        table.setBordered(true);
        table.setEmptyTableWidget(new Label(ImportConstants.INSTANCE.noImportsDefined()));

        //Columns
        final TextColumn<Import> importTypeColumn = new TextColumn<Import>() {

            @Override
            public String getValue(final Import importType) {
                return importType.getType();
            }
        };

        deleteImportColumn.setFieldUpdater((index, importType, value) -> {
            if (isReadOnly) {
                return;
            }
            final YesNoCancelPopup confirm = YesNoCancelPopup.newYesNoCancelPopup(ImportConstants.INSTANCE.remove(),
                                                                                  ImportConstants.INSTANCE.promptForRemovalOfImport0(importType.getType()),
                                                                                  () -> getRemoveImportCommand().execute(importType),
                                                                                  () -> {/*Nothing*/},
                                                                                  null);
            confirm.show();
        });

        table.addColumn(importTypeColumn,
                        new TextHeader(ImportConstants.INSTANCE.importType()));
        table.addColumn(deleteImportColumn,
                        ImportConstants.INSTANCE.remove());

        //Link data
        getDataProvider().addDataDisplay(table);
        getDataProvider().setList(importTypes);
    }

    @Override
    public void init(final ImportsWidgetView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setContent(final List<Import> importTypes,
                           final boolean isReadOnly) {
        this.importTypes = importTypes;
        this.getDataProvider().setList(importTypes);
        this.getDataProvider().getList().sort(Sorters.sortByFQCN());
        this.addImportButton.setEnabled(!isReadOnly);
        this.isReadOnly = isReadOnly;
    }

    @UiHandler("addImportButton")
    public void onClickAddImportButton(final ClickEvent event) {
        addImportPopup.setCommand(addImportCommand);
        addImportPopup.show();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    Command makeAddImportCommand() {
        return () -> {
            final Import importType = new Import(addImportPopup.getImportType());
            getDataProvider().getList().add(importType);
            getDataProvider().getList().sort(Sorters.sortByFQCN());
            lockRequired.fire(new LockRequiredEvent());
            updateRenderedColumns();
        };
    }

    Command getAddImportCommand() {
        return addImportCommand;
    }

    ParameterizedCommand<Import> makeRemoveImportCommand() {
        return (i) -> {
            getDataProvider().getList().remove(i);
            getDataProvider().getList().sort(Sorters.sortByFQCN());
            lockRequired.fire(new LockRequiredEvent());
            updateRenderedColumns();
        };
    }

    ParameterizedCommand<Import> getRemoveImportCommand() {
        return removeImportCommand;
    }

    ListDataProvider<Import> getDataProvider() {
        return dataProvider;
    }

    @Override
    public void updateRenderedColumns() {
        final boolean isAtLeastOneImportRemovable = getDataProvider().getList()
                .stream()
                .anyMatch(iType -> !BuiltInTypeImportHelper.isBuiltIn(iType));

        final int columnCount = table.getColumnCount();
        if (isAtLeastOneImportRemovable && columnCount == 1) {
            table.addColumn(deleteImportColumn,
                            ImportConstants.INSTANCE.remove());
        } else if (!isAtLeastOneImportRemovable && columnCount > 1) {
            table.removeColumn(deleteImportColumn);
        }
    }
}
