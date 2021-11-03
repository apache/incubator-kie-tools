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

package org.uberfire.ext.layout.editor.client.components.columns;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementType;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.infra.BeanHelper;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.ext.layout.editor.client.infra.UniqueIDGenerator;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ColumnWithComponents implements Column {

    private final View view;
    private String id;
    private LayoutEditorElement parentElement;
    private Map<String,String> properties = new HashMap<>();
    private ParameterizedCommand<ColumnDrop> dropCommand;
    private ParameterizedCommand<ColumnDrop> removeComponentCommand;
    private ParameterizedCommand<Column> removeColumnCommand;
    private Row row;
    private UniqueIDGenerator idGenerator = new UniqueIDGenerator();
    private Instance<Row> rowInstance;
    private DnDManager dndManager;
    private LayoutDragComponentHelper layoutDragComponentHelper;
    private boolean canResizeLeft;
    private boolean canResizeRight;
    private Event<ColumnResizeEvent> columnResizeEvent;
    private Event<LockRequiredEvent> lockRequiredEvent;
    private LayoutTemplate.Style pageStyle;
    private Supplier<LayoutTemplate> currentLayoutTemplateSupplier;
    private Supplier<Boolean> lockSupplier;
    private Integer columnHeight = DEFAULT_COLUMN_HEIGHT;
    private Integer columnWidth;
    private boolean selected = false;
    private boolean selectable = true;

    @Inject
    public ColumnWithComponents(final View view,
                                Instance<Row> rowInstance,
                                DnDManager dndManager,
                                LayoutDragComponentHelper layoutDragComponentHelper,
                                Event<ColumnResizeEvent> columnResizeEvent,
                                Event<LockRequiredEvent> lockRequiredEvent) {
        this.view = view;
        this.rowInstance = rowInstance;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.columnResizeEvent = columnResizeEvent;
        this.lockRequiredEvent = lockRequiredEvent;
    }

    @PostConstruct
    public void post() {
        view.init(this);
    }

    @PreDestroy
    public void preDestroy() {
        destroy(row);
    }

    public void init(LayoutEditorElement parent,
                     Integer columnWidth,
                     LayoutTemplate.Style pageStyle,
                     ParameterizedCommand<ColumnDrop> dropCommand,
                     ParameterizedCommand<ColumnDrop> removeComponentCommand,
                     ParameterizedCommand<Column> removeCommand,
                     Supplier<LayoutTemplate> currentLayoutTemplateSupplier,
                     Supplier<Boolean> lockSupplier,
                     Integer columnHeight) {
        this.columnWidth = columnWidth;
        this.parentElement = parent;
        this.dropCommand = dropCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.removeColumnCommand = removeCommand;
        this.pageStyle = pageStyle;
        this.currentLayoutTemplateSupplier = currentLayoutTemplateSupplier;
        this.lockSupplier = lockSupplier;
        this.columnHeight = columnHeight;
        view.setWidth(columnWidth);
        setupPageLayout();
        row = createInstanceRow();
        row.disableDrop();
        row.setup(this, idGenerator.createRowID(id),
                  pageStyle);
        row.init(createDropCommand(),
                 createRowRemoveCommand(),
                 createComponentRemoveCommand(),
                 this,
                 currentLayoutTemplateSupplier,
                 lockSupplier,
                 Row.ROW_DEFAULT_HEIGHT);
    }

    @Override
    public LayoutEditorElementType geElementType() {
        return LayoutEditorElementType.COLUMN_WITH_COMPONENTS;
    }

    @Override
    public LayoutEditorElement getParentElement() {
        return parentElement;
    }

    @Override
    public List<? extends LayoutEditorElement> getChildElements() {
        return Collections.singletonList(row);
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean status) {
        selected = status;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperty(String property, String value) {
        properties.put(property, value);
    }

    @Override
    public void removeProperty(String property) {
        properties.remove(property);
    }

    @Override
    public void clearProperties() {
        properties.clear();
    }

    @Override
    public List<PropertyEditorCategory> getPropertyCategories() {
        return null;
    }

    private void setupPageLayout() {
        if (pageStyle == LayoutTemplate.Style.PAGE) {
            view.setupPageLayout();
        }
    }

    public void onDrop(ColumnDrop.Orientation orientation,
                       String dndData) {
        if (dndManager.isOnComponentMove()) {
            dropCommand.execute(new ColumnDrop(dndManager.getLayoutComponentMove(),
                                               id,
                                               orientation)
                                        .fromMove(dndManager.getDraggedColumn()));
        } else {
            dropCommand.execute(
                    new ColumnDrop(layoutDragComponentHelper.getLayoutComponentFromDrop(dndData),
                                   id,
                                   orientation));
        }
        lockRequiredEvent.fire(new LockRequiredEvent());
    }

    public boolean hasComponent(Column targetColumn) {
        return row.hasComponent(targetColumn) != null;
    }

    public void remove(Column targetColumn) {
        row.removeColumn(targetColumn);
    }

    public boolean canResizeLeft() {
        return canResizeLeft;
    }

    public void resizeLeft() {
        columnResizeEvent.fire(new ColumnResizeEvent(hashCode(),
                                                     parentElement.hashCode()).left());
    }

    public boolean canResizeRight() {
        return canResizeRight;
    }

    public void resizeRight() {
        columnResizeEvent.fire(new ColumnResizeEvent(hashCode(),
                                                     parentElement.hashCode()).right());
    }

    protected Row createInstanceRow() {
        Row row = rowInstance.get();
        row.setSelectable(selectable);
        row.setup(this, idGenerator.createRowID(id),
                  pageStyle);
        return row;
    }

    private ParameterizedCommand<ColumnDrop> createComponentRemoveCommand() {
        return drop -> removeComponentCommand.execute(drop);
    }

    private ParameterizedCommand<Row> createRowRemoveCommand() {
        return row -> removeColumnCommand.execute(this);
    }

    public void withComponents(ComponentColumn... _columns) {
        row.addColumns(_columns);
    }

    ParameterizedCommand<RowDrop> createDropCommand() {
        return rowDrop -> {
        };
    }

    public void setColumnHeight(Integer columnHeight) {
        this.columnHeight = columnHeight;
    }

    @Override
    public UberElement<ColumnWithComponents> getView() {
        view.clear();
        if (hasInnerRows()) {
            view.addRow(row.getView());
        }
        view.calculateWidth();
        return view;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
        view.setWidth(columnWidth);
    }

    @Override
    public void reduceWidth() {
        final int newSize = this.columnWidth - 1;
        setColumnWidth(newSize);
    }

    @Override
    public void incrementWidth() {
        final int newSize = this.columnWidth + 1;
        setColumnWidth(newSize);
    }

    public ParameterizedCommand<Column> getRemoveColumnCommand() {
        return removeColumnCommand;
    }

    @Override
    public LayoutComponent getLayoutComponent() {
        return null;
    }

    @Override
    public boolean hasInnerRows() {
        return row != null;
    }

    @Override
    public void calculateWidth() {
        view.calculateWidth();
    }

    public Row getRow() {
        return row;
    }

    protected void destroy(Object o) {
        BeanHelper.destroy(o);
    }

    public void calculateSizeChilds() {
        row.calculateSizeChilds();
    }

    @Override
    public void setupResize(boolean canResizeLeft,
                            boolean canResizeRight) {
        this.canResizeLeft = canResizeLeft;
        this.canResizeRight = canResizeRight;
    }

    @Override
    public String getId() {
        return id;
    }

    public Integer getColumnHeight() {
        return columnHeight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean shouldPreviewDrop() {
        return !dndManager.isOnRowMove();
    }

    public interface View extends UberElement<ColumnWithComponents> {

        void setWidth(Integer size);

        void addRow(UberElement<Row> view);

        void calculateWidth();

        void clear();

        void setupPageLayout();
    }
}
