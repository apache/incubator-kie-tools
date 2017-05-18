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

package org.kie.workbench.common.screens.datamodeller.client.handlers.advanceddomain;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ResourceOptions;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

@ApplicationScoped
public class AdvancedDomainHandler implements DomainHandler {

    public AdvancedDomainHandler() {
    }

    @Override
    public String getName() {
        return "ADVANCED";
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public ResourceOptions getResourceOptions(boolean newInstance) {
        //this domain has no special options at resource creation time.
        return null;
    }

    @Override
    public void postCommandProcessing(DataModelCommand command) {
        //no post command processing for this domain.
    }

    @Override
    public boolean isDomainSpecificProperty(ObjectProperty objectProperty) {
        // no specific object properties defined for this domain
        return false;
    }
}
