/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.command.DataModelChangeNotifier;
import org.kie.workbench.common.screens.datamodeller.client.command.AbstractDataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

@ApplicationScoped
public class JPACommandBuilder {

    @Inject
    private DataModelChangeNotifier notifier;

    @Inject
    private DataModelCommandBuilder commandBuilder;

    public JPACommandBuilder() {
    }

    public AdjustFieldDefaultRelationsCommand buildAdjustFieldDefaultRelationsCommand( AbstractDataModelCommand command, String source, ObjectProperty field ) {
        return new AdjustFieldDefaultRelationsCommand( command, source, field, notifier, commandBuilder );
    }
}
