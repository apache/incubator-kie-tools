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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.workbench.screens.scenariosimulation.backend.server.ScenarioSimulationXMLPersistence;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.type.ScenarioSimulationResourceTypeDefinition;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import static org.drools.workbench.screens.scenariosimulation.backend.server.util.ResourceHelper.getResourcesByExtension;

public class ScenarioJunitActivator extends ParentRunner<Simulation> {

    public static final String ACTIVATOR_CLASS_NAME = "ScenarioJunitActivatorTest";

    public static final Function<String, String> ACTIVATOR_CLASS_CODE = modulePackage ->
            String.format("package %s;\n/**\n* Do not remove this file\n*/\n@%s(%s.class)\npublic class %s {\n}",
                          modulePackage,
                          RunWith.class.getCanonicalName(),
                          ScenarioJunitActivator.class.getCanonicalName(),
                          ScenarioJunitActivator.ACTIVATOR_CLASS_NAME);

    public ScenarioJunitActivator(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<Simulation> getChildren() {
        return getResources().map(elem -> {
            try {
                String rawFile = new Scanner(new File(elem)).useDelimiter("\\Z").next();
                return getXmlReader().unmarshal(rawFile).getSimulation();
            } catch (FileNotFoundException e) {
                throw new ScenarioException("File not found, this should not happen: " + elem, e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    protected Description describeChild(Simulation child) {
        return ScenarioRunnerImpl.getDescriptionForSimulationDescriptor(child.getSimulationDescriptor());
    }

    @Override
    protected void runChild(Simulation child, RunNotifier notifier) {
        KieContainer kieClasspathContainer = getKieContainer();
        Runner scenarioRunner = newRunner(kieClasspathContainer, child);
        scenarioRunner.run(notifier);
    }

    ScenarioSimulationXMLPersistence getXmlReader() {
        return ScenarioSimulationXMLPersistence.getInstance();
    }

    Stream<String> getResources() {
        ScenarioSimulationResourceTypeDefinition typeDefinition = new ScenarioSimulationResourceTypeDefinition();
        return getResourcesByExtension(typeDefinition.getSuffix());
    }

    KieContainer getKieContainer() {
        return KieServices.get().getKieClasspathContainer();
    }

    Runner newRunner(KieContainer kieContainer, Simulation simulation) {
        return new ScenarioRunnerImpl(kieContainer, simulation);
    }
}
