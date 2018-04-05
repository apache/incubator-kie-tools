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
package org.kie.workbench.common.services.refactoring.backend.server.drl;

import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;

import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.drools.AbstractDrlFileIndexer;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class TestDrlFileIndexer
        extends AbstractDrlFileIndexer
        implements TestIndexer<TestDrlFileTypeDefinition> {

    private TestDrlFileTypeDefinition type;

    @Override
    public void setIOService(final IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void setModuleService(final KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public void setResourceTypeDefinition(final TestDrlFileTypeDefinition type) {
        this.type = type;
    }

    @Override
    public boolean supportsPath(final Path path) {
        return type.accept(Paths.convert(path));
    }

    @Override
    protected IndexBuilder fillIndexBuilder(Path path) throws Exception {
        final String drl = ioService.readAllString(path);

        return fillDrlIndexBuilder(path, drl);
    }

    @Override
    protected ModuleDataModelOracle getModuleDataModelOracle(Path path) {
        final ModuleDataModelOracle dmo = new ModuleDataModelOracleImpl();
        dmo.addModuleModelFields(new HashMap<String, ModelField[]>() {{
            put("org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant",
                new ModelField[]{new ModelField("age",
                                                "java.lang.Integer",
                                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                ModelField.FIELD_ORIGIN.DECLARED,
                                                FieldAccessorsAndMutators.ACCESSOR,
                                                DataType.TYPE_NUMERIC_INTEGER)});
            put("org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage",
                new ModelField[]{new ModelField("amount",
                                                "java.lang.Integer",
                                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                ModelField.FIELD_ORIGIN.DECLARED,
                                                FieldAccessorsAndMutators.ACCESSOR,
                                                DataType.TYPE_NUMERIC_INTEGER)});
            put("org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage",
                new ModelField[]{new ModelField("applicant",
                                                "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant",
                                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                ModelField.FIELD_ORIGIN.DECLARED,
                                                FieldAccessorsAndMutators.ACCESSOR,
                                                "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant")});
            put("org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Bank",
                new ModelField[]{new ModelField("mortgage",
                                                "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage",
                                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                ModelField.FIELD_ORIGIN.DECLARED,
                                                FieldAccessorsAndMutators.ACCESSOR,
                                                "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage")});
        }});
        return dmo;
    }
}
