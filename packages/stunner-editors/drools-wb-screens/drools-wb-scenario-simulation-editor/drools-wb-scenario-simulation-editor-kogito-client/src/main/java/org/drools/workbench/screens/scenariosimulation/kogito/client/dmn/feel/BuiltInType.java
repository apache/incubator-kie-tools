/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.feel;

/**
 * FEEL Types managed by DMN Editor. Copied from org.kie.workbench.common.dmn.api module
 */
public enum BuiltInType {

    NUMBER("number"),
    STRING("string"),
    BOOLEAN("boolean"),
    DURATION_DAYS_TIME("days and time duration", "dayTimeDuration"),
    DURATION_YEAR_MONTH("years and months duration", "yearMonthDuration"),
    TIME("time"),
    DATE_TIME("date and time", "dateTime"),

    ANY("Any"),
    DATE("date"),
    CONTEXT("context"),
    UNDEFINED("<Undefined>");

    private final String[] names;

    BuiltInType(String... names) {
        this.names = names;
    }

    public String getName() {
        return names[0];
    }

    public String[] getNames() {
        return names;
    }

    @Override
    public String toString() {
        return "Type{ " +
               names[0] +
               " }";
    }
}
