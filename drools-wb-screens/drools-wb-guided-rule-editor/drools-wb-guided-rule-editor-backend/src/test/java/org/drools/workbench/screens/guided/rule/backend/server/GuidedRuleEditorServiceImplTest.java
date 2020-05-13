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

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.oracle.DSLActionSentence;
import org.drools.workbench.models.datamodel.oracle.DSLConditionSentence;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.model.GuidedEditorContent;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDSLRResourceTypeDefinition;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedRuleEditorServiceImplTest {

    private static final String MODIFY_SCORE_ACTION = "scoreHolder.addMultiConstraintMatch(kcontext, 123, 456);";

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEventEvent;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private GenericValidator genericValidator;

    @InjectMocks
    GuidedRuleEditorServiceImpl service = new GuidedRuleEditorServiceImpl(sessionInfo,
                                                                          mock(Instance.class));

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

    @Mock
    private SaveAndRenameServiceImpl<RuleModel, Metadata> saveAndRenameService;

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
        when(path.toURI()).thenReturn("file://project/src/main/resources/mypackage/rule.rdrl");
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

    @Test
    public void testInit() throws Exception {
        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void testValidateModifyScoreAction() throws Exception {
        final Path path = mock(Path.class);
        final FreeFormLine modifyAction = new FreeFormLine();
        modifyAction.setText(MODIFY_SCORE_ACTION);
        final RuleModel model = new RuleModel();
        model.addRhsItem(modifyAction);

        service.validate(path, model);

        final ArgumentCaptor<String> capturedFileContent = ArgumentCaptor.forClass(String.class);
        verify(genericValidator).validate(eq(path), capturedFileContent.capture());

        Assertions.assertThat(capturedFileContent.getValue()).contains(MODIFY_SCORE_ACTION);
    }

    @Test
    public void testLoadModifyScoreAction() throws Exception {
        final Path path = getPath("modifyScore.rdrl");
        final String ruleFileContent = getFileContent("modifyScore.rdrl");
        doReturn(ruleFileContent).when(ioService).readAllString(any());

        final RuleModel model = service.load(path);

        Assertions.assertThat(model.rhs).hasSize(1);
        final FreeFormLine action = (FreeFormLine) model.rhs[0];
        Assertions.assertThat(action.getText()).isEqualTo(MODIFY_SCORE_ACTION);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final RuleModel content = mock(RuleModel.class);
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }

    private Path getPath(final String fileName) throws Exception {
        final URI testedFileURI = getClass().getResource(fileName).toURI();
        return PathFactory.newPath(fileName, testedFileURI.toString());
    }

    private String getFileContent(final String fileName) throws Exception {
        final URI testedFileURI = getClass().getResource(fileName).toURI();
        final java.nio.file.Path testedFile = Paths.get(testedFileURI);
        return Files.readAllLines(testedFile)
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
