/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dsltext.backend.server.indexing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.compiler.lang.dsl.DSLMapping;
import org.drools.compiler.lang.dsl.DSLMappingEntry;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.workbench.screens.dsltext.type.DSLResourceTypeDefinition;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.IndexElementsGenerator;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.drools.AbstractDrlFileIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

// TODO: yeah, so there's no "rule => <rule name>" key-value pair added to the lucene index doc here
// (because we don't want to add the name of a dummy rule -- a name which is repeatedly used in order to
// be able to convert the DSl to a DRL and parse it..

// so..  what to do? :D (Maybe add DSL("dsl") to ResourceType and add a key-value pair of dsl => <dsl name>
// but what's the "dsl name" then?!?
@ApplicationScoped
public class DslFileIndexer extends AbstractDrlFileIndexer {

    public static final String MOCK_RULE_NAME = DslFileIndexer.class.getSimpleName() + "_parsing_dummy_rule";
    private static final Logger logger = LoggerFactory.getLogger(AbstractDrlFileIndexer.class);
    @Inject
    protected DSLResourceTypeDefinition dslType;
    @Inject
    private DataModelService dataModelService;

    @Override
    public boolean supportsPath(final Path path) {
        return dslType.accept(Paths.convert(path));
    }

    @Override
    public IndexBuilder fillIndexBuilder(final Path path) throws Exception {

        final List<String> lhs = new ArrayList<String>();
        final List<String> rhs = new ArrayList<String>();
        final String dsl = ioService.readAllString(path);

        //Construct a dummy DRL file to parse index elements
        final DSLTokenizedMappingFile dslLoader = new DSLTokenizedMappingFile();
        if (dslLoader.parseAndLoad(new StringReader(dsl))) {
            DSLMapping dslMapping = dslLoader.getMapping();
            for (DSLMappingEntry e : dslMapping.getEntries()) {
                switch (e.getSection()) {
                    case CONDITION:
                        lhs.add(e.getValuePattern());
                        break;
                    case CONSEQUENCE:
                        rhs.add(e.getValuePattern());
                        break;
                    default:
                        // no-op
                }
            }

            final String drl = makeDrl(path,
                                       lhs,
                                       rhs);

            return fillDrlIndexBuilder(path,
                                       drl);
        }

        return null;
    }

    @Override
    protected DefaultIndexBuilder getIndexBuilder(Path path) {
        final Module module = moduleService.resolveModule(Paths.convert(path));
        if (module == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": module could not be resolved.");
            return null;
        }

        final Package pkg = moduleService.resolvePackage(Paths.convert(path));
        if (pkg == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": package could not be resolved.");
            return null;
        }

        // responsible for basic index info: module name, branch, etc
        return new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                       module,
                                       pkg) {
            @Override
            public DefaultIndexBuilder addGenerator(final IndexElementsGenerator generator) {
                // Don't include the rule created to parse DSL
                if (generator instanceof Resource && ((Resource) generator).getResourceFQN().endsWith(MOCK_RULE_NAME)) {
                    return this;
                }
                return super.addGenerator(generator);
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.indexing.drools.AbstractDrlFileIndexer#getModuleDataModelOracle(org.uberfire.java.nio.file.Path)
     */
    @Override
    protected ModuleDataModelOracle getModuleDataModelOracle(final Path path) {
        return dataModelService.getModuleDataModel(Paths.convert(path));
    }

    private String makeDrl(final Path path,
                           final List<String> lhs,
                           final List<String> rhs) {
        final StringBuilder sb = new StringBuilder();
        final String packageName = getPackageName(path);
        if (!(packageName == null || packageName.isEmpty())) {
            sb.append("package ").append(packageName).append("\n");
        }
        sb.append("rule \"" + MOCK_RULE_NAME + "\"\n");
        sb.append("when\n");
        for (String e : lhs) {
            sb.append(e).append("\n");
        }
        sb.append("then\n");
        for (String e : rhs) {
            sb.append(e).append("\n");
        }
        sb.append("end\n");

        final String drl = sb.toString();
        return drl.replaceAll("\\{.*\\}",
                              "0");
    }
}
