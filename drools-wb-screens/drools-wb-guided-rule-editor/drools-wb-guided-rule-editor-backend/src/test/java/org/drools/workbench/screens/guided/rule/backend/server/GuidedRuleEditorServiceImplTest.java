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

package org.drools.workbench.screens.guided.rule.backend.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.drools.workbench.models.datamodel.oracle.DSLActionSentence;
import org.drools.workbench.models.datamodel.oracle.DSLConditionSentence;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.screens.guided.rule.model.GuidedEditorContent;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDSLRResourceTypeDefinition;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedRuleEditorServiceImplTest {

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEventEvent;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private GuidedRuleDSLRResourceTypeDefinition dslrResourceTypeDefinition;

    @Mock
    private GuidedRuleEditorServiceUtilities utilities;

    @Mock
    private DataModelService dataModelService;

    @Mock
    private IOService ioService;

    @Mock
    private DSLSentence dslSentence;

    @InjectMocks
    GuidedRuleEditorServiceImpl service = new GuidedRuleEditorServiceImpl(sessionInfo,
                                                                          mock(Instance.class));

    @Test
    public void checkConstructContentPopulateProjectCollectionTypesAndDSLSentences() throws Exception {
        final Path path = mock(Path.class);
        final Overview overview = mock(Overview.class);
        final ModuleDataModelOracle projectDataModelOracle = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addClass(List.class)
                .addClass(Set.class)
                .addClass(Collection.class)
                .addClass(Integer.class)
                .build();
        final PackageDataModelOracle oracle = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectDataModelOracle)
                .addExtension(DSLActionSentence.INSTANCE, Collections.singletonList(dslSentence))
                .addExtension(DSLConditionSentence.INSTANCE, Collections.singletonList(dslSentence))
                .build();
        when(path.toURI()).thenReturn("default://project/src/main/resources/mypackage/rule.rdrl");
        when(dataModelService.getDataModel(any())).thenReturn(oracle);

        final GuidedEditorContent content = service.constructContent(path,
                                                                     overview);
        assertEquals(3,
                     content.getDataModel().getCollectionTypes().size());
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.Collection"));
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.List"));
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.Set"));
        assertTrue(content.getDataModel().getPackageElements(DSLActionSentence.INSTANCE).contains(dslSentence));
        assertTrue(content.getDataModel().getPackageElements(DSLConditionSentence.INSTANCE).contains(dslSentence));
    }
}
