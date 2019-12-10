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

package org.uberfire.ext.layout.editor.client;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnPart;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.*;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractLayoutEditorTest {

    public static final String SAMPLE_FULL_FLUID_LAYOUT = "org/uberfire/ext/layout/editor/client/sampleFullFluidLayout.txt";
    public static final String SAMPLE_FULL_PAGE_LAYOUT = "org/uberfire/ext/layout/editor/client/sampleFullPageLayout.txt";
    public static final String SINGLE_ROW_COMPONENT_LAYOUT = "org/uberfire/ext/layout/editor/client/singleRowComponentLayout.txt";
    public static final String SINGLE_ROW_COMPONENT_LAYOUT_WITH_PARTS = "org/uberfire/ext/layout/editor/client/singleRowComponentLayoutWithParts.txt";
    public static final String SINGLE_ROW_TWO_COMPONENTS_LAYOUT = "org/uberfire/ext/layout/editor/client/singleRowTwoComponentsLayout.txt";
    public static final String FULL_LAYOUT_FLUID = "org/uberfire/ext/layout/editor/client/fullLayoutFluid.txt";
    public static final String FULL_LAYOUT_PAGE = "org/uberfire/ext/layout/editor/client/fullLayoutPage.txt";
    public static final String SAMPLE_COLUMN_WITH_COMPONENTS_LAYOUT = "org/uberfire/ext/layout/editor/client/columnWithComponentsLayout.txt";
    public static final int EMPTY_ROW = 0;
    public static final int FIRST_ROW = 0;
    public static final int SECOND_ROW = 1;
    public static final int FIRST_COLUMN = 0;
    public static final int SECOND_COLUMN = 1;

    @Mock
    protected Instance<Row> rowInstance;

    @Mock
    protected Instance<EmptyDropRow> emptyDropRowInstance;

    @Mock
    protected Container.View view;

    @Mock
    protected LayoutDragComponentHelper dragHelper;

    @Mock
    protected LayoutEditorCssHelper cssHelper;

    @Mock
    protected EventSourceMock<ComponentDropEvent> componentDropEventMock;
    @Mock
    protected EventSourceMock<LayoutEditorElementSelectEvent> layoutElementSelectEventMock;
    @Mock
    protected EventSourceMock<LayoutEditorElementUnselectEvent> layoutElementUnselectEventMock;
    @Mock
    protected EventSourceMock<ComponentRemovedEvent> componentRemoveEventMock;
    @Mock
    protected EventSourceMock<LayoutEditorElementSelectEvent> rowSelectedEvent;
    @Mock
    protected EventSourceMock<LayoutEditorElementUnselectEvent> rowUnselectedEvent;
    @Mock
    protected EventSourceMock<LayoutEditorElementSelectEvent> columnSelectedEvent;
    @Mock
    protected EventSourceMock<LayoutEditorElementUnselectEvent> columnUnselectedEvent;
    @Mock
    protected EventSourceMock<LockRequiredEvent> lockRequiredEventMock;
    @Mock
    protected LayoutEditorFocusController layoutEditorFocusController;

    protected EmptyDropRow emptyDropRow = new EmptyDropRow(mock(EmptyDropRow.View.class),
            dragHelper);

    @Spy
    protected DnDManager dnDManager;
    protected Container container;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Container createContainer() {
        return new Container(view,
                             cssHelper,
                             rowInstance,
                             emptyDropRowInstance,
                             componentDropEventMock,
                             layoutElementSelectEventMock,
                             layoutElementUnselectEventMock,
                             lockRequiredEventMock,
                             dnDManager,
                             layoutEditorFocusController) {
            private UniqueIDGenerator idGenerator = new UniqueIDGenerator();

            @Override
            protected EmptyDropRow createInstanceEmptyDropRow() {
                emptyDropRow.setId(idGenerator.createRowID("container"));
                return emptyDropRow;
            }

            @Override
            protected Row createInstanceRow() {
                Row row = rowProducer();
                row.setup(null, idGenerator.createRowID("container"),
                          LayoutTemplate.Style.PAGE);
                return row;
            }

            @Override
            protected void destroy(Object o) {
            }
        };
    }

    private Row rowProducer() {
        return new Row(mock(Row.View.class),
                       null,
                       null,
                       dnDManager,
                       dragHelper,
                       cssHelper,
                       componentDropEventMock,
                       componentRemoveEventMock,
                       null,
                       rowSelectedEvent,
                       rowUnselectedEvent,
                       layoutEditorFocusController) {
            private UniqueIDGenerator idGenerator = new UniqueIDGenerator();

            @Override
            protected ComponentColumn createComponentColumnInstance() {

                ManagedInstance managedInstanceMock = mock(ManagedInstance.class);
                when(managedInstanceMock.get()).thenReturn(new ComponentColumnPart());
                ComponentColumn componentColumn = new ComponentColumn(mock(ComponentColumn.View.class),
                                                                      dnDManager,
                                                                      dragHelper,
                                                                      mock(Event.class),
                                                                      columnSelectedEvent,
                                                                      columnUnselectedEvent,
                                                                      mock(Event.class),
                                                                      managedInstanceMock) {
                    @Override
                    protected boolean hasConfiguration() {
                        return false;
                    }
                };
                componentColumn.setId(idGenerator.createColumnID(getId()));
                return componentColumn;
            }

            @Override
            protected ColumnWithComponents createColumnWithComponentsInstance() {
                ColumnWithComponents columnWithComponents = new ColumnWithComponents(
                        mock(ColumnWithComponents.View.class),
                        null,
                        dnDManager,
                        dragHelper,
                        mock(Event.class),
                        mock(Event.class)) {
                    @Override
                    protected Row createInstanceRow() {
                        return rowProducer();
                    }

                    @Override
                    protected void destroy(Object o) {
                    }
                };
                columnWithComponents.setId(idGenerator.createColumnID(getId()));
                return columnWithComponents;
            }

            @Override
            protected void destroy(Object o) {
            }
        };
    }

    public LayoutTemplate getLayoutFromFileTemplate(String templateURL) throws Exception {
        URL resource = getClass().getClassLoader()
                .getResource(templateURL);
        String layoutEditorModel = new String(Files.readAllBytes(Paths.get(resource.toURI())));

        LayoutTemplate layoutTemplate = gson.fromJson(layoutEditorModel,
                                                      LayoutTemplate.class);

        return layoutTemplate;
    }

    public String convertLayoutToString(LayoutTemplate layoutTemplate) {
        String layoutContent = gson.toJson(layoutTemplate);
        return layoutContent;
    }

    protected int getRowsSizeFromContainer() {
        return container.getRows().size();
    }

    protected List<Column> getColumns(Row row) {
        return row.getColumns();
    }

    protected Column getColumnByIndex(Row row,
                                      int index) {
        return row.getColumns().get(index);
    }

    protected Row getRowByIndex(int index) {
        return container.getRows().get(index);
    }

    @Before
    public void setup() {
        container = createContainer();
        container.setup();
        when(dragHelper.getLayoutComponentFromDrop(any())).thenReturn(new LayoutComponent());
    }

    protected LayoutTemplate loadLayout(String singleRowComponentLayout) throws Exception {
        LayoutTemplate layoutTemplate = getLayoutFromFileTemplate(singleRowComponentLayout);
        container.load(layoutTemplate,
                       "title",
                       "subtitle");
        return layoutTemplate;
    }

    protected List<Column> extractColumnsFrom(ColumnWithComponents columnWithComponents) {
        return columnWithComponents.getRow().getColumns();
    }

//    public static class ComponentDropEventMock extends EventSourceMock<ComponentDropEvent> {
//
//    }
//
//    public static class ComponentRemovedEventMock extends EventSourceMock<ComponentRemovedEvent> {
//
//    }
}
