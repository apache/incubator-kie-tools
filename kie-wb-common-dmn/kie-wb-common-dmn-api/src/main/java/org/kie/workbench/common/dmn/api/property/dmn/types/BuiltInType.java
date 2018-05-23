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

package org.kie.workbench.common.dmn.api.property.dmn.types;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
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
    ANY("any"),
    DATE("date"),
    CONTEXT("context");

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
        return new QName(Namespace.FEEL.getUri(),
                         getName());
    }

    @Override
    public String toString() {
        return "Type{ " +
                names[0] +
                " }";
    }

}
