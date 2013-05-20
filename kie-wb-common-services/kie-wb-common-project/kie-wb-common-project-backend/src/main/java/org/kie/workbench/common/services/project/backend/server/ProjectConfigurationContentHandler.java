package org.kie.workbench.common.services.project.backend.server;


import com.thoughtworks.xstream.XStream;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.kie.workbench.common.services.project.service.model.ProjectImports;

import javax.enterprise.context.Dependent;

@Dependent
public class ProjectConfigurationContentHandler {

    public ProjectConfigurationContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(ProjectImports configuration) {
        return createXStream().toXML(configuration);
    }

    public ProjectImports toModel(String text) {
        return (ProjectImports) createXStream().fromXML(text);
    }

    private XStream createXStream() {
        XStream xStream = new XStream();
        xStream.alias("configuration", ProjectImports.class);
        xStream.alias("import", Import.class);
        return xStream;
    }
}
