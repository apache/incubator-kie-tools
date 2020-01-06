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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Arrays;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;

public class ConstraintValueChangeHandler
        implements com.google.gwt.event.logical.shared.ValueChangeHandler<String> {

    private final BaseSingleFieldConstraint constraint;
    private final Command onChangeCommand;

    public ConstraintValueChangeHandler(final BaseSingleFieldConstraint constraint,
                                        final Command onChangeCommand) {
        this.constraint = constraint;
        this.onChangeCommand = onChangeCommand;
    }

    @Override
    public void onValueChange(final ValueChangeEvent<String> event) {

        if (Arrays.asList(OperatorsOracle.EXPLICIT_LIST_OPERATORS).contains(constraint.getOperator())) {
            constraint.setValue(replaceQuotes(event.getValue()));
        } else {
            constraint.setValue(event.getValue());
        }
        if (onChangeCommand != null) {
            onChangeCommand.execute();
        }
    }

    private String replaceQuotes(final String value) {
        return value.replace("\\\\", "\\").replace("\\\"", "\"");
    }
}
