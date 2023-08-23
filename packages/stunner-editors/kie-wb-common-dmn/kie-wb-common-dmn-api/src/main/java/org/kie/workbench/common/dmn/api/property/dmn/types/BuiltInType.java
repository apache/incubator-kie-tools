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

package org.kie.workbench.common.dmn.api.property.dmn.types;

import java.util.Comparator;

import org.kie.workbench.common.dmn.api.property.dmn.QName;

public enum BuiltInType {

    //DMN 1.1 Specification, Section 10.3.1.3
    NUMBER("number"),
    STRING("string"),
    BOOLEAN("boolean"),
    DURATION_DAYS_TIME("days and time duration", "dayTimeDuration"),
    DURATION_YEAR_MONTH("years and months duration", "yearMonthDuration"),
    TIME("time"),
    DATE_TIME("date and time", "dateTime"),

    //Requested by Edson Tirelli
    ANY("Any"),
    DATE("date"),
    CONTEXT("context"),
    UNDEFINED("<Undefined>");

    public static final Comparator<BuiltInType> BUILT_IN_TYPE_COMPARATOR = Comparator.comparing(o -> {
        if (o == BuiltInType.UNDEFINED) {
            return "";
        }
        return o.getName();
    });

    private final String[] names;

    BuiltInType(final String... names) {
        this.names = names;
    }

    public String getName() {
        return names[0];
    }

    public String[] getNames() {
        return names;
    }

    public QName asQName() {
        return new QName(QName.NULL_NS_URI,
                         getName());
    }

    @Override
    public String toString() {
        return "Type{ " +
                names[0] +
                " }";
    }

}
