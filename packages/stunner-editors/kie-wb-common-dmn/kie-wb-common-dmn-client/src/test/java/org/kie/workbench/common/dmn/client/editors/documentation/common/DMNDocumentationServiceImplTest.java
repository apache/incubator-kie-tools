/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.documentation.model.HTMLDocumentationTemplate;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDocumentationServiceImplTest {

    @Mock
    private ClientMustacheTemplateRenderer mustacheTemplateRenderer;

    @Mock
    private DMNDocumentationFactory dmnDocumentationFactory;

    @Mock
    private Diagram diagram;

    private DMNDocumentationServiceImpl service;

    @Before
    public void setup() {
        service = spy(new DMNDocumentationServiceImpl(mustacheTemplateRenderer, dmnDocumentationFactory));
    }

    @Test
    public void testProcessDocumentation() {

        final DMNDocumentation expectedDocumentation = mock(DMNDocumentation.class);

        when(dmnDocumentationFactory.create(diagram)).thenReturn(expectedDocumentation);

        final DMNDocumentation actualDocumentation = service.processDocumentation(diagram);

        assertEquals(expectedDocumentation, actualDocumentation);
    }

    @Test
    public void testGetDocumentationTemplate() {

        final HTMLDocumentationTemplate documentationTemplate = service.getDocumentationTemplate();
        final String expectedTemplate = "documentationTemplate";
        final String actualTemplate = documentationTemplate.getTemplate();

        assertEquals(expectedTemplate, actualTemplate);
    }

    @Test
    public void testBuildDocumentation() {

        final HTMLDocumentationTemplate template = mock(HTMLDocumentationTemplate.class);
        final DMNDocumentation documentation = mock(DMNDocumentation.class);
        final String documentationTemplate = "documentationTemplate";
        final String rendered = "<template rendered='true' />";
        final DocumentationOutput expectedOutput = new DocumentationOutput(rendered);

        when(template.getTemplate()).thenReturn(documentationTemplate);
        when(mustacheTemplateRenderer.render(documentationTemplate, documentation)).thenReturn(rendered);

        final DocumentationOutput actualOutput = service.buildDocumentation(template, documentation);

        assertEquals(expectedOutput.getValue(), actualOutput.getValue());
    }

    @Test
    public void testGenerateWhenDiagramIsPresent() {

        final HTMLDocumentationTemplate template = mock(HTMLDocumentationTemplate.class);
        final DMNDocumentation documentation = mock(DMNDocumentation.class);
        final DocumentationOutput expectedOutput = mock(DocumentationOutput.class);

        doReturn(template).when(service).getDocumentationTemplate();
        doReturn(documentation).when(service).processDocumentation(diagram);
        doReturn(expectedOutput).when(service).buildDocumentation(template, documentation);

        final DocumentationOutput actualOutput = service.generate(diagram);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testGenerateWhenDiagramIsNotPresent() {

        final DocumentationOutput expectedOutput = DocumentationOutput.EMPTY;
        final DocumentationOutput actualOutput = service.generate(null);

        assertEquals(expectedOutput, actualOutput);
    }
}
