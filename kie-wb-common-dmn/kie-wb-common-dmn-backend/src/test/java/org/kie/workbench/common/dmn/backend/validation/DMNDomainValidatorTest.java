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

package org.kie.workbench.common.dmn.backend.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.validation.DMNValidator;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.backend.DMNMarshaller;
import org.kie.workbench.common.dmn.backend.common.DMNIOHelper;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDomainValidatorTest {

    private static final String DMN_XML = "<Some XML/>";

    private static final String IMPORTED_DMN_XML = "<Some other XML/>";

    @Mock
    private DMNMarshaller dmnMarshaller;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private DMNMarshallerImportsHelper importsHelper;

    @Mock
    private DMNValidator dmnValidator;

    @Mock
    private DMNValidator.ValidatorBuilder dmnValidatorBuilder;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Consumer<Collection<DomainViolation>> resultConsumer;

    @Mock
    private DMNIOHelper dmnIOHelper;

    @Mock
    private DMNValidator.ValidatorBuilder.ValidatorImportReaderResolver resolver;

    @Captor
    private ArgumentCaptor<Collection<DomainViolation>> domainViolationsArgumentCaptor;

    @Captor
    private ArgumentCaptor<StringReader> readerArgumentCaptor;

    private Definitions definitions;

    private List<DMNMessage> validationMessages;

    private DMNDomainValidator domainValidator;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws IOException {
        this.definitions = new Definitions();
        this.validationMessages = new ArrayList<>();
        this.domainValidator = spy(new DMNDomainValidator(dmnMarshaller,
                                                          dmnDiagramUtils,
                                                          importsHelper,
                                                          dmnIOHelper));

        doReturn(dmnValidator).when(domainValidator).getDMNValidator();
        domainValidator.setupValidator();

        when(dmnMarshaller.marshall(diagram)).thenReturn(DMN_XML);
        when(dmnDiagramUtils.getDefinitions(diagram)).thenReturn(definitions);
        when(dmnValidator.validateUsing(anyVararg())).thenReturn(dmnValidatorBuilder);
        when(dmnValidatorBuilder.usingImports(resolver)).thenReturn(dmnValidatorBuilder);
        when(dmnValidatorBuilder.theseModels(any(Reader.class))).thenReturn(validationMessages);
        when(diagram.getMetadata()).thenReturn(metadata);
    }

    @Test
    public void testGetDefinitionSetId() {
        assertThat(domainValidator.getDefinitionSetId()).isEqualTo(DMNDefinitionSet.class.getName());
    }

    @Test
    public void testGetPMMLURI() {

        final Path modelPath = mock(Path.class);
        final String dmnModel = "model.dmn";
        final String path = "default://org/kie/drools/workbench/";

        final BiConsumer<String, String> assertPMMLURI = (expected, pmmlFileName) -> {
            assertEquals(expected, domainValidator.getPMMLURI(modelPath, pmmlFileName).toString());
        };

        when(modelPath.getFileName()).thenReturn(dmnModel);
        when(modelPath.toURI()).thenReturn(path + dmnModel);

        assertPMMLURI.accept("default://org/kie/drools/workbench/client/file.pmml", "client/file.pmml");
        assertPMMLURI.accept("default://org/kie/drools/workbench/file.pmml", "file.pmml");
        assertPMMLURI.accept("default://org/kie/file.pmml", "../../file.pmml");
    }

    @Test
    public void testGetValidatorImportReaderResolver() {

        final String modelNamespace = "0000-1111-2222-3333";
        final String modelName = "model.dmn";
        final String locationURI = "file.pmml";
        final String pmmlXML = "<pmml/>";
        final Path modelPath = mock(Path.class);
        final URI pmmlURI = URI.create(locationURI);
        final Path pmmlPath = mock(Path.class);
        final InputStream inputStream = mock(InputStream.class);

        when(importsHelper.getDMNModelPath(metadata, modelNamespace, modelName)).thenReturn(modelPath);
        when(importsHelper.loadPath(pmmlPath)).thenReturn(Optional.of(inputStream));
        when(dmnIOHelper.isAsString(inputStream)).thenReturn(pmmlXML);

        doReturn(pmmlURI).when(domainValidator).getPMMLURI(modelPath, locationURI);
        doReturn(pmmlPath).when(domainValidator).getPath(pmmlURI);

        final Reader actualReader = domainValidator.getValidatorImportReaderResolver(metadata).newReader(modelNamespace, modelName, locationURI);
        final Reader expectedReader = new StringReader(pmmlXML);

        assertContent(expectedReader, actualReader);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBasicValidation() throws IOException {
        final StringReader stringReader = mock(StringReader.class);

        doReturn(resolver).when(domainValidator).getValidatorImportReaderResolver(metadata);

        when(domainValidator.getStringReader(anyString())).thenReturn(stringReader);

        domainValidator.validate(diagram,
                                 resultConsumer);

        verify(dmnMarshaller).marshall(diagram);
        verify(dmnDiagramUtils).getDefinitions(diagram);
        verify(dmnValidator).validateUsing(DMNValidator.Validation.VALIDATE_MODEL,
                                           DMNValidator.Validation.VALIDATE_COMPILATION,
                                           DMNValidator.Validation.ANALYZE_DECISION_TABLE);
        verify(domainValidator).getStringReader(DMN_XML);
        verify(dmnValidatorBuilder).usingImports(resolver);
        verify(dmnValidatorBuilder).theseModels(readerArgumentCaptor.capture());
        assertThat(readerArgumentCaptor.getAllValues()).containsExactly(stringReader);

        verify(resultConsumer).accept(Collections.emptyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImportedModelValidation() throws IOException {
        final StringReader stringReader1 = mock(StringReader.class);
        final StringReader stringReader2 = mock(StringReader.class);

        doReturn(resolver).when(domainValidator).getValidatorImportReaderResolver(metadata);

        when(domainValidator.getStringReader(anyString())).thenReturn(stringReader1, stringReader2);

        definitions.getImport().add(new Import());

        when(importsHelper.getImportXML(eq(metadata), anyList())).thenAnswer(i -> {
            final Map<org.kie.dmn.model.api.Import, String> importedModels = new HashMap<>();
            final List<org.kie.dmn.model.api.Import> imports = (List) i.getArguments()[1];
            importedModels.put(imports.get(0), IMPORTED_DMN_XML);
            return importedModels;
        });

        domainValidator.validate(diagram,
                                 resultConsumer);

        verify(dmnMarshaller).marshall(diagram);
        verify(dmnDiagramUtils).getDefinitions(diagram);
        verify(dmnValidator).validateUsing(DMNValidator.Validation.VALIDATE_MODEL,
                                           DMNValidator.Validation.VALIDATE_COMPILATION,
                                           DMNValidator.Validation.ANALYZE_DECISION_TABLE);
        verify(domainValidator).getStringReader(DMN_XML);
        verify(domainValidator).getStringReader(IMPORTED_DMN_XML);
        verify(dmnValidatorBuilder).usingImports(resolver);
        verify(dmnValidatorBuilder).theseModels(readerArgumentCaptor.capture());
        assertThat(readerArgumentCaptor.getAllValues()).containsExactly(stringReader1, stringReader2);

        verify(resultConsumer).accept(Collections.emptyList());
    }

    @Test
    public void testValidationMessageConversion() {
        final String dmnElementUUID = "element-uuid";
        final DMNElement dmnElement1 = mock(DMNElement.class);
        final DMNElement dmnElement2 = mock(DMNElement.class);

        doReturn(resolver).when(domainValidator).getValidatorImportReaderResolver(metadata);

        //Default UUID
        validationMessages.add(makeDMNMessage(DMNMessage.Severity.ERROR, "error", null));
        //Explicit UUID
        when(dmnElement1.getId()).thenReturn(dmnElementUUID);
        validationMessages.add(makeDMNMessage(DMNMessage.Severity.WARN, "warn", dmnElement1));
        //Parent UUID
        when(dmnElement2.getParent()).thenReturn(dmnElement1);
        validationMessages.add(makeDMNMessage(DMNMessage.Severity.INFO, "info", dmnElement2));

        domainValidator.validate(diagram,
                                 resultConsumer);

        verify(resultConsumer).accept(domainViolationsArgumentCaptor.capture());

        final Collection<DomainViolation> domainViolations = domainViolationsArgumentCaptor.getValue();
        assertThat(domainViolations).hasSize(3);
        final Iterator<DomainViolation> domainViolationIterator = domainViolations.iterator();

        final DomainViolation domainViolation0 = domainViolationIterator.next();
        assertThat(domainViolation0.getViolationType()).isEqualTo(Violation.Type.ERROR);
        assertThat(domainViolation0.getMessage()).contains("error");
        assertThat(domainViolation0.getUUID()).isEqualTo(DMNDomainValidator.DEFAULT_UUID);

        final DomainViolation domainViolation1 = domainViolationIterator.next();
        assertThat(domainViolation1.getViolationType()).isEqualTo(Violation.Type.WARNING);
        assertThat(domainViolation1.getMessage()).contains("warn");
        assertThat(domainViolation1.getUUID()).isEqualTo(dmnElementUUID);

        final DomainViolation domainViolation2 = domainViolationIterator.next();
        assertThat(domainViolation2.getViolationType()).isEqualTo(Violation.Type.INFO);
        assertThat(domainViolation2.getMessage()).contains("info");
        assertThat(domainViolation2.getUUID()).isEqualTo(dmnElementUUID);
    }

    private DMNMessage makeDMNMessage(final DMNMessage.Severity severity,
                                      final String text,
                                      final DMNModelInstrumentedBase source) {
        return new DMNMessageImpl(severity, text, DMNMessageType.KIE_API, source);
    }

    private void assertContent(final Reader expectedReader,
                               final Reader actualReader) {
        try {
            assertTrue(IOUtils.contentEquals(expectedReader, actualReader));
        } catch (final IOException e) {
            fail();
        }
    }
}
