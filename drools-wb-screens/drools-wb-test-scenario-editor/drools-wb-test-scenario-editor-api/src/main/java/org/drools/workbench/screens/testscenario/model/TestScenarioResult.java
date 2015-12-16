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

package org.drools.workbench.screens.testscenario.model;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.Set;

@Portable
public class TestScenarioResult {

    private String identifier;
    private Scenario scenario;
    private Set<String> log;

    public TestScenarioResult() {
    }

    public TestScenarioResult(String identifier,
                              Scenario scenario,
                              Set<String> log) {
        this.identifier = identifier;
        this.scenario = scenario;
        this.log = log;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public Set<String> getLog() {
        return log;
    }
}
