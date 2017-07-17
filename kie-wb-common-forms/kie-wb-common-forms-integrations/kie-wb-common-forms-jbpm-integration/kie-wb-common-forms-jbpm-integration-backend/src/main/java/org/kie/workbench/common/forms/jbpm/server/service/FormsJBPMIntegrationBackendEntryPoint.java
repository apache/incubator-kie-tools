package org.kie.workbench.common.forms.jbpm.server.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.jbpm.model.authoring.document.type.DocumentFieldType;
import org.kie.workbench.common.forms.model.util.ModelPropertiesUtil;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class FormsJBPMIntegrationBackendEntryPoint {

    @PostConstruct
    public void init() {
        // registering Document Types to ModelPropertiesUtil
        ModelPropertiesUtil.registerBaseType(DocumentFieldType.DOCUMENT_TYPE);
        ModelPropertiesUtil.registerBaseType(DocumentFieldType.DOCUMENT_IMPL_TYPE);
    }
}
