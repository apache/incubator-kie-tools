/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic.GenericSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.number.NumberSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.string.StringSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelector;

import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.DATE;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.DATE_TIME;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.DURATION_DAYS_TIME;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.DURATION_YEAR_MONTH;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.NUMBER;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.STRING;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.TIME;

@Dependent
public class TypedValueComponentSelector {

    private final GenericSelector genericSelector;

    private final DateSelector dateSelector;

    private final DayTimeSelector dayTimeSelector;

    private final YearsMonthsSelector yearsMosSelector;

    private final TimeSelector timeSelector;

    private final StringSelector stringSelector;

    private final NumberSelector numberSelector;

    private final DateTimeSelector dateTimeSelector;

    @Inject
    public TypedValueComponentSelector(final GenericSelector genericSelector,
                                       final DateSelector dateSelector,
                                       final DayTimeSelector dayTimeSelector,
                                       final YearsMonthsSelector yearsMosSelector,
                                       final StringSelector stringSelector,
                                       final NumberSelector numberSelector,
                                       final TimeSelector timeSelector,
                                       final DateTimeSelector dateTimeSelector) {
        this.genericSelector = genericSelector;
        this.dateSelector = dateSelector;
        this.dayTimeSelector = dayTimeSelector;
        this.yearsMosSelector = yearsMosSelector;
        this.stringSelector = stringSelector;
        this.numberSelector = numberSelector;
        this.timeSelector = timeSelector;
        this.dateTimeSelector = dateTimeSelector;
    }

    public TypedValueSelector makeSelectorForType(final String type) {

        if (isEqual(type, DATE)) {
            return dateSelector;
        }

        if (isEqual(type, DURATION_DAYS_TIME)) {
            return dayTimeSelector;
        }

        if (isEqual(type, DURATION_YEAR_MONTH)) {
            return yearsMosSelector;
        }

        if (isEqual(type, STRING)) {
            return stringSelector;
        }

        if (isEqual(type, NUMBER)) {
            return numberSelector;
        }

        if (isEqual(type, TIME)) {
            return timeSelector;
        }

        if (isEqual(type, DATE_TIME)) {
            return dateTimeSelector;
        }

        return genericSelector;
    }

    private boolean isEqual(final String type,
                            final BuiltInType builtInType) {
        return Objects.equals(builtInType.getName(), type);
    }
}
