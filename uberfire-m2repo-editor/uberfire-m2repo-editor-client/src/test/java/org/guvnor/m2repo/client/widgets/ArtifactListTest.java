/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.m2repo.client.widgets;

import javax.enterprise.event.Event;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactListTest {

    private static final int REQUEST_RANGE_LENGTH = 53;
    private static final int REQUEST_RANGE_START = 19;
    private final boolean REQUEST_SORT_ORDER = true;
    private final String REQUEST_SORT_COLUMN = "C";
    private static final boolean RESPONSE_EXACT_ROWS = true;
    private static final int RESPONSE_ROWS_COUNT = 61;
    private static final String POM_TEXT = "POM text";
    @Mock
    private Event<NotificationEvent> event;
    @Mock
    private M2RepoService m2service;
    @Mock
    private PageResponse<JarListPageRow> response;
    @Mock
    private ArtifactListView view;
    @Mock
    private HasData<JarListPageRow> table;
    @Mock
    private Range range;
    @Mock
    @SuppressWarnings("rawtypes")
    private Column column;
    @Mock
    private ColumnSortList sortList;
    @Mock
    private ColumnSortList.ColumnSortInfo sortInfo;
    @Captor
    private ArgumentCaptor<JarListPageRequest> request;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(m2service.listArtifacts(any(JarListPageRequest.class))).thenReturn(response);
        when(m2service.getPomText(Mockito.anyString())).thenReturn(POM_TEXT);
        when(response.getTotalRowSize()).thenReturn(RESPONSE_ROWS_COUNT);
        when(response.isTotalRowSizeExact()).thenReturn(RESPONSE_EXACT_ROWS);

        when(view.getDisplay()).thenReturn(table);
        when(table.getVisibleRange()).thenReturn(range);
        when(range.getStart()).thenReturn(REQUEST_RANGE_START);
        when(range.getLength()).thenReturn(REQUEST_RANGE_LENGTH);

        when(view.getColumnSortList()).thenReturn(sortList);
        when(sortList.size()).thenReturn(1);
        when(sortList.get(0)).thenReturn(sortInfo);
        when(sortInfo.isAscending()).thenReturn(REQUEST_SORT_ORDER);
        when(sortInfo.getColumn()).thenReturn(column); // unchecked
        when(column.getDataStoreName()).thenReturn(REQUEST_SORT_COLUMN);
    }

    @Test
    public void testSearch() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl(view,
                                                                            new CallerMock<M2RepoService>(m2service),
                                                                            event);
        // Disable sort info for this test
        when(view.getColumnSortList()).thenReturn(null);
        presenter.init();
        ArtifactListPresenterImpl.RefreshableAsyncDataProvider dataProvider = spy(presenter.dataProvider);
        presenter.dataProvider = dataProvider;

        // Search request with filter
        presenter.search("filters");
        verify(event).fire(any(NotificationEvent.class));
        verify(dataProvider).addDataDisplay(Matchers.<HasData<JarListPageRow>>any());
        verify(dataProvider,
               never()).goToFirstPage();
        verify(m2service).listArtifacts(request.capture());
        JarListPageRequest searchRequest = request.getValue();
        verifyRequest(searchRequest,
                      null,
                      "filters",
                      REQUEST_RANGE_LENGTH,
                      REQUEST_RANGE_START,
                      ArtifactListPresenterImpl.DEFAULT_ORDER_ASCENDING);

        // Row data updated
        verify(table).setRowCount(RESPONSE_ROWS_COUNT,
                                  RESPONSE_EXACT_ROWS);

        // Second search does not add the display again
        reset(event);
        reset(dataProvider);
        presenter.search("other filters");
        verify(event).fire(any(NotificationEvent.class));
        verify(dataProvider,
               never()).addDataDisplay(Matchers.<HasData<JarListPageRow>>any());
        verify(dataProvider).goToFirstPage();
    }

    @Test
    public void testNoEvent() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl(view,
                                                                            new CallerMock<M2RepoService>(m2service),
                                                                            event);
        // Disable sort info for this test
        when(view.getColumnSortList()).thenReturn(null);
        presenter.init();
        ArtifactListPresenterImpl.RefreshableAsyncDataProvider dataProvider = spy(presenter.dataProvider);
        presenter.dataProvider = dataProvider;

        // Search request with filter
        presenter.notifyOnRefresh(false);
        presenter.search("filters");
        presenter.refresh();
        verify(event,
               never()).fire(any(NotificationEvent.class));
    }

    @Test
    public void testDefaultColumns() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl(view,
                                                                            new CallerMock<M2RepoService>(m2service),
                                                                            event);
        presenter.init();
        presenter.setup(ColumnType.GAV);
        verify(view).setup(ColumnType.GAV);
        presenter.getView();
        verify(view,
               never()).setup();
    }

    @Test
    public void testColumnSortList() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl(view,
                                                                            new CallerMock<M2RepoService>(m2service),
                                                                            event);
        presenter.init();

        // Change sort parameters and refresh
        when(sortInfo.isAscending()).thenReturn(!REQUEST_SORT_ORDER);
        when(column.getDataStoreName()).thenReturn("X");
        presenter.search("");

        // Verify request
        verify(m2service).listArtifacts(request.capture());
        verifyRequest(request.getValue(),
                      "X",
                      "",
                      REQUEST_RANGE_LENGTH,
                      REQUEST_RANGE_START,
                      !REQUEST_SORT_ORDER);

        // Row data updated
        verify(table).setRowCount(RESPONSE_ROWS_COUNT,
                                  RESPONSE_EXACT_ROWS);
    }

    @Test
    public void testShowPom() {
        ArtifactListPresenterImpl presenter = new ArtifactListPresenterImpl(view,
                                                                            new CallerMock<M2RepoService>(m2service),
                                                                            event);
        presenter.init();
        presenter.onOpenPom("");
        verify(view).showPom(POM_TEXT);
    }

    private static void verifyRequest(final JarListPageRequest request,
                                      final String dataSourceName,
                                      final String filters,
                                      final Integer pageSize,
                                      final int startRowIndex,
                                      final boolean isAscending) {
        assertEquals(dataSourceName,
                     request.getDataSourceName());
        assertEquals(filters,
                     request.getFilters());
        assertEquals(pageSize,
                     request.getPageSize());
        assertEquals(startRowIndex,
                     request.getStartRowIndex());
        assertEquals(isAscending,
                     request.isAscending());
    }
}
