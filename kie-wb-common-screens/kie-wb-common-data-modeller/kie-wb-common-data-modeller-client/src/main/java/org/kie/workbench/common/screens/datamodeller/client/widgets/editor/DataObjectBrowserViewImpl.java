/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.resources.images.ImagesResources;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.mvp.Command;

@Dependent
public class DataObjectBrowserViewImpl
        extends Composite
        implements DataObjectBrowserView {

    interface DataObjectBrowserViewImplUIBinder
            extends UiBinder<Widget, DataObjectBrowserViewImpl> {

    }

    enum ColumnId {
        NAME_COLUMN,
        LABEL_COLUMN,
        TYPE_COLUMN
    }

    private static DataObjectBrowserViewImplUIBinder uiBinder = GWT.create(DataObjectBrowserViewImplUIBinder.class);

    private final ButtonCell deleteCell = new ButtonCell(IconType.TRASH,
                                                         ButtonType.DANGER,
                                                         ButtonSize.SMALL);

    @UiField
    Button objectButton;

    @UiField
    Button newPropertyButton;

    @UiField(provided = true)
    SimpleTable<ObjectProperty> propertiesTable = new BrowserSimpleTable<ObjectProperty>(1000);

    @Inject
    private ValidationPopup validationPopup;

    Map<Column<?, ?>, ColumnId> columnIds = new HashMap<Column<?, ?>, ColumnId>();

    ListDataProvider<ObjectProperty> dataProvider;

    private Presenter presenter;

    private boolean readonly = true;

    private int tableHeight = 480;

    @Inject
    public DataObjectBrowserViewImpl(final ValidationPopup validationPopup) {
        initWidget(uiBinder.createAndBindUi(this));
        this.validationPopup = validationPopup;
    }

    @PostConstruct
    protected void init() {

        newPropertyButton.setIcon(IconType.PLUS);
        newPropertyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onNewProperty();
            }
        });

        objectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onSelectCurrentDataObject();
            }
        });

        //Init properties table
        propertiesTable.setEmptyTableCaption(Constants.INSTANCE.objectBrowser_emptyTable());
        propertiesTable.setColumnPickerButtonVisible(false);
        propertiesTable.setToolBarVisible(false);
        setTableHeight(tableHeight);

        addPropertyNameColumn();
        addPropertyLabelColumn();
        addPropertyTypeBrowseColumn();
        addPropertyTypeColumn();
        addRemoveRowColumn();
        addSortHandler();

        //Init the selection model
        SingleSelectionModel<ObjectProperty> selectionModel = new SingleSelectionModel<ObjectProperty>();
        propertiesTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ObjectProperty selectedProperty = ((SingleSelectionModel<ObjectProperty>) propertiesTable.getSelectionModel()).getSelectedObject();
                presenter.onSelectProperty(selectedProperty);
            }
        });

        setReadonly(true);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDataProvider(ListDataProvider<ObjectProperty> dataProvider) {
        if (!dataProvider.getDataDisplays().contains(propertiesTable)) {
            dataProvider.addDataDisplay(propertiesTable);
        }
        this.dataProvider = dataProvider;
    }

    public void setObjectSelectorLabel(String label,
                                       String title) {
        objectButton.setText(label);
        objectButton.setTitle(title);
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        enableNewPropertyAction(!readonly);
        enableDeleteRowAction(!readonly);
    }

    public void enableNewPropertyAction(boolean enable) {
        newPropertyButton.setEnabled(enable);
    }

    @Override
    public void enableDeleteRowAction(boolean enable) {
        deleteCell.setEnabled(enable);
    }

    @Override
    public void redrawRow(int row) {
        //ideally we should have a method redrawRow( row );
        propertiesTable.redraw();
    }

    @Override
    public void redrawTable() {
        propertiesTable.redraw();
    }

    @Override
    public ObjectProperty getSelectedRow() {
        return ((SingleSelectionModel<ObjectProperty>) propertiesTable.getSelectionModel()).getSelectedObject();
    }

    @Override
    public void setSelectedRow(ObjectProperty objectProperty,
                               boolean select) {
        ((SingleSelectionModel<ObjectProperty>) propertiesTable.getSelectionModel()).setSelected(objectProperty,
                                                                                                 select);
    }

    @Override
    public void setTableHeight(int height) {
        this.tableHeight = height;
        propertiesTable.setHeight(tableHeight + "px");
    }

    @Override
    public int getTableHeight() {
        return tableHeight;
    }

    private void addPropertyNameColumn() {

        Column<ObjectProperty, String> column = new Column<ObjectProperty, String>(new TextCell()) {

            @Override
            public String getValue(ObjectProperty objectProperty) {
                if (objectProperty.getName() != null) {
                    return objectProperty.getName();
                } else {
                    return "";
                }
            }
        };

        column.setSortable(true);
        propertiesTable.addColumn(column,
                                  Constants.INSTANCE.objectBrowser_columnName());
        propertiesTable.setColumnWidth(column,
                                       25,
                                       Style.Unit.PCT);
        columnIds.put(column,
                      ColumnId.NAME_COLUMN);
    }

    private void addPropertyLabelColumn() {

        Column<ObjectProperty, String> column = new Column<ObjectProperty, String>(new TextCell()) {

            @Override
            public String getValue(ObjectProperty objectProperty) {
                if (objectProperty.getName() != null) {
                    return AnnotationValueHandler.getStringValue(objectProperty,
                                                                 MainDomainAnnotations.LABEL_ANNOTATION,
                                                                 MainDomainAnnotations.VALUE_PARAM);
                } else {
                    return "";
                }
            }
        };

        column.setSortable(true);
        propertiesTable.addColumn(column,
                                  Constants.INSTANCE.objectBrowser_columnLabel());
        propertiesTable.setColumnWidth(column,
                                       25,
                                       Style.Unit.PCT);
        columnIds.put(column,
                      ColumnId.LABEL_COLUMN);
    }

    private void addPropertyTypeBrowseColumn() {

        ClickableImageResourceCell typeImageCell = new ClickableImageResourceCell(true,
                                                                                  20);
        final Column<ObjectProperty, ImageResource> column = new Column<ObjectProperty, ImageResource>(typeImageCell) {
            @Override
            public ImageResource getValue(final ObjectProperty property) {
                if (presenter != null && presenter.isSelectablePropertyType(property)) {
                    return ImagesResources.INSTANCE.BrowseObject();
                } else {
                    return null;
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<ObjectProperty, ImageResource>() {
            public void update(final int index,
                               final ObjectProperty property,
                               final ImageResource value) {

                presenter.onSelectPropertyType(property);
            }
        });

        propertiesTable.addColumn(column,
                                  " ");
        propertiesTable.setColumnWidth(column,
                                       38,
                                       Style.Unit.PX);
    }

    private void addPropertyTypeColumn() {

        Column<ObjectProperty, String> column = new Column<ObjectProperty, String>(new TextCell()) {

            @Override
            public String getValue(ObjectProperty objectProperty) {
                if (objectProperty.getName() != null && presenter != null) {
                    return presenter.getPropertyTypeDisplayValue(objectProperty);
                } else {
                    return "";
                }
            }
        };

        column.setSortable(true);
        propertiesTable.addColumn(column,
                                  Constants.INSTANCE.objectBrowser_columnType());
        propertiesTable.setColumnWidth(column,
                                       35,
                                       Style.Unit.PCT);
        columnIds.put(column,
                      ColumnId.TYPE_COLUMN);
    }

    private void addRemoveRowColumn() {
        ButtonCell buttonCell = new ButtonCell(IconType.TRASH,
                                               ButtonType.DANGER,
                                               ButtonSize.SMALL);
        Column<ObjectProperty, String> column = new Column<ObjectProperty, String>(buttonCell) {
            @Override
            public String getValue(ObjectProperty objectProperty) {
                return Constants.INSTANCE.objectBrowser_action_delete();
            }
        };

        column.setFieldUpdater(new FieldUpdater<ObjectProperty, String>() {
            @Override
            public void update(int index,
                               ObjectProperty objectProperty,
                               String value) {

                if (!readonly) {
                    presenter.onDeleteProperty(objectProperty,
                                               index);
                }
            }
        });

        propertiesTable.addColumn(column,
                                  "");
        propertiesTable.setColumnWidth(column,
                                       calculateButtonSize(Constants.INSTANCE.objectBrowser_action_delete()),
                                       Style.Unit.PX);
    }

    private void addSortHandler() {

        propertiesTable.addColumnSortHandler(new ColumnSortEvent.Handler() {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                Column<?, ?> column = event.getColumn();
                ColumnId columnId;

                if ((columnId = columnIds.get(column)) != null) {
                    switch (columnId) {
                        case NAME_COLUMN:
                            presenter.onSortByName(event.isSortAscending());
                            break;
                        case LABEL_COLUMN:
                            presenter.onSortByLabel(event.isSortAscending());
                            break;
                        case TYPE_COLUMN:
                            presenter.onSortByType(event.isSortAscending());
                    }
                }
            }
        });
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void showYesNoCancelPopup(final String title,
                                     final String content,
                                     final Command yesCommand,
                                     final String yesButtonText,
                                     final ButtonType yesButtonType,
                                     final Command noCommand,
                                     final String noButtonText,
                                     final ButtonType noButtonType,
                                     final Command cancelCommand,
                                     final String cancelButtonText,
                                     final ButtonType cancelButtonType) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                                 content,
                                                                                 yesCommand,
                                                                                 yesButtonText,
                                                                                 yesButtonType,
                                                                                 noCommand,
                                                                                 noButtonText,
                                                                                 noButtonType,
                                                                                 cancelCommand,
                                                                                 cancelButtonText,
                                                                                 cancelButtonType);

        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }

    @Override
    public void showUsagesPopupForDeletion(final String message,
                                           final List<Path> usedByFiles,
                                           final Command yesCommand,
                                           final Command cancelCommand) {

        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForDeletion(message,
                                                                                    usedByFiles,
                                                                                    yesCommand,
                                                                                    cancelCommand);

        showUsagesPopup.setClosable(false);
        showUsagesPopup.show();
    }

    @Override
    public void showValidationPopupForDeletion(List<ValidationMessage> validationMessages,
                                               Command yesCommand,
                                               Command cancelCommand) {
        validationPopup.showDeleteValidationMessages(yesCommand,
                                                     cancelCommand,
                                                     validationMessages);
    }

    private int calculateButtonSize(String buttonLabel) {
        return 11 * buttonLabel.length() + 12 + 4;
    }

    private class BrowserSimpleTable<T> extends SimpleTable<T> {

        public BrowserSimpleTable(int pageSize) {
            super();
            dataGrid.setPageSize(pageSize);
        }
    }
}
