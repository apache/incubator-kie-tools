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

package org.guvnor.common.services.project.backend.server;

import javax.enterprise.context.Dependent;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.kie.soup.xstream.XStreamUtils;

@Dependent
public class ModuleRepositoriesContentHandler {

    public ModuleRepositoriesContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(final ModuleRepositories repositories) {
        if (repositories == null) {
            return "";
        }
        return createXStream().toXML(repositories);
    }

    public ModuleRepositories toModel(final String text) {
        try {
            if (text == null || text.isEmpty()) {
                return new ModuleRepositories();
            }
            return (ModuleRepositories) createXStream().fromXML(text);
        } catch (Exception e) {
            return new ModuleRepositories();
        }
    }

    private XStream createXStream() {
        XStream xStream = XStreamUtils.createTrustingXStream();
        xStream.alias("project-repositories",
                      ModuleRepositories.class);
        xStream.alias("repository",
                      ModuleRepositories.ModuleRepository.class);
        return xStream;
    }
}
