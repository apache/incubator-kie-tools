/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.rows;

import java.util.*;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.css.CssValue;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.*;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.*;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class Row implements LayoutEditorElement {

    public static final Integer ROW_DEFAULT_HEIGHT = 12;
    public static final int ROW_MIN_HEIGHT = 2;

    private final LayoutDragComponentHelper layoutDragComponentHelper;
    private LayoutEditorCssHelper layoutCssHelper;
    private UniqueIDGenerator idGenerator = new UniqueIDGenerator();

    private LayoutEditorElement parentElement;
    private String id;
    private Map<String,String> properties = new HashMap<>();
    private LayoutTemplate.Style pageStyle;

    private View view;

    private List<Column> columns = new ArrayList<>();

    private Instance<ComponentColumn> columnInstance;

    private Instance<ColumnWithComponents> columnWithComponentsInstance;

    private ParameterizedCommand<RowDrop> dropOnRowCommand;

    private ParameterizedCommand<Row> removeRowCommand;

    private ParameterizedCommand<ColumnDrop> removeComponentCommand;
    private Supplier<LayoutTemplate> currentLayoutTemplateSupplier;
    private Supplier<Boolean> lockSupplier;

    private ColumnWithComponents parentColumnWithComponents;

    private DnDManager dndManager;

    private boolean dropEnable = true;
    private boolean selectable = false;
    private boolean selected = false;
    private Event<ComponentDropEvent> componentDropEvent;
    private Event<ComponentRemovedEvent> componentRemovedEvent;
    private Event<RowResizeEvent> rowResizeEvent;
    private Event<LayoutEditorElementSelectEvent> rowSelectEvent;
    private Event<LayoutEditorElementUnselectEvent> rowUnselectEvent;

    private Integer height;
    private boolean canResizeUp;
    private boolean canResizeDown;

    LayoutEditorFocusController layoutEditorFocusController;

    @Inject
    public Row(View view,
               Instance<ComponentColumn> columnInstance,
               Instance<ColumnWithComponents> columnWithComponentsInstance,
               DnDManager dndManager,
               LayoutDragComponentHelper layoutDragComponentHelper,
               LayoutEditorCssHelper layoutCssHelper,
               Event<ComponentDropEvent> componentDropEvent,
               Event<ComponentRemovedEvent> componentRemovedEvent,
               Event<RowResizeEvent> rowResizeEvent,
               Event<LayoutEditorElementSelectEvent> rowSelectEvent,
               Event<LayoutEditorElementUnselectEvent> rowUnselectEvent,
               LayoutEditorFocusController layoutEditorFocusController) {

        this.view = view;
        this.columnInstance = columnInstance;
        this.columnWithComponentsInstance = columnWithComponentsInstance;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.layoutCssHelper = layoutCssHelper;
        this.componentDropEvent = componentDropEvent;
        this.componentRemovedEvent = componentRemovedEvent;
        this.rowResizeEvent = rowResizeEvent;
        this.rowSelectEvent = rowSelectEvent;
        this.rowUnselectEvent = rowUnselectEvent;
        this.layoutEditorFocusController = layoutEditorFocusController;
    }

    public void init(ParameterizedCommand<RowDrop> dropOnRowCommand,
                     ParameterizedCommand<Row> removeCommand,
                     ParameterizedCommand<ColumnDrop> removeComponentCommand,
                     Supplier<LayoutTemplate> currentLayoutTemplateSupplier,
                     Supplier<Boolean> lockSupplier,
                     Integer height) {
        this.dropOnRowCommand = dropOnRowCommand;
        this.removeRowCommand = removeCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.currentLayoutTemplateSupplier = currentLayoutTemplateSupplier;
        this.lockSupplier = lockSupplier;
        this.parentColumnWithComponents = null;
        this.height = height;
        setupPageLayout(height);
    }

    public void init(ParameterizedCommand<RowDrop> dropOnRowCommand,
                     ParameterizedCommand<Row> removeCommand,
                     ParameterizedCommand<ColumnDrop> removeComponentCommand,
                     ColumnWithComponents parentColumnWithComponents,
                     Supplier<LayoutTemplate> currentLayoutTemplateSupplier,
                     Supplier<Boolean> lockSupplier,
                     Integer height) {
        this.dropOnRowCommand = dropOnRowCommand;
        this.removeRowCommand = removeCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.parentColumnWithComponents = parentColumnWithComponents;
        this.currentLayoutTemplateSupplier = currentLayoutTemplateSupplier;
        this.lockSupplier = lockSupplier;
        this.height = height;
        setupPageLayout(height);
    }

    public void load(ParameterizedCommand<RowDrop> dropOnRowCommand,
                     LayoutRow layoutRow,
                     ParameterizedCommand<Row> removeCommand,
                     ParameterizedCommand<ColumnDrop> removeComponentCommand,
                     Supplier<LayoutTemplate> currentLayoutTemplateSupplier,
                     Supplier<Boolean> lockSupplier) {
        this.dropOnRowCommand = dropOnRowCommand;
        this.removeRowCommand = removeCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.currentLayoutTemplateSupplier = currentLayoutTemplateSupplier;
        this.lockSupplier = lockSupplier;
        this.height = getHeight(layoutRow.getHeight());
        this.properties = layoutRow.getProperties();
        setupPageLayout(height);
        extractColumns(layoutRow);
        setupColumnResizeActions();
        setupCssProperties();
    }

    private int getHeight(String layoutRow) {
        if (shouldILoadDefaultHeight(layoutRow)) {
            return Row.ROW_DEFAULT_HEIGHT;
        }
        return Integer.parseInt(layoutRow);
    }

    private boolean shouldILoadDefaultHeight(String layoutRow) {
        return layoutRow == null || layoutRow.isEmpty();
    }

    private void setupPageLayout(Integer height) {
        if (pageStyle == LayoutTemplate.Style.PAGE) {
            view.setupPageLayout(height);
        }
    }

    private void extractColumns(LayoutRow layoutRow) {
        for (LayoutColumn layoutColumn : layoutRow.getLayoutColumns()) {
            if (isColumnWithComponents(layoutColumn)) {
                extractColumnWithComponents(layoutColumn);
            } else {
                extractComponentColumn(layoutColumn);
            }
        }
    }

    private void extractComponentColumn(LayoutColumn layoutColumn) {
        ComponentColumn newComponentColumn = getComponentColumn(layoutColumn);
        this.columns.add(newComponentColumn);
    }

    private void extractColumnWithComponents(LayoutColumn layoutColumn) {
        for (LayoutRow row : layoutColumn.getRows()) {
            Integer columnWidth = new Integer(layoutColumn.getSpan());
            final ColumnWithComponents columnWithComponents = createColumnWithComponentsInstance();

            columnWithComponents
                    .init(this,
                          columnWidth,
                          pageStyle,
                          dropCommand(),
                          removeComponentCommand,
                          removeColumnCommand(),
                          currentLayoutTemplateSupplier,
                          lockSupplier,
                          getHeight(layoutColumn.getHeight()));

            for (LayoutColumn column : row.getLayoutColumns()) {
                ComponentColumn newComponentColumn = getComponentColumn(column);
                newComponentColumn.setColumnHeight(getHeight(column.getHeight()));
                columnWithComponents.withComponents(newComponentColumn);
            }

            this.columns.add(columnWithComponents);
        }
    }

    public Column hasComponent(Column targetColumn) {
        for (Column column : columns) {
            if (targetColumn.hashCode() == column.hashCode()) {
                return column;
            }
        }
        return null;
    }

    public void dragStart() {
        dndManager.beginRowMove(id);
    }

    public boolean canDrag() {
        return dndManager.canMoveRow();
    }

    public void dragEndMove() {
        dndManager.dragEndMove();
    }

    private boolean isColumnWithComponents(LayoutColumn layoutColumn) {
        return layoutColumn.hasRows();
    }

    private ComponentColumn getComponentColumn(LayoutColumn column) {
        LayoutComponent layoutComponent = column.getLayoutComponents().get(0);
        return createNewComponentColumn(layoutComponent,
                                        new Integer(
                                                column.getSpan()),
                                        false);
    }

    public void addColumns(ComponentColumn... _columns) {
        for (ComponentColumn column : _columns) {
            column.setParentElement(this);
            column.setId(idGenerator.createColumnID(id));
            column.setDropCommand(dropCommand());
            columns.add(column);
        }
    }

    public void withOneColumn(LayoutComponent layoutComponent,
                              boolean newComponent) {
        final ComponentColumn column = createComponentColumnInstance();

        column.init(this,
                    Column.DEFAULT_COLUMN_WIDTH,
                    layoutComponent,
                    dropCommand(),
                    removeColumnCommand(),
                    currentLayoutTemplateSupplier,
                    lockSupplier,
                    newComponent);
        columns.add(column);
        setupColumnResizeActions();
    }

    protected ComponentColumn createComponentColumnInstance() {
        final ComponentColumn column = columnInstance.get();
        column.setSelectable(selectable);
        column.setup(idGenerator.createColumnID(id),
                     pageStyle);
        return column;
    }

    public ParameterizedCommand<ColumnDrop> dropCommand() {
        return (drop) -> {
            ColumnDropContext.setActiveDrop(drop);
            if (dropFromMoveComponent(drop)) {
                removeOldComponent(drop);
                // notifying dndManager that the move has finished!
                dndManager.endComponentMove();
            }
            notifyDrop(drop);
            Row.this.columns = updateColumns(drop,
                                             Row.this.columns);
            updateView();
            ColumnDropContext.clear();
        };
    }

    private void notifyDrop(ColumnDrop columnDrop) {
        componentDropEvent.fire(new ComponentDropEvent(columnDrop.getComponent(),
                                                       columnDrop.getType().equals(ComponentDropType.FROM_MOVE)));
    }

    private void removeOldComponent(ColumnDrop drop) {
        removeComponentCommand.execute(drop);
    }

    private boolean dropFromMoveComponent(ColumnDrop drop) {
        return !drop.newComponent();
    }

    ParameterizedCommand<Column> removeColumnCommand() {
        if (parentColumnWithComponents != null) {
            return parentColumnWithComponents.getRemoveColumnCommand();
        }
        return (targetCol) -> {
            removeColumn(targetCol);
        };
    }

    public void removeColumn(Column targetColumn) {
        removeChildColumn(targetColumn);
    }

    public void removeChildColumn(Column targetColumn) {
        if (isAChildColumn(targetColumn)) {
            // Removing a child Column
            removeChildComponentColumn(targetColumn);
        } else {
            // Removing a column inside a ColumnWithComponents
            lookupAndRemoveFromColumnsWithComponents(targetColumn);
        }

        // If the current row is empty we must remove it from the layout
        if (rowIsEmpty()) {
            removeRowCommand.execute(this);
        }
    }

    private void removeChildComponentColumn(Column targetColumn) {
        if (needToUpdateWidthOfMySiblings(targetColumn)) {
            updateWidthOfMySiblings(targetColumn);
        }
        if (needToUpdateHeightOfMySiblings(targetColumn)) {
            updateHeightOfSiblingColumn(targetColumn);
        }
        columns.remove(targetColumn);
        destroy(targetColumn);
        notifyRemoval(targetColumn.getLayoutComponent());
        updateView();
    }

    private boolean needToUpdateHeightOfMySiblings(Column targetColumn) {
        return targetColumn.getColumnHeight() != ComponentColumn.DEFAULT_COLUMN_HEIGHT;
    }

    private void updateHeightOfSiblingColumn(Column columnToRemove) {
        final int removeIndex = getColumnIndex(columnToRemove);
        if (isFirstColumn(removeIndex)) {
            final Column sibling = columns.get(1);
            Integer remove = columnToRemove.getColumnHeight();
            Integer add = sibling.getColumnHeight();
            Integer newHeight = remove + add;
            sibling.setColumnHeight(newHeight);
        } else {
            final Column sibling = columns.get(removeIndex - 1);
            Integer remove = columnToRemove.getColumnHeight();
            Integer add = sibling.getColumnHeight();
            Integer newHeight = remove + add;
            sibling.setColumnHeight(newHeight);
        }
    }

    private void notifyRemoval(LayoutComponent layoutComponent) {
        componentRemovedEvent.fire(new ComponentRemovedEvent(layoutComponent,
                                                             dndManager.isOnComponentMove()));
    }

    public boolean cointainsColumn(Column targetColumn) {
        return isAChildColumn(targetColumn) || checkIfColumnExistsInChildColumnWithComponents(targetColumn).isPresent();
    }

    private boolean isAChildColumn(Column targetColumn) {
        return columns.contains(targetColumn);
    }

    private Optional<Column> checkIfColumnExistsInChildColumnWithComponents(Column targetColumn) {
        return columns.stream()
                .filter(column -> column instanceof ColumnWithComponents && ((ColumnWithComponents) column).hasComponent(targetColumn))
                .findAny();
    }

    public boolean rowIsEmpty() {
        return columns.isEmpty();
    }

    private void lookupAndRemoveFromColumnsWithComponents(Column targetColumn) {
        // find the ColumnWithComponents that contains the targetColumn
        Optional<Column> optional = checkIfColumnExistsInChildColumnWithComponents(targetColumn);

        // If present let's remove it!
        optional.ifPresent(column -> removeComponentFromColumnWithComponents((ColumnWithComponents) column,
                                                                             targetColumn));
    }

    private void removeComponentFromColumnWithComponents(ColumnWithComponents parent,
                                                         Column targetColumn) {
        PortablePreconditions.checkNotNull("parent",
                                           parent);
        PortablePreconditions.checkNotNull("targetColumn",
                                           targetColumn);

        // if parent contains targetColumn remove & destroy targetColumn
        if (parent.hasComponent(targetColumn)) {
            parent.remove(targetColumn);
            destroy(targetColumn);
        }

        // if parent has only one child remaining we'll remove parent and promote the remaining child on the layout.
        if (parent.getRow().getColumns().size() == 1 && !isDropInSameColumnWithComponent(ColumnDropContext.getActiveDrop())) {
            replaceColumnWithComponents(parent);
        }
    }

    protected boolean isDropInSameColumnWithComponent(ColumnDrop drop) {
        if (drop != null) {
            int indexOfRowIdOfColumn = drop.getOldColumn().getId().lastIndexOf("column");
            int indexOfEndIdOfColumn = drop.getEndId().lastIndexOf("column");
            if (indexOfRowIdOfColumn > 0 && indexOfEndIdOfColumn > 0) {
                String rowIdOfColumn = drop.getOldColumn().getId().substring(0, indexOfRowIdOfColumn);
                String rowEndIdOfColumn = drop.getEndId().substring(0, indexOfEndIdOfColumn);
                return rowIdOfColumn.equals(rowEndIdOfColumn);
            }
        }
        return false;
    }

    private void replaceColumnWithComponents(ColumnWithComponents columnToReplace) {
        PortablePreconditions.checkNotNull("columnToReplace",
                                           columnToReplace);

        // check again if parent has only one child remaining
        if (columnToReplace.getRow().getColumns().size() == 1) {

            // getting the remaining column
            ComponentColumn originalColumn = (ComponentColumn) columnToReplace.getRow().getColumns().remove(0);
            ComponentColumn column = createNewComponentColumn(originalColumn.getLayoutComponent(),
                                              columnToReplace.getColumnWidth(),
                                              false);

            column.setId(columnToReplace.getId());

            int index = columns.indexOf(columnToReplace);

            // promoting the remaining child on the actual row
            columns.set(index, column);

            // destroy current column & update view
            columnToReplace.preDestroy();

            updateView();
        }
    }

    private boolean needToUpdateWidthOfMySiblings(Column targetColumn) {
        return targetColumn.getColumnWidth() != Column.DEFAULT_COLUMN_WIDTH && !targetColumn.isInnerColumn();
    }

    private void updateWidthOfMySiblings(Column columnToRemove) {
        final int removeIndex = getColumnIndex(columnToRemove);
        if (isFirstColumn(removeIndex)) {
            if (firstColumnHasRightSibling()) {
                final Column sibling = columns.get(1);
                sibling.setColumnWidth(sibling.getColumnWidth() + columnToRemove.getColumnWidth());
            }
        } else {
            final Column sibling = columns.get(removeIndex - 1);
            sibling.setColumnWidth(sibling.getColumnWidth() + columnToRemove.getColumnWidth());
        }
    }

    private boolean firstColumnHasRightSibling() {
        return columns.size() >= 2;
    }

    private int getColumnIndex(Column columnToRemove) {
        return columns.indexOf(columnToRemove);
    }

    private boolean isFirstColumn(int columnIndex) {
        return columnIndex == 0;
    }

    public void disableDrop() {
        this.dropEnable = false;
    }

    protected ColumnWithComponents createColumnWithComponentsInstance() {
        final ColumnWithComponents column = columnWithComponentsInstance.get();
        column.setId(idGenerator.createColumnID(id));
        return column;
    }

    public void drop(String dropData,
                     RowDrop.Orientation orientation) {
        if (dndManager.isOnRowMove()) {
            dndManager.endRowMove(id,
                                  orientation);
        } else if (dndManager.isOnComponentMove()) {
            dropOnRowCommand
                    .execute(new RowDrop(dndManager.getLayoutComponentMove(),
                                         id,
                                         orientation)
                                     .fromMove(dndManager.getRowId(),
                                               dndManager.getDraggedColumn()));
        } else {
            dropOnRowCommand
                    .execute(
                            new RowDrop(layoutDragComponentHelper.getLayoutComponentFromDrop(dropData),
                                        id,
                                        orientation));
        }
    }

    @PostConstruct
    public void post() {
        view.init(this);
    }

    @PreDestroy
    public void preDestroy() {
        for (Column column : columns) {
            destroy(column);
        }
    }

    private List<Column> updateColumns(ColumnDrop drop,
                                       List<Column> originalColumns) {
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < originalColumns.size(); i++) {
            final Column currentColumn = originalColumns.get(i);
            if (dropIsOn(drop,
                         currentColumn) && columnCanBeSplitted(currentColumn)) {
                if (isComponentColumn(currentColumn)) {
                    handleDropOnComponentColumn(drop,
                                                columns,
                                                i,
                                                currentColumn);
                } else {
                    handleDropOnColumnWithComponents(drop,
                                                     columns,
                                                     i
                            ,
                                                     currentColumn);
                }
            } else {
                columns.add(currentColumn);
            }
        }
        return columns;
    }

    private void handleDropOnColumnWithComponents(ColumnDrop drop,
                                                  List<Column> columns,
                                                  int columnIndex,
                                                  Column currentColumn) {
        ColumnWithComponents column = (ColumnWithComponents) currentColumn;
        if (drop.isASideDrop()) {
            handleSideDrop(drop,
                           columns,
                           columnIndex,
                           currentColumn);
        } else {
            if (column.hasInnerRows()) {
                Row innerRow = column.getRow();
                innerRow.columns = updateInnerColumns(drop,
                                                      innerRow.getColumns());
            }
            columns.add(column);
        }
    }

    private List<Column> updateInnerColumns(ColumnDrop drop,
                                            List<Column> originalColumns) {
        List<Column> columns = new ArrayList<>();

        for (int i = 0; i < originalColumns.size(); i++) {
            final Column currentColumn = originalColumns.get(i);
            if (isComponentColumn(currentColumn)) {
                handleDropInnerColumn(drop,
                                      columns,
                                      i,
                                      currentColumn);
            }
        }
        return columns;
    }

    private void handleDropInnerColumn(ColumnDrop drop,
                                       List<Column> columns,
                                       int columnIndex,
                                       Column column) {
        ComponentColumn currentColumn = (ComponentColumn) column;
        if (dropIsOn(drop,
                     currentColumn) && columnCanBeSplitted(currentColumn)) {
            if (drop.isASideDrop()) {
                handleSideDrop(drop,
                               columns,
                               columnIndex,
                               currentColumn);
            } else {
                handleInnerDrop(drop,
                                columns,
                                currentColumn);
            }
        } else {
            columns.add(currentColumn);
        }
    }

    private void handleInnerDrop(ColumnDrop drop,
                                 List<Column> columns,
                                 ComponentColumn currentColumn) {
        Integer newInnerColumnHeight = currentColumn.getColumnHeight() / 2;
        final ComponentColumn newColumn = createNewInnerColumn(drop,
                                                               currentColumn,
                                                               newInnerColumnHeight);
        currentColumn.setColumnHeight(calculateColumnHeight(currentColumn));
        addColumnsInTheRightPosition(drop,
                                     columns,
                                     currentColumn,
                                     newColumn);
    }

    private Integer calculateColumnHeight(ComponentColumn column) {
        Integer originalHeight = column.getColumnHeight();
        Integer newColumnHeight = originalHeight / 2;
        if (originalHeight % 2 == 0) {
            return newColumnHeight;
        } else {
            newColumnHeight = newColumnHeight + 1;
            return newColumnHeight;
        }
    }

    private void addColumnsInTheRightPosition(ColumnDrop drop,
                                              List<Column> columns,
                                              ComponentColumn currentColumn,
                                              ComponentColumn newColumn) {
        if (drop.isADownDrop()) {
            columns.add(currentColumn);
            columns.add(newColumn);
        } else {
            columns.add(newColumn);
            columns.add(currentColumn);
        }
    }

    private ComponentColumn createNewInnerColumn(ColumnDrop drop,
                                                 ComponentColumn currentColumn,
                                                 Integer innerColumnHeight) {
        final ComponentColumn newColumn = createComponentColumnInstance();

        newColumn.init(currentColumn.getParentElement(),
                       Column.DEFAULT_COLUMN_WIDTH,
                       drop.getComponent(),
                       dropCommand(),
                       removeColumnCommand(),
                       currentLayoutTemplateSupplier,
                       lockSupplier,
                       drop.newComponent());
        newColumn.setColumnHeight(innerColumnHeight);
        return newColumn;
    }

    private void handleInnerComponentDrop(ColumnDrop drop,
                                          int columnIndex,
                                          List<Column> columns,
                                          ComponentColumn currentColumn) {
        Integer innerColumnHeight = (currentColumn.getColumnHeight() / 2);
        if (parentColumnWithComponents == null) {
            Integer width = currentColumn.getColumnWidth();
            final ColumnWithComponents columnWithComponents = createColumnWithComponentsInstance();
            columnWithComponents
                    .init(this,
                          width,
                          pageStyle,
                          dropCommand(),
                          removeComponentCommand,
                          removeColumnCommand(),
                          currentLayoutTemplateSupplier,
                          lockSupplier,
                          currentColumn.getColumnHeight());

            final ComponentColumn newColumn = createComponentColumn(
                    drop.getComponent(),
                    drop.newComponent());
            newColumn.setColumnHeight(innerColumnHeight);
            currentColumn = updateCurrentColumn(currentColumn);

            if (drop.isADownDrop()) {
                columnWithComponents.withComponents(currentColumn,
                                                    newColumn);
            } else {
                columnWithComponents.withComponents(newColumn,
                                                    currentColumn);
            }

            columns.add(columnWithComponents);
        } else {
            final ComponentColumn newColumn = createComponentColumn(
                    drop.getComponent(),
                    drop.newComponent());
            newColumn.setColumnHeight(innerColumnHeight);
            currentColumn.setColumnHeight(calculateColumnHeight(currentColumn));
            addColumnsInTheRightPosition(drop,
                                         columns,
                                         currentColumn,
                                         newColumn);
        }
    }

    private ComponentColumn updateCurrentColumn(ComponentColumn currentColumn) {
        currentColumn.setColumnWidth(Column.DEFAULT_COLUMN_WIDTH);
        currentColumn.recalculateWidth();
        currentColumn.setColumnHeight(calculateColumnHeight(currentColumn));
        return currentColumn;
    }

    private ComponentColumn createComponentColumn(LayoutComponent layoutComponent,
                                                  boolean newComponent) {
        return createNewComponentColumn(layoutComponent,
                                        12,
                                        newComponent);
    }

    private ComponentColumn createNewComponentColumn(LayoutComponent layoutComponent,
                                                     Integer columnSize,
                                                     boolean newComponent) {
        final ComponentColumn newColumn = createComponentColumnInstance();

        newColumn.init(this,
                       columnSize,
                       layoutComponent,
                       dropCommand(),
                       removeColumnCommand(),
                       currentLayoutTemplateSupplier,
                       lockSupplier,
                       newComponent);
        return newColumn;
    }

    private void handleDropOnComponentColumn(ColumnDrop drop,
                                             List<Column> columns,
                                             int columnIndex,
                                             Column column) {
        ComponentColumn componentColumn = (ComponentColumn) column;
        if (drop.isASideDrop()) {
            handleSideDrop(drop,
                           columns,
                           columnIndex,
                           componentColumn);
        } else {
            handleInnerComponentDrop(drop,
                                     columnIndex,
                                     columns,
                                     componentColumn);
        }
    }

    private boolean columnCanBeSplitted(Column column) {
        return column.getColumnWidth() != 1;
    }

    private boolean isComponentColumn(Column currentColumn) {
        return currentColumn instanceof ComponentColumn;
    }

    private void handleSideDrop(ColumnDrop drop,
                                List<Column> columns,
                                int columnIndex,
                                Column currentColumn) {

        if (drop.isALeftDrop()) {
            final ComponentColumn newColumn = createNewComponentColumn(drop.getComponent(),
                                                                       currentColumn.getColumnWidth() / 2,
                                                                       drop.newComponent());
            setupColumnWidth(currentColumn);

            columns.add(newColumn);
            columns.add(currentColumn);
        } else {
            final ComponentColumn newColumn = createNewComponentColumn(drop.getComponent(),
                                                                       currentColumn.getColumnWidth() / 2,
                                                                       drop.newComponent());
            setupColumnWidth(currentColumn);

            columns.add(currentColumn);
            columns.add(newColumn);
        }
    }

    private Integer setupColumnWidth(Column column) {
        Integer originalSize = column.getColumnWidth();
        Integer newColumnSize = originalSize / 2;
        if (originalSize % 2 == 0) {
            column.setColumnWidth(newColumnSize);
        } else {
            column.setColumnWidth(newColumnSize + 1);
        }
        return newColumnSize;
    }

    private boolean dropIsOn(ColumnDrop drop,
                             Column column) {
        return drop.getEndId().equalsIgnoreCase(column.getId());
    }

    public void resizeColumns(@Observes ColumnResizeEvent resize) {
        if (resizeEventIsinThisRow(resize)) {

            Column resizedColumn = getColumn(resize);

            if (resizedColumn != null) {
                Column affectedColumn = null;
                if (resize.isLeft()) {
                    affectedColumn = lookUpForLeftNeighbor(resizedColumn);
                } else {
                    affectedColumn = lookUpForRightNeighbor(resizedColumn);
                }
                if (affectedColumn != null) {
                    resizedColumn.incrementWidth();
                    affectedColumn.reduceWidth();
                }
            }
            updateView();
        }
    }

    private Column lookUpForLeftNeighbor(Column resizedColumn) {
        int idx = getColumnIndex(resizedColumn) - 1;
        return idx < 0 ? null : columns.get(idx);
    }

    private Column lookUpForRightNeighbor(Column resizedColumn) {
        int idx = getColumnIndex(resizedColumn) + 1;
        return idx < columns.size() ? columns.get(idx) : null;
    }

    private boolean resizeEventIsinThisRow(@Observes ColumnResizeEvent resize) {
        return resize.getRowHash() == hashCode();
    }

    private Column getColumn(ColumnResizeEvent resize) {
        for (Column column : columns) {
            if (resize.getColumnHash() == column.hashCode()) {
                return column;
            }
        }
        return null;
    }

    public void updateView() {
        layoutEditorFocusController.recordFocus();
        view.clear();
        setupColumnResizeActions();
        for (Column column : columns) {
            view.addColumn(column.getView());
        }
    }

    private void setupColumnResizeActions() {
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            setupColumnResizeActions(columns,
                                     column,
                                     i);
        }
    }

    private void setupCssProperties() {
        List<CssValue> cssValueList = layoutCssHelper.readCssValues(properties);
        view.applyCssValues(cssValueList);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperty(String property, String value) {
        properties.put(property, value);
        setupCssProperties();
    }

    @Override
    public void removeProperty(String property) {
        properties.remove(property);
        setupCssProperties();
    }

    @Override
    public void clearProperties() {
        properties.clear();
        setupCssProperties();
    }

    @Override
    public List<PropertyEditorCategory> getPropertyCategories() {
        return layoutCssHelper.getRowPropertyCategories(this);
    }

    private void setupColumnResizeActions(List<Column> columns,
                                          Column currentColumn,
                                          int index) {
        if (firstColumn(index)) {
            boolean canResizeRight = canResizeRight(index,
                                                    columns);
            currentColumn.setupResize(false,
                                      canResizeRight);
        } else {
            currentColumn.setupResize(canResizeLeft(index,
                                                    columns),
                                      canResizeRight(index,
                                                     columns));
        }
    }

    private boolean canResizeLeft(int index,
                                  List<Column> columns) {
        Column rightSibling = columns.get(index - 1);
        return rightSibling.getColumnWidth() > 1;
    }

    private boolean canResizeRight(int index,
                                   List<Column> columns) {
        if (hasRightSibling(index,
                            columns)) {
            Column rightSibling = columns.get(index + 1);
            return rightSibling.getColumnWidth() > 1;
        }
        return false;
    }

    private boolean hasRightSibling(int index,
                                    List<Column> columns) {
        return columns.size() > (index + 1);
    }

    private boolean firstColumn(int index) {
        return index == 0;
    }

    public UberElement<Row> getView() {
        updateView();
        return view;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean isDropEnable() {
        return dropEnable && canISplitMySize();
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        view.setSelectEnabled(selectable);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (isSelectable()) {
            if (selected) {
                this.selected = true;
                view.setSelected(true);
            } else {
                this.selected = false;
                view.setSelected(false);
            }
        }
    }

    private boolean canISplitMySize() {
        if (pageStyle == LayoutTemplate.Style.PAGE) {
            Integer size = Integer.valueOf(getHeight());
            return size > (ROW_MIN_HEIGHT * 2);
        }
        return true;
    }

    protected void destroy(Object o) {
        BeanHelper.destroy(o);
    }

    public void calculateSizeChilds() {
        for (Column column : columns) {
            column.calculateWidth();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LayoutEditorElementType geElementType() {
        return LayoutEditorElementType.ROW;
    }

    @Override
    public LayoutEditorElement getParentElement() {
        return parentElement;
    }

    public void setup(LayoutEditorElement parent,
                      String id,
                      LayoutTemplate.Style pageStyle) {
        this.parentElement = parent;
        this.id = id;
        this.pageStyle = pageStyle;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
        view.setHeight(height);
    }

    public boolean canResizeUp() {
        return canResizeUp;
    }

    public boolean canResizeDown() {
        return canResizeDown;
    }

    public void resizeUp() {
        rowResizeEvent.fire(new RowResizeEvent(parentElement.hashCode(),
                hashCode()).up());
    }

    public void resizeDown() {
        rowResizeEvent.fire(new RowResizeEvent(parentElement.hashCode(),
                hashCode()).down());
    }

    public void incrementHeight() {
        Integer newSize = height + 1;
        this.height = newSize;
        view.setHeight(newSize);
    }

    public void reduceHeight() {
        Integer newSize = height - 1;
        this.height = newSize;
        view.setHeight(newSize);
    }

    public void setupResize(boolean canResizeUp,
                            boolean canResizeDown) {

        this.canResizeUp = canResizeUp;
        this.canResizeDown = canResizeDown;
        view.setupResize();
    }

    public ColumnWithComponents getParentColumnWithComponents() {
        return parentColumnWithComponents;
    }

    public void onSelected() {
        if (isSelectable()) {
            if (selected) {
                rowUnselectEvent.fire(new LayoutEditorElementUnselectEvent(this));
            } else {
                rowSelectEvent.fire(new LayoutEditorElementSelectEvent(this));
            }
        }
    }

    public List<Column> getChildElements() {
        return columns;
    }

    public interface View extends UberElement<Row> {

        void addColumn(UberElement<ComponentColumn> view);

        void clear();

        void setupPageLayout(Integer height);

        void setHeight(Integer height);

        void setupResize();

        void setSelectEnabled(boolean enabled);

        void setSelected(boolean selected);

        void applyCssValues(List<CssValue> cssValues);
    }

}