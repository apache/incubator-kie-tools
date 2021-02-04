/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTableGuidedToDecisionTableXLSConverterTest {

    @Mock
    GuidedDecisionTableEditorService guidedDecisionTableEditorService;
    @Mock
    DecisionTableXLSService decisionTableXLSService;
    @Mock
    DataModelService dataModelService;
    @Mock
    SessionInfo sessionInfo;
    @InjectMocks
    DecisionTableGuidedToDecisionTableXLSConverter converter;
    @Captor
    ArgumentCaptor<Path> pathArgumentCaptor;
    @Mock
    private IOService ioService;

    @Test
    public void convertEmptyTable() throws IOException {
        final Path path = PathFactory.newPath("file.gdst", "file:///contextpath/file.gdst");
        final GuidedDecisionTable52 table52 = new GuidedDecisionTable52();

        doReturn(mock(PackageDataModelOracle.class)).when(dataModelService).getDataModel(any());
        doReturn(table52).when(guidedDecisionTableEditorService).load(path);
        doReturn("id").when(sessionInfo).getId();

        final XLSConversionResult convert = converter.convert(path);

        assertNotNull(convert);
        verify(decisionTableXLSService).create(any(),
                                               any(ByteArrayInputStream.class),
                                               eq("id"),
                                               eq("Converted from file.gdst"));
    }

    @Test
    public void failureOnConversion() throws IOException {
        final Path path = PathFactory.newPath("file.gdst", "file:///contextpath/file.gdst");
        final GuidedDecisionTable52 table52 = new GuidedDecisionTable52();
        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute(Attribute.NEGATE_RULE.getAttributeName());
        table52.getAttributeCols().add(attributeCol52);

        doReturn(mock(PackageDataModelOracle.class)).when(dataModelService).getDataModel(any());
        doReturn(table52).when(guidedDecisionTableEditorService).load(path);

        final XLSConversionResult convert = converter.convert(path);

        assertNotNull(convert);
        verify(decisionTableXLSService, never()).create(any(),
                                                        any(ByteArrayInputStream.class),
                                                        any(),
                                                        any());
        assertFalse(convert.isConverted());
    }

    @Test
    public void fileExists() throws IOException {
        final Path path = PathFactory.newPath("file.gdst", "file:///contextpath/file.gdst");
        final GuidedDecisionTable52 table52 = new GuidedDecisionTable52();
        doReturn(mock(PackageDataModelOracle.class)).when(dataModelService).getDataModel(any());
        doReturn(table52).when(guidedDecisionTableEditorService).load(path);
        doReturn("id").when(sessionInfo).getId();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final org.uberfire.java.nio.file.Path proposedPath = (org.uberfire.java.nio.file.Path) invocation.getArguments()[0];
                return Objects.equals("file.gdst export.xls", Paths.convert(proposedPath).getFileName());
            }
        }).when(ioService).exists(any());

        final XLSConversionResult convert = converter.convert(path);

        assertNotNull(convert);
        verify(decisionTableXLSService).create(pathArgumentCaptor.capture(),
                                               any(ByteArrayInputStream.class),
                                               any(),
                                               any());
        final Path value = pathArgumentCaptor.getValue();
        assertEquals("file.gdst export (1).xls", value.getFileName());
    }
}