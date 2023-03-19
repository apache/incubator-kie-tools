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

import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.CSVDataSetDefBuilder;
import org.dashbuilder.dataset.def.DataSetDef;

public class CSVDataSetDefBuilderImpl extends AbstractDataSetDefBuilder<CSVDataSetDefBuilderImpl> implements CSVDataSetDefBuilder<CSVDataSetDefBuilderImpl> {

    protected DataSetDef createDataSetDef() {
        return new CSVDataSetDef();
    }

    public CSVDataSetDefBuilderImpl fileURL(String url) {
        ((CSVDataSetDef) def).setFileURL(url);
        return this;
    }

    public CSVDataSetDefBuilderImpl filePath(String path) {
        ((CSVDataSetDef) def).setFilePath(path);
        return this;
    }

    public CSVDataSetDefBuilderImpl allColumns(boolean all) {
        ((CSVDataSetDef) def).setAllColumnsEnabled(all);
        return this;
    }

    public CSVDataSetDefBuilderImpl separatorChar(char separator) {
        ((CSVDataSetDef) def).setSeparatorChar(separator);
        return this;
    }

    public CSVDataSetDefBuilderImpl quoteChar(char quote) {
        ((CSVDataSetDef) def).setQuoteChar(quote);
        return this;
    }

    public CSVDataSetDefBuilderImpl escapeChar(char escape) {
        ((CSVDataSetDef) def).setEscapeChar(escape);
        return this;
    }

    public CSVDataSetDefBuilderImpl date(String columnId, String datePattern) {
        def.setPattern(columnId, datePattern);
        super.date(columnId);
        return this;
    }

    public CSVDataSetDefBuilderImpl number(String columnId, String numberPattern) {
        def.setPattern(columnId, numberPattern);
        super.number(columnId);
        return this;
    }

    public CSVDataSetDefBuilderImpl datePattern(String datePattern) {
        ((CSVDataSetDef) def).setDatePattern(datePattern);
        return this;
    }

    public CSVDataSetDefBuilderImpl numberPattern(String numberPattern) {
        ((CSVDataSetDef) def).setNumberPattern(numberPattern);
        return this;
    }
}
