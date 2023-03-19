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

import org.kie.workbench.common.command.client.impl.CommandManagerImpl;

/**
 * This class is used to actually invoke <code>Command</code>' methods on given command (<b>allow</b>, <b>execute</b>, <b>undo</b>)
 */
@Dependent
public class ScenarioCommandManager extends CommandManagerImpl<ScenarioSimulationContext, ScenarioSimulationViolation> {

}
