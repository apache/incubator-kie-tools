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
package org.dashbuilder.dataset.def;

/**
 * A builder for defining static data sets
 *
 * <pre>
 *    DataSetDef dataSetDef = DataSetDefFactory.newCSVDataSetDef()
 *     .uuid("all_employees")
 *     .url("http://myhost.com/file.csv")
 *     .separatorChar(";")
 *     .label("name")
 *     .date("creationDate", "MM/dd/yyyy")
 *     .number("amount", "#.###,00")
 *     .buildDef();
 * </pre>
 */
public interface CSVDataSetDefBuilder<T extends DataSetDefBuilder> extends DataSetDefBuilder<T> {

    /**
     * A valid CSV stream URL
     *
     * @param url A valid URL to a CSV stream
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T fileURL(String url);

    /**
     * The CSV file path
     *
     * @param path A valid path to a local file.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T filePath(String path);

    /**
     * It tells the CSV provider whether to include all the columns in the CSV defintion or
     * only those columns explicitly declared into the data set definition.
     *
     * @param all If tru all the CSV columns will be part of the data set (default true).
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T allColumns(boolean all);

    /**
     * Set the CSV column separator char.
     *
     * @param separator An string for separating columns
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T separatorChar(char separator);

    /**
     * Set the quote symbol.
     *
     * @param quote A char representing the quote symbol
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T quoteChar(char quote);

    /**
     * Set the escape char.
     *
     * @param escape The scape char
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T escapeChar(char escape);

    /**
     * Set the pattern for the specified date column.
     *
     * @param columnId The id of the column
     * @param pattern The pattern of the column values. (See <i>java.text.DateFormat</i>)
     * @see java.text.SimpleDateFormat
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T date(String columnId, String pattern);

    /**
     * Set the pattern for the specified numeric column.
     *
     * @param columnId The id of the column
     * @param pattern The pattern of the column values. (See <i>java.text.DecimalFormat</i>)
     * @see java.text.DecimalFormat
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T number(String columnId, String pattern);

    /**
     * Set the overall pattern used to read date columns.
     *
     * @param pattern The pattern of the column values. (See <i>java.text.DateFormat</i>)
     * @see java.text.SimpleDateFormat
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T datePattern(String pattern);

    /**
     * Set the overall pattern used to read numeric columns.
     *
     * @param pattern The pattern of the column values. (See <i>java.text.DecimalFormat</i>)
     * @see java.text.DecimalFormat
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T numberPattern(String pattern);
}
