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
