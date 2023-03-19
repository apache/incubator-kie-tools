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
package org.dashbuilder.dataset;

/**
 * It allows for the building of DataSet instances in a friendly manner.
 *
 * <pre>
 new DataSetBuilder()
 .column("month", ColumnType.LABEL)
 .column("2016", ColumnType.NUMBER)
 .column("2017", ColumnType.NUMBER)
 .column("2018", ColumnType.NUMBER)
 .row(JANUARY, 1000d, 2000d, 3000d)
 .row(FEBRUARY, 1400d, 2300d, 2000d)
 .row(MARCH, 1300d, 2000d, 1400d)
 .row(APRIL, 900d, 2100d, 1500d)
 .row(MAY, 1300d, 2300d, 1600d)
 .row(JUNE, 1010d, 2000d, 1500d)
 .row(JULY, 1050d, 2400d, 3000d)
 .row(AUGUST, 2300d, 2000d, 3200d)
 .row(SEPTEMBER, 1900d, 2700d, 3000d)
 .row(OCTOBER, 1200d, 2200d, 3100d)
 .row(NOVEMBER, 1400d, 2100d, 3100d)
 .row(DECEMBER, 1100d, 2100d, 4200d)
 .buildDataSet()
 </pre>
 */
public interface DataSetBuilder {

    /**
     * Assigns an UUID to the data set
     */
    DataSetBuilder uuid(String uuid);

    /**
     * Add an empty column of type label.
     */
    DataSetBuilder label(String columnId);

    /**
     * Add an empty column of type text.
     */
    DataSetBuilder text(String columnId);

    /**
     * Add an empty column of numeric type.
     */
    DataSetBuilder number(String columnId);

    /**
     * Add an empty column of type date.
     */
    DataSetBuilder date(String columnId);

    /**
     * Add an empty column of the specified type.
     */
    DataSetBuilder column(String columnId, ColumnType type);

    /**
     * Add a row with the given values at the end of the data set.
     */
    DataSetBuilder row(Object... values);

    /**
     * Build and get the resulting DataSet instance.
     */
    DataSet buildDataSet();
}
