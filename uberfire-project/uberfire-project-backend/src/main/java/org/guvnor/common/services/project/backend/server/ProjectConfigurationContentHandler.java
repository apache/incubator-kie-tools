/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server;

import javax.enterprise.context.Dependent;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.project.model.ProjectImports;
import org.kie.soup.xstream.XStreamUtils;
import org.kie.soup.project.datamodel.imports.Import;

@Dependent
public class ProjectConfigurationContentHandler {

    public ProjectConfigurationContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(final ProjectImports configuration) {
        if (configuration == null) {
            return "";
        }
        return createXStream().toXML(configuration);
    }

    public ProjectImports toModel(final String text) {
        if (text == null || text.isEmpty()) {
            return new ProjectImports();
        }
        return (ProjectImports) createXStream().fromXML(text);
    }

    private XStream createXStream() {
        XStream xStream = XStreamUtils.createTrustingXStream();
        xStream.alias("configuration",
                      ProjectImports.class);
        xStream.alias("import",
                      Import.class);
        return xStream;
    }
}
