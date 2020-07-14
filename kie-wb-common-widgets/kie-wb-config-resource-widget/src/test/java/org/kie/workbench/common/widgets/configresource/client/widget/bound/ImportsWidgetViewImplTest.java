/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.mockito.Mock;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportsWidgetViewImplTest {

    @Mock
    private AddImportPopup addImportPopup;

    @Mock
    private ImportsWidgetView.Presenter presenter;

    @Mock
    private EventSourceMock<LockRequiredEvent> lockRequired;

    private List<Import> internalFactTypes = new ArrayList<>();
    private List<Import> externalFactTypes = new ArrayList<>();
    private List<Import> modelFactTypes = new ArrayList<>();

    private Import internal1 = new Import("zInternal");
    private Import internal2 = new Import("yInternal");
    private Import model1 = new Import("zModel");
    private Import model2 = new Import("yModel");

    private ImportsWidgetViewImpl view;

    @Before
    public void setup() {
        this.view = new ImportsWidgetViewImpl(addImportPopup,
                                              lockRequired);
        view.table = mock(CellTable.class);

        this.view.init(presenter);

        internalFactTypes.add(internal1);
        internalFactTypes.add(internal2);
        modelFactTypes.add(model1);
        modelFactTypes.add(model2);

        when(presenter.isInternalImport(eq(internal1))).thenReturn(true);
        when(presenter.isInternalImport(eq(internal2))).thenReturn(true);

        view.setContent(internalFactTypes,
                        externalFactTypes,
                        modelFactTypes,
                        false);
    }

    @Test
    public void setContentSortsImportsByInternalThenExternalAlphabetically() {
        final List<Import> imports = view.getDataProvider().getList();
        assertEquals(4,
                     imports.size());

        assertEquals(internal2,
                     imports.get(0));
        assertEquals(internal1,
                     imports.get(1));
        assertEquals(model2,
                     imports.get(2));
        assertEquals(model1,
                     imports.get(3));
    }

    @Test
    public void checkAddImportSortsAlphabetically() {
        final Import newImport = new Import("new1");
        when(addImportPopup.getImportType()).thenReturn(newImport.getType());
        when(presenter.isInternalImport(eq(newImport))).thenReturn(false);

        view.makeAddImportCommand().execute();

        verify(lockRequired).fire(any(LockRequiredEvent.class));

        final List<Import> imports = view.getDataProvider().getList();
        assertEquals(5,
                     imports.size());

        assertEquals(internal2,
                     imports.get(0));
        assertEquals(internal1,
                     imports.get(1));
        assertEquals(newImport,
                     imports.get(2));
        assertEquals(model2,
                     imports.get(3));
        assertEquals(model1,
                     imports.get(4));
    }

    @Test
    public void checkRemoveImportSortsAlphabetically() {
        view.makeRemoveImportCommand().execute(internal1);

        verify(lockRequired).fire(any(LockRequiredEvent.class));

        final List<Import> imports = view.getDataProvider().getList();
        assertEquals(3,
                     imports.size());

        assertEquals(internal2,
                     imports.get(0));
        assertEquals(model2,
                     imports.get(1));
        assertEquals(model1,
                     imports.get(2));
    }

    @Test
    public void checkAddPopupInitialisation() {
        view.onClickAddImportButton(mock(ClickEvent.class));

        verify(addImportPopup).setContent(eq(view.getAddImportCommand()),
                                          eq(externalFactTypes));
        verify(addImportPopup).show();
    }

    @Test
    public void testUpdateRenderedColumnsUpdateNotNeeded() {
        when(view.table.getColumnCount()).thenReturn(2);
        view.setContent(Collections.singletonList(new Import("com.demo.Address")),
                        Collections.singletonList(new Import("com.demo.Address")),
                        Collections.emptyList(),
                        false);

        view.updateRenderedColumns();

        verify(view.table, never()).removeColumn(any(Column.class));
        verify(view.table, never()).addColumn(any(Column.class),
                                              anyString());
    }

    @Test
    public void testUpdateRenderedColumnsRemoveNotNeededBecauseBuiltIn() {
        when(view.table.getColumnCount()).thenReturn(2);
        view.setContent(Collections.singletonList(new Import("java.lang.Number")),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false);

        view.updateRenderedColumns();

        verify(view.table).removeColumn(any(Column.class));
        verify(view.table, never()).addColumn(any(Column.class),
                                              anyString());
    }

    @Test
    public void testUpdateRenderedColumnsRemoveColumnNeeded() {
        when(view.table.getColumnCount()).thenReturn(1);
        view.setContent(Collections.singletonList(new Import("com.demo.Address")),
                        Collections.singletonList(new Import("com.demo.Address")),
                        Collections.emptyList(),
                        false);

        view.updateRenderedColumns();

        verify(view.table, never()).removeColumn(any(Column.class));
        verify(view.table).addColumn(any(Column.class),
                                     eq("remove"));
    }

    @Test
    public void testUpdateRenderedColumnsRemoveColumnNotNeeded() {
        when(view.table.getColumnCount()).thenReturn(2);
        view.setContent(Collections.singletonList(internal1),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false);

        view.updateRenderedColumns();

        verify(view.table).removeColumn(any(Column.class));
        verify(view.table, never()).addColumn(any(Column.class),
                                              anyString());
    }

    @Test
    public void testAddImportButtonEnabled() {
        assertAddImportButtonWhenViewIsReadonly(false);
    }

    @Test
    public void testAddImportButtonDisabled() {
        assertAddImportButtonWhenViewIsReadonly(true);
    }

    private void assertAddImportButtonWhenViewIsReadonly(final boolean viewIsReadonly) {
        reset(view.addImportButton);

        view.setContent(Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        viewIsReadonly);

        verify(view.addImportButton).setEnabled(!viewIsReadonly);
    }
}
