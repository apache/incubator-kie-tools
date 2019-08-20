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
package org.drools.workbench.screens.scenariosimulation.service;

import org.drools.scenariosimulation.api.model.AuditLog;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * This interface define the service to download a report with the <b>audit messages</b> of a given <code>Simulation</code>
 */
@Remote
public interface RunnerReportService {

    /**
     * This method returns the report of the given <code>AuditLog</code>>
     *
     * @param auditLog
     * @return
     */
    Object getReport(AuditLog auditLog);

}
