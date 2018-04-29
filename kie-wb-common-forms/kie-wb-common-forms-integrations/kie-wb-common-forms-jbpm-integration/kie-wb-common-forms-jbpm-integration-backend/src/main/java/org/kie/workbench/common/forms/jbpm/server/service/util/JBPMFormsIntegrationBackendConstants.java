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

package org.kie.workbench.common.forms.jbpm.server.service.util;

public interface JBPMFormsIntegrationBackendConstants {

    String BUNDLE = "org.kie.workbench.common.forms.jbpm.server.service.BackendConstants";

    String MISSING_PROCESS_SHORT_KEY = "MissingProcess.shortMessage";

    String MISSING_PROCESS_FULL_KEY = "MissingProcess.fullMessage";

    String PROCESS_KEY = "process";

    String MISSING_TASK_SHORT_KEY = "MissingTask.shortMessage";

    String MISSING_TASK_FULL_KEY = "MissingTask.fullMessage";

    String RUNTIME_FORM_GENERATION_WARNING_TEMPLATE = "<div class=\"alert alert-warning\" role=\"alert\"><span class=\"pficon pficon-warning-triangle-o\"></span>\n <strong>{0}</strong>{1}</div>";

    String RUNTIMER_FORM_GENERATION_WARNING_KEY = "AutomaticallGeneratedForm.warning";

    String RUNTIMER_FORM_GENERATION_MESSAGE_KEY = "AutomaticallGeneratedForm.message";
}
