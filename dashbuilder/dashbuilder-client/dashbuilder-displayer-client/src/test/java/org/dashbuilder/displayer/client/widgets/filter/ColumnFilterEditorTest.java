/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets.filter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.displayer.client.events.ColumnFilterChangedEvent;
import org.dashbuilder.displayer.client.events.ColumnFilterDeletedEvent;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumnFilterEditorTest {

    @Mock
    ColumnFilterEditor.View view;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    SyncBeanDef<TextParameterEditor> textParameterBeanDef;

    @Mock
    SyncBeanDef<NumberParameterEditor> numberParameterBeanDef;

    @Mock
    SyncBeanDef<DateParameterEditor> dateParameterBeanDef;

    @Mock
    SyncBeanDef<TimeFrameEditor> timeFrameBeanDef;

    @Mock
    SyncBeanDef<LikeToFunctionEditor> likeToFunctionBeanDef;

    @Mock
    TextParameterEditor textParameterEditor;

    @Mock
    NumberParameterEditor numberParameterEditor;

    @Mock
    DateParameterEditor dateParameterEditor;

    @Mock
    TimeFrameEditor timeFrameEditor;

    @Mock
    LikeToFunctionEditor likeToFunctionEditor;

    @Mock
    DataSetMetadata metadata;

    @Mock
    EventSourceMock<ColumnFilterChangedEvent> changedEvent;

    @Mock
    EventSourceMock<ColumnFilterDeletedEvent> deletedEvent;

    @Before
    public void init() {
        when(beanManager.lookupBean(TextParameterEditor.class)).thenReturn(textParameterBeanDef);
        when(beanManager.lookupBean(NumberParameterEditor.class)).thenReturn(numberParameterBeanDef);
        when(beanManager.lookupBean(DateParameterEditor.class)).thenReturn(dateParameterBeanDef);
        when(beanManager.lookupBean(TimeFrameEditor.class)).thenReturn(timeFrameBeanDef);
        when(beanManager.lookupBean(LikeToFunctionEditor.class)).thenReturn(likeToFunctionBeanDef);

        when(textParameterBeanDef.newInstance()).thenReturn(textParameterEditor);
        when(numberParameterBeanDef.newInstance()).thenReturn(numberParameterEditor);
        when(dateParameterBeanDef.newInstance()).thenReturn(dateParameterEditor);
        when(likeToFunctionBeanDef.newInstance()).thenReturn(likeToFunctionEditor);
        when(timeFrameBeanDef.newInstance()).thenReturn(timeFrameEditor);
    }

    protected ColumnFilterEditor setupEditor(ColumnType columnType, CoreFunctionType functionType, Comparable... params) {
        when(metadata.getColumnType("col")).thenReturn(columnType);

        CoreFunctionFilter filter = new CoreFunctionFilter("col", functionType, params);
        ColumnFilterEditor filterEditor = new ColumnFilterEditor(view, beanManager, changedEvent, deletedEvent);
        filterEditor.init(metadata, filter);

        assertEquals(view, filterEditor.getView());
        return filterEditor;
    }

    @Test
    public void testTextParam() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.EQUALS_TO, "Test");

        int n = CoreFunctionType.getSupportedTypes(ColumnType.LABEL).size()-1;
        verify(view).clearFunctionSelector();
        verify(view, times(n)).addFunctionItem(any(CoreFunctionType.class));
        verify(view, never()).addFunctionItem(CoreFunctionType.TIME_FRAME);

        verify(view).clearFilterConfig();
        verify(view).addFilterConfigWidget(textParameterEditor);
        verify(view).setFunctionSelected("col = Test");
    }

    @Test
    public void testNumberParam() throws Exception {
        double number = 1000.23;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        when(view.formatNumber(number)).thenReturn(numberFormat.format(number));
        setupEditor(ColumnType.NUMBER, CoreFunctionType.EQUALS_TO, number);

        int n = CoreFunctionType.getSupportedTypes(ColumnType.NUMBER).size()-1;
        verify(view).clearFunctionSelector();
        verify(view, times(n)).addFunctionItem(any(CoreFunctionType.class));
        verify(view, never()).addFunctionItem(CoreFunctionType.TIME_FRAME);
        verify(view, never()).addFunctionItem(CoreFunctionType.LIKE_TO);

        verify(view).clearFilterConfig();
        verify(view).addFilterConfigWidget(numberParameterEditor);
        verify(view).setFunctionSelected("col = " + numberFormat.format(number));
    }

    @Test
    public void testDateParam() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateStr = "23-11-2020 23:59:59";
        Date d = dateFormat.parse(dateStr);
        when(view.formatDate(d)).thenReturn(dateStr);
        setupEditor(ColumnType.DATE, CoreFunctionType.EQUALS_TO, d);

        int n = CoreFunctionType.getSupportedTypes(ColumnType.DATE).size()-1;
        verify(view).clearFunctionSelector();
        verify(view, times(n)).addFunctionItem(any(CoreFunctionType.class));
        verify(view).addFunctionItem(CoreFunctionType.TIME_FRAME);
        verify(view, never()).addFunctionItem(CoreFunctionType.LIKE_TO);

        verify(view).clearFilterConfig();
        verify(view).addFilterConfigWidget(dateParameterEditor);
        verify(view).setFunctionSelected("col = " + dateStr);
    }

    @Test
    public void testNotEquals() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.NOT_EQUALS_TO, "Test");
        verify(view).setFunctionSelected("col != Test");
    }

    @Test
    public void testBetween() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.BETWEEN, "A", "B");
        verify(view).setFunctionSelected("col [A B]");
    }

    @Test
    public void testGreaterOrEquals() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.GREATER_OR_EQUALS_TO, "Test");
        verify(view).setFunctionSelected("col >= Test");
    }

    @Test
    public void testGreaterThan() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.GREATER_THAN, "Test");
        verify(view).setFunctionSelected("col > Test");
    }
    @Test
    public void testLowerOrEquals() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.LOWER_OR_EQUALS_TO, "Test");
        verify(view).setFunctionSelected("col <= Test");
    }

    @Test
    public void testLowerThan() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.LOWER_THAN, "Test");
        verify(view).setFunctionSelected("col < Test");
    }

    @Test
    public void testNull() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.IS_NULL);
        verify(view).setFunctionSelected("col = null ");
    }

    @Test
    public void testNotNull() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.NOT_NULL);
        verify(view).setFunctionSelected("col != null ");
    }

    @Test
    public void testLikeTo() {
        setupEditor(ColumnType.LABEL, CoreFunctionType.LIKE_TO, "Test");
        verify(view).clearFilterConfig();
        verify(view).setFunctionSelected("col like Test");
        verify(view).addFilterConfigWidget(likeToFunctionEditor);
    }

    @Test
    public void testTimeFrame() {
        setupEditor(ColumnType.DATE, CoreFunctionType.TIME_FRAME, "begin[year February] till now");
        verify(view).clearFilterConfig();
        verify(view).setFunctionSelected("col = begin[year February] till now");
        verify(view).addFilterConfigWidget(timeFrameEditor);
    }

    @Test
    public void testSelectFunction() {
        ColumnFilterEditor presenter = setupEditor(ColumnType.LABEL, CoreFunctionType.EQUALS_TO, "value");
        verify(view).clearFilterConfig();
        verify(view).setFunctionSelected("col = value");

        reset(view);
        when(view.getSelectedFunctionIndex()).thenReturn(2);
        presenter.onSelectFilterFunction();

        assertEquals(presenter.getCoreFilter().getType(), CoreFunctionType.NOT_EQUALS_TO);
        verify(changedEvent).fire(any(ColumnFilterChangedEvent.class));
        verify(view).clearFilterConfig();
        verify(view).setFunctionSelected("col != value1");
    }
}
