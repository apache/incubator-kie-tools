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

package org.uberfire.ext.layout.editor.client.components.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.css.CssValue;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropType;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementType;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.*;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class Container implements LayoutEditorElement {

    private final Instance<Row> rowInstance;
    private final Instance<EmptyDropRow> emptyDropRowInstance;
    private final View view;
    private LayoutTemplate layoutTemplate;
    private String id;
    private UniqueIDGenerator idGenerator = new UniqueIDGenerator();
    private LayoutEditorCssHelper layoutCssHelper;
    private List<Row> rows = new ArrayList<>();
    private EmptyDropRow emptyDropRow;
    private String layoutName;
    private String emptyTitleText;
    private String emptySubTitleText;
    private Map<String, String> properties = new HashMap<>();
    private Event<ComponentDropEvent> componentDropEvent;
    private LayoutTemplate.Style pageStyle = LayoutTemplate.Style.FLUID;
    private Event<LayoutEditorElementSelectEvent> containerSelectEvent;
    private Event<LayoutEditorElementUnselectEvent> containerUnselectEvent;
    private Event<LockRequiredEvent> lockRequiredEvent;
    private DnDManager dndManager;
    private boolean selectable = false;
    private boolean selected = false;
    private Supplier<Boolean> lockSupplier = () -> false;

    LayoutEditorFocusController layoutEditorFocusController;

    @Inject
    public Container(final View view,
                     LayoutEditorCssHelper layoutCssHelper,
                     Instance<Row> rowInstance,
                     Instance<EmptyDropRow> emptyDropRowInstance,
                     Event<ComponentDropEvent> componentDropEvent,
                     Event<LayoutEditorElementSelectEvent> containerSelectEvent,
                     Event<LayoutEditorElementUnselectEvent> containerUnselectEvent,
                     Event<LockRequiredEvent> lockRequiredEvent,
                     DnDManager dndManager,
                     LayoutEditorFocusController layoutEditorFocusController) {
        this.layoutCssHelper = layoutCssHelper;
        this.rowInstance = rowInstance;
        this.emptyDropRowInstance = emptyDropRowInstance;
        this.view = view;
        this.componentDropEvent = componentDropEvent;
        this.containerSelectEvent = containerSelectEvent;
        this.containerUnselectEvent = containerUnselectEvent;
        this.lockRequiredEvent = lockRequiredEvent;
        this.dndManager = dndManager;
        this.layoutEditorFocusController = layoutEditorFocusController;
        this.id = idGenerator.createContainerID();
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        init();
    }

    @PreDestroy
    public void preDestroy() {
        for (Row row : rows) {
            destroy(row);
        }
        destroy(emptyDropRow);
    }

    private void init() {
        view.clear();
        for (Row row : rows) {
            destroy(row);
        }
        rows = new ArrayList<>();
    }

    public void setLockSupplier(Supplier<Boolean> lockSupplier) {
        this.lockSupplier = lockSupplier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LayoutEditorElementType geElementType() {
        return LayoutEditorElementType.CONTAINER;
    }

    @Override
    public LayoutEditorElement getParentElement() {
        return null;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        view.setSelectEnabled(selectable);
    }

    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean status) {
        if (isSelectable()) {
            if (status) {
                selected = true;
                view.setSelected(true);
            } else {
                selected = false;
                view.setSelected(false);
            }
        }
    }

    private void createEmptyDropRow() {
        emptyDropRow = createEmptyRow();
        view.addEmptyRow(emptyDropRow.getView());
    }

    public void loadEmptyLayout(String layoutName,
                                LayoutTemplate.Style pageStyle,
                                String emptyTitleText,
                                String emptySubTitleText) {
        this.layoutName = layoutName;
        this.pageStyle = pageStyle;
        this.emptyTitleText = emptyTitleText;
        this.emptySubTitleText = emptySubTitleText;
        clearView();
        createEmptyDropRow();
        setupResizeRows();
    }

    public void load(LayoutTemplate layoutTemplate,
                     String emptyTitleText,
                     String emptySubTitleText) {
        this.layoutTemplate = layoutTemplate;
        this.layoutName = layoutTemplate.getName();
        this.pageStyle = layoutTemplate.getStyle();
        this.emptyTitleText = emptyTitleText;
        this.emptySubTitleText = emptySubTitleText;
        this.rows.forEach(this::destroy);
        this.rows.clear();
        if (!layoutTemplate.isEmpty()) {
            this.properties = layoutTemplate.getLayoutProperties();
            for (LayoutRow layoutRow : layoutTemplate.getRows()) {
                rows.add(load(layoutRow));
            }
            updateView();
        } else {
            clearView();
            createEmptyDropRow();
        }
        setupResizeRows();
        setupCssProperties();
        layoutEditorFocusController.setTargetContainerView(view);
    }

    public void reset() {
        init();
        layoutTemplate = null;
        emptyTitleText = null;
        emptySubTitleText = null;
        layoutName = null;
        properties.clear();;
        emptyDropRow = null;
        pageStyle = LayoutTemplate.Style.FLUID;
    }

    private EmptyDropRow createEmptyRow() {
        emptyDropRow = createInstanceEmptyDropRow();
        emptyDropRow.init(createEmptyDropCommand(),
                          emptyTitleText,
                          emptySubTitleText);
        return emptyDropRow;
    }

    protected EmptyDropRow createInstanceEmptyDropRow() {
        EmptyDropRow emptyDropRow = emptyDropRowInstance.get();
        emptyDropRow.setId(idGenerator.createRowID(id));
        return emptyDropRow;
    }

    public ParameterizedCommand<RowDrop> createEmptyDropCommand() {
        return (drop) -> {
            destroy(emptyDropRow);
            notifyDrop(drop);
            rows.add(createRow(drop,
                               Row.ROW_DEFAULT_HEIGHT));
            updateView();
        };
    }

    private void notifyDrop(RowDrop rowDrop) {
        componentDropEvent.fire(new ComponentDropEvent(rowDrop.getComponent(),
                                                       rowDrop.getType().equals(ComponentDropType.FROM_MOVE)));
    }

    private Row createRow(RowDrop drop,
                          Integer height) {
        final Row row = createInstanceRow();
        row.init(createRowDropCommand(),
                 createRemoveRowCommand(),
                 createRemoveComponentCommand(),
                 createCurrentLayoutTemplateSupplier(),
                 getLockSupplier(),
                 height);
        row.withOneColumn(drop.getComponent(),
                          drop.newComponent());
        view.addRow(row.getView());
        return row;
    }

    public Supplier<Boolean> getLockSupplier() {
        return () -> lockSupplier.get();
    }

    Supplier<LayoutTemplate> createCurrentLayoutTemplateSupplier() {
        return () -> toLayoutTemplate();
    }

    private ParameterizedCommand<Row> createRemoveRowCommand() {
        return (row) -> {
            removeRow(row);
        };
    }

    private void removeRow(Row row) {
        if (needToUpdateSizeOfMySiblings(row)) {
            updateHeightOfSiblingRow(row);
        }
        this.rows.remove(row);
        destroy(row);
        if (layoutIsEmpty()) {
            init();
            createEmptyDropRow();
        } else {
            updateView();
        }
    }

    private void updateHeightOfSiblingRow(Row rowToRemove) {
        final int removeIndex = getRowIndex(rowToRemove);
        if (firstRow(removeIndex)) {
            if (hasDownSibling(removeIndex,
                               rows)) {
                final Row sibling = rows.get(removeIndex + 1);
                Integer newSize = (sibling.getHeight() + rowToRemove.getHeight());
                sibling.setHeight(newSize);
            }
        } else {
            final Row sibling = rows.get(removeIndex - 1);
            Integer newSize = sibling.getHeight() + rowToRemove.getHeight();
            sibling.setHeight(newSize);
        }
    }

    private boolean needToUpdateSizeOfMySiblings(Row row) {
        return !row.getHeight().equals(Row.ROW_DEFAULT_HEIGHT);
    }

    private ParameterizedCommand<ColumnDrop> createRemoveComponentCommand() {
        return drop -> removeOldComponent(drop.getOldColumn());
    }

    private boolean layoutIsEmpty() {
        return rows.isEmpty();
    }

    public ParameterizedCommand<RowDrop> createRowDropCommand() {
        return (dropRow) -> {
            List<Row> updatedRows = new ArrayList<>();
            for (Row row : rows) {
                handleDrop(dropRow,
                           updatedRows,
                           row);
            }
            rows = updatedRows;
            getView();
            lockRequiredEvent.fire(new LockRequiredEvent());
        };
    }

    private void handleDrop(RowDrop dropRow,
                            List<Row> updatedRows,
                            Row row) {
        if (dropIsInthisRow(row,
                            dropRow)) {
            if (dropRow.newComponent()) {
                addNewRow(row,
                          dropRow,
                          updatedRows);
            } else {
                handleMoveComponent(dropRow,
                                    updatedRows,
                                    row);
            }
        } else {
            updatedRows.add(row);
        }
    }

    private void handleMoveComponent(RowDrop dropRow,
                                     List<Row> updatedRows,
                                     Row row) {
        removeOldComponent(dropRow.getOldColumn());
        addNewRow(row,
                  dropRow,
                  updatedRows);
        // notifying dndManager that the move has finished!
        dndManager.endComponentMove();
    }

    private void removeOldComponent(Column column) {

        // Search the row that contains the column
        Optional<Row> rowOptional = rows.stream()
                .filter(row -> row.cointainsColumn(column))
                .findAny();

        // If the row is present remove it!
        if (rowOptional.isPresent()) {
            rowOptional.get().removeChildColumn(column);
        }
    }

    private void addNewRow(Row currentRow,
                           RowDrop dropRow,
                           List<Row> newRows) {
        Integer newRowHeight;
        if (pageStyle == LayoutTemplate.Style.PAGE) {
            newRowHeight = currentRow.getHeight() / 2;
        } else {
            newRowHeight = currentRow.getHeight();
        }
        if (newRowIsBeforeThisRow(dropRow)) {
            newRows.add(createRow(dropRow,
                                  newRowHeight));
            if (pageStyle == LayoutTemplate.Style.PAGE) {
                setupRowSize(currentRow);
            }
            if (!currentRow.rowIsEmpty()) {
                newRows.add(currentRow);
            }
        } else {
            if (!currentRow.rowIsEmpty()) {
                newRows.add(currentRow);
            }
            newRows.add(createRow(dropRow,
                                  newRowHeight));
            if (pageStyle == LayoutTemplate.Style.PAGE) {
                setupRowSize(currentRow);
            }
        }
        notifyDrop(dropRow);
    }

    private void setupRowSize(Row currentRow) {
        Integer originalSize = currentRow.getHeight();
        Integer newColumnSize = originalSize / 2;
        if (originalSize % 2 == 0) {
            currentRow.setHeight(newColumnSize);
        } else {
            newColumnSize = newColumnSize + 1;
            currentRow.setHeight(newColumnSize);
        }
    }

    private boolean newRowIsBeforeThisRow(RowDrop dropRow) {
        return dropRow.getOrientation() == RowDrop.Orientation.BEFORE;
    }

    private boolean dropIsInthisRow(Row row,
                                    RowDrop dropRow) {
        return dropRow.getRowId() == row.getId();
    }

    private void clearView() {
        view.clear();
    }

    protected void swapRows(@Observes RowDnDEvent rowDndEvent) {
        List<Row> newRows = new ArrayList<>();
        Row beginRow = lookForBeginningRow(rowDndEvent);

        if (beginRow != null) {
            for (Row row : rows) {
                if (row.getId() == rowDndEvent.getRowIdEnd()) {
                    if (rowDndEvent.getOrientation() == RowDrop.Orientation.AFTER) {
                        newRows.add(row);
                        newRows.add(beginRow);
                    } else {
                        newRows.add(beginRow);
                        newRows.add(row);
                    }
                } else {
                    if (row.getId() != beginRow.getId()) {
                        newRows.add(row);
                    }
                }
            }
            this.rows = newRows;
        }

        updateView();
    }

    private Row lookForBeginningRow(@Observes RowDnDEvent rowDndEvent) {
        Row beginRow = null;

        for (Row row : rows) {
            if (row.getId() == rowDndEvent.getRowIdBegin()) {
                beginRow = row;
            }
        }
        return beginRow;
    }

    public String getLayoutName() {
        return layoutName;
    }

    private Row load(LayoutRow layoutRow) {
        final Row row = createInstanceRow();
        row.load(createRowDropCommand(),
                 layoutRow,
                 createRemoveRowCommand(),
                 createRemoveComponentCommand(),
                 createCurrentLayoutTemplateSupplier(),
                 getLockSupplier());
        return row;
    }

    protected Row createInstanceRow() {
        Row row = rowInstance.get();
        row.setSelectable(selectable);
        row.setup(this, idGenerator.createRowID(id),
                  pageStyle);
        return row;
    }

    public void addProperty(String key,
                            String value) {
        properties.put(key,
                       value);
    }

    public String getProperty(String key) {
        return properties.get(key);
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
        return layoutCssHelper.getContainerPropertyCategories(this);
    }

    public LayoutTemplate toLayoutTemplate() {
        LayoutTemplate convert = LayoutTemplateAdapter.convert(this);
        return convert;
    }

    public List<Row> getRows() {
        return rows;
    }

    void updateView() {
        layoutEditorFocusController.recordFocus();
        cleanupEmptyRows();
        setupPageStyle();
        setupResizeRows();
        if (!rows.isEmpty()) {
            clearView();
            for (Row row : rows) {
                view.addRow(row.getView());
            }
        }
    }

    private void cleanupEmptyRows() {
        List<Row> rowsToRemove = new ArrayList<>();
        for (Row row : rows) {
            if (row.rowIsEmpty()) {
                rowsToRemove.add(row);
            }
        }
        for (Row row : rowsToRemove) {
            removeRow(row);
        }
    }

    private void setupResizeRows() {
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            setupRowResizeActions(rows,
                                  row,
                                  i);
        }
    }

    private void setupRowResizeActions(List<Row> rows,
                                       Row row,
                                       int index) {
        if (pageStyle == LayoutTemplate.Style.FLUID) {
            row.setupResize(false,
                            false);
        } else {
            if (firstRow(index)) {
                boolean canResizeDown = canResizeDown(index,
                                                      rows);
                row.setupResize(false,
                                canResizeDown);
            } else {
                row.setupResize(canResizeUp(index,
                                            rows),
                                canResizeDown(index,
                                              rows));
            }
        }
    }

    private boolean canResizeDown(int index,
                                  List<Row> rows) {
        if (hasDownSibling(index,
                           rows)) {
            Row downSibling = rows.get(index + 1);
            return downSibling.getHeight() > 1;
        }
        return false;
    }

    private boolean hasDownSibling(int index,
                                   List<Row> rows) {
        return rows.size() > index + 1;
    }

    private boolean canResizeUp(int index,
                                List<Row> rows) {
        return (rows.get(index - 1).getHeight() > 1);
    }

    private boolean firstRow(int index) {
        return index == 0;
    }

    private void setupPageStyle() {
        if (pageStyle == LayoutTemplate.Style.PAGE) {
            view.pageMode();
        }
    }

    private void setupCssProperties() {
        List<CssValue> cssValueList = layoutCssHelper.readCssValues(properties);
        view.applyCssValues(cssValueList);
    }

    public View getView() {
        updateView();
        return view;
    }

    public void resizeRows(@Observes RowResizeEvent resize) {
        if (resizeEventIsinThisContainer(resize)) {

            Row resizedRow = getRow(resize);
            if (resizedRow != null) {
                Row affectedRow = null;
                if (resize.isUP()) {
                    affectedRow = lookUpForUpperNeighbor(resizedRow);
                } else {
                    affectedRow = lookUpForBottomNeighbor(resizedRow);
                }
                if (affectedRow != null) {
                    resizedRow.incrementHeight();
                    affectedRow.reduceHeight();
                }
            }
            setupResizeRows();
        }
    }

    private boolean resizeEventIsinThisContainer(@Observes RowResizeEvent resize) {
        return resize.getContainerHash() == hashCode();
    }

    private Row lookUpForUpperNeighbor(Row resizedRow) {
        return rows
                .get(getRowIndex(resizedRow) - 1);
    }

    private Row lookUpForBottomNeighbor(Row resizedRow) {
        return rows
                .get(getRowIndex(resizedRow) + 1);
    }

    private int getRowIndex(Row row) {
        return rows.indexOf(row);
    }

    private Row getRow(RowResizeEvent resize) {
        for (Row row : getRows()) {
            if (resize.getRowHash() == row.hashCode()) {
                return row;
            }
        }
        return null;
    }

    EmptyDropRow getEmptyDropRow() {
        return emptyDropRow;
    }

    protected void destroy(Object o) {
        BeanHelper.destroy(o);
    }

    public LayoutTemplate.Style getPageStyle() {
        return pageStyle;
    }

    public void onSelected() {
        if (isSelectable()) {
            if (selected) {
                containerUnselectEvent.fire(new LayoutEditorElementUnselectEvent(this));
            } else {
                containerSelectEvent.fire(new LayoutEditorElementSelectEvent(this));
            }
        }
    }
    
    public List<Row> getChildElements() {
        return rows;
    }

    public interface View extends UberElement<Container> {

        void addRow(UberElement<Row> view);

        void clear();

        void addEmptyRow(UberElement<EmptyDropRow> emptyDropRow);

        void pageMode();

        void setSelectEnabled(boolean enabled);

        void setSelected(boolean selected);

        void applyCssValues(List<CssValue> cssValues);
    }

}
