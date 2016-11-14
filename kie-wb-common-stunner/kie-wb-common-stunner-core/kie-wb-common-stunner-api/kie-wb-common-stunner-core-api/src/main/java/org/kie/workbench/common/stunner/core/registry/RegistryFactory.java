/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.registry;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;

/**
 * Factory for the different Stunner's registry types available.
 */
public interface RegistryFactory {

    /**
     * Creates a new registry instance for definition adapters.
     */
    AdapterRegistry newAdapterRegistry();

    /**
     * Creates a new registry instance for definition sets.
     */
    <T> TypeDefinitionSetRegistry<T> newDefinitionSetRegistry();

    /**
     * Creates a new registry instance for definitions.
     */
    <T> TypeDefinitionRegistry<T> newDefinitionRegistry();

    /**
     * Creates a new registry instance for commands.
     */
    <C extends Command> CommandRegistry<C> newCommandRegistry();

    /**
     * Creates a new registry instance for model domain factories.
     */
    <T extends Factory<?>> FactoryRegistry<T> newFactoryRegistry();

    /**
     * Creates a new registry instance for diagrams.
     */
    <T extends Diagram> DiagramRegistry<T> newDiagramRegistry();

}
