/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.group.GroupFunction;

public class DataSetImpl implements DataSet {

    protected DataSetDef definition;
    protected String uuid = null;
    protected Date creationDate = new Date();
    protected List<DataColumnImpl> columns = new ArrayList<DataColumnImpl>();
    protected int rowCountNonTrimmed = -1;

    public DataSetMetadata getMetadata() {
        return new DataSetMetadataImpl(this);
    }

    public DataSetDef getDefinition() {
        return definition;
    }

    public void setDefinition(DataSetDef definition) {
        this.definition = definition;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<DataColumn> getColumns() {
        return new ArrayList<DataColumn>(columns);
    }

    public void setColumns(List<DataColumn> columnList) {
        columns.clear();
        for (DataColumn column : columnList) {
            columns.add((DataColumnImpl) column);
        }
    }

    public DataColumn getColumnById(String id) {
        for (DataColumn column : columns) {
            if (column.getId().equalsIgnoreCase(id)) {
                return column;
            }
            GroupFunction gf = column.getGroupFunction();
            if (gf != null && gf.getSourceId() != null && gf.getSourceId().equalsIgnoreCase(id)) {
                return column;
            }
        }
        return null;
    }

    public DataColumn getColumnByIndex(int index) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("The data set is empty.");
        }
        if (index >= columns.size()) {
            throw new IllegalArgumentException("The column index " + index + " is out of bounds: " + (columns.size()-1));
        }
        return columns.get(index);
    }

    @Override
    public int getColumnIndex( DataColumn dataColumn ) {
        if (dataColumn == null || "".equals(dataColumn.getId())) {
            throw new IllegalArgumentException("Wrong column specified.");
        }
        for (int i = 0; i < columns.size(); i++) {
            if ( dataColumn.getId().equalsIgnoreCase( columns.get( i ).getId() ) ) return i;
        }
        throw new IllegalArgumentException( "The column with id " + dataColumn.getId() + " does not exist." );
    }

    public DataSet addColumn(String id, ColumnType type) {
        return addColumn(id, type, null);
    }

    public DataSet addColumn(String id, ColumnType type, List values) {
        DataColumnImpl c = new DataColumnImpl();
        c.setDataSet(this);
        c.setId(id);
        c.setColumnType(type);
        if (values != null) c.setValues(values);
        columns.add(c);
        return this;
    }

    public DataSet addColumn(DataColumn column) {
        columns.add((DataColumnImpl) column);
        return this;
    }

    public DataSet removeColumn(String id) {
        Iterator<DataColumnImpl> it = columns.iterator();
        while (it.hasNext()) {
            DataColumn column = it.next();
            if (column.getId().equalsIgnoreCase(id)) {
                it.remove();
            }
        }
        return this;
    }

    public boolean isEmpty() {
        return getRowCount() == 0;
    }

    public int getRowCount() {
        if (columns == null || columns.isEmpty()) {
            return 0;
        }
        return columns.get(0).getValues().size();
    }

    public int getRowCountNonTrimmed() {
        if (rowCountNonTrimmed == -1) return getRowCount();
        return rowCountNonTrimmed;
    }

    public void setRowCountNonTrimmed(int rowCountNonTrimmed) {
        this.rowCountNonTrimmed = rowCountNonTrimmed;
    }

    public Object getValueAt(int row, String columnId) {
        DataColumn columnObj = getColumnById(columnId);
        return getValueAt(row, columnObj);
    }

    public Object getValueAt(int row, int column) {
        DataColumn columnObj = getColumnByIndex(column);
        return getValueAt(row, columnObj);
    }

    protected Object getValueAt(int row, DataColumn column) {
        if (row >= getRowCount()) {
            throw new IllegalArgumentException("The row index " + row + " is out of bounds: " + (getRowCount()-1));
        }
        return column.getValues().get(row);
    }

    public DataSet setValueAt(int row, int column, Object value) {
        _setValueAt(row, column, value, false);
        return this;
    }

    public DataSet addValueAt(int row, int column, Object value) {
        _setValueAt(row, column, value, true);
        return this;
    }

    public DataSet addValueAt(int column, Object value) {
        _setValueAt(-1, column, value, true);
        return this;
    }

    protected void _setValueAt(int row, int column, Object value, boolean insert) {
        DataColumn columnObj = getColumnByIndex(column);

        List l = columnObj.getValues();
        if (row > l.size()) {
            throw new IllegalArgumentException("The row index " + row + " is out of bounds: " + (l.size()-1));
        }

        Object _val = convert(columnObj, value);
        if (row < 0 || row == l.size()) l.add(_val);
        else if (insert) l.add(row, _val);
        else l.set(row, _val);
    }

    public DataSet setValuesAt(int row, Object... values) {
        _setValuesAt(row, false, values);
        return this;
    }

    public DataSet addValuesAt(int row, Object... values) {
        _setValuesAt(row, true, values);
        return this;
    }

    public DataSet addValues(Object... values) {
        _setValuesAt(-1, true, values);
        return this;
    }

    protected void _setValuesAt(int row, boolean insert, Object... values) {
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            _setValueAt(row, i, value, insert);
        }
    }

    public DataSet addEmptyRowAt(int row) {
        _setEmptyRowAt(row, true);
        return this;
    }

    protected void _setEmptyRowAt(int row, boolean insert) {
        for (int i = 0; i < columns.size(); i++) {
            DataColumn column = columns.get(i);
            if (ColumnType.DATE.equals(column.getColumnType())) {
                _setValueAt(row, i, new Date(), insert);
            }
            else if (ColumnType.NUMBER.equals(column.getColumnType())) {
                _setValueAt(row, i, 0d, insert);
            }
            else {
                _setValueAt(row, i, "", insert);
            }
        }
    }

    public Object convert(DataColumn column, Object value) {
        if (ColumnType.NUMBER.equals(column.getColumnType())) {
            return convertToDouble(value);
        }
        if (ColumnType.DATE.equals(column.getColumnType())) {
            return convertToDate(value);
        }
        return convertToString(value);
    }

    public String convertToString(Object value) {
        try {
            return value == null ? null : (String) value;
        } catch (ClassCastException e) {
            return value.toString();
        }
    }

    public Double convertToDouble(Object value) {
        try {
            return value == null ? null : ((Number) value).doubleValue();
        } catch (ClassCastException e) {
            return Double.parseDouble(value.toString());
        }
    }

    public Date convertToDate(Object value) {
        try {
            return value == null ? null : (Date) value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Not a java.util.Date: " + value + " (" + value.getClass().getName() + ")");
        }
    }

    public DataSet trim(int offset, int rows) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset can't be negative: " + offset);
        }
        if (offset == 0 && (rows <= 0 || rows >= this.getRowCount())) {
            this.rowCountNonTrimmed = -1;
            return this;
        }
        if (offset > getRowCount()) {
            throw new IllegalArgumentException("Offset can't be greater than the number of rows: " + offset);
        }

        DataSetImpl other = cloneEmpty();
        other.rowCountNonTrimmed = getRowCount();
        for (int i=0; i<columns.size(); i++) {
            DataColumn column = columns.get(i);
            DataColumn colOther = other.getColumns().get(i);
            List values = column.getValues();
            List valOther = colOther.getValues();
            for (int j=offset; j<values.size() && j<( offset+rows ); j++) {
                Object value = values.get(j);
                valOther.add(value);
            }
        }
        return other;
    }

    public DataSet trim(List<Integer> rows) {
        if (rows == null) {
            return this;
        }
        DataSetImpl other = cloneEmpty();
        other.rowCountNonTrimmed = getRowCount();
        if (rows.isEmpty()) return other;

        for (int i=0; i<columns.size(); i++) {
            List values = columns.get(i).getValues();
            List valOther = other.getColumns().get(i).getValues();
            for (Integer row : rows) {
                if (row >= values.size()) {
                    throw new IllegalArgumentException("Row number is out of bounds: " + row);
                }
                Object value = values.get(row);
                valOther.add(value);
            }
        }
        return other;
    }

    public DataSetImpl cloneEmpty() {
        DataSetImpl other = new DataSetImpl();
        for (int i=0; i<columns.size(); i++) {
            DataColumn column = columns.get(i);
            DataColumn otherCol = column.cloneEmpty();
            other.addColumn(otherCol);
        }
        return other;
    }

    public DataSetImpl cloneInstance() {
        DataSetImpl other = new DataSetImpl();
        for (int i=0; i<columns.size(); i++) {
            DataColumn column = columns.get(i);
            DataColumn otherCol = column.cloneInstance();
            other.addColumn(otherCol);
        }
        return other;
    }

    public boolean equals(Object obj) {
        try {
            DataSetImpl other = (DataSetImpl) obj;
            if (other == null) {
                return false;
            }
            if (getEstimatedSize() != other.getEstimatedSize()) {
                return false;
            }
            if (columns.size() != other.columns.size()) {
                return false;
            }
            for (int i=0; i<columns.size(); i++) {
                if (!columns.get(i).equals(other.columns.get(i))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public long getEstimatedSize() {
        int nrows = getRowCount();
        if (nrows == 0) return 0;

        List<DataColumn> columns = getColumns();
        int ncells = nrows * columns.size();
        int result = ncells * 4;
        for (int i = 0; i < columns.size(); i++) {
            Object firstRowValue = getValueAt(0, i);
            if (firstRowValue instanceof String) {
                for (int j = 0; j < nrows; j++) {
                    String stringValue = (String) getValueAt(j, i);
                    result += MemSizeEstimator.sizeOfString(stringValue);
                }
            } else {
                int singleValueSize = MemSizeEstimator.sizeOf(firstRowValue);
                result += nrows * singleValueSize;
            }
        }
        return result;
    }
}
