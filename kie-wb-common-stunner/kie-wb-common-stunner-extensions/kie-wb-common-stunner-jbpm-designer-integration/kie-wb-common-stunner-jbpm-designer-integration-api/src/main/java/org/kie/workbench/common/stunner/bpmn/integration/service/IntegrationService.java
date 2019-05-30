/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.integration.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.uberfire.backend.vfs.Path;

@Remote
public interface IntegrationService {

    enum ServiceError {
        JBPM_DESIGNER_PROCESS_ALREADY_EXIST("integration.service.error.JBPM_DESIGNER_PROCESS_ALREADY_EXIST"),
        STUNNER_PROCESS_ALREADY_EXIST("integration.service.error.STUNNER_PROCESS_ALREADY_EXIST");

        private final String i18nKey;

        ServiceError(String i18nKey) {
            this.i18nKey = i18nKey;
        }

        public String i18nKey() {
            return i18nKey;
        }

    }

    MigrateResult migrateDiagram(final MigrateRequest request);

    MarshallingResponse<ProjectDiagram> getDiagramByPath(final Path file, final MarshallingRequest.Mode mode);
}
