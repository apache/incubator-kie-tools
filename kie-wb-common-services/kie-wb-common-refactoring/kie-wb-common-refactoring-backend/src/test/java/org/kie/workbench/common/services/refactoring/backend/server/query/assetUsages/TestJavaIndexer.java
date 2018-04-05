/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class TestJavaIndexer extends AbstractFileIndexer implements TestIndexer<TestJavaResourceTypeDefinition> {

    private IOService ioService;

    private TestJavaResourceTypeDefinition javaResourceTypeDefinition;

    public TestJavaIndexer(KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public void setIOService(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void setModuleService(KieModuleService moduleService) {
    }

    @Override
    public void setResourceTypeDefinition(TestJavaResourceTypeDefinition type) {
        this.javaResourceTypeDefinition = type;
    }

    @Override
    public boolean supportsPath(Path path) {
        return javaResourceTypeDefinition.accept(Paths.convert(path));
    }

    @Override
    protected IndexBuilder fillIndexBuilder(Path path) throws Exception {
        final DefaultIndexBuilder builder = getIndexBuilder(path);
        if (builder == null) {
            return null;
        }

        String javaSource = ioService.readAllString(path);
        org.jboss.forge.roaster.model.JavaType<?> javaType = Roaster.parse(javaSource);

        if (javaType.getSyntaxErrors() == null || javaType.getSyntaxErrors().isEmpty()) {
            String pkgName = javaType.getPackage();
            if (pkgName == null) {
                pkgName = "";
            }
            // use Java class package name, not Package name
            builder.setPackageName(pkgName);

            String javaTypeName = javaType.getQualifiedName();
            Resource resParts = new Resource(javaTypeName,
                                             ResourceType.JAVA);

            if (javaType instanceof JavaSource) {
                TestJavaSourceVisitor visitor = new TestJavaSourceVisitor((JavaSource) javaType,
                                                                          resParts);
                visitor.visit((JavaSource) javaType);
                addReferencedResourcesToIndexBuilder(builder,
                                                     visitor);
            }

            builder.addGenerator(resParts);
        }

        return builder;
    }
}
