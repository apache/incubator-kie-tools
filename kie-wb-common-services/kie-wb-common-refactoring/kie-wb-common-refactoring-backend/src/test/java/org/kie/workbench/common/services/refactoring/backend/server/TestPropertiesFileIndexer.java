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
package org.kie.workbench.common.services.refactoring.backend.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.services.refactoring.KPropertyImpl;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

/**
 * Test indexer that simply loads Properties from the file
 */
@ApplicationScoped
public class TestPropertiesFileIndexer implements TestIndexer<TestPropertiesFileTypeDefinition> {

    private IOService ioService;

    private KieProjectService projectService;

    private TestPropertiesFileTypeDefinition type;

    @Override
    public void setIOService(final IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void setProjectService(final KieProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void setResourceTypeDefinition(final TestPropertiesFileTypeDefinition type) {
        this.type = type;
    }

    @Override
    public boolean supportsPath(final Path path) {
        return type.accept(Paths.convert(path));
    }

    @Override
    public KObject toKObject(final Path path) {
        InputStream is = null;
        final Properties properties = new Properties();
        try {
            is = ioService.newInputStream(path);
            properties.load(is);
            is.close();
        } catch (IOException e) {
            //Swallow
        }
        final Set<KProperty<?>> indexElements = new HashSet<>();
        for (String propertyName : properties.stringPropertyNames()) {
            indexElements.add(new KPropertyImpl<>(propertyName,
                                                  properties.getProperty(propertyName)));
        }
        return KObjectUtil.toKObject(path,
                                     IndexTerm.REFACTORING_CLASSIFIER,
                                     indexElements);
    }

    @Override
    public KObjectKey toKObjectKey(final Path path) {
        return KObjectUtil.toKObjectKey(path,
                                        IndexTerm.REFACTORING_CLASSIFIER);
    }
}
