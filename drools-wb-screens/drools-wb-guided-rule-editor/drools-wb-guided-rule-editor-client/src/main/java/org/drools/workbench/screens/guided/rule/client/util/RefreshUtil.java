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

package org.drools.workbench.screens.guided.rule.client.util;

import java.util.Map;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.ActionValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.ConstraintValueEditor;

public class RefreshUtil {

    public static void refreshActionValueEditorsDropDownData(final Map<ActionFieldValue, ActionValueEditor> actionValueEditors, final ActionFieldValue modifiedField) {
        for (Map.Entry<ActionFieldValue, ActionValueEditor> e : actionValueEditors.entrySet()) {
            final ActionFieldValue afv = e.getKey();
            if (afv.getNature() == FieldNatureType.TYPE_LITERAL || afv.getNature() == FieldNatureType.TYPE_ENUM) {
                if (!afv.equals(modifiedField)) {
                    e.getValue().refresh();
                }
            }
        }
    }

    public static void refreshConstraintValueEditorsDropDownData(final Map<SingleFieldConstraint, ConstraintValueEditor> constraintValueEditors, final SingleFieldConstraint modifiedConstraint) {
        for (Map.Entry<SingleFieldConstraint, ConstraintValueEditor> e : constraintValueEditors.entrySet()) {
            final SingleFieldConstraint sfc = e.getKey();
            if (sfc.getConstraintValueType() == SingleFieldConstraint.TYPE_LITERAL || sfc.getConstraintValueType() == SingleFieldConstraint.TYPE_ENUM) {
                if (!sfc.equals(modifiedConstraint)) {
                    e.getValue().refresh();
                }
            }
        }
    }
}
