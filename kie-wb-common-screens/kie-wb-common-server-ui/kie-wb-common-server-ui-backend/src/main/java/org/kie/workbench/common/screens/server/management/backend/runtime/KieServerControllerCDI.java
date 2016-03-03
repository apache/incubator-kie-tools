/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.server.controller.api.service.NotificationService;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.rest.RestKieServerControllerImpl;

@ApplicationScoped
public class KieServerControllerCDI extends RestKieServerControllerImpl {

    @Inject
    @Override
    public void setTemplateStorage(KieServerTemplateStorage templateStorage) {
        super.setTemplateStorage(templateStorage);
    }

    @Inject
    @Override
    public void setNotificationService(NotificationService notificationService) {
        super.setNotificationService(notificationService);
    }
}
