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

package org.appformer.wildfly;

import java.io.IOException;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.errai.cdi.server.gwt.spi.ContainerConfigExtension;

import static org.jboss.as.controller.client.helpers.Operations.createRemoveOperation;
import static org.jboss.as.controller.client.helpers.Operations.isSuccessfulOutcome;

public class DisableJBossLoggingExtension implements ContainerConfigExtension {

    private static final String LOGGING_SUBSYSTEM_NAME = "logging";

    @Override
    public void configure(ModelControllerClient controller) {
        ModelNode removeLoggingOp = createRemoveOperation(loggingSubsystemAddress());
        try {
            ModelNode result = controller.execute(removeLoggingOp);
            if (isSuccessfulOutcome(result)) {
                reload(controller);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to disable logging subsystem.", e);
        }
    }

    private void reload(ModelControllerClient controller) throws IOException {
        controller.execute(Operations.createOperation("reload"));
    }

    private ModelNode loggingSubsystemAddress() {
        return new ModelNode().add(ClientConstants.SUBSYSTEM, LOGGING_SUBSYSTEM_NAME);
    }

}
