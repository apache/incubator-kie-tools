/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.AbstractProxyPopupDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigDecimalDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericByteDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericDoubleDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericFloatDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericLongDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericShortDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupTextDropDownEditCell;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub(DateTimeFormat.class)
@RunWith(GwtMockitoTestRunner.class)
public class TemplateDataCellFactoryTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private TemplateDropDownManager dropDownManager;

    private boolean isReadOnly = false;

    @Mock
    private EventBus eventBus;

    @Mock
    private TemplateDataColumn column;

    @Captor
    private ArgumentCaptor<AbstractProxyPopupDropDownEditCell> puddCaptor;

    private TemplateDataCellFactory testedFactory;

    @Before
    public void setUp() throws Exception {
        testedFactory = spy(new TemplateDataCellFactory(oracle, dropDownManager, isReadOnly, eventBus));
    }

    @Test
    public void testGetCell() throws Exception {
        final String factType = "org.kiegroup.Car";
        final String factField = "color";
        final String dataType = DataType.DataTypes.STRING.name();

        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn("==").when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory, never()).makeSelectionEnumCell(factType, factField, "==", dataType);
        verify(testedFactory).makeTextCellWrapper();
    }

    @Test
    public void testGetCellWhenEnumPresent() throws Exception {
        final String factType = "org.kiegroup.Car";
        final String factField = "color";
        final String dataType = DataType.DataTypes.STRING.name();

        doReturn(true).when(oracle).hasEnums(factType, factField);
        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn("==").when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory).makeSelectionEnumCell(factType, factField, "==", dataType);
        verify(testedFactory, never()).makeTextCellWrapper();
    }

    @Test
    public void testDoNotGetCellWhenEnumPresentIfDataTypeIsDefault() throws Exception {
        final String factType = "org.kiegroup.Car";
        final String factField = "color";
        final String dataType = TemplateModel.DEFAULT_TYPE;

        doReturn(true).when(oracle).hasEnums(factType, factField);
        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn("==").when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory, never()).makeSelectionEnumCell(anyString(), anyString(), anyString(), anyString());
        verify(testedFactory).makeTextCellWrapper();
    }

    @Test
    public void testGetCellForInteger() {
        final String factType = "org.kiegroup.Car";
        final String factField = "price";
        final String dataType = DataType.TYPE_NUMERIC_INTEGER;
        final String operator = ">";

        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn(operator).when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory, never()).makeSelectionEnumCell(factType, factField, operator, dataType);
        verify(testedFactory, never()).makeTextCellWrapper();
    }

    @Test
    public void testGetCellForListOperator() {
        final String factType = "org.kiegroup.Car";
        final String factField = "price";
        final String dataType = DataType.TYPE_NUMERIC_INTEGER;
        final String operator = "in";

        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn(operator).when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory, never()).makeSelectionEnumCell(factType, factField, operator, dataType);
        verify(testedFactory).makeTextCellWrapper();
    }

    @Test
    /**
     * https://issues.redhat.com/browse/DROOLS-5011
     * Even if the operator is for a list the enumerations should override.
     */
    public void testEnumHasPriorityOverListOperator() throws Exception {
        testEnumAndOperator(DataType.TYPE_STRING, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorNumeric() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorBigDecimal() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_BIGDECIMAL, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorBigInteger() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_BIGINTEGER, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorByte() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_BYTE, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorDouble() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_DOUBLE, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorFloat() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_FLOAT, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorInteger() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_INTEGER, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorLong() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_LONG, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityOverListOperatorShort() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_SHORT, "in");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupTextDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorNumeric() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericBigDecimalDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorBigDecimal() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_BIGDECIMAL, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericBigDecimalDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorBigInteger() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_BIGINTEGER, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericBigIntegerDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorByte() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_BYTE, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericByteDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorDouble() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_DOUBLE, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericDoubleDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorFloat() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_FLOAT, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericFloatDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorInteger() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_INTEGER, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericIntegerDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorLong() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_LONG, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericLongDropDownEditCell);
    }

    @Test
    public void testEnumHasPriorityButSimpleOperatorShort() throws Exception {
        testEnumAndOperator(DataType.TYPE_NUMERIC_SHORT, "==");
        verify(testedFactory).decoratedGridCellValueAdaptor(puddCaptor.capture());
        assertTrue(puddCaptor.getValue() instanceof ProxyPopupNumericShortDropDownEditCell);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalNumericType() {
        final String factType = "org.kiegroup.Car";
        final String factField = "carAttribute";
        final String dataType = DataType.TYPE_DATE;
        final String operator = "in";

        testedFactory.makeNumericSelectionEnumCell(factType, factField, dataType, operator);
    }

    private void testEnumAndOperator(final String dataType, final String operator) throws Exception {
        final String factType = "org.kiegroup.Car";
        final String factField = "carAttribute";

        doReturn(true).when(oracle).hasEnums(factType, factField);
        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn(operator).when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory).makeSelectionEnumCell(factType, factField, operator, dataType);
        verify(testedFactory, never()).makeTextCellWrapper();
    }
}
