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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridRow;
import org.uberfire.mvp.Command;

/**
 * <code>Command</code> to <b>duplicate</b> a row.
 */
@Dependent
public class DuplicateRowCommand implements Command {

    private ScenarioGridModel model;
    private int rowIndex;

    public DuplicateRowCommand() {
    }

    /**
     * @param model
     * @param rowIndex
     */
    public DuplicateRowCommand(ScenarioGridModel model, int rowIndex) {
        this.model = model;
        this.rowIndex = rowIndex;
    }

    @Override
    public void execute() {
        model.duplicateNewRow(rowIndex, new ScenarioGridRow());
    }
}
