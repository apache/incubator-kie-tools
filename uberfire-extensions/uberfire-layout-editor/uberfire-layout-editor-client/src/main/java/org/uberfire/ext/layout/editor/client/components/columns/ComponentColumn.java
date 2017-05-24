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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ComponentColumn implements Column {

    private final View view;
    private String id;
    private String parentId;
    private DnDManager dndManager;
    private Integer columnWidth;
    private Integer columnHeight = DEFAULT_COLUMN_HEIGHT;
    private Integer innerColumnMinimumHeight = 3;
    private ParameterizedCommand<ColumnDrop> dropCommand;
    private boolean innerColumn = false;
    private LayoutComponent layoutComponent;
    private boolean componentReady;
    private ParameterizedCommand<Column> removeCommand;
    private LayoutDragComponentHelper layoutDragComponentHelper;
    private Event<ColumnResizeEvent> columnResizeEvent;
    private boolean canResizeLeft;
    private boolean canResizeRight;
    private LayoutTemplate.Style pageStyle;

    @Inject
    public ComponentColumn(final View view,
                           DnDManager dndManager,
                           LayoutDragComponentHelper layoutDragComponentHelper,
                           Event<ColumnResizeEvent> columnResizeEvent) {
        this.view = view;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.columnResizeEvent = columnResizeEvent;
    }

    @PostConstruct
    public void post() {
        view.init(this);
    }

    public void init(String parentId,
                     Integer columnWidth,
                     LayoutComponent layoutComponent,
                     ParameterizedCommand<ColumnDrop> dropCommand,
                     ParameterizedCommand<Column> removeCommand,
                     boolean newComponent) {
        this.layoutComponent = layoutComponent;
        view.setup(layoutComponent);
        this.parentId = parentId;
        this.columnWidth = columnWidth;
        this.dropCommand = dropCommand;
        this.removeCommand = removeCommand;
        view.setWidth(columnWidth.toString());
        setupPageLayout();
        if (newComponent && hasConfiguration()) {
            configComponent(newComponent);
        } else {
            componentReady = true;
        }
        view.setupWidget();
    }

    private void setupPageLayout() {
        view.setupPageLayout();
    }

    protected boolean hasConfiguration() {
        return view.hasModalConfiguration();
    }

    public void setDropCommand(
            ParameterizedCommand<ColumnDrop> dropCommand) {
        this.dropCommand = dropCommand;
    }

    void configComponent(boolean newComponent) {

        if (hasModalConfiguration(newComponent)) {
            view.showConfigComponentModal(this::configurationFinish,
                                          this::configurationCanceled);
        } else {
            configurationFinish();
        }
    }

    private boolean hasModalConfiguration(boolean newComponent) {
        return newComponent && view.hasModalConfiguration();
    }

    private void configurationFinish() {
        this.componentReady = true;
        updateView();
    }

    private void configurationCanceled() {
        if (!componentReady) {
            remove();
        }
    }

    public void remove() {
        removeCommand.execute(this);
    }

    public void edit() {
        configComponent(true);
    }

    public boolean shouldPreviewDrop() {
        return !dndManager.isOnRowMove() && canISplitMyHeight();
    }

    private boolean canISplitMyHeight() {
        if (isInnerColumn()) {
            return getColumnHeight() > innerColumnMinimumHeight;
        }
        return true;
    }

    public void dragStartComponent() {
        dndManager.dragComponent(layoutComponent,
                                 parentId,
                                 this);
    }

    public void dragEndComponent() {
        dndManager.dragEndComponent();
    }

    @Override
    public LayoutComponent getLayoutComponent() {
        return layoutComponent;
    }

    @Override
    public boolean hasInnerRows() {
        return false;
    }

    @Override
    public void calculateWidth() {
        view.calculateWidth();
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

    @Override
    public void setupResize(boolean canResizeLeft,
                            boolean canResizeRight) {
        this.canResizeLeft = canResizeLeft;
        this.canResizeRight = canResizeRight;
    }

    public boolean canResizeLeft() {
        if (innerColumn) {
            return false;
        }
        return canResizeLeft;
    }

    public void resizeLeft() {
        columnResizeEvent.fire(new ColumnResizeEvent(id,
                                                     parentId).left());
    }

    public boolean canResizeRight() {
        if (innerColumn) {
            return false;
        }
        return canResizeRight;
    }

    public void resizeRight() {
        columnResizeEvent.fire(new ColumnResizeEvent(id,
                                                     parentId).right());
    }

    public void recalculateWidth() {
        view.calculateWidth();
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Integer size) {
        this.columnWidth = size;
        view.setWidth(size.toString());
    }

    public void onDrop(ColumnDrop.Orientation orientation,
                       String dndData) {
        if (dndManager.isOnComponentMove()) {
            moveDrop(orientation);
        } else {
            newComponentDrop(orientation,
                             dndData);
        }
    }

    private void newComponentDrop(ColumnDrop.Orientation orientation,
                                  String dndData) {
        dropCommand.execute(
                new ColumnDrop(layoutDragComponentHelper.getLayoutComponentFromDrop(dndData),
                               id,
                               orientation));
    }

    private void moveDrop(ColumnDrop.Orientation orientation) {
        dndManager.endComponentMove();
        if (!dropInTheSameColumn()) {
            dropCommand.execute(new ColumnDrop(dndManager.getLayoutComponentMove(),
                                               id,
                                               orientation)
                                        .fromMove(
                                                dndManager.getDraggedColumn()));
        }
    }

    private boolean dropInTheSameColumn() {
        return dndManager.getDraggedColumn() == this;
    }

    @Override
    public boolean isInnerColumn() {
        return innerColumn;
    }

    public void setColumnHeight(Integer columnHeight) {
        this.columnHeight = columnHeight;
        this.innerColumn = true;
        view.setColumnHeight(columnHeight);
    }

    public void updateView() {
        if (componentReady) {
            view.clearContent();
            view.setContent();
            view.calculateWidth();
        }
    }

    public UberElement<ComponentColumn> getView() {
        updateView();
        return view;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public LayoutDragComponentHelper getLayoutDragComponentHelper() {
        return layoutDragComponentHelper;
    }

    public boolean enableSideDnD() {
        return !isInnerColumn();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setup(String id,
                      LayoutTemplate.Style pageStyle) {
        this.id = id;
        this.pageStyle = pageStyle;
    }

    public Integer getColumnHeight() {
        return columnHeight;
    }

    public interface View extends UberElement<ComponentColumn> {

        void setWidth(String size);

        void calculateWidth();

        void clearContent();

        void setContent();

        void showConfigComponentModal(Command configurationFinish,
                                      Command configurationCanceled);

        boolean hasModalConfiguration();

        void setup(LayoutComponent layoutComponent);

        void setupWidget();

        void setupPageLayout();

        void setColumnHeight(Integer columnHeight);
    }
}
