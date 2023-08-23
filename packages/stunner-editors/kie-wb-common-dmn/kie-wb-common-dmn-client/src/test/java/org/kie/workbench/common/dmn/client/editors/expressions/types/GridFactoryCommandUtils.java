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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;

import static org.assertj.core.api.Assertions.assertThat;

public class GridFactoryCommandUtils {

    public static void assertCommands(final org.kie.workbench.common.stunner.core.command.Command executedCommand,
                                      final Class... expectedCommandClasses) {
        //If only one command is expected; it's plausible the executed command is not a CompositeCommand with one element
        if (expectedCommandClasses.length == 1) {
            if (executedCommand.getClass().isAssignableFrom(expectedCommandClasses[0])) {
                return;
            }
        }

        assertThat(executedCommand).isInstanceOf(CompositeCommand.class);
        final CompositeCommand compositeCommand = (CompositeCommand) executedCommand;
        assertThat(compositeCommand.getCommands()).hasSize(expectedCommandClasses.length);

        for (int i = 0; i < expectedCommandClasses.length; i++) {
            assertThat(compositeCommand.getCommands().get(i)).isInstanceOf(expectedCommandClasses[i]);
        }
    }
}
