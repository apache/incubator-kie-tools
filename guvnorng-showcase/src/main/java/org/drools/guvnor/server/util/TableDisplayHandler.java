/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.util;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.AssetItemPageResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


/**
 * This utility class handles loading of tables.
 * <p/>
 * This is to give some flexibility in what fields are displayed.
 * rulelist.properties and archivedrulelist.properties are the files used.
 *
 * @deprecated in favour of {@link AbstractPagedTable}
 */
public class TableDisplayHandler {

    /**
     * how many rows we show on a grid page
     */

    private static final int ROWS_PER_PAGE = 40;
    private final RowLoader ASSET_LIST;


    /**
     * Produce a table dataset for a given iterator.
     *
     * @param list    The iterator.
     * @param numRows The number of rows to go to. -1 means don't stop.
     * @throws SerializationException
     */

    public TableDisplayHandler(String tableconfig) {
        ASSET_LIST = new RowLoader(tableconfig);
    }

    public TableDataResult loadRuleListTable(AssetItemPageResult list) throws SerializationException {
        List<TableDataRow> data = loadRows(list.assets.iterator(), -1);
        TableDataResult result = new TableDataResult();
        result.data = data.toArray(new TableDataRow[data.size()]);
        result.currentPosition = list.currentPosition;
        result.hasNext = list.hasNext;
        return result;
    }

    private TableDataResult loadRuleListTable(
            List<AssetItem> assetList, long curPos, boolean hasNext)
            throws SerializationException {
        List<TableDataRow> data = loadRows(assetList.iterator(), -1);
        TableDataResult result = new TableDataResult();
        result.data = data.toArray(new TableDataRow[data.size()]);
        result.currentPosition = curPos;
        result.hasNext = hasNext;
        return result;
    }

    public TableDataResult loadRuleListTable(
            List<AssetItem> assetList, int skip, int numRows) throws SerializationException {
        int size = assetList.size();
        boolean hasNext = false;
        int startPos = 0;
        int endPos = 0;

        if (numRows != -1) {
            if (skip > size) {
                List<AssetItem> tempList = new ArrayList<AssetItem>();
                return loadRuleListTable(tempList, 0, false);
            }

            if (skip > 0) {
                startPos = skip;
            } else {
                skip = 0;
            }

            if ((skip + numRows) > size) {
                endPos = size;
            } else {
                endPos = skip + numRows;
                hasNext = true;
            }

            List<AssetItem> tempList2 = assetList.subList(startPos, endPos);

            return loadRuleListTable(tempList2, endPos, hasNext);
        }

        return loadRuleListTable(assetList, 0, false);
    }

    public TableDataResult loadRuleListTable(AssetItemIterator it, int skip, int numRows) {
        if (numRows != -1) {
            it.skip(skip);
        }
        List<TableDataRow> data = loadRows(it, numRows);
        TableDataResult result = new TableDataResult();
        result.data = data.toArray(new TableDataRow[data.size()]);
        result.total = it.getSize();
        result.hasNext = it.hasNext();
        result.currentPosition = it.getPosition();
        return result;
    }

    private List<TableDataRow> loadRows(Iterator<AssetItem> iterator, int numRows) {
        List<TableDataRow> data = new ArrayList<TableDataRow>();

        while(iterator.hasNext()){
            AssetItem r = iterator.next();
            TableDataRow row = new TableDataRow();

            row.id = r.getUUID();
            row.format = r.getFormat();
            row.values = ASSET_LIST.getRow(r);
            data.add(row);
            if (numRows != -1) {
                if (data.size() == numRows) {
                    break;
                }
            }
        }
        return data;
    }

    public String formatDate(Calendar cal) {
        DateFormat localFormat = DateFormat.getDateInstance();

        return localFormat.format(cal.getTime());
    }

    public TableConfig loadTableConfig() {
        final TableConfig config = new TableConfig();

        config.headers = ASSET_LIST.getHeaders();
        config.headerTypes = ASSET_LIST.getHeaderTypes();
        config.rowsPerPage = ROWS_PER_PAGE;
        return config;
    }
}
