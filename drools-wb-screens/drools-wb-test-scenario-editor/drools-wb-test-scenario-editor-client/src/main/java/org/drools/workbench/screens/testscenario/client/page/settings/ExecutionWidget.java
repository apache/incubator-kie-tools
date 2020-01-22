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

package org.drools.workbench.screens.testscenario.client.page.settings;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;

@Dependent
public class ExecutionWidget extends HorizontalPanel {

    private ExecutionTrace executionTrace;

    private DateTimePicker dateTimePicker;

    private ListBox dateConfigurationChoice;

    @Inject
    public ExecutionWidget(final DateTimePicker dateTimePicker, final ListBox dateConfigurationChoice) {
        this.dateTimePicker = dateTimePicker;
        this.dateConfigurationChoice = dateConfigurationChoice;
    }

    @PostConstruct
    public void setup() {
        setVisible(false);

        dateConfigurationChoice.addItem(TestScenarioConstants.INSTANCE.UseRealDateAndTime());
        dateConfigurationChoice.addItem(TestScenarioConstants.INSTANCE.UseASimulatedDateAndTime());
        dateConfigurationChoice.setSelectedIndex(0);
        dateConfigurationChoice.addChangeHandler(getChangeHandler());

        dateTimePicker.setFormat("yyyy-MM-dd HH:mm");
        dateTimePicker.addValueChangeHandler(event -> getExecutionTrace().ifPresent(e -> e.setScenarioSimulatedDate(event.getValue())));

        add(dateConfigurationChoice);
        add(dateTimePicker);
    }

    public void show(final ExecutionTrace executionTrace) {
        setVisible(true);

        this.executionTrace = executionTrace;

        if (executionTrace.getScenarioSimulatedDate() != null) {
            dateTimePicker.setValue(executionTrace.getScenarioSimulatedDate());
            dateConfigurationChoice.setSelectedIndex(1);
        } else {
            dateTimePicker.setValue(null);
            dateConfigurationChoice.setSelectedIndex(0);
        }
    }

    ChangeHandler getChangeHandler() {
        return event -> {
            if (dateConfigurationChoice.getSelectedIndex() == 0) {
                dateTimePicker.setValue(null);
                getExecutionTrace().ifPresent(e -> e.setScenarioSimulatedDate(null));
            }
        };
    }

    Optional<ExecutionTrace> getExecutionTrace() {
        return Optional.ofNullable(executionTrace);
    }
}
