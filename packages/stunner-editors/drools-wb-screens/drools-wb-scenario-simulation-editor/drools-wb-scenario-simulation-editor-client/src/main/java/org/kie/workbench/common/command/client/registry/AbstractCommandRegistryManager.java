/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.command.client.registry;

import javax.inject.Inject;

import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.kie.workbench.common.command.client.Command;

/**
 * This bean provides two Registries which can be used to manage the Undo/Redo functionality for an editor
 * Registry doneCommands contains executed commands by the user, while undoneCommand registry holds the undo commands
 * by the user, which can be redo. Used registries are compatible with Kogito and StateControlAPI.
 * @param <C>
 */
public abstract class AbstractCommandRegistryManager<C extends Command> {

    @Inject
    protected Registry<C> doneCommandsRegistry;
    @Inject
    protected DefaultRegistryImpl<C> undoneCommandsRegistry;

}
