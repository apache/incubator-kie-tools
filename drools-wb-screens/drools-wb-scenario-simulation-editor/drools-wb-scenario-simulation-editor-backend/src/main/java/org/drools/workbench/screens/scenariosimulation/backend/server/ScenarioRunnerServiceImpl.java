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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioRunnerService;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class ScenarioRunnerServiceImpl
        implements ScenarioRunnerService {

    @Inject
    private Event<TestResultMessage> defaultTestResultMessageEvent;

    public ScenarioRunnerServiceImpl() {
    }

    @Override
    public void runAllTests(final String identifier,
                            final Path path) {

        defaultTestResultMessageEvent.fire(
                new TestResultMessage(
                        identifier,
                        1,
                        1,
                        new ArrayList<>()));
    }

    @Override
    public void runAllTests(final String identifier,
                            final Path path,
                            final Event<TestResultMessage> customTestResultEvent) {

        customTestResultEvent.fire(
                new TestResultMessage(
                        identifier,
                        1,
                        1,
                        new ArrayList<>()));
    }

    @Override
    public void runTest(final String identifier,
                        final Path path,
                        final ScenarioSimulationModel model) {

        defaultTestResultMessageEvent.fire(
                new TestResultMessage(
                        identifier,
                        1,
                        1,
                        new ArrayList<>()));
    }
}
