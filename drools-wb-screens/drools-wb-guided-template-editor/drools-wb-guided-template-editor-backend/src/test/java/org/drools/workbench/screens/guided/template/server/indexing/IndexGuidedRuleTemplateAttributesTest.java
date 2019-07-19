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

package org.drools.workbench.screens.guided.template.server.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.guided.template.backend.RuleTemplateModelXMLPersistenceImpl;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.type.GuidedRuleTemplateResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexGuidedRuleTemplateAttributesTest extends BaseIndexingTest<GuidedRuleTemplateResourceTypeDefinition> {

    @Test
    public void testIndexGuidedRuleTemplateAttributes() throws IOException, InterruptedException {
        //Add test files
        final Path path = basePath.resolve("template1.template");
        final TemplateModel model = GuidedRuleTemplateFactory.makeModelWithAttributes("org.drools.workbench.screens.guided.template.server.indexing",
                                                                                      new ArrayList<Import>() {{
                                                                                          add(new Import("org.drools.workbench.screens.guided.template.server.indexing.classes.Applicant"));
                                                                                          add(new Import("org.drools.workbench.screens.guided.template.server.indexing.classes.Mortgage"));
                                                                                      }},
                                                                                      "template1");


        model.addAttribute(new RuleAttribute("salience",
                                             "100"));
        model.addAttribute(new RuleAttribute("dialect",
                                             "java"));

        final String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal(model);
        ioService().write(path,
                          xml);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath).getClusterId());

        {
            final Query query = new SingleTermQueryBuilder(new ValueSharedPartIndexTerm("*", PartType.RULEFLOW_GROUP, TermSearchType.WILDCARD))
                    .build();
            searchFor(index, query, 1, path);
        }

        //Rule Template defining a RuleFlow-Group named myRuleFlowGroup. This should match template1.template
        //This checks whether there is a Rule Attribute "ruleflow-group" and its Value is "myRuleflowGroup"
        {
            final Query query = new SingleTermQueryBuilder(new ValueSharedPartIndexTerm("myRuleFlowGroup", PartType.RULEFLOW_GROUP))
                    .build();
            searchFor(index, query, 1, path);
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGuidedRuleTemplateFileIndexer();
    }

    @Override
    protected GuidedRuleTemplateResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedRuleTemplateResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
