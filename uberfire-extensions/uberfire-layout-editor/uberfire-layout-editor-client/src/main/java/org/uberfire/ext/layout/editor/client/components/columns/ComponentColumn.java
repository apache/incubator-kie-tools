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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementPart;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementType;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.*;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ComponentColumn implements Column {

    private final View view;
    private String id;
    private LayoutEditorElement parentElement;
    private Map<String,String> properties = new HashMap<>();
    private DnDManager dndManager;
    private Integer columnWidth;
    private Integer columnHeight = DEFAULT_COLUMN_HEIGHT;
    private Integer innerColumnMinimumHeight = 3;
    private ParameterizedCommand<ColumnDrop> dropCommand;
    private boolean innerColumn = false;
    private LayoutComponent layoutComponent;
    private Supplier<LayoutTemplate> currentLayoutTemplateSupplier;
    private Supplier<Boolean> lockSupplier;
    private boolean componentReady;
    private ParameterizedCommand<Column> removeCommand;
    private LayoutDragComponentHelper layoutDragComponentHelper;
    private Event<ColumnResizeEvent> columnResizeEvent;
    private boolean canResizeLeft;
    private boolean canResizeRight;
    private LayoutTemplate.Style pageStyle;
    private boolean selected = false;
    private boolean selectable = true;
    private Event<LayoutEditorElementSelectEvent> columnSelectEvent;
    private Event<LayoutEditorElementUnselectEvent> columnUnselectEvent;
    private Event<LockRequiredEvent> lockRequiredEvent;
    private ManagedInstance<ComponentColumnPart> componentColumnManagedInstance;
    private List<LayoutEditorElementPart> parts = new ArrayList<>();

    @Inject
    public ComponentColumn(final View view,
                           DnDManager dndManager,
                           LayoutDragComponentHelper layoutDragComponentHelper,
                           Event<ColumnResizeEvent> columnResizeEvent,
                           Event<LayoutEditorElementSelectEvent> columnSelectEvent,
                           Event<LayoutEditorElementUnselectEvent> columnUnselectEvent,
                           Event<LockRequiredEvent> lockRequiredEvent,
                           ManagedInstance<ComponentColumnPart> componentColumnManagedInstance) {
        this.view = view;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.columnResizeEvent = columnResizeEvent;
        this.columnSelectEvent = columnSelectEvent;
        this.columnUnselectEvent = columnUnselectEvent;
        this.lockRequiredEvent = lockRequiredEvent;
        this.componentColumnManagedInstance = componentColumnManagedInstance;
    }

    @PostConstruct
    public void post() {
        view.init(this);
    }

    public void init(LayoutEditorElement parent,
                     Integer columnWidth,
                     LayoutComponent layoutComponent,
                     ParameterizedCommand<ColumnDrop> dropCommand,
                     ParameterizedCommand<Column> removeCommand,
                     Supplier<LayoutTemplate> currentLayoutTemplateSupplier,
                     Supplier<Boolean> lockSupplier,
                     boolean newComponent) {
        this.layoutComponent = layoutComponent;
        this.currentLayoutTemplateSupplier = currentLayoutTemplateSupplier;
        this.lockSupplier = lockSupplier;
        view.setup(layoutComponent, pageStyle);
        this.parentElement = parent;
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
        setupParts();
    }

    @Override
    public LayoutEditorElementType geElementType() {
        return LayoutEditorElementType.COLUMN;
    }

    @Override
    public LayoutEditorElement getParentElement() {
        return parentElement;
    }

    public void setParentElement(LayoutEditorElement parentElement) {
        this.parentElement = parentElement;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
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

    private void setupPageLayout() {
        view.setupPageLayout();
    }
    
    public void setupParts() {
        parts.clear();
        layoutComponent.getParts().forEach(part -> {
            ComponentColumnPart componentColumnPart = componentColumnManagedInstance.get();
            componentColumnPart.init(this, part);
            parts.add(componentColumnPart);
        });
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperty(String property, String value) {
        properties.put(property, value);
        layoutComponent.getProperties().put(property, value);
        this.updateView();
    }

    @Override
    public void removeProperty(String property) {
        properties.remove(property);
        layoutComponent.getProperties().remove(property);
        this.updateView();
    }

    @Override
    public void clearProperties() {
        properties.clear();
        layoutComponent.getProperties().clear();
        this.updateView();
    }

    @Override
    public List<PropertyEditorCategory> getPropertyCategories() {
        return view.getPropertyCategories();
    }

    protected boolean hasConfiguration() {
        return view.hasModalConfiguration();
    }

    public void setDropCommand(
            ParameterizedCommand<ColumnDrop> dropCommand) {
        this.dropCommand = dropCommand;
    }

    void configComponent(boolean newComponent) {
        if (lockSupplier.get()) {
            return;
        }
        if (hasModalConfiguration(newComponent)) {
            view.showConfigComponentModal(this::configurationFinish,
                                          this::configurationCanceled,
                                          currentLayoutTemplateSupplier);
        } else {
            configurationFinish();
        }
    }

    private boolean hasModalConfiguration(boolean newComponent) {
        return newComponent && view.hasModalConfiguration();
    }

    private void configurationFinish() {
        this.componentReady = true;
        setupParts();
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
                                 parentElement.getId(),
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
        columnResizeEvent.fire(new ColumnResizeEvent(hashCode(),
                                                     parentElement.hashCode()).left());
    }

    public boolean canResizeRight() {
        if (innerColumn) {
            return false;
        }
        return canResizeRight;
    }

    public void resizeRight() {
        columnResizeEvent.fire(new ColumnResizeEvent(hashCode(),
                                                     parentElement.hashCode()).right());
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
        requiredLock();
    }

    public void requiredLock() {
        lockRequiredEvent.fire(new LockRequiredEvent());
    }

    private void newComponentDrop(ColumnDrop.Orientation orientation,
                                  String dndData) {
        dropCommand.execute(
                new ColumnDrop(layoutDragComponentHelper.getLayoutComponentFromDrop(dndData),
                               id,
                               orientation));
    }

    private void moveDrop(ColumnDrop.Orientation orientation) {
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

    public void onSelected() {
        if (isSelectable()) {
            if (selected) {
                selected = false;
                columnUnselectEvent.fire(new LayoutEditorElementUnselectEvent(this));
            } else {
                selected = true;
                columnSelectEvent.fire(new LayoutEditorElementSelectEvent(this));
            }
        }
    }

    public void onDragEnd(@Observes DragComponentEndEvent dragComponentEndEvent) {
        view.notifyDragEnd();
        requiredLock();
    }
    
    @Override
    public List<LayoutEditorElementPart> getLayoutEditorElementParts() {
        return parts;
    }
    
    public LayoutDragComponent getLayoutDragComponent() {
        return view.getLayoutDragComponent();
    }

    public interface View extends UberElement<ComponentColumn> {

        void setWidth(String size);

        LayoutDragComponent getLayoutDragComponent();

        void calculateWidth();

        void clearContent();

        void setContent();

        void showConfigComponentModal(Command configurationFinish,
                                      Command configurationCanceled,
                                      Supplier<LayoutTemplate> createCurrentLayoutTemplateSupplier);

        boolean hasModalConfiguration();

        void setup(LayoutComponent layoutComponent,
                   LayoutTemplate.Style pageStyle);

        void setupWidget();

        void setupPageLayout();

        void setColumnHeight(Integer columnHeight);

        void setSelected(boolean selected);

        void notifyDragEnd();

        List<PropertyEditorCategory> getPropertyCategories();
    }
}
