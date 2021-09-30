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
package org.dashbuilder.dataprovider;

import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetGenerator;

import static org.dashbuilder.dataset.date.Month.*;

public class SalesPerYearDataSetGenerator implements DataSetGenerator {

    public DataSet buildDataSet(Map<String,String> params) {

        double multiplier = params.get("multiplier") == null ? 1 : Double.parseDouble(params.get("multiplier"));

        return DataSetFactory.newDataSetBuilder()
                .label("month")
                .number("2012")
                .number("2013")
                .number("2014")
                .row(JANUARY, 1000d*multiplier, 2000d*multiplier, 3000d*multiplier)
                .row(FEBRUARY, 1400d*multiplier, 2300d*multiplier, 2000d*multiplier)
                .row(MARCH, 1300d*multiplier, 2000d*multiplier, 1400d*multiplier)
                .row(APRIL, 900d*multiplier, 2100d*multiplier, 1500d*multiplier)
                .row(MAY, 1300d*multiplier, 2300d*multiplier, 1600d*multiplier)
                .row(JUNE, 1010d*multiplier, 2000d*multiplier, 1500d*multiplier)
                .row(JULY, 1050d*multiplier, 2400d*multiplier, 3000d*multiplier)
                .row(AUGUST, 2300d*multiplier, 2000d*multiplier, 3200d*multiplier)
                .row(SEPTEMBER, 1900d*multiplier, 2700d*multiplier, 3000d*multiplier)
                .row(OCTOBER, 1200d*multiplier, 2200d*multiplier, 3100d*multiplier)
                .row(NOVEMBER, 1400d*multiplier, 2100d*multiplier, 3100d*multiplier)
                .row(DECEMBER, 1100d*multiplier, 2100d*multiplier, 4200d*multiplier)
                .buildDataSet();
    }
}
