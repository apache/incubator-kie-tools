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
package org.dashbuilder.dataset;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.junit.Test;

import static org.dashbuilder.dataset.ColumnType.*;
import static org.junit.Assert.*;

public class DataSetApiLookupConstraintsTest {

    public static final String OFFICE = "office";
    public static final String DEPARTMENT = "department";
    public static final String EMPLOYEE = "employee";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";

    public static final DataSetMetadata METADATA = new DataSetMetadataImpl(null, "test", 100, 5,
            Arrays.asList(OFFICE, DEPARTMENT, EMPLOYEE, AMOUNT, DATE),
            Arrays.asList(LABEL, LABEL, LABEL, NUMBER, ColumnType.DATE), 0);

    public static final DataSetMetadata METADATA2 = new DataSetMetadataImpl(null, "test", 100, 3,
            Arrays.asList(OFFICE, DEPARTMENT, EMPLOYEE),
            Arrays.asList(LABEL, LABEL, LABEL), 0);


    public static final DataSetLookupConstraints TWO_COLUMNS_GROUPED = new DataSetLookupConstraints()
            .setGroupRequired(true)
            .setGroupColumn(true)
            .setMaxColumns(2)
            .setMinColumns(2)
            .setExtraColumnsAllowed(false)
            .setGroupsTitle("Categories")
            .setColumnsTitle("Values")
            .setColumnTypes(new ColumnType[]{
                    LABEL,
                    NUMBER});

    public static final DataSetLookupConstraints MULTIPLE_COLUMNS = new DataSetLookupConstraints()
            .setGroupAllowed(true)
            .setGroupRequired(false)
            .setMaxColumns(-1)
            .setMinColumns(1)
            .setExtraColumnsAllowed(true)
            .setGroupsTitle("Rows")
            .setColumnsTitle("Columns");

    DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
            .setGroupRequired(true)
            .setGroupColumn(true)
            .setMaxColumns(10)
            .setMinColumns(2)
            .setExtraColumnsAllowed(true)
            .setExtraColumnsType( ColumnType.NUMBER)
            .setColumnTypes(new ColumnType[] {
                    ColumnType.LABEL,
                    ColumnType.NUMBER});
    @Test
    public void testTwoColumns() {
        DataSetLookup lookup = TWO_COLUMNS_GROUPED.newDataSetLookup(METADATA);
        assertEquals(lookup.getDataSetUUID(), "test");

        List<DataSetOp> opList = lookup.getOperationList();
        assertEquals(opList.size(), 1);

        List<DataSetGroup> groupList = lookup.getOperationList(DataSetGroup.class);
        assertEquals(groupList.size(), 1);

        DataSetGroup groupOp = groupList.get(0);
        assertNotNull(groupOp);
        assertEquals(groupOp.getGroupFunctions().size(), 2);

        ColumnGroup cg = groupOp.getColumnGroup();
        assertNotNull(groupOp);
        assertEquals(cg.getSourceId(), OFFICE);
        assertEquals(cg.getColumnId(), OFFICE);
        assertEquals(cg.getStrategy(), GroupStrategy.DYNAMIC);

        GroupFunction gf1 = groupOp.getGroupFunction(OFFICE);
        assertNotNull(gf1);
        assertNull(gf1.getFunction());
        assertEquals(gf1.getSourceId(), OFFICE);
        assertEquals(gf1.getColumnId(), OFFICE);

        GroupFunction gf2 = groupOp.getGroupFunction(AMOUNT);
        assertNotNull(gf2);
        assertNotNull(gf2.getFunction());
        assertEquals(gf2.getSourceId(), AMOUNT);
        assertEquals(gf2.getColumnId(), AMOUNT);
    }
    @Test
    public void testLabelOnlyMetadata() {
        DataSetLookup lookup = lookupConstraints.newDataSetLookup(METADATA2);
        assertEquals(lookup.getDataSetUUID(), "test");

        List<DataSetOp> opList = lookup.getOperationList();
        assertEquals(opList.size(), 1);

        List<DataSetGroup> groupList = lookup.getOperationList(DataSetGroup.class);
        assertEquals(groupList.size(), 1);

        DataSetGroup groupOp = groupList.get(0);
        assertNotNull(groupOp);
        assertEquals(groupOp.getGroupFunctions().size(), 2);

        ColumnGroup cg = groupOp.getColumnGroup();
        assertNotNull(groupOp);
        assertEquals(cg.getSourceId(), OFFICE);
        assertEquals(cg.getColumnId(), OFFICE);
        assertEquals(cg.getStrategy(), GroupStrategy.DYNAMIC);

        GroupFunction gf1 = groupOp.getGroupFunctions().get(0);
        assertNotNull(gf1);
        assertNull(gf1.getFunction());
        assertEquals(gf1.getSourceId(), OFFICE);
        assertEquals(gf1.getColumnId(), OFFICE);

        GroupFunction gf2 = groupOp.getGroupFunctions().get(1);
        assertNotNull(gf2);
        assertEquals(gf2.getFunction(), AggregateFunctionType.COUNT);
        assertNull(gf2.getSourceId());
        assertNotNull(gf2.getColumnId());

        // DASHBUILDE-180: Unable to add new series in displayer based on SQL query dataset
        GroupFunction extra = gf2.cloneInstance();
        String extraId = lookupConstraints.buildUniqueColumnId(lookup, extra);
        extra.setColumnId(extraId);
        groupOp.getGroupFunctions().add(extra);
        assertNotNull(extraId);

        extra = gf2.cloneInstance();
        extraId = lookupConstraints.buildUniqueColumnId(lookup, extra);
        extra.setColumnId(extraId);
        groupOp.getGroupFunctions().add(extra);
        assertNotNull(extraId);
    }

    @Test
    public void testMultipleColumns() {

        DataSetLookup lookup = MULTIPLE_COLUMNS.newDataSetLookup(METADATA);
        assertEquals(lookup.getDataSetUUID(), "test");

        List<DataSetOp> opList = lookup.getOperationList();
        assertEquals(opList.size(), 1);

        List<DataSetGroup> groupList = lookup.getOperationList(DataSetGroup.class);
        assertEquals(groupList.size(), 1);

        DataSetGroup groupOp = groupList.get(0);
        assertNotNull(groupOp);

        ColumnGroup cg = groupOp.getColumnGroup();
        assertNull(cg);
        assertEquals(groupOp.getGroupFunctions().size(), MULTIPLE_COLUMNS.getMinColumns());

        for (int i = 0; i < MULTIPLE_COLUMNS.getMinColumns(); i++) {
            GroupFunction gf = groupOp.getGroupFunction(METADATA.getColumnId(i));
            assertNotNull(gf);
            assertEquals(gf.getSourceId(), METADATA.getColumnId(i));
            assertEquals(gf.getColumnId(), METADATA.getColumnId(i));
            assertNull(gf.getFunction());
        }
    }

    @Test
    public void testValidationOk() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .group(DATE).fixed(DateIntervalType.MONTH, true)
                .column(DATE)
                .column(AMOUNT, AggregateFunctionType.SUM)
                .buildLookup();

        ValidationError error = TWO_COLUMNS_GROUPED.check(lookup, METADATA);
        assertNull(error);
    }

    @Test
    public void testColumnExcess() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .group(DATE).fixed(DateIntervalType.MONTH, true)
                .column(DATE)
                .column(AMOUNT, AggregateFunctionType.SUM)
                .column(AMOUNT, AggregateFunctionType.AVERAGE)
                .buildLookup();

        ValidationError error = TWO_COLUMNS_GROUPED.check(lookup, METADATA);
        assertNotNull(error);
        assertEquals(error.getCode(), DataSetLookupConstraints.ERROR_COLUMN_NUMBER);
    }

    @Test
    public void testColumnMissing() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .group(DEPARTMENT)
                .column(AMOUNT, AggregateFunctionType.SUM)
                .buildLookup();

        ValidationError error = TWO_COLUMNS_GROUPED.check(lookup, METADATA);
        assertNotNull(error);
        assertEquals(error.getCode(), DataSetLookupConstraints.ERROR_COLUMN_NUMBER);
    }

    @Test
    public void testMissingGroup() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .column(DATE)
                .column(AMOUNT, AggregateFunctionType.SUM)
                .buildLookup();

        ValidationError error = TWO_COLUMNS_GROUPED.check(lookup, METADATA);
        assertNotNull(error);
        assertEquals(error.getCode(), DataSetLookupConstraints.ERROR_GROUP_REQUIRED);
    }

    @Test
    public void testWrongColumnType() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .group(DEPARTMENT)
                .column(DEPARTMENT)
                .column(EMPLOYEE)
                .buildLookup();

        ValidationError error = TWO_COLUMNS_GROUPED.check(lookup, METADATA);
        assertNotNull(error);
        assertEquals(error.getCode(), DataSetLookupConstraints.ERROR_COLUMN_TYPE);

        lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .group(DATE)
                .column(DATE)
                .column(EMPLOYEE)
                .buildLookup();

        error = TWO_COLUMNS_GROUPED.check(lookup, METADATA);
        assertNotNull(error);
        assertEquals(error.getCode(), DataSetLookupConstraints.ERROR_COLUMN_TYPE);
    }

    @Test
    public void testUniqueColumns() {
        DataSetLookupConstraints UNIQUE_COLUMNS = new DataSetLookupConstraints()
                .setUniqueColumnIds(true);

        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .group(DEPARTMENT)
                .column(DEPARTMENT, "dept")
                .column(AMOUNT, AggregateFunctionType.AVERAGE, "amount")
                .column(AMOUNT, AggregateFunctionType.SUM, "amount")
                .buildLookup();

        ValidationError error = UNIQUE_COLUMNS.check(lookup, METADATA);
        assertNotNull(error);
        assertEquals(error.getCode(), DataSetLookupConstraints.ERROR_DUPLICATED_COLUMN_ID);

        UNIQUE_COLUMNS.setUniqueColumnIds(false);
        error = UNIQUE_COLUMNS.check(lookup, METADATA);
        assertNull(error);
    }
}