/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.utils;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;

/**
 * Class used to store constants used throughout the code
 */
public class ConstantHolder {

    private ConstantHolder() {
        // Not instantiable
    }

    public static final String FA_ANGLE_DOWN = "fa-angle-down";
    public static final String FA_ANGLE_RIGHT = "fa-angle-right";
    public static final String HIDDEN = "hidden";
    public static final String NODE_HIDDEN = "node-hidden";
    public static final String DISABLED = "disabled";
    public static final String SELECTED = "selected";

    public static final String EXPRESSION = "expression";
    public static final String EXPRESSION_INSTANCE_PLACEHOLDER = EXPRESSION + " </>";
    public static final String EXPRESSION_VALUE_PREFIX = MVEL_ESCAPE_SYMBOL + " ";

    // GWT doesn't support Java 8 LocalDate
    public static final String LOCALDATE_SIMPLE_NAME = "LocalDate";
    public static final String LOCALDATE_CANONICAL_NAME = "java.time.LocalDate";
    public static final String DMN_DATE = "date";
}
